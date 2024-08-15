package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
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
        restoreParent();
    }

    public void doCommit() {
        File f = Utils.join(Repository.COMMIT_DIR, getSha1());
        Utils.writeObject(f, this);
    }

    public void restoreParent(){
        if (parentCode == null) {
            this.parent = null;
        } else {
            File parentFile = Utils.join(Repository.COMMIT_DIR, parentCode);
            this.parent = Utils.readObject(parentFile, Commit.class);
        }
    }

    public String getSha1() {
        return Utils.sha1(Utils.serialize(this));
    }

    public TreeMap<String, String> getTrackedFiles(){return this.trackedFiles;}
}
