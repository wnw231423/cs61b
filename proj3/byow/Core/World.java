package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.time.Instant;
import java.util.*;

/** Including anything to do with world generation. */
public class World {
    private int seed;
    private Random random;
    public TETile[][] world;

    private int[][] worldIndex;
    private int currentIndex = 2;

    public static final int WORLD_WIDTH = 51;
    public static final int WORLD_HEIGHT = 51;
    public static final int ROOM_NUM = 10;

    private static final int bendPercentage = 25;

    public enum Direction {UP, DOWN, LEFT, RIGHT;}

    private static class Position {
        public int x;
        public int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(x + dx, y + dy);
        }

        public Position getNeighbor(Direction d, int dl) {
            switch (d) {
                case UP -> {
                    return this.shift(0, dl);
                }
                case DOWN -> {
                    return this.shift(0, -dl);
                }
                case LEFT -> {
                    return this.shift(-dl, 0);
                }
                case RIGHT -> {
                    return this.shift(dl, 0);
                }
                default -> {
                    return null;
                }
            }
        }
    }

    private static class Room {
        public static int ROOM_MIN_SIZE = 3;
        public static int ROOM_MAX_SIZE = 15;
        public Position ldp, rup;//left down point, right up point.

        public Room(Position ldp, Position rup) {
            this.ldp = ldp;
            this.rup = rup;
        }

        public Room(Position ldp, int width, int height) {
            this.ldp = ldp;
            this.rup = ldp.shift(width, height);
        }
    }

    private boolean createRoom(Room r) {
        for (int i = r.ldp.x; i <= r.rup.x; i++) {
            for (int j = r.ldp.y; j <= r.rup.y; j++) {
                if (i >= WORLD_WIDTH - 1 || j >= WORLD_HEIGHT - 1 || world[i][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }

        for (int i = r.ldp.x; i <= r.rup.x; i++) {
            for (int j = r.ldp.y; j <= r.rup.y; j++) {
                world[i][j] = Tileset.FLOOR; // Here can be other type of tile to distinguish
                // room from other places.
                worldIndex[i][j] = currentIndex;
            }
        }
        currentIndex += 1;
        return true;
    }

    private void generateMaze() {
        for (int i = 1; i < WORLD_WIDTH; i += 2) {
            for (int j = 1; j < WORLD_HEIGHT; j += 2) {
                if (worldIndex[i][j] == 1) {
                    generateMazeHelper(new Position(i, j), Direction.RIGHT);
                }
                currentIndex += 1;
            }
        }
    }

    private void generateMazeHelper(Position start, Direction last) {
        mark(start, currentIndex);
        place(start, Tileset.FLOOR);

        Direction next;
        int bend = random.nextInt(100);
        if (bend < bendPercentage) {
            next = getRandomDirection();
        } else {
            next = last;
        }

        Position nextP = start.getNeighbor(next, 2);
        if (placeable(nextP)) {
            Position temp = start.getNeighbor(next, 1);
            mark(temp, currentIndex);
            place(temp, Tileset.FLOOR);
            generateMazeHelper(nextP, next);
        }

        for (Direction d: Direction.values()) {
            Position other = start.getNeighbor(d, 2);
            if (placeable(other)) {
                Position temp = start.getNeighbor(d, 1);
                mark(temp, currentIndex);
                place(temp, Tileset.FLOOR);
                generateMazeHelper(other, d);
            }
        }
    }

    private static class ConnectorManager {
        HashMap<HashSet<Integer>, HashSet<Position>> connectors;

        public ConnectorManager() {
            this.connectors = new HashMap<>();
        }

        public void put(HashSet<Integer> s, Position p) {
            if (connectors.containsKey(s)) {
                connectors.get(s).add(p);
            } else {
                HashSet<Position> temp = new HashSet<>();
                temp.add(p);
                connectors.put(s, temp);
            }
        }
    }

    private void connect() {
        ConnectorManager connectors = new ConnectorManager();
        for (int i = 1; i < WORLD_WIDTH - 1; i++) {
            for (int j = 1; j < WORLD_HEIGHT - 1; j++) {
                Position current = new Position(i, j);
                //case 1: up and down connection:
                Position up = current.getNeighbor(Direction.UP, 1);
                Position down =current.getNeighbor(Direction.DOWN, 1);
                if (getMark(current) == 0 && getMark(up) != getMark(down)) {
                    HashSet<Integer> twoRegion = new HashSet<>();
                    twoRegion.add(getMark(up));
                    twoRegion.add(getMark(down));
                    connectors.put(twoRegion, current);
                }
            }
        }


    }

    private void place(Position p, TETile t) {
        world[p.x][p.y] = t;
    }

    private void mark(Position p, int i) {
        worldIndex[p.x][p.y] = i;
    }

    private int getMark(Position p) {
        return worldIndex[p.x][p.y];
    }

    private boolean validate(Position p) {
        return p.x >= 0 && p.x < WORLD_WIDTH && p.y >= 0 && p.y < WORLD_HEIGHT;
    }

    private boolean placeable(Position p) {
        return validate(p) && (worldIndex[p.x][p.y] == 0 || worldIndex[p.x][p.y] == 1);
    }

    private Direction getRandomDirection() {
        return Direction.values()[random.nextInt(Direction.values().length)];
    }

    private int getRandomOddInt(int bound) {
        while (true) {
            int res = random.nextInt(bound);
            if (res % 2 != 0) {
                return res;
            }
        }
    }

    private int getRandomOddInt(int min, int bound) {
        while (true) {
            int res = random.nextInt(bound - min) + min;
            if (res % 2 != 0) {
                return res;
            }
        }
    }

    private void addRooms() {
        int n = 0;
        while (n < World.ROOM_NUM) {
            int ldx = getRandomOddInt(WORLD_WIDTH);
            int ldy = getRandomOddInt(WORLD_HEIGHT);

            int rux = getRandomOddInt(ldx + Room.ROOM_MIN_SIZE - 1, ldx + Room.ROOM_MAX_SIZE - 1);
            int ruy = getRandomOddInt(ldy + Room.ROOM_MIN_SIZE - 1, ldy + Room.ROOM_MAX_SIZE - 1);

            Room r = new Room(new Position(ldx, ldy), new Position(rux, ruy));
            if (createRoom(r)) {
                n += 1;
            }
        }
    }

    private void init() {
        this.world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        this.worldIndex = new int[WORLD_WIDTH][WORLD_HEIGHT];
        for (int i = 0; i < WORLD_WIDTH; i++) {
            for (int j = 0; j < WORLD_HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
                if ((i % 2 != 0) || (j % 2 != 0)) {
                    worldIndex[i][j] = 1;
                }
            }
        }
    }

    public World(int seed) {
        this.seed = seed;
        this.random = new Random(seed);
        //TODO: Finish rest.
        init();
        addRooms();
        generateMaze();
        connect();
    }
}
