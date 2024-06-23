package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void initial(GuitarString[] strings) {
        for (int i = 0; i < keyboard.length(); i++) {
            double frequency = 440 * Math.pow(2, (double) (i - 24) / 12);
            strings[i] = new GuitarString(frequency);
        }
    }

    public static void main(String[] args) {
        GuitarString[] strings = new GuitarString[37];
        initial(strings);

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index != -1) {
                    strings[index].pluck();
                }
            }

            double sample = 0;
            for (GuitarString string: strings) {
                sample += string.sample();
            }

            StdAudio.play(sample);

            for (GuitarString string: strings) {
                string.tic();
            }
        }
    }
}
