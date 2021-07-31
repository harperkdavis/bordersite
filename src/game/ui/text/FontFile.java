package game.ui.text;

import engine.io.Window;
import game.ui.UserInterface;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides functionality for getting the values from a font file.
 *
 * @author Karl
 *
 */
public class FontFile {

    private static final int PAD_TOP = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_BOTTOM = 2;
    private static final int PAD_RIGHT = 3;

    private static final int DESIRED_PADDING = 3;

    private static final String SPLITTER = " ";
    private static final String NUMBER_SEPARATOR = ",";

    private float lineHeight;

    private float aspectRatio;

    private float verticalPerPixelSize;
    private float horizontalPerPixelSize;
    private float spaceWidth;
    private int[] padding;
    private int paddingWidth;
    private int paddingHeight;

    private Map<Integer, FontCharacter> metaData = new HashMap<Integer, FontCharacter>();

    private BufferedReader reader;
    private Map<String, String> values = new HashMap<String, String>();

    /**
     * Opens a font file in preparation for reading.
     *
     * @param resourcesPath
     *            - the path to the .fnt file in the resources folder
     */
    protected FontFile(String resourcesPath) {
        this.aspectRatio = (float) Window.getWidth() / (float) Window.getHeight();
        openFile("/fonts/" + resourcesPath);
        loadPaddingData();
        loadLineSizes();
        int imageWidth = getValueOfVariable("scaleW");
        loadFontCharacterData(imageWidth);
        close();
    }

    protected float getSpaceWidth() {
        return spaceWidth;
    }

    protected FontCharacter getFontCharacter(int ascii) {
        return metaData.get(ascii);
    }

    /**
     * Read in the next line and store the variable values.
     *
     * @return {@code true} if the end of the file hasn't been reached.
     */
    private boolean processNextLine() {
        values.clear();
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e1) {
        }
        if (line == null) {
            return false;
        }
        for (String part : line.split(SPLITTER)) {
            String[] valuePairs = part.split("=");
            if (valuePairs.length == 2) {
                values.put(valuePairs[0], valuePairs[1]);
            }
        }
        return true;
    }

    /**
     * Gets the {@code int} value of the variable with a certain name on the
     * current line.
     *
     * @param variable
     *            - the name of the variable.
     * @return The value of the variable.
     */
    private int getValueOfVariable(String variable) {
        return Integer.parseInt(values.get(variable));
    }

    /**
     * Gets the array of ints associated with a variable on the current line.
     *
     * @param variable
     *            - the name of the variable.
     * @return The int array of values associated with the variable.
     */
    private int[] getValuesOfVariable(String variable) {
        String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
        int[] actualValues = new int[numbers.length];
        for (int i = 0; i < actualValues.length; i++) {
            actualValues[i] = Integer.parseInt(numbers[i]);
        }
        return actualValues;
    }

    /**
     * Closes the font file after finishing reading.
     */
    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the font file, ready for reading.
     *
     * @param file
     *            - the font file.
     */
    private void openFile(String file) {
        try {
            reader = new BufferedReader(new InputStreamReader(
                    this.getClass().getResourceAsStream(file)));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't read font meta file!");
        }
    }

    /**
     * Loads the data about how much padding is used around each character in
     * the texture atlas.
     */
    private void loadPaddingData() {
        processNextLine();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
        this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
    }

    /**
     * Loads information about the line height for this font in pixels, and uses
     * this as a way to find the conversion rate between pixels in the texture
     * atlas and screen-space.
     */
    private void loadLineSizes() {
        processNextLine();
        lineHeight = getValueOfVariable("lineHeight") * UserInterface.PIXEL;
        verticalPerPixelSize = UserInterface.PIXEL;
        horizontalPerPixelSize = verticalPerPixelSize;
    }

    /**
     * Loads in data about each character and stores the data in the
     * {@link FontCharacter} class.
     *
     * @param imageWidth
     *            - the width of the texture atlas in pixels.
     */
    private void loadFontCharacterData(int imageWidth) {
        processNextLine();
        processNextLine();
        while (processNextLine()) {
            FontCharacter c = loadFontCharacter(imageWidth);
            if (c != null) {
                metaData.put(c.getId(), c);
            }
        }
    }

    /**
     * Loads all the data about one character in the texture atlas and converts
     * it all from 'pixels' to 'screen-space' before storing. The effects of
     * padding are also removed from the data.
     *
     * @param imageSize
     *            - the size of the texture atlas in pixels.
     * @return The data about the character.
     */
    private FontCharacter loadFontCharacter(int imageSize) {
        int id = getValueOfVariable("id");
        if (id == 32) {
            this.spaceWidth = getValueOfVariable("xadvance") * horizontalPerPixelSize;
            return null;
        }
        float xTex = ((float) getValueOfVariable("x") - 1.0f) / imageSize;
        float yTex = ((float) getValueOfVariable("y") - 1.0f) / imageSize;
        int width = getValueOfVariable("width") + 1;
        int height = getValueOfVariable("height") + 1;
        float quadWidth = width * horizontalPerPixelSize;
        float quadHeight = height * verticalPerPixelSize;
        float xTexSize = (float) width / imageSize;
        float yTexSize = (float) height / imageSize;
        float xOff = getValueOfVariable("xoffset") * horizontalPerPixelSize;
        float yOff = getValueOfVariable("yoffset") * verticalPerPixelSize;
        float xAdvance = getValueOfVariable("xadvance") * horizontalPerPixelSize;
        return new FontCharacter(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
    }

    public float getLineHeight() {
        return lineHeight;
    }
}