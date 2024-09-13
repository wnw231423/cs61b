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

    public static void main(String[] args) {
        HexWorld h = new HexWorld();
        h.initWorldWithNothing();
        Position start = new Position(50, 20);
        h.drawSingleHex(5, start, Tileset.WALL);
        TERenderer t = new TERenderer();
        t.initialize(h.WIDTH, h.HEIGHT);
        t.renderFrame(h.world);
    }
}
