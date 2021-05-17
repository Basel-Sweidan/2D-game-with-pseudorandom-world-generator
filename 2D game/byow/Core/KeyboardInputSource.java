package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

/**
 * @source from InputDemo
 */
public class KeyboardInputSource implements InputSource {
    private static final boolean PRINT_TYPED_KEYS = true;
    public static final char EMPTY_KEY = 0;

    public KeyboardInputSource() {
    }

    /** If a key has been typed, return the key. Otherwise, returns an empty key,
     * represented by EMPTY_KEY. */
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            } else {
                return EMPTY_KEY;
            }
        }
    }

    public boolean possibleNextInput() {
        return true;
    }
}