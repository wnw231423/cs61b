package deque;

public class LinkedListDeque<T> {
    public static class Node<T>{
        public T item;
        public Node<T> next;
        public Node<T> last;

        public Node(T item){
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

    public void addFirst(T item){
        Node<T> newNode = new Node<>(item);

        newNode.next = head.next;
        head.next.last = newNode;

        newNode.last = head;
        head.next = newNode;

        size += 1;
    }

    public void addLast(T item){
        Node<T> newNode = new Node<>(item);

        newNode.last = head.last;
        head.last.next = newNode;

        head.last = newNode;
        newNode.next = head;

        size += 1;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        Node<T> temp = head;
        while (temp.next!=head){
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
        System.out.println(temp.item);
    }

    public T removeFirst(){
        if (isEmpty()) {
            return null;
        }
        Node<T> res = head.next;
        head.next = res.next;
        res.next.last = head;

        size -= 1;

        return res.item;
    }

    public T removeLast(){
        if (isEmpty()) {
            return null;
        }
        Node<T> res = head.last;
        head.last = res.last;
        res.last.next = head;

        size -= 1;

        return res.item;
    }

    public T get(int index){
        if (index+1>size){
            return null;
        } else {
            int i = 0;
            Node<T> temp = head.next;

            while (i!=index){
                i++;
                temp = temp.next;
            }

            return temp.item;
        }
    }

    public T getRecursive(int index){
        return getRecursiveHelper(index, head.next);
    }

    private T getRecursiveHelper(int index, Node<T> start){
        if (start == head) {
            return null;
        } else if (index == 0) {
            return start.item;
        } else {
            return getRecursiveHelper(index - 1, start.next);
        }
    }

//    public Iterable<T> iterator(){
//        //TODO
//    }

    public boolean equals(Object o){
        if (!(o instanceof LinkedListDeque)){
            return false;
        } else if (size != ((LinkedListDeque<?>) o).size){
            return false;
        } else if (size == 0){
            return true;
        } else {
            Node<T> temp1 = head.next;
            Node<?> temp2 = ((LinkedListDeque<?>) o).head.next;
            //I have no idea how to check T is ? or not.
            while (temp1 != head){
                if (temp1.item != temp2.item){
                    return false;
                }
                temp1 = temp1.next;
                temp2 = temp2.next;
            }
            return true;
        }
    }
}
