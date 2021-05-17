package byow.Core;

import byow.TileEngine.TETile;

/** A class representing a structure whose tiles can be accessed. */
public interface Structure {
    TETile[][] getTiles();
}