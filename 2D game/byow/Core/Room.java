package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import static byow.Core.DrawUtils.*;

/** A class representing a Room. */
public class Room implements Structure {
    /** A 2D Array representing the room. */
    private final TETile[][] room;
    /** The position of the particular room in the world. Null by default. */
    private Position position;
    /** The center of the room. (For rooms of even width/height, is one of the centers) */
    private Position center;

    /** Constructs a room of WIDTH x HEIGHT using WALL as the wall tile and FLOOR as the
     * floor tile. */
    public Room(int width, int height, TETile wall, TETile floor) {
        room = new TETile[width][height];
        position = null;
        center = new Position(width / 2, height / 2);

        for (int i = 0; i < height; i += 1) {
            fillRow(room, new Position(0, i), width, floor);
        }

        fillRow(room, new Position(0, 0), width, wall);
        fillRow(room, new Position(0, height - 1), width, wall);
        fillColumn(room, new Position(0, 0), height, wall);
        fillColumn(room, new Position(width - 1, 0), height, wall);
    }

    public TETile[][] getTiles() {
        return room;
    }

    public void setPosition(Position p) {
        position = p;
    }

    /** Returns the center relative to the bottom left corner of the world. */
    public Position getAbsoluteCenter() {
        if (position == null) {
            return null;
        } else {
            return new Position(position.getX() + center.getX(), position.getY() + center.getY());
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(10, 10);
        Room r = new Room(10, 10, Tileset.WALL, Tileset.FLOOR);
        ter.renderFrame(r.getTiles());
    }
}