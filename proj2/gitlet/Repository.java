package gitlet;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  Here are its utilities:
 *  1. give access to corresponding file and directory include commits, branches, blobs, stage in .gitlet
 *     as well as cwd.
 *  2. serve as bridge between main and each explicit operation, call methods for commands that user inputs.
 *
 *  @author wnw231423
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    /** The branch directory, which contains the head, master and other possible pointer. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");
    /** The head pointer */
    public static final File HEAD_POINTER = join(GITLET_DIR, "head");
    /** The master pointer */
    public static final File MASTER_POINTER = join(BRANCH_DIR, "master");
    /** The stage area */
    public static final File STAGE = join(GITLET_DIR, "stage");
    /** The Blobs directory. */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blob");

    /** To init a gitlet repo. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BRANCH_DIR.mkdir();
        BLOBS_DIR.mkdir();

        //initCommit
        Commit initCommit = new Commit(null, "initial commit", new TreeMap<>());
        initCommit.doCommit();
        setHeadPointer(initCommit.getSha1());
        setMasterPointer(initCommit.getSha1());

        //Create stage
        Stage stage = new Stage();
        Utils.writeObject(STAGE, stage);
    }

    /** Check if there exists .gitlet dir so that other commands works. */
    public static void checkInit() {
        if (!GITLET_DIR.exists()) {
            System.exit(0);
        }
    }

    /** Stage a file */
    public static void add(String fileName) {
        Stage stage = Utils.readObject(STAGE, Stage.class);
        stage.addFile(fileName);
    }

    /** Make a commit */
    public static void commit(String message) {
        Stage stage = Utils.readObject(STAGE, Stage.class);
        if (!stage.clearStage()) {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }

        String currentCommitCode = getHeadCommitCode();
        Commit commit = new Commit(currentCommitCode, message, stage.getTrackedList());
        commit.doCommit();

        setHeadPointer(commit.getSha1());
        setMasterPointer(commit.getSha1());
    }

    /** Show commit log. */
    public static void log() {
        Commit m = getCommitFromHash(getHeadCommitCode());
        while (!m.isInit()) {
            System.out.println(m);
            m = getCommitFromHash(m.getParentCode());
        }
        System.out.println(m);
    }

    /** Checkout one file from head or one specific commit using hash code. */
    public static void checkoutWithoutBranch(String fileName, String commitCode) {
        Commit commit = getCommitFromHash(commitCode);
        if (commit == null) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
        }

        String blobHash = commit.searchBlobHash(fileName);
        if (blobHash == null) {
            Utils.message("File does not exist in that commit.");
            System.exit(0);
        }

        File blob = Utils.join(BLOBS_DIR, blobHash);
        File cwdFile = Utils.join(CWD, fileName);
        Utils.writeContents(cwdFile, Utils.readContents(blob));
    }

    /** Just Overload. */
    public static void checkoutWithoutBranch(String fileName) {
        checkoutWithoutBranch(fileName, Repository.getHeadCommitCode());
    }

    /** rm command. */
    public static void rm(String fileName) {
        Stage stage = Utils.readObject(STAGE, Stage.class);
        if (!stage.rmFile(fileName)) {
            mq("No reason to remove the file.");
        }
    }

    /** global-log command. */
    public static void globalLog() {
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String hash: commits) {
            Commit m = getCommitFromHash(hash);
            System.out.println(m);
        }
    }

    /** status command. */
    public static void status() {
        Stage s = getStage();
        System.out.println(s);
    }

    /** find command. */
    public static void find(String message) {
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);
        boolean found = false;
        for (String uid: commits) {
            Commit commit = getCommitFromHash(uid);
            if (commit.getMessage().equals(message)) {
                System.out.println(uid);
                found = true;
            }
        }
        if (!found) {
            Utils.message("Found no commit with that message.");
        }
    }

    /** checkout [branch] */
    public static void checkoutWithBranch(String branch) {
        File f = Utils.join(BRANCH_DIR, branch);
        if (!f.exists()) {
            mq("No such branch exists.");
        }

        Stage stage = getStage();
        //check if no need to checkout branch.
        if (branch.equals(stage.getBranch())) {
            mq("No need to checkout the current branch.");
        }

        String targetHashCode = Utils.readContentsAsString(f);
        validateCheckout(branch, targetHashCode);
    }

    /** branch command. */
    public static void addBranch(String branchName) {
        File branchFile = Utils.join(BRANCH_DIR, branchName);
        Utils.writeContents(branchFile, getHeadCommitCode());
    }

    /** rm-branch command. */
    public static void rmBranch(String branchName) {
        File f = Utils.join(BRANCH_DIR, branchName);
        if (!f.exists()) {
            mq("A branch with that name does not exist.");
        }
        if (f.equals(getStage().getBranch())) {
            mq("Cannot remove the current branch.");
        }
        f.delete();
    }

    public static void reset(String id) {
        File f = Utils.join(COMMIT_DIR, id);
        if (!f.exists()) {
            mq("No commit with that id exists.");
        }
        String branch = getStage().getBranch();
        validateCheckout(branch, id);
        setHeadPointer(id);
        setBranchPointer(branch, id);
    }


    /* Helper functions. */

    /** This method check if a working file is untracked in the
     *  current branch and would be overwritten. Then check to
     *  the commit of given id.
     */
    private static void validateCheckout(String branch, String id) {
        //check if there exists untracked file.
        Commit currentCommit = getCommitFromHash(getHeadCommitCode());
        TreeMap<String, String> trackedFiles = currentCommit.getTrackedFiles();
        List<String> workingFiles = Utils.plainFilenamesIn(CWD);
        for (String workingFile: workingFiles) {
            if (!trackedFiles.containsKey(workingFile)) {
                mq("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        //do checkout operation.
        for (String workingFile: workingFiles) {
            restrictedDelete(workingFile);
        }
        String targetHashCode = id;
        Commit targetCommit = getCommitFromHash(targetHashCode);
        TreeMap<String, String> targetTrackedFiles = targetCommit.getTrackedFiles();
        for (Map.Entry<String, String> e: targetTrackedFiles.entrySet()) {
            File temp = Utils.join(CWD, e.getKey());
            File blob = Utils.join(BLOBS_DIR, e.getValue());
            Utils.writeContents(temp, Utils.readContents(blob));
        }
        getStage().clearStageWithBranchChange(branch, targetTrackedFiles);
        setHeadPointer(targetHashCode);
    }

    private static String getBranchPointerCode(String branchName) {
        return Utils.readContentsAsString(Utils.join(BRANCH_DIR, branchName));
    }

    private static String getHeadCommitCode() {
        return Utils.readContentsAsString(HEAD_POINTER);
    }

    private static String getMasterCommitCode() {
        return getBranchPointerCode("master");
    }

    private static Commit getCommitFromHash(String hashCode) {
        if (hashCode.length() == UID_LENGTH) {
            Commit res = Utils.readObject(Utils.join(COMMIT_DIR, hashCode), Commit.class);
            res.restoreParent();
            return res;
        } else {
            List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);
            for (String code: commits) {
                if (code.startsWith(hashCode)) {
                    return getCommitFromHash(code);
                }
            }
            return null;
        }
    }

    private static void setHeadPointer(String code) {
        Utils.writeContents(HEAD_POINTER, code);
    }

    private static void setBranchPointer(String branch, String code) {
        Utils.writeContents(Utils.join(BRANCH_DIR, branch), code);
    }

    private static void setMasterPointer(String code) {
        setBranchPointer("master", code);
    }

    private static Stage getStage() {
        return Utils.readObject(STAGE, Stage.class);
    }

    public static void mq(String m) {
        Utils.message(m);
        System.exit(0);
    }
}
