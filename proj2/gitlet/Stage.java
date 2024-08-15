package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/** Represents stage Object in gitlet.
 *  It can add or remove files. TODO: implement remove operation later.
 */
public class Stage implements Serializable {
    /** Tracked list from head commit. */
    private TreeMap<String, String> trackedList;
    /** Add list */
    private TreeMap<String, String> addList;
    /** Remove list */
    private TreeMap<String, String> removeList;

    /** Constructor, there should be only one stage instance so
     *  constructor will be called only once during init.
     */
    public Stage() {
        this.trackedList = new TreeMap<>();
        this.addList = new TreeMap<>();
        this.removeList = new TreeMap<>();
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

    /** Clear stage and ready to commit, this method should only be called during committing. */
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
            //TODO: handle remove situation
            removeList.clear();

            updateStatus();
            return true;
        }
    }

    public TreeMap<String, String> getTrackedList() {
        return trackedList;
    }

    private void updateStatus() {
        Utils.writeObject(Repository.STAGE, this);
    }
}
