/**
 * Evan Zhang and Max Li
 * Mrs Krasteva
 * Due: June 10, 2019
 * The Level Select scene.
 */
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

/**
 * The Level Select scene.
 * <pre>
 * Revision history:
 *  - May 13, 2019: Created ~Evan Zhang
 *  - May 14, 2019: Updated ~Evan Zhang
 *  - May 16, 2019: Updated ~Evan Zhang
 *  - May 17, 2019: Updated ~Evan Zhang
 *  - May 18, 2019: Updated ~Evan Zhang
 *  - May 23, 2019: Finished ~Evan Zhang
 *  - May 27, 2019: Commented ~Evan Zhang
 *  - May 29, 2019: Updated ~Evan Zhang
 *  - May 30, 2019: Updated ~Evan Zhang
 *  - May 31, 2019: Updated ~Evan Zhang
 *  - Jun 3, 2019: Updated ~Evan Zhang
 *  - Jun 3, 2019: Commented ~Evan Zhang
 *  - Jun 7, 2019: Finished ~Evan Zhang
 * </pre>
 * @author Evan Zhang
 * @version 1
 */
public class LevelSelect extends BaseScene {
    /**
     * Constructor
     * @param  game The current game
     */
    public LevelSelect(Game game) {
        super(game);
    }

    /**
     * {@inheritDoc}
     */
    public void initScene() {
        EventHandler[] handlers = {
            event -> game.updateState(State.LEVEL_ONE),
            event -> game.updateState(State.LEVEL_TWO),
            event -> game.updateState(State.LEVEL_THREE),
        };

        VBox body = new VBox(10);
        body.setAlignment(Pos.TOP_CENTER);

        body.getChildren().add(new ImageView(ResourceLoader.loadImage("level-select-logo.png")));

        boolean prefixLevelComplete = true;

        for (int x = 0; x < Constants.NUM_LEVELS; x++) {
            StackPane button = Util.getMainButton("" + (x + 1), handlers[x]);
            if (!prefixLevelComplete) {
                button.setDisable(true);
            }
            prefixLevelComplete &= this.game.levelComplete(x);
            body.getChildren().add(button);
        }

        ImageButton backButton = Util.getMainImageButton("back", event -> game.updateState(State.MAIN_MENU));

        ImageButton helpButton = Util.getMainImageButton("help", event -> {
            game.setNextState(game.getCurrentState());
            game.updateState(State.HELP);
        });

        ImageButton deleteButton = Util.getMainImageButton("trash-can", event -> {
            for (int i = 0; i < Constants.NUM_LEVELS; i++) {
                game.currentUser.levelSaves[i] = null;
            }
            game.updateState(game.getCurrentState());
        });
        deleteButton.setTooltip(new Tooltip("Clear your game data"));
        /** Only allow game data deletion once the game is completed */
        deleteButton.setVisible(this.game.levelComplete(Constants.NUM_LEVELS - 1));

        this.game.setScene(new Scene(Util.getMainRoot(body, Util.getFooter(backButton, deleteButton, helpButton))));
    }
}
