package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class DrawUtils {
    /** Fills TILES with a row of tiles starting at P going right of length LENGTH with TILE. */
    public static void fillRow(TETile[][] tiles, Position p, int length, TETile tile) {
        int x = p.getX();
        int y = p.getY();
        int start = x;
        int end = x + length;
        for (int i = start; i < end; i += 1) {
            tiles[i][y] = tile;
        }
    }

    /** Fills TILES with a column of tiles starting at P going up of length LENGTH with TILE. */
    public static void fillColumn(TETile[][] tiles, Position p, int length, TETile tile) {
        int x = p.getX();
        int y = p.getY();
        int start = y;
        int end = y + length;
        for (int i = start; i < end; i += 1) {
            tiles[x][i] = tile;
        }
    }

    /** Fills TILES with TILE. */
    public static void fillAll(TETile[][] tiles, TETile tile) {
        int rows = tiles.length;
        int cols = tiles[0].length;
        for (int i = 0; i < rows; i += 1) {
            for (int j = 0; j < cols; j += 1) {
                tiles[i][j] = tile;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(20, 20);

        TETile[][] world = new TETile[20][20];
        fillAll(world, Tileset.NOTHING);

        fillRow(world, new Position(0, 0), 8, Tileset.WALL);
        fillRow(world, new Position(4, 2), 8, Tileset.WALL);
        fillColumn(world, new Position(4, 2), 8, Tileset.WALL);
        ter.renderFrame(world);
    }
}