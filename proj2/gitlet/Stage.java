package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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

    /** Constructor, there should be only one stage instance so
     *  constructor will be called only once during init.
     */
    public Stage() {
        this.trackedList = new TreeMap<>();
        this.addList = new TreeMap<>();
        this.removeList = new ArrayList<>();
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
        } else if (addList.containsKey(fileName)) {
            addList.remove(fileName);
        } else {
            removeList.add(fileName);
        }
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
                Utils.writeContents(blob, Utils.readContents(source));
            }
            trackedList.putAll(addList);
            addList.clear();

            for (String fn: removeList) {
                trackedList.remove(fn);
                Utils.restrictedDelete(Utils.join(Repository.CWD, fn));
            }
            removeList.clear();

            updateStatus();
            return true;
        }
    }

    public TreeMap<String, String> getTrackedList() {
        return trackedList;
    }

    /** Update status.
     * <p>
     * Write stage into STAGE file so that the status can be updated between add and commit.
     */
    private void updateStatus() {
        Utils.writeObject(Repository.STAGE, this);
    }
}
