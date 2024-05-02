package deque;

public class ArrayDeque<T> {
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

    public boolean isEmpty() {
        return rear==head;
    }

    public void addFirst(T item) {
        if (size + 1 >= arraySize){
            resize(2*arraySize);
        }
        array[head] = item;
        head = (head+arraySize-1)%arraySize;
        size += 1;
    }

    public void addLast(T item) {
        if (size + 1 >= arraySize){
            resize(2*arraySize);
        }
        rear = (rear+1)%arraySize;
        array[rear] = item;
        size += 1;
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (arraySize>=16) {
            if (4*(size-1) < arraySize) {
                resize(arraySize/2);
            }
        }
        head = (head+1)%arraySize;
        T res = array[head];
        array[head] = null;
        size -= 1;
        return res;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (arraySize>=16) {
            if (4*(size-1) < arraySize) {
                resize(arraySize/2);
            }
        }
        T res = array[rear];
        array[rear] = null;
        rear = (rear-1+arraySize)%arraySize;
        size -= 1;
        return res;
    }

    public void printDeque() {
        int index = 0;
        while (index!=size){
            System.out.print(get(index) + " ");
            index += 1;
        }
        System.out.println(get(index));
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        if (index > size() - 1) {
            return null;
        } else {
            return array[(head+index+1)%arraySize];
        }
    }

//    public Iterator<T> iterator() {
//        //TODO
//    }

    public boolean equals(Object o) {
        if (!(o instanceof ArrayDeque)) {
            return false;
        } else {
            if (size != ((ArrayDeque<?>) o).size) {
                return false;
            } else {
                if (isEmpty()) {
                    return true;
                }
                int i = 0;
                while (i != size ) {
                    if (get(i) != ((ArrayDeque<?>) o).get(i)) {
                        return false;
                    }
                    i++;
                }
                return true;
            }
        }
    }

    private void resize(int x) {
        T[] resizedArray = (T[]) new Object[x];
        for (int i=0; i<size; i++) {
            resizedArray[i+1] = get(i);
        }
        arraySize = x;
        head = 0;
        rear = size;
        array = resizedArray;
    }
}
