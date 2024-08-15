package gitlet;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
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
    public static final File HEAD_POINTER = join(BRANCH_DIR, "head");
    /** The master pointer */
    public static final File MASTER_POINTER = join(BRANCH_DIR, "master");
    /** The stage area */
    public static final File STAGE = join(GITLET_DIR, "stage");
    /** The Blobs directory. */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");

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

    public static void add(String fileName) {
        Stage stage = Utils.readObject(STAGE, Stage.class);
        stage.addFile(fileName);
    }

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

    public static void log() {
        Commit m = getCommitFromHash(getHeadCommitCode());
        while (!m.isInit()) {
            System.out.println(m);
            m = getCommitFromHash(m.getParentCode());
        }
        System.out.println(m);
    }

    private static String getHeadCommitCode() {
        return Utils.readContentsAsString(HEAD_POINTER);
    }

    private static String getMasterCommitCode() {
        return Utils.readContentsAsString(MASTER_POINTER);
    }

    private static Commit getCommitFromHash(String hashCode) {
        Commit res = Utils.readObject(Utils.join(COMMIT_DIR, hashCode), Commit.class);
        res.restoreParent();
        return res;
    }

    private static void setHeadPointer(String code) {
        Utils.writeContents(HEAD_POINTER, code);
    }

    private static void setMasterPointer(String code) {
        Utils.writeContents(MASTER_POINTER, code);
    }
}
