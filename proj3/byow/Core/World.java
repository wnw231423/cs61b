package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/** Including anything to do with world generation. */
public class World {
    private int seed;
    private Random random;
    public TETile[][] world;

    public static final int WORLD_WIDTH = 50;
    public static final int WORLD_HEIGHT = 50;

    private static final int ROOM_MIN = 3;
    private static final int ROOM_MAX = 6;

    private static class Position {
        public int x;
        public int y;

        public Position(int x, int y) {
            if (x < 0) {
                this.x = 0;
            } else if (x > WORLD_WIDTH - 1) {
                this.x = WORLD_WIDTH - 1;
            } else {
                this.x = x;
            }

            if (y < 0) {
                this.y = 0;
            } else if (y > WORLD_HEIGHT - 1) {
                this.y = WORLD_HEIGHT - 1;
            } else {
                this.y = y;
            }
        }

        public Position shift(int dx, int dy) {
            return new Position(x + dx, y + dy);
        }
    }

    public World(int seed) {
        this.seed = seed;
        this.random = new Random(seed);
        this.world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        //TODO: Finish rest.
        for (int i = 0; i < WORLD_WIDTH; i++) {
            for (int j = 0; j < WORLD_HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        walkAround();
    }

    private void walkAround() {
        int stepNum = WORLD_HEIGHT * WORLD_WIDTH * 4;
        Position start = new Position(0, 0);
        for (int i = 0; i < stepNum; i++) {
            start = stepOnce(start);
        }
    }

    private Position stepOnce(Position start) {
        int r = random.nextInt();
        int dx = 0, dy = 0;
        switch (r % 4) {
            case (0) -> {
                dx = 1;
            }
            case (1) -> {
                dx = -1;
            }
            case (2) -> {
                dy = 1;
            }
            case (3) -> {
                dy = -1;
            }
        }
        world[start.x][start.y] = Tileset.FLOOR;
        return start.shift(dx, dy);
    }
}
