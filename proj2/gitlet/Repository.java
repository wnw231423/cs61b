package gitlet;

import java.io.File;
import java.util.ArrayList;

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

    public static void init() {
        if (GITLET_DIR.exists()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BRANCH_DIR.mkdir();

        //initCommit
        Commit initCommit = new Commit(null, "initial commit", new ArrayList<>());
        Utils.writeObject(HEAD_POINTER, initCommit);
        Utils.writeObject(MASTER_POINTER, initCommit);
    }

    private static Commit getHeadCommit() {
        Commit res = Utils.readObject(HEAD_POINTER, Commit.class);
        res.restoreParent();
        return res;
    }

    private static Commit getMasterCommit() {
        Commit res = Utils.readObject(MASTER_POINTER, Commit.class);
        res.restoreParent();
        return res;
    }

    private static Commit getCommitFromHash(String hashCode) {
        Commit res = Utils.readObject(Utils.join(COMMIT_DIR, hashCode), Commit.class);
        res.restoreParent();
        return res;
    }
}
