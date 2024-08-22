package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Represents stage Object in gitlet.
 *  It can add or remove files.
 */
public class Stage implements Serializable {
    /** Tracked list from head commit. */
    private TreeMap<String, String> trackedList;
    /** Add list */
    private TreeMap<String, String> addList;
    /** Remove list */
    private ArrayList<String> removeList;
    /** Current branch. */
    private String branch;

    /** Constructor, there should be only one stage instance so
     *  constructor will be called only once during init.
     */
    public Stage() {
        this.trackedList = new TreeMap<>();
        this.addList = new TreeMap<>();
        this.removeList = new ArrayList<>();
        this.branch = "master";
    }

    /** Add operation */
    public void addFile(String fileName) {
        File f = Utils.join(Repository.CWD, fileName);
        if (!f.exists()) {
            System.out.println("File doesn't exist.");
            System.exit(0);
        }
        String cwdCode = Utils.sha1(Utils.readContents(f));
        if (!trackedList.containsKey(fileName)) {
            addList.put(fileName, cwdCode);
        } else {
            if (trackedList.get(fileName).equals(cwdCode)) {
                addList.remove(fileName);
            } else {
                addList.put(fileName, cwdCode);
            }
        }
        updateStatus();
    }

    /** Remove operation. Return false if neither the file is staged for addition nor it's tracked. */
    public boolean rmFile(String fileName) {
        if (!trackedList.containsKey(fileName) && !addList.containsKey(fileName)) {
            return false;
        }
        addList.remove(fileName);
        if (trackedList.containsKey(fileName)) {
            removeList.add(fileName);
            trackedList.remove(fileName);
            Utils.restrictedDelete(Utils.join(Repository.CWD, fileName));
        }
        updateStatus();
        return true;
    }

    /** Clear stage and ready to commit, this method should only be called during committing.
     * <p>
     *  In my implementation, the stage will always inherit tracked files from head commit,
     *  every time it clear stage for commit, it will combine tracked files and files in stage,
     *  store it and return it for construct the next newly head commit. */
    public boolean clearStage() {
        if (addList.isEmpty() && removeList.isEmpty()) {
            return false;
        } else {
            for (Map.Entry<String, String> e:addList.entrySet()) {
                File source = Utils.join(Repository.CWD, e.getKey());
                File blob = Utils.join(Repository.BLOBS_DIR, e.getValue());
                if (!blob.exists()) {
                    Utils.writeContents(blob, Utils.readContents(source));
                }
            }
            trackedList.putAll(addList);
            addList.clear();

            removeList.clear();

            updateStatus();
            return true;
        }
    }

    public void clearStageWithBranchChange(String branch, TreeMap<String, String> tf) {
        this.branch = branch;
        this.trackedList = tf;
        this.addList.clear();
        this.removeList.clear();
        updateStatus();
    }

    public TreeMap<String, String> getTrackedList() {
        return trackedList;
    }

    public String getBranch() {
        return branch;
    }

    public ArrayList<String> getRemoveList() {
        return removeList;
    }

    public TreeMap<String, String> getAddList() {
        return addList;
    }

    /** Update status.
     * <p>
     * Write stage into STAGE file so that the status can be updated between add and commit.
     */
    private void updateStatus() {
        Utils.writeObject(Repository.STAGE, this);
    }

    /** Override for status command. */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("=== Branches ===\n");
        List<String> branches = Utils.plainFilenamesIn(Repository.BRANCH_DIR);
        s.append("*");
        s.append(this.branch);
        s.append("\n");
        for (String branch: branches) {
            if (!branch.equals(this.branch)) {
                s.append(branch);
                s.append("\n");
            }
        }
        s.append("\n");

        s.append("=== Staged Files ===\n");
        for (String fileName: addList.keySet()) {
            s.append(fileName);
            s.append("\n");
        }
        s.append("\n");

        s.append("=== Removed Files ===\n");
        for (String fileName: removeList) {
            s.append(fileName);
            s.append("\n");
        }
        s.append("\n");

        s.append("=== Modifications Not Staged For Commit ===\n");
        s.append("\n");

        s.append("=== Untracked Files ===\n");
        s.append("\n");

        return s.toString();
    }
}
