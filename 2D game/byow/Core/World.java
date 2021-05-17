package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static byow.Core.DrawUtils.*;
import static byow.Core.RandomUtils.*;

public class World {
    /** Parameters for random generation. */
    private static final int ROOM_MIN_WIDTH = 6;
    private static final int ROOM_MAX_WIDTH = 14;
    private static final int ROOM_MIN_HEIGHT = 6;
    private static final int ROOM_MAX_HEIGHT = 14;
    private static final int HALLWAY_WIDTH = 3;
    private static final int ROOM_CREATION_ATTEMPTS = 20;

    private static final TETile WALL = Tileset.WALL;
    private static final TETile FLOOR = Tileset.FLOOR;
    private static final TETile NOTHING = Tileset.NOTHING;

    // this is going to break (I think) if we want to generate more than one map in a given
    // execution of the program
    private static final ArrayList<Room> ROOMS = new ArrayList<>();
    private static final HashSet<Room> CONNECTED_ROOMS = new HashSet<>();


    // Maybe try to connect ROOMS differently in generateWorld, choosing ROOMS and
    // connecting them to the closest room. Pseudocode ex:
    /*
    toConnect = list of all ROOMS
    for every room in toConnect:
        connect room to the closest room on toConnect
     */
    // not super sure but this might be guaranteed to produce a fully connected map

    /** Adds the given room whose bottom left corner is at P to TILES. If the room would go out
     * of bounds or overlap with an existing structure, does not add the room and returns false. */
    private static boolean addRoom(TETile[][] tiles, Room room, Position p) {
        TETile[][] roomTiles = room.getTiles();
        if (outOfBounds(tiles, room, p) || overlaps(tiles, room, p)) {
            return false;
        }
        copyTiles(roomTiles, tiles, p);
        room.setPosition(p);
        ROOMS.add(room);
        return true;
    }

    /** Returns true if adding ROOM to position P in TILES would result in part of the room to
     * go out of bounds, and false otherwise. */
    private static boolean outOfBounds(TETile[][] tiles, Room room, Position p) {
        TETile[][] roomTiles = room.getTiles();
        int roomWidth = roomTiles.length;
        int roomHeight = roomTiles[0].length;
        int tilesWidth = tiles.length;
        int tilesHeight = tiles[0].length;
        int x = p.getX();
        int y = p.getY();
        return x + roomWidth >= tilesWidth || y + roomHeight >= tilesHeight;
    }

    /** Returns true if adding ROOM to position P in TILES would result in part of the room
     * to overlap with an existing room or hallway. Assumes this would not be out of bounds. */
    private static boolean overlaps(TETile[][] tiles, Room room, Position p) {
        TETile[][] roomTiles = room.getTiles();
        int roomWidth = roomTiles.length;
        int roomHeight = roomTiles[0].length;
        int x = p.getX();
        int y = p.getY();
        for (int i = x; i < x + roomWidth; i += 1) {
            for (int j = y; j < y + roomHeight; j += 1) {
                if (!(tiles[i][j] == NOTHING)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Copies all tiles from SOURCE to TARGET, placing SOURCE's bottom left corner at P
     * in TARGET. */
    private static void copyTiles(TETile[][] source, TETile[][] target, Position p) {
        int x = p.getX();
        int y = p.getY();
        int width = source.length;
        int height = source[0].length;
        for (int i = 0; i < width; i += 1) {
            System.arraycopy(source[i], 0, target[x + i], y, height);
        }
    }

    /** Adds the given hallway whose bottom left corner is at P to TILES. Does not overwrite
     * tiles that are already floors, but overwrites walls. */
    private static void addHallway(TETile[][] tiles, Hallway hallway, Position p) {
        TETile[][] hallwayTiles = hallway.getTiles();
        int hallwayWidth = hallwayTiles.length;
        if (hallwayWidth == 0) {
            return;
        }
        int hallwayHeight = hallwayTiles[0].length;
        int x = p.getX();
        int y = p.getY();
        for (int i = 0; i < hallwayWidth; i += 1) {
            for (int j = 0; j < hallwayHeight; j += 1) {
                if (tiles[x + i][y + j] != FLOOR) {
                    tiles[x + i][y + j] = hallwayTiles[i][j];
                }
            }
        }
    }

    /** Connects FIRST to SECOND in TILES using a horizontal and vertical hallway. */
    private static void connectROOMS(TETile[][] tiles, Room first, Room second) {
        Position firstCenter = first.getAbsoluteCenter();
        Position secondCenter = second.getAbsoluteCenter();
        int firstX = firstCenter.getX();
        int firstY = firstCenter.getY();
        int secondX = secondCenter.getX();
        int secondY = secondCenter.getY();
        int horizontalHallwayLength = Math.abs(secondX - firstX);
        int verticalHallwayLength = Math.abs(secondY - firstY);
        Hallway horizontalHallway = new Hallway(horizontalHallwayLength, HALLWAY_WIDTH,
                'h', WALL, FLOOR);
        Hallway verticalHallway = new Hallway(verticalHallwayLength, HALLWAY_WIDTH,
                'v', WALL, FLOOR);

        // 2 configurations: one (room) is to the left and lower, or one is to the left and higher
        Position leftCenter, rightCenter;
        if (firstX < secondX) {
            leftCenter = firstCenter;
            rightCenter = secondCenter;
        } else {
            leftCenter = secondCenter;
            rightCenter = firstCenter;
        }
        int leftX = leftCenter.getX();
        int leftY = leftCenter.getY();
        int rightX = rightCenter.getX();
        int rightY = rightCenter.getY();

        Position horizontalHallwayPosition, verticalHallwayPosition;
        if (leftY < rightY) {
            // horizontal first, might want to make this random whether we go h -> v or v -> h
            horizontalHallwayPosition = leftCenter;
            verticalHallwayPosition = new Position(leftX + horizontalHallwayLength - 2, leftY + 1);
        } else {
            // also horizontal first
            verticalHallwayPosition = new Position(leftX, leftY - verticalHallwayLength);
            horizontalHallwayPosition = new Position(leftX + 1, leftY - verticalHallwayLength - 1);
        }

        addHallway(tiles, horizontalHallway, horizontalHallwayPosition);
        addHallway(tiles, verticalHallway, verticalHallwayPosition);
        CONNECTED_ROOMS.add(first);
        CONNECTED_ROOMS.add(second);
    }

    /** Returns a randomly generated room. */
    private static Room generateRandomRoom(Random random) {
        int width = uniform(random, ROOM_MIN_WIDTH, ROOM_MAX_WIDTH);
        int height = uniform(random, ROOM_MIN_HEIGHT, ROOM_MAX_HEIGHT);
        return new Room(width, height, WALL, FLOOR);
    }

    /** Returns a random (valid) position in TILES. */
    private static Position generateRandomPosition(Random random, TETile[][] tiles) {
        int width = tiles.length;
        int height = tiles[0].length;
        int x = uniform(random, 0, width);
        int y = uniform(random, 0, height);
        return new Position(x, y);
    }

    /** Generates a random world of WIDTH x HEIGHT using SEED as a random seed. */
    public static TETile[][] generateWorld(int width, int height, long seed) {
        Random random = new Random(seed);
        TETile[][] world = new TETile[width][height];
        fillAll(world, NOTHING);
        for (int i = 0; i < ROOM_CREATION_ATTEMPTS; i += 1) {
            addRoom(world, generateRandomRoom(random), generateRandomPosition(random, world));
        }
        for (int i = 0; i < ROOMS.size() - 1; i += 1) {
            connectROOMS(world, ROOMS.get(i), ROOMS.get(i + 1));
        }
        return world;
    }

    /** Returns the center of a room to be the avatar's starting position. */
    public static Position getAvatarStartingPosition() {
        if (ROOMS.size() > 0) {
            return ROOMS.get(0).getAbsoluteCenter();
        } else {
            return null;
        }
    }

    /** Returns the center of a room to be the avatar's starting position. */
    public static Position getSecondAvatarStartingPosition() {
        if (ROOMS.size() > 0) {
            return ROOMS.get(1).getAbsoluteCenter();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(80, 40);
        TETile[][] world = generateWorld(80, 40, 234765);
        ter.renderFrame(world);
    }
}