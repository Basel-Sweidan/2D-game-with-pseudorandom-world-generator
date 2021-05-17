package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class UI {
    private static Font mainLine = new Font(Font.MONOSPACED, Font.BOLD, 40);
    private static Font normalLine = new Font(Font.DIALOG, Font.BOLD, 20);

    //method to draw the main menu
    public static void DrawMainMenu () {
        StdDraw.clear(StdDraw.WHITE);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(mainLine);
        StdDraw.text(Engine.WIDTH/2, Engine.HEIGHT/1.2, "CS61B BYOW");
        StdDraw.setFont(normalLine);
        StdDraw.text(Engine.WIDTH/2, Engine.HEIGHT /2, "Press (N) For New Game");
        StdDraw.text(Engine.WIDTH/2, Engine.HEIGHT /2.5, "Press (S) To Enter A Seed");
        StdDraw.text(Engine.WIDTH/2, Engine.HEIGHT /3, "Press (Q) To Quit");
        StdDraw.show();
    }

}
