package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import static byow.Core.PersistenceUtils.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final Font FONT = new Font("Monaco", Font.BOLD, 30);
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File SAVE = join(CWD, "savefile.txt");

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource inputSource = new KeyboardInputSource();
        initializeDisplay();
        drawTitleScreen();
        interactWithInputSource(inputSource);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        InputSource inputSource = new StringInputSource(input);
        Game game = interactWithInputSource(inputSource);
        return game.getWorld();
    }

    /** Carries out interactions with inputSource, returning the state of the game at the end. */
    public Game interactWithInputSource(InputSource inputSource) {
        Game game = mainMenuLoop(inputSource);
        gameLoop(inputSource, game);
        return game;
    }

    /** Starts the game loop, loading GAME and taking input through inputSource. Terminates when
     * the program is closed or when ':Q' is entered. In the case of ':Q', saves a record of the
     * current state of GAME to a .txt file. */
    public Game gameLoop(InputSource inputSource, Game game) {
        if (inputSource instanceof KeyboardInputSource) {
            renderWorld(game.getWorld());
        }
        boolean aboutToQuitSave = false;
        boolean worldChanged = false;
        Position previousMousePosition = new Position(0, 0);
        Position currentMousePosition = new Position(0, 0);
        while (inputSource.possibleNextInput()) {
            char key = inputSource.getNextKey();
            switch (key) {
                case KeyboardInputSource.EMPTY_KEY:
                    worldChanged = false;
                    break;
                case 'w', 'W':
                    game.moveAvatar('n');
                    worldChanged = true;
                    break;
                case 'a', 'A':
                    game.moveAvatar('w');
                    worldChanged = true;
                    break;
                case 's', 'S':
                    game.moveAvatar('s');
                    worldChanged = true;
                    break;
                case 'd', 'D':
                    game.moveAvatar('e');
                    worldChanged = true;
                    break;
                case 'u', 'U':
                    game.moveSecondAvatar('n');
                    worldChanged = true;
                    break;
                case 'h', 'H':
                    game.moveSecondAvatar('w');
                    worldChanged = true;
                    break;
                case 'j', 'J':
                    game.moveSecondAvatar('s');
                    worldChanged = true;
                    break;
                case 'k', 'K':
                    game.moveSecondAvatar('e');
                    worldChanged = true;
                    break;
                case ':':
                    aboutToQuitSave = true;
                    worldChanged = false;
                    break;
                case 'q', 'Q':
                    if (aboutToQuitSave) {
                        String gameString = game.getGameString();
                        saveGame(gameString);
                        System.exit(0);
                    }
                    break;
                case 'l', 'L':
                    game.toggleRestrictedSight();
                    worldChanged = true;
                    break;
                default:
                    aboutToQuitSave = false;
                    worldChanged = false;
                    break;
            }
            if (inputSource instanceof KeyboardInputSource) {
                currentMousePosition = getMousePosition();
                if (worldChanged || !currentMousePosition.equals(previousMousePosition)) {
                    displayGame(game, currentMousePosition);
                }
                previousMousePosition = currentMousePosition;
            }
            if (worldChanged) {
                game.updateGameString(key);
            }
        }
        return game;
    }

    /** Starts title screen loop, taking input through inputSource. Terminates when 'N###S' is input,
     * L is pressed, or Q is pressed. In the case of N and L, returns the Game to be loaded on termination.
     * Terminates the program when Q is pressed. */
    public Game mainMenuLoop(InputSource inputSource) {
        while (inputSource.possibleNextInput()) {
            char key = inputSource.getNextKey();
            switch (key) {
                case 'n', 'N':
                    long seed = getSeedLoop(inputSource);
                    return new Game(WIDTH, HEIGHT, seed);
                case 'l', 'L':
                    if (!SAVE.exists()) {
                        System.exit(0);
                    }
                    String gameString = readContentsAsString(SAVE);
                    InputSource gameStringInputSource = new StringInputSource(gameString);
                    return interactWithInputSource(gameStringInputSource);
                case 'q', 'Q':
                    System.exit(0);
                    break;
            }
        }
        return null;
    }

    /** Initializes the get seed loop, prompting the user to enter the seed and displaying the
     * seed so far on the screen. */
    private long getSeedLoop(InputSource inputSource) {
        boolean isKeyboardInputSource = inputSource instanceof KeyboardInputSource;
        int halfX = WIDTH / 2;
        int eightY = HEIGHT / 8;
        if (isKeyboardInputSource) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(halfX, 6 * eightY, "Enter seed and press S when done.");
            StdDraw.show();
        }
        StringBuilder result = new StringBuilder();
        while (inputSource.possibleNextInput()) {
            char key = inputSource.getNextKey();
            if (key == 's' || key == 'S') {
                break;
            }
            if (key != KeyboardInputSource.EMPTY_KEY) {
                result.append(key);
                if (isKeyboardInputSource) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.text(halfX, 6 * eightY, "Enter seed and press S when done.");
                    StdDraw.text(halfX, 4 * eightY, result.toString());
                    StdDraw.show();
                }
            }
        }
        return Long.parseLong(result.toString());

    }

    /** Initializes the display for StdDraw. */
    private void initializeDisplay() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setFont(FONT);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    /** Draws the title screen. */
    private void drawTitleScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        int halfX = WIDTH / 2;
        int eightY = HEIGHT / 8;
        StdDraw.text(halfX, 7 * eightY, "BYOW");
        StdDraw.text(halfX, 5 * eightY, "New Game (N)");
        StdDraw.text(halfX, 4 * eightY, "Load Game (L)");
        StdDraw.text(halfX, 3 * eightY, "Quit (Q)");
        StdDraw.show();
    }

    /** Displays the game from bottom up (renders world, then HUD). */
    public void displayGame(Game game, Position mousePosition) {
        ter.renderFrame(game.getWorld());
        displayHUD(game, mousePosition);
    }

    /** Displays the heads-up display. */
    private void displayHUD(Game game, Position mousePosition) {
        displayTileInfo(game, mousePosition);
    }

    /** Displays the info of the tile at position P. */
    private void displayTileInfo(Game game, Position p) {
        TETile[][] world = game.getWorld();
        int x = p.getX();
        int y = p.getY();
        if (x >= world.length || y >= world[0].length) {
            return;
        }
        StdDraw.setPenColor(Color.WHITE);
        TETile tile = world[p.getX()][p.getY()];
        StdDraw.text(6, HEIGHT - 1, tile.description());
        StdDraw.show();
    }

    /** Return's the current position of the mouse as a Position. */
    private Position getMousePosition() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        return new Position(x, y);
    }

    /** Saves gameString to a .txt file. */
    private void saveGame(String gameString) {
        try {
            SAVE.createNewFile();
        } catch (IOException ignored) {
        }
        writeContents(SAVE, gameString);
    }

    /** Initializes the renderer and renders the given world. */
    public void renderWorld(TETile[][] world) {
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
    }
}