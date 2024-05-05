package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> comparator) {
        super();
        this.comparator = comparator;
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        } else {
            T max = get(0);
            for (int i=1; i<size(); i++) {
                T temp = get(i);
                if (c.compare(max, temp) < 0) {
                    max = temp;
                }
            }
            return max;
        }
    }
}
