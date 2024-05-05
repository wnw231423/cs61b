package deque;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Comparator;

public class MaxArrayDequeTest {
    public static class IntegerComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1-o2;
        }
    }

    public static class WeirdIntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return -(o1-o2);
        }
    }

    @Test
    public void maxTest() {
        IntegerComparator c = new IntegerComparator();
        MaxArrayDeque<Integer> ma = new MaxArrayDeque<>(c);
        ma.addLast(1);
        ma.addLast(5);
        ma.addLast(9);
        ma.addLast(11);
        ma.addLast(3);
        int max = ma.max();
        assertEquals(11, max);
    }

    @Test
    public void maxWithComparatorTest() {
        IntegerComparator c = new IntegerComparator();
        MaxArrayDeque<Integer> ma = new MaxArrayDeque<>(c);
        ma.addLast(1);
        ma.addLast(5);
        ma.addLast(9);
        ma.addLast(11);
        ma.addLast(3);
        WeirdIntegerComparator w = new WeirdIntegerComparator();
        int max = ma.max(w);
        assertEquals(1, max);
    }
}
