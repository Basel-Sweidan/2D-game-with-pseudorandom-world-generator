package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import static byow.Core.DrawUtils.*;

/** A class representing a Hallway. */
public class Hallway implements Structure {
    /** A 2D Array representing the hallway. */
    private TETile[][] hallway;
    // some instance variables that keep track of where the hallway should connect to other places

    /** Constructs a hallway of LENGTH and WIDTH using WALL as the wall tile and FLOOR as the
     * floor tile. Orientation of the hallway depends on orientationCode. 'h' corresponds to
     * horizontal and 'v' corresponds to vertical. The resulting hallway will be either
     * LENGTH x WIDTH or WIDTH x LENGTH. */
    public Hallway(int length, int width, char orientationCode, TETile wall, TETile floor) {
        if (orientationCode == 'h') {
            hallway = new TETile[length][width];
            fillRow(hallway, new Position(0, 0), length, wall);
            for (int i = 0; i < width - 2; i += 1) {
                fillRow(hallway, new Position(0, i + 1), length, floor);
            }
            fillRow(hallway, new Position(0, width - 1), length, wall);
        } else if (orientationCode == 'v') {
            hallway = new TETile[width][length];
            fillColumn(hallway, new Position(0, 0), length, wall);
            for (int i = 0; i < width - 2; i += 1) {
                fillColumn(hallway, new Position(i + 1, 0), length, floor);
            }
            fillColumn(hallway, new Position(width - 1, 0), length, wall);
        }
    }

    public TETile[][] getTiles() {
        return hallway;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(4, 8);
        Hallway h = new Hallway(8, 4, 'v', Tileset.WALL, Tileset.FLOOR);
        ter.renderFrame(h.getTiles());
    }
}