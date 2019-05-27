import java.io.*;
import java.util.TreeMap;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

/**
 * @version 1
 * @author Evan Zhang
 * Revision history:
 *  - May 15, 2019: Created ~Evan Zhang
 *  - May 16, 2019: Updated ~Evan Zhang
 *  - May 27, 2019: Updated ~Evan Zhang
 */
public class ResourceLoader
{
    private static TreeMap<String,Image> imageCache = new TreeMap();

    public static InputStream getResource(String location) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(location);
    }

    public static String getResourceLocation(String location) {
        return ResourceLoader.class.getClassLoader().getResource(location).toExternalForm();
    }

    public static Image loadImage(String filename) {
        Image ret = imageCache.get(filename);
        if (ret == null) {
            ret = new Image(getResource("resources/images/" + filename));
            imageCache.put(filename, ret);
        }
        return ret;
    }

    public static Reader loadLevel(String filename) {
        try {
            return new InputStreamReader(getResource("resources/levels/" + filename), "UTF-8");
        } catch(UnsupportedEncodingException e) {
            System.err.println("Cannot decode level file: \"" + filename + "\".");
        }
        return null;
    }

    public static String loadCSS(String filename) {
        return getResourceLocation("resources/css/" + filename);
    }
}
