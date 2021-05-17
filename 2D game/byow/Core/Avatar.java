package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Avatar {
    private TETile avatarTile;
    private Position position;

    /** Initializes the avatar, placing the avatar at position P in WORLD. */
    public Avatar(TETile[][] world, Position p) {
        avatarTile = Tileset.AVATAR;
        position = p;
        world[p.getX()][p.getY()] = avatarTile;
    }

    public Position getPosition() {
        return position;
    }
    /** Moves the avatar in the direction corresponding to directionCode in WORLD.
     * 'n' for north, 'e' for east, 's' for south, and 'w' for west. */
    public void move(TETile[][] world, char directionCode) {
        int x = position.getX();
        int y = position.getY();
        int targetX, targetY;
        switch (directionCode) {
            case 'n':
                targetX = x;
                targetY = y + 1;
                break;
            case 'e':
                targetX = x + 1;
                targetY = y;
                break;
            case 's':
                targetX = x;
                targetY = y - 1;
                break;
            case 'w':
                targetX = x - 1;
                targetY = y;
                break;
            default:
                targetX = x;
                targetY = y;
                break;
        }
        // maybe change from Tileset.FLOOR to a static variable so we can modify the floor
        // tile later
        if (world[targetX][targetY] == Tileset.FLOOR) {
            world[targetX][targetY] = avatarTile;
            world[x][y] = Tileset.FLOOR;
            position = new Position(targetX, targetY);
        }
    }
}