package hashmap;


import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int bucketsSize;
    private int size = 0;
    private double loadFactor;
    private final double defaultLoadFactor = 0.75;

    /** Constructors */
    public MyHashMap() {
        this.bucketsSize = 16;
        this.loadFactor = defaultLoadFactor;
        this.buckets = createTable(bucketsSize);
    }

    public MyHashMap(int initialSize) {
        this.bucketsSize = initialSize;
        this.loadFactor = defaultLoadFactor;
        this.buckets = createTable(bucketsSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.bucketsSize = initialSize;
        this.loadFactor = maxLoad;
        this.buckets = createTable(bucketsSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        assert key!=null;
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] res = new Collection[tableSize];
        for (int i=0; i<tableSize; i++) {
            res[i] = createBucket();
        }
        return res;
    }

    private int getIndex(K k) {
        int hash = k.hashCode();
        return (hash % bucketsSize + bucketsSize) % bucketsSize;
    }

    private void resize(int n) {
        this.bucketsSize = n;
        Collection<Node>[] old = this.buckets;
        this.buckets = createTable(n);
        this.size = 0;

        for (Collection<Node> c: old) {
            for (Node node: c) {
                put(node.key, node.value);
            }
        }
    }

    @Override
    public void clear() {
        // Not required to resize down.
        this.size = 0;
        for (Collection<Node> e: buckets) {
            e.clear();
        }
    }

    @Override
    public boolean containsKey(K key) {
        int index = getIndex(key);
        for (Node n: buckets[index]) {
            if (n.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int index = getIndex(key);
        for (Node n: buckets[index]) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if ((double) (size + 1) / bucketsSize > loadFactor) {
            resize(2 * bucketsSize);
        }

        int index = getIndex(key);
        for (Node n: buckets[index]) {
            if (n.key.equals(key)) {
                n.value = value;
                return;
            }
        }
        Node n = createNode(key, value);
        buckets[index].add(n);
        size += 1;
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> res = new HashSet<>();
        for (Collection<Node> bucket: buckets) {
            for (Node n: bucket) {
                res.add(n.key);
            }
        }
        return res;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        Set<K> res = keySet();
        return res.iterator();
    }
}
