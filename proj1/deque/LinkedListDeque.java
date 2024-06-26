package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private static class Node<T> {
        private T item;
        private Node<T> next;
        private Node<T> last;

        private Node(T item) {
            this.item = item;
            this.last = null;
            this.next = null;
        }
    }

    private Node<T> head;
    private int size;

    public LinkedListDeque() {
        size = 0;
        head = new Node<>(null);
        head.next = head;
        head.last = head;
    }

    @Override
    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);

        newNode.next = head.next;
        head.next.last = newNode;

        newNode.last = head;
        head.next = newNode;

        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);

        newNode.last = head.last;
        head.last.next = newNode;

        head.last = newNode;
        newNode.next = head;

        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node<T> temp = head;
        while (temp.next != head) {
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
        System.out.println(temp.item);
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node<T> res = head.next;
        head.next = res.next;
        res.next.last = head;

        size -= 1;

        return res.item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node<T> res = head.last;
        head.last = res.last;
        res.last.next = head;

        size -= 1;

        return res.item;
    }

    @Override
    public T get(int index) {
        if (index + 1 > size) {
            return null;
        } else {
            int i = 0;
            Node<T> temp = head.next;

            while (i != index) {
                i++;
                temp = temp.next;
            }

            return temp.item;
        }
    }

    public T getRecursive(int index) {
        return getRecursiveHelper(index, head.next);
    }

    private T getRecursiveHelper(int index, Node<T> start) {
        if (start == head) {
            return null;
        } else if (index == 0) {
            return start.item;
        } else {
            return getRecursiveHelper(index - 1, start.next);
        }
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof Deque<?>)) {
            return false;
        } else if (this.size != ((Deque<?>) o).size()) {
            return false;
        } else {
            for (int i = 0; i < size; i++) {
                if (!get(i).equals(((Deque<?>) o).get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator<T>();
    }

    private class LinkedListDequeIterator<T> implements Iterator<T> {
        private int wizPos;

        LinkedListDequeIterator() {
            wizPos  = 0;
        }

        @Override
        public boolean hasNext() {
            return wizPos <= size - 1;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T res = (T) get(wizPos);
                wizPos += 1;
                return res;
            } else {
                return null;
            }
        }
    }
}
