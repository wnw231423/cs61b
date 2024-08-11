package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private int size;
    private class BSTMapNode {
        private K key;
        private V value;

        BSTMapNode(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "<"+key+": "+value+">";
        }
    }

    private BSTMapNode root;
    private BSTMap<K, V> left;
    private BSTMap<K, V> right;

    public BSTMap() {
        this.size = 0;
        this.root = null;
    }

    public void printInOrder() {
        if (size() == 0) {
            System.out.println("<None>");
        } else {
            if (left != null) {
                left.printInOrder();
            }
            System.out.print(root);
            if (right != null) {
                right.printInOrder();
            }
        }
    }

    @Override
    public void clear() {
        this.size = 0;
        this.root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }

        K rk = root.getKey();
        if (key.equals(rk)) {
            return true;
        } else if (key.compareTo(rk) < 0) {
            return left.containsKey(key);
        } else {
            return right.containsKey(key);
        }
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        }

        K rk = root.getKey();
        if (key.equals(rk)) {
            return root.getValue();
        } else if (key.compareTo(rk) < 0 && left != null) {
            return left.get(key);
        } else if (key.compareTo(rk) > 0 && right != null) {
            return right.get(key);
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTMapNode(key, value);
            size += 1;
        } else {
            K rk = root.getKey();
            if (key.equals(rk)) {
                root.value = value;
            } else if (key.compareTo(rk) < 0) {
                if (left == null) {
                    left = new BSTMap<>();
                }
                left.put(key, value);
            } else {
                if (right == null) {
                    right = new BSTMap<>();
                }
                right.put(key, value);
            }

            //refresh size.
            size = 1;
            if (left != null) {
                size += left.size();
            }
            if (right != null) {
                size += right.size();
            }
        }
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }
}
