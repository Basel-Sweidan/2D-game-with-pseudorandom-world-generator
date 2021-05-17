package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import static byow.Core.DrawUtils.*;

public class Game {
    /** The distance from the center of the square of sight around the avatar. */
    private static final int SIGHT_RANGE = 5;
    /** The base world of the game. Does not depend on field of view of avatar. */
    private TETile[][] world;
    private boolean restrictedSight;
    private Avatar avatar;
    private Avatar secondAvatar;
    private StringBuilder gameStringBuilder;

    /** Constructs a new game with a new world of WIDTH x HEIGHT with SEED. */
    public Game(int width, int height, long seed) {
        world = World.generateWorld(width, height, seed);
        restrictedSight = false;
        avatar = new Avatar(world, World.getAvatarStartingPosition());
        secondAvatar = new Avatar(world, World.getSecondAvatarStartingPosition());
        gameStringBuilder = new StringBuilder("N" + seed + "S");
    }

    /** Returns the world as it should be displaced on screen. */
    public TETile[][] getWorld() {
        if (restrictedSight) {
            return restrictedSightWorld();
        } else {
            return world;
        }
    }

    public String getGameString() {
        return gameStringBuilder.toString();
    }

    /** Appends C onto the gameString. */
    public void updateGameString(char c) {
        gameStringBuilder.append(c);
    }

    /** Moves the avatar in the direction of directionCode. 'n' is north, 'e' is east,
     * 's' is south, and 'w' is west. */
    public void moveAvatar(char directionCode) {
        avatar.move(world, directionCode);
    }

    public void moveSecondAvatar(char directionCode) {
        secondAvatar.move(world, directionCode);
    }

    public void toggleRestrictedSight() {
        restrictedSight = !restrictedSight;
    }

    /** Returns a copy of WORLD but with tiles not within the line of sight of the avatar
     * set to NOTHING. */
    private TETile[][] restrictedSightWorld() {
        int width = world.length;
        int height = world[0].length;
        TETile[][] copy = new TETile[width][height];
        fillAll(copy, Tileset.NOTHING);
        copyLineOfSight(copy, avatar.getPosition());
        copyLineOfSight(copy, secondAvatar.getPosition());
        return copy;
    }

    /** Copies the tiles from WORLD in the line of sight of the avatar with avatarPosition to
     * COPY. */
    private void copyLineOfSight(TETile[][] copy, Position avatarPosition) {
        int width = world.length;
        int height = world[0].length;
        int avatarX = avatarPosition.getX();
        int avatarY = avatarPosition.getY();
        for (int i = avatarX - SIGHT_RANGE; i <= avatarX + SIGHT_RANGE; i += 1) {
            if (i >= 0 && i < width) {
                for (int j = avatarY - SIGHT_RANGE; j <= avatarY + SIGHT_RANGE; j += 1) {
                    if (j >= 0 && j < height) {
                        copy[i][j] = world[i][j];
                    }
                }
            }
        }
    }
}