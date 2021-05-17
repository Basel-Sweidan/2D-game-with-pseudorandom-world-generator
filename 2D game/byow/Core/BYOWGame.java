package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class BYOWGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;

    public BYOWGame(int width, int height) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    /** Draws the title screen. */
    public void drawTitleScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        int halfX = width / 2;
        int eightY = height / 8;
        StdDraw.text(halfX, 7 * eightY, "BYOW");
        StdDraw.text(halfX, 5 * eightY, "New Game (N)");
        StdDraw.text(halfX, 4 * eightY, "Load Game (L)");
        StdDraw.text(halfX, 3 * eightY, "Quit (Q)");
        StdDraw.show();
    }

    public static void main(String[] args) {
        BYOWGame game = new BYOWGame(40, 40);
        game.drawTitleScreen();
    }
}