package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> ls1 = new AListNoResizing<>();
        BuggyAList<Integer> ls2 = new BuggyAList<>();

        int i = 0;
        while (i<3){
            ls1.addLast(i);
            ls2.addLast(i);
            i++;
        }

        i = 0;
        while (i<3){
            int r1 = ls1.removeLast();
            int r2 = ls2.removeLast();
            assertEquals(r2, r1);
            i++;
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (L.size() == 0 && B.size() == 0){
                operationNumber = StdRandom.uniform(0, 2);
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
                } else if (operationNumber == 2) {
                    int r1 = L.getLast();
                    int r2 = B.getLast();
                    assertEquals(r2, r1);
                } else if (operationNumber == 3) {
                    int r1 = L.removeLast();
                    int r2 = B.removeLast();
                    assertEquals(r2, r1);
                }
        }
    }
}
