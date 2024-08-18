package gitlet;

import java.io.File;
import java.util.List;
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


    /* Helper functions. */
    private static String getHeadCommitCode() {
        return Utils.readContentsAsString(HEAD_POINTER);
    }

    private static String getMasterCommitCode() {
        return Utils.readContentsAsString(MASTER_POINTER);
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

    private static void setMasterPointer(String code) {
        Utils.writeContents(MASTER_POINTER, code);
    }

    private static Stage getStage() {
        return Utils.readObject(STAGE, Stage.class);
    }

    public static void mq(String m) {
        Utils.message(m);
        System.exit(0);
    }
}
