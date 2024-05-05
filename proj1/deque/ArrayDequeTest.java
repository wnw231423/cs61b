package deque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void testResize() {
        ArrayDeque<Integer> L = new ArrayDeque<>();

        for (int i=0; i<10; i++) {
            L.addLast(i);
        }

        for (int i=0; i<10; i++) {
            int temp = L.get(i);
            assertEquals(temp, i);
        }
    }

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> lld1 = new ArrayDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        ArrayDeque<Double> lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

    }

    @Test
    public void testThreeAddLastThreeRemoveLast(){
        ArrayDeque<Integer> ls1 = new ArrayDeque<Integer>();

        int i = 0;
        while (i<3){
            ls1.addLast(i);
            i++;
        }

        i = 0;
        while (i<3){
            int r1 = ls1.removeLast();
            assertEquals(r1, 2-i);
            i++;
        }
    }

    @Test
    public void testThreeAddFirstThreeRemoveFirst(){
        ArrayDeque<Integer> ls1 = new ArrayDeque<Integer>();

        int i = 0;
        while (i<3){
            ls1.addFirst(i);
            i++;
        }

        i = 0;
        while (i<3){
            int r1 = ls1.removeFirst();
            assertEquals(r1, 2-i);
            i++;
        }
    }

    @Test
    public void testThreeAddLastThreeRemoveFirst(){
        ArrayDeque<Integer> ls1 = new ArrayDeque<Integer>();

        int i = 0;
        while (i<3){
            ls1.addLast(i);
            i++;
        }

        i = 0;
        while (i<3){
            int r1 = ls1.removeFirst();
            assertEquals(r1, i);
            i++;
        }
    }

    @Test
    public void testGet() {
        ArrayDeque<Integer> ls1 = new ArrayDeque<Integer>();

        for (int i=0; i<8; i++) {
            ls1.addLast(i);
        }
        for (int i=0; i<8; i++) {
            int temp = ls1.get(i);
            assertEquals(temp, i);
        }

        assertNull(ls1.get(8));
    }

    @Test
    public void randomizedTest(){
        ArrayDeque<Integer> B = new ArrayDeque<>();
        ArrayDeque<Integer> L = new ArrayDeque<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (L.isEmpty() && B.isEmpty()){
                operationNumber = StdRandom.uniform(0, 3);
            }
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeB = B.size();
                assertEquals(sizeB, size);
            } else if (operationNumber == 2){
                //addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                B.addFirst(randVal);
            } else if (operationNumber == 3) {
                int r1 = L.removeFirst();
                int r2 = B.removeFirst();
                assertEquals(r2, r1);
            } else if (operationNumber == 4) {
                int r1 = L.removeLast();
                int r2 = B.removeLast();
                assertEquals(r2, r1);
            }
        }
    }

    @Test
    public void testIteration() {
        ArrayDeque<Integer> A = new ArrayDeque<>();
        A.addLast(1);
        A.addLast(2);
        A.addLast(3);
        int i = 1;
        for (int num: A) {
            assertEquals(num, i);
            i++;
        }
    }

    @Test
    public void testEquals() {
        ArrayDeque<Integer> A = new ArrayDeque<>();
        ArrayDeque<Integer> B = new ArrayDeque<>();
        ArrayDeque<Integer> C = new ArrayDeque<>();
        int[] array = {1, 2, 3};

        int i = 1;
        while (i<=3) {
            A.addLast(i);
            B.addLast(i);
            C.addLast(3-i);
            i++;
        }

        assertTrue(A.equals(B));
        assertFalse(A.equals(C));
        assertFalse(A.equals(array));
    }
}
