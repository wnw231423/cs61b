package gitlet;

import com.sun.source.tree.Tree;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  Here are its utilities:
 *  1. give access to corresponding file and directory include commits, branches, blobs, stage
 *     in .gitlet as well as cwd.
 *  2. serve as bridge between main and each explicit operation, call methods for commands that
 *     user inputs.
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
            Utils.message("A Gitlet version-control system already exists " +
                    "in the current directory.");
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
    public static void commit(String message, String parent2Code) {
        if (message.isEmpty()) {
            mq("Please enter a commit message.");
        }
        Stage stage = Utils.readObject(STAGE, Stage.class);
        if (!stage.clearStage()) {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }

        String currentCommitCode = getHeadCommitCode();
        Commit commit = new Commit(currentCommitCode, parent2Code, message, stage.getTrackedList());
        commit.doCommit();

        setHeadPointer(commit.getSha1());
        setBranchPointer(stage.getBranch(), commit.getSha1());
    }

    public static void commit(String message) {
        commit(message, null);
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

        setHeadPointer(targetHashCode);
    }

    /** branch command. */
    public static void addBranch(String branchName) {
        File branchFile = Utils.join(BRANCH_DIR, branchName);
        if (branchFile.exists()) {
            mq("A branch with that name already exists.");
        }
        Utils.writeContents(branchFile, getHeadCommitCode());
    }

    /** rm-branch command. */
    public static void rmBranch(String branchName) {
        File f = Utils.join(BRANCH_DIR, branchName);
        if (!f.exists()) {
            mq("A branch with that name does not exist.");
        }
        if (branchName.equals(getStage().getBranch())) {
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
    }

    public static void merge(String branch) {
        Stage stage = getStage();
        if (!stage.getAddList().isEmpty() || !stage.getRemoveList().isEmpty()) {
            mq("You have uncommitted changes.");
        }
        File targetBranchFile = Utils.join(BRANCH_DIR, branch);
        if (!targetBranchFile.exists()) {
            mq("A branch with that name does not exist.");
        }
        String currentBranch = stage.getBranch();
        if (branch.equals(currentBranch)) {
            mq("Cannot merge a branch with itself.");
        }
        String targetCommitHash = Utils.readContentsAsString(targetBranchFile);
        String currentCommitHash = Utils.readContentsAsString(HEAD_POINTER);
        String splitCommitHash = getSplitPoint(currentCommitHash, targetCommitHash);
        if (splitCommitHash.equals(targetCommitHash)) {
            mq("Given branch is an ancestor of the current branch.");
        }
        if (splitCommitHash.equals(currentCommitHash)) {
            checkoutWithBranch(branch);
            mq("Current branch fast-forwarded.");
        }

        Commit currentCommit = getCommitFromHash(currentCommitHash);
        Commit splitCommit = getCommitFromHash(splitCommitHash);
        Commit targetCommit = getCommitFromHash(targetCommitHash);

        TreeMap<String, String> currentTrackedList = stage.getTrackedList();
        TreeMap<String, String> splitTrackedList = splitCommit.getTrackedFiles();
        TreeMap<String, String> targetTrackedList = targetCommit.getTrackedFiles();

        TreeSet<String> allFiles = new TreeSet<>();
        allFiles.addAll(currentTrackedList.keySet());
        allFiles.addAll(targetTrackedList.keySet());

        boolean conflicted = false;
        for (String file:allFiles) {
            boolean inSplit = splitTrackedList.containsKey(file);
            boolean inOther = targetTrackedList.containsKey(file);
            boolean inHead = currentTrackedList.containsKey(file);
            boolean modifiedHead = (!inSplit && inHead) || (inSplit && !inHead)
                    || (inSplit && inHead
                    && !currentTrackedList.get(file).equals(splitTrackedList.get(file)));
            boolean modifiedOther = (!inSplit && inOther) || (inSplit && !inOther)
                    || (inSplit && inOther
                    && !targetTrackedList.get(file).equals(splitTrackedList.get(file)));

            if (modifiedOther && !modifiedHead) {
                if (inSplit && !inOther) {
                    stage.rmFile(file);
                } else {
                    targetCommit.checkOutFile(file);
                    stage.addFile(file);
                }
            } else {
                if (!inHead && inOther) {
                    targetCommit.checkOutFile(file);
                    stage.addFile(file);
                } else if (inHead && inOther
                        && !currentTrackedList.get(file).equals(targetTrackedList.get(file))) {
                    //conflict
                    File head = Utils.join(BLOBS_DIR, currentTrackedList.get(file));
                    File other = Utils.join(BLOBS_DIR, targetTrackedList.get(file));
                    Utils.writeContents(Utils.join(CWD, file), "<<<<<<< HEAD\n",
                            Utils.readContents(head), "=======\n", Utils.readContents(other),
                            ">>>>>>>");
                    conflicted = true;
                }
            }
        }
        commit("Merged " + branch + " into " + stage.getBranch() + ".");
        if (conflicted) {
            System.out.println("Encountered a merge conflict.");
        }
    }


    /* Helper functions. */

    /** This method check if a working file is untracked in the
     *  current branch and would be overwritten. Then check to
     *  the commit of given id.
     */
    private static void validateCheckout(String branch, String id) {
        //check if there exists current untracked file that would be overwritten.
        //Delete the file that given commit is not tracking.
        String targetHashCode = id;
        Commit targetCommit = getCommitFromHash(targetHashCode);
        TreeMap<String, String> targetTrackedFiles = targetCommit.getTrackedFiles();
        TreeMap<String, String> currentTrackedFiles = getStage().getTrackedList();
        List<String> workingFiles = Utils.plainFilenamesIn(CWD);
        for (String workingFile: workingFiles) {
            String workingHash = Utils.sha1(readContents(Utils.join(CWD, workingFile)));
            if (!currentTrackedFiles.containsKey(workingFile) &&
                    targetTrackedFiles.containsKey(workingFile) &&
                    !targetTrackedFiles.get(workingFile).equals(workingHash)) {
                mq("There is an untracked file in the way; delete it, or add and commit it first.");
            }
            if (!targetTrackedFiles.containsKey(workingFile)) {
                Utils.restrictedDelete(Utils.join(CWD, workingFile));
            }
        }

        //do checkout operation.
        for (String workingFile: workingFiles) {
            restrictedDelete(workingFile);
        }
        for (Map.Entry<String, String> e: targetTrackedFiles.entrySet()) {
            File temp = Utils.join(CWD, e.getKey());
            File blob = Utils.join(BLOBS_DIR, e.getValue());
            Utils.writeContents(temp, Utils.readContents(blob));
        }
        getStage().clearStageWithBranchChange(branch, targetTrackedFiles);
        setHeadPointer(targetHashCode);
    }

    private static String getSplitPoint(String hash1, String hash2) {
        Commit c1 = getCommitFromHash(hash1);
        Commit c2 = getCommitFromHash(hash2);
        ArrayList<String> c1Ancestor = getAncestors(c1);
        ArrayList<String> c2Ancestor = getAncestors(c2);
        int x = 0;
        while (x < c1Ancestor.size() && x < c2Ancestor.size()) {
            if (!c1Ancestor.get(x).equals(c2Ancestor.get(x))) {
                return c1Ancestor.get(x - 1);
            } else {
                x += 1;
            }
        }
        return c1Ancestor.get(x - 1);
    }

    private static ArrayList<String> getAncestors(Commit c) {
        ArrayList<String> res = new ArrayList<>();
        res.add(c.getSha1());
        while (!c.isInit()) {
            res.add(0, c.getParentCode());
            c = getCommitFromHash(c.getParentCode());
        }
        return res;
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
            File commit = Utils.join(COMMIT_DIR, hashCode);
            if (commit.exists()) {
                Commit res = Utils.readObject(commit, Commit.class);
                res.restoreParent();
                return res;
            } else {
                return null;
            }
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
