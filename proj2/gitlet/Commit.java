package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  includes some operations about commit.
 *
 *  @author wnw231423
 */
public class Commit implements Serializable {
    /** The message of this Commit. */
    private String message;
    /** The parent of this commit. */
    private transient Commit parent;
    /** The parent of this commit, represented by hash code. */
    private String parentCode;
    /** commit date infomation */
    private Date commitDate;
    /** Another parent for merged commits, this should be null and
     *  only be set when merging.
     */
    private String parent2Code;
    /** Hash codes of tracked files. */
    private TreeMap<String, String> trackedFiles;

    /** Constructor of commit. */
    public Commit(String parentCode, String message, TreeMap<String, String> trackedFiles) {
        if (parentCode == null) {
            this.commitDate = new Date(0);
            this.parentCode = null;
        } else {
            this.parentCode = parentCode;
            this.commitDate = new Date();
        }
        this.message = message;
        this.trackedFiles = trackedFiles;
        this.parent2Code = null;
        restoreParent();
    }

    public Commit(String parentCode, String parent2Code, String message, TreeMap<String, String> trackedFiles) {
        this(parentCode, message, trackedFiles);
        this.parent2Code = parent2Code;
    }

    /** make this commit persistent. */
    public void doCommit() {
        File f = Utils.join(Repository.COMMIT_DIR, getSha1());
        Utils.writeObject(f, this);
    }

    /** Restore its parent commit pointer from deserialization,
     * only be called by getCommitFromHash of Repository class. */
    public void restoreParent(){
        if (parentCode == null) {
            this.parent = null;
        } else {
            File parentFile = Utils.join(Repository.COMMIT_DIR, parentCode);
            this.parent = Utils.readObject(parentFile, Commit.class);
        }
    }

    /** Search the hash code of given file name. */
    public String searchBlobHash(String fileName) {
        return trackedFiles.get(fileName);
    }

    /** Check if this commit is initial commit. */
    public boolean isInit() {
        return parentCode == null;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getSha1() {
        return Utils.sha1(Utils.serialize(this));
    }

    public TreeMap<String, String> getTrackedFiles(){return this.trackedFiles;}

    public String getMessage() {
        return message;
    }

    public void checkOutFile(String f) {
        File blob = Utils.join(Repository.BLOBS_DIR, trackedFiles.get(f));
        File target = Utils.join(Repository.CWD, f);
        Utils.writeContents(target, Utils.readContents(blob));
    }

    /** Override for log command. */
    @Override
    public String toString() {
        String hashCode = getSha1();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        String formattedDate = formatter.format(commitDate);
        StringBuilder s = new StringBuilder();

        s.append("===\n");

        s.append("commit ");
        s.append(hashCode);
        s.append("\n");

        if (parent2Code != null) {
            s.append("merge: ");
            s.append(parentCode, 0, 6);
            s.append(" ");
            s.append(parent2Code, 0, 6);
            s.append("\n");
        }

        s.append("Date: ");
        s.append(formattedDate);
        s.append("\n");

        s.append(message);
        s.append("\n");

        return s.toString();
    }
}
