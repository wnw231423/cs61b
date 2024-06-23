package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private int size;
    private int head;
    private int rear;
    private int arraySize;
    private T[] array;

    public ArrayDeque() {
        size = 0;
        head = 0;
        rear = 0;
        arraySize = 8;
        array = (T[]) new Object[arraySize];
    }

    @Override
    public void addFirst(T item) {
        if (size + 1 >= arraySize) {
            resize(2 * arraySize);
        }
        array[head] = item;
        head = (head + arraySize - 1) % arraySize;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size + 1 >= arraySize) {
            resize(2 * arraySize);
        }
        rear = (rear + 1) % arraySize;
        array[rear] = item;
        size += 1;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (arraySize >= 16) {
            if (4 * (size - 1) < arraySize) {
                resize(arraySize / 2);
            }
        }
        head = (head + 1) % arraySize;
        T res = array[head];
        array[head] = null;
        size -= 1;
        return res;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (arraySize >= 16) {
            if (4 * (size - 1) < arraySize) {
                resize(arraySize / 2);
            }
        }
        T res = array[rear];
        array[rear] = null;
        rear = (rear - 1 + arraySize) % arraySize;
        size -= 1;
        return res;
    }

    @Override
    public void printDeque() {
        int index = 0;
        while (index != size) {
            System.out.print(get(index) + " ");
            index += 1;
        }
        System.out.println(get(index));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        if (index > size() - 1) {
            return null;
        } else {
            return array[(head + index + 1) % arraySize];
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

    private void resize(int x) {
        T[] resizedArray = (T[]) new Object[x];
        for (int i = 0; i < size; i++) {
            resizedArray[i + 1] = get(i);
        }
        arraySize = x;
        head = 0;
        rear = size;
        array = resizedArray;
    }

    private class ArrayDequeIterator<T> implements Iterator<T> {
        private int wizPos;

        ArrayDequeIterator() {
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

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator<T>();
    }
}
