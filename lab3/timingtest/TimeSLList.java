package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int n = 1000;
        AList<Integer> ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        while (n<=128000) {
            ns.addLast(n);
            opCounts.addLast(10000);
            SLList<Integer> ls = new SLList<>();

            int i = 0;
            while (i <= n) {
                ls.addLast(i);
                i++;
            }

            Stopwatch sw = new Stopwatch();
            i = 0;
            while (i<=10000){
                ls.getLast();
                i++;
            }
            double timeInSecond = sw.elapsedTime();
            times.addLast(timeInSecond);

            n *= 2;
        }
        printTimingTable(ns, times, opCounts);
    }
}
