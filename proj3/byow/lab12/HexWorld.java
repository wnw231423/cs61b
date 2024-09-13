package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private final int HEIGHT = 100;
    private final int WIDTH = 100;
    TETile[][] world = new TETile[WIDTH][HEIGHT];
    private final int seed = 231423;
    private final Random RANDOM = new Random(seed);

    /**
     * To give one specific start point for drawing.
     */
    public static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(x + dx, y + dy);
        }
    }

    public void drawTile(Position start, TETile tile) {
        this.world[start.x][start.y] = tile;
    }

    public void drawLine(Position start, TETile tile, int num) {
        for (int i = 0; i < num; i++) {
            drawTile(start.shift(i, 0), tile);
        }
    }

    public void drawSingleHex(int size, Position start, TETile tile) {
        drawSingleHexHelper(size-1, size, start, tile);
    }

    public void drawSingleHexHelper(int nothing, int num, Position start, TETile tile) {
        drawLine(start.shift(nothing, 0), tile, num);
        if (nothing > 0 ) {
            drawSingleHexHelper(nothing-1, num+2, start.shift(0, 1), tile);
        }
        drawLine(start.shift(nothing, 2 * nothing + 1), tile, num);
    }

    public void initWorldWithNothing() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    public void tessellate(int size, Position start) {
        // Because the world need exactly 19 hexes, so I do hard code here.
        tessellateHelper(size, start, 5);
        tessellateHelper(size, start.shift(size + (size - 1), size), 4);
        tessellateHelper(size, start.shift(-(size + (size - 1)), size), 4);
        tessellateHelper(size, start.shift((size + (size - 1)) * 2, size * 2), 3);
        tessellateHelper(size, start.shift(-((size + (size - 1)) * 2), size * 2), 3);
    }

    public void tessellateHelper(int size, Position start, int num) {
        for (int i = 0; i < num; i++) {
            drawSingleHex(size, start, getRandomTile());
            start = start.shift(0, 2 * size);
        }
    }

    public TETile getRandomTile() {
        int x = RANDOM.nextInt();
        switch (x % 4) {
            case 1 -> {
                return Tileset.FLOWER;
            }
            case 2 -> {
                return Tileset.WALL;
            }
            case 0 -> {
                return Tileset.MOUNTAIN;
            }
            default -> {
                return Tileset.AVATAR;
            }
        }
    }

    public static void main(String[] args) {
        HexWorld h = new HexWorld();
        h.initWorldWithNothing();
        Position start = new Position(50, 20);
        h.tessellate(4, start);
        TERenderer t = new TERenderer();
        t.initialize(h.WIDTH, h.HEIGHT);
        t.renderFrame(h.world);
    }
}
