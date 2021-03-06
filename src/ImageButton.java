/**
 * Evan Zhang and Max Li
 * Mrs Krasteva
 * Due: June 10, 2019
 * The ImageButton class for buttons with images instead of text.
 */
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;

/**
 * The ImageButton class for buttons with images instead of text.
 * <pre>
 * Revision history:
 *  - May 16, 2019: Created ~Evan Zhang
 *  - May 27, 2019: Commented ~Max Li
 *  - Jun 3, 2019: Updated ~Evan Zhang
 *  - Jun 3, 2019: Commented ~Evan Zhang
 *  - Jun 5, 2019: Updated ~Evan Zhang
 *  - Jun 6, 2019: Commented ~Evan Zhang
 *  - Jun 7, 2019: Finished ~Evan Zhang
 * </pre>
 * @author Evan Zhang
 * @version 1
 */
public class ImageButton extends Button {
    /** The ImageView overlayed on the button */
    private ImageView image;

    /**
     * Constructor for the ImageButton class.
     */
    public ImageButton() {
        image = new ImageView();
        this.getChildren().add(image);
        this.setPadding(new Insets(0));
        setFocusTraversable(false);
    }

    /**
     * Sets button component with normal, hover, and selected images.
     * @param normal   The image on the button when it is not being hovered over, or selected.
     * @param hover    The image on the button when it is being hovered over by the mouse.
     * @param selected The image on the button when it is clicked.
     */
    public void setImages(Image normal, Image hover, Image selected) {
        image.setImage(normal);
        image.setSmooth(true);

        this.setOnMousePressed(event -> image.setImage(selected));
        this.setOnMouseReleased(event -> image.setImage(normal));
        this.setOnMouseEntered(event -> image.setImage(hover));
        this.setOnMouseExited(event -> image.setImage(normal));

        super.getStylesheets().add(ResourceLoader.loadCSS("transparent-button.css"));
        super.setGraphic(image);
    }

    /**
     * Sets button component with normal and hover images.
     * @param normal The image on the button when it is not being hovered over, or selected.
     * @param hover  The image on the button when it is being hovered over by the mouse.
     */
    public void setImages(Image normal, Image hover) {
        setImages(normal, hover, hover);
    }

    /**
     * Sets button component with a normal images.
     * @param normal The image on the button when it is not being hovered over, or selected.
     */
    public void setImages(Image normal) {
        setImages(normal, normal, normal);
    }

    /**
     * Sets the width of the button.
     * @param width The width of the button.
     */
    public void setFitWidth(double width) {
        super.setMinWidth(width);
        image.setFitWidth(width);
    }

    /**
     * Sets the height of the button.
     * @param height The height of the button.
     */
    public void setFitHeight(double height) {
        super.setMinHeight(height);
        image.setFitHeight(height);
    }
}
