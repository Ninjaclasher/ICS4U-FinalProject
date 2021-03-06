/**
 * Evan Zhang and Max Li
 * Mrs Krasteva
 * Due: June 10, 2019
 * Level one of the game.
 */
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;

/**
 * Level one of the game.
 * <pre>
 * Revision history:
 *  - May 13, 2019: Created ~Evan Zhang
 *  - May 17, 2019: Updated ~Evan Zhang
 *  - May 21, 2019: Updated ~Evan Zhang
 *  - May 22, 2019: Updated ~Evan Zhang
 *  - May 27, 2019: Commented ~Evan Zhang
 *  - May 29, 2019: Updated ~Evan Zhang
 *  - May 30, 2019: Updated ~Evan Zhang
 *  - May 31, 2019: Updated ~Evan Zhang
 *  - Jun 1, 2019: Updated ~Evan Zhang
 *  - Jun 1, 2019: Commented ~Evan Zhang
 *  - Jun 2, 2019: Updated ~Evan Zhang
 *  - Jun 3, 2019: Updated ~Evan Zhang
 *  - Jun 3, 2019: Commented ~Evan Zhang
 *  - Jun 5, 2019: Updated ~Evan Zhang
 *  - Jun 6, 2019: Commented ~Evan Zhang
 *  - Jun 7, 2019: Finished ~Evan Zhang
 * </pre>
 * @author Evan Zhang
 * @version 1
 */
public class LevelOne extends BaseLevel {
    /** A local class to simulate a command */
    private class Command {
        /** The command */
        private String command;
        /** The arguments of the command */
        private String[] arguments;
        /** The current position of the argument pointer for nextArgument() */
        private int argumentPointer = 0;

        /**
         * Constructor
         * @param  line The line to parse
         */
        public Command(String line) {
            String[] tokens = line.trim().split(":");
            command = tokens[0].trim();
            arguments = new String[tokens.length - 1];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = tokens[i + 1].trim();
            }
        }

        /**
         * Copy Constructor
         * @param  other The other Command to clone
         */
        public Command(Command other) {
            this.command = other.command;
            this.arguments = new String[other.arguments.length];
            for (int i = 0; i < this.arguments.length; i++) {
                this.arguments[i] = other.arguments[i];
            }
        }

        /**
         * Gets the command
         * @return The command
         */
        public String getCommand() {
            return command;
        }

        /**
         * Gets the arguments
         * @return The arguments
         */
        public String[] getArguments() {
            return arguments;
        }

        /**
         * Gets the next argument and increments a pointer
         * @return The next argument
         */
        public String nextArgument() {
            return arguments[argumentPointer++];
        }

        /**
         * Returns whether the specified line is a comment
         * @return Whether the specified line is a comment
         */
        public boolean isComment() {
            return command.startsWith("#");
        }
    }

    /** The width of the text overlay */
    public static final int TEXT_OVERLAY_WIDTH = Constants.SCREEN_WIDTH - 150;
    /** The height of the text overlay */
    public static final int TEXT_OVERLAY_HEIGHT = 200;
    /** The padding of the text overlay */
    public static final int TEXT_PADDING = 50;

    /** The transition overlay object */
    private Rectangle transitionOverlay = new Rectangle(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Color.BLACK);
    /** The background object */
    private ImageView background;
    /** The text overlay object */
    private BorderPane textOverlay;
    /** The position of the dialog command */
    private int dialogPosition = 0;
    /** The array of all the dialog commands */
    private Command[] dialogCommands;
    /** The map of dialog command labels to dialog positions */
    private Map<String, Integer> dialogCommandsLabelMap = new TreeMap<String, Integer>();

    /**
     * Constructor
     * @param  game The current game
     */
    public LevelOne(Game game) {
        super(game);
        root = new Group();
        List<String> lines = Util.readLines(ResourceLoader.loadLevel(getLevelFile()));
        dialogCommands = new Command[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            dialogCommands[i] = new Command(lines.get(i));
            if (dialogCommands[i].getCommand().equals("LABEL")) {
                dialogCommandsLabelMap.put(dialogCommands[i].getArguments()[0], i);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected int getLevel() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    protected int getScoreCount() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    public void initScene() {
        background = new ImageView();
        background.setFitWidth(Constants.SCREEN_WIDTH);
        background.setFitHeight(Constants.SCREEN_HEIGHT);
        root.getChildren().add(background);

        textOverlay = new BorderPane();
        textOverlay.setTranslateX((Constants.SCREEN_WIDTH - TEXT_OVERLAY_WIDTH) / 2);
        textOverlay.setMinWidth(TEXT_OVERLAY_WIDTH);
        textOverlay.setTranslateY(Constants.SCREEN_HEIGHT - TEXT_OVERLAY_HEIGHT);
        textOverlay.setMinHeight(TEXT_OVERLAY_HEIGHT);
        textOverlay.setBackground(new Background(
            new BackgroundFill(Color.web("#ff9900"), new CornerRadii(20, 20, 0, 0, false), Insets.EMPTY),
            new BackgroundFill(Color.web("#ffd640"), new CornerRadii(15, 15, 0, 0, false), new Insets(25, 25, 0, 25))
        ));
        textOverlay.setPadding(new Insets(TEXT_PADDING));

        root.getChildren().add(textOverlay);

        root.getChildren().add(transitionOverlay);
        transitionOverlay.setOpacity(0);
        setScene(root);
        start();
    }

    /**
     * {@inheritDoc}
     */
    protected void update() {
        /** No game loop */
    }

    /**
     * {@inheritDoc}
     */
    protected void handleFinish() {
        fadeOut(e -> {
            StackPane nextLevel = initBasicOverlay(Util.getMainButton("Proceed to the next level",
                                                                      event -> this.game.updateState(State.LEVEL_TWO),
                                                                      15));
            setOverlay(nextLevel);
            nextLevel.setOpacity(0);
            Util.fade(nextLevel, 1, 0, 1);
        });
    }

    /**
     * {@inheritDoc}
     */
    protected void onFirstEnter() {
        super.onFirstEnter();
        nextDialog(true);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean overlayVisible() {
        return super.overlayVisible() || transitionOverlay.getOpacity() > 0;
    }

    /**
     * {@inheritDoc}
     */
    protected void handleKeyPressed(KeyCode key) {
        super.handleKeyPressed(key);
        switch (key) {
            case ENTER:
                nextDialog();
                break;
            default: break;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void onTutorialComplete() {
        super.onTutorialComplete();
        root.setOnMouseClicked(e -> nextDialog());
    }

    /**
     * Formats the specified text
     * @param  text The specified text
     * @return      The formatted version of the specified text
     */
    private Text getFormattedText(String text) {
        Text ret = new Text(text);
        ret.setFont(Util.getDefaultFont(15));
        ret.setFill(Color.RED);
        ret.setWrappingWidth(TEXT_OVERLAY_WIDTH - TEXT_PADDING * 2);
        return ret;
    }

    /**
     * Sets the text overlay to the specified text
     * @param text The specified text
     */
    private void setTextOverlay(Text text) {
        textOverlay.setTop(text);
        Text footer = getFormattedText("Press enter or left click to continue...\nPress escape to pause...");
        footer.setTextAlignment(TextAlignment.RIGHT);
        footer.setStyle("-fx-font-style: italic");
        textOverlay.setBottom(footer);
    }

    /**
     * Handles the next dialog command
     * @param  position             The index of the dialog command
     * @param  onlyMutatingCommands Whether to run only mutating commands or all commands (e.g transition commands)
     * @return                      Whether to continue onto the next command or pause
     */
    private boolean handleDialog(int position, boolean onlyMutatingCommands) {
        Command command = new Command(dialogCommands[position]);
        if (command.isComment()) {
            return true;
        }
        switch (command.getCommand()) {
            case "PAUSE":
                return false;
            case "TEXT":
                setTextOverlay(getFormattedText(command.nextArgument()));
                break;
            case "NARRATION":
                Text text = getFormattedText(command.nextArgument());
                text.setStyle("-fx-font-style: italic");
                setTextOverlay(text);
                break;
            case "BACKGROUND":
                switch (command.nextArgument()) {
                    case "IMAGE":
                        background.setImage(ResourceLoader.loadImage(command.nextArgument()));
                        break;
                    case "COLOR":
                        WritableImage colorImage = new WritableImage(1, 1);
                        colorImage.getPixelWriter().setColor(0, 0, Color.web(command.nextArgument()));
                        background.setImage(colorImage);
                        break;
                    default: break;
                }
                break;
            case "CLEAR":
                scores[0] = 0;
                break;
            case "FADEOUT":
                if (!onlyMutatingCommands) {
                    double delay = 0.5;
                    try {
                        delay = Double.parseDouble(command.nextArgument());
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: failed to parse FADEOUT delay");
                    } catch (Exception e) {
                        /** Catch all */
                    }
                    fadeOut(event -> nextDialog(true), delay);
                    return false;
                }
                break;
            case "FADEIN":
                if (!onlyMutatingCommands) {
                    double delay = 0.5;
                    try {
                        delay = Double.parseDouble(command.nextArgument());
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: failed to parse FADEOUT delay");
                    } catch (Exception e) {
                        /** Catch all */
                    }
                    fadeIn(event -> nextDialog(true), delay);
                    return false;
                }
                break;
            case "GOTO":
            case "JUMP":
                jumpToLabel(command.nextArgument());
                break;
            case "CHOICE":
                /**
                 * CHOICE: [question] : [number of choices] : [label1] : [label2] : [labeln] : [choice1] : [choice2] : [choicen] : [score1] : [score2] : [scoren]
                 */
                if (!onlyMutatingCommands) {
                    String question = command.nextArgument();
                    int numChoices = Integer.parseInt(command.nextArgument());

                    String[] choices = new String[numChoices];
                    String[] labelJumps = new String[numChoices];
                    EventHandler[] handlers = new EventHandler[numChoices];
                    for (int i = 0; i < numChoices; i++) {
                        choices[i] = command.nextArgument();
                    }
                    for (int i = 0; i < numChoices; i++) {
                        labelJumps[i] = command.nextArgument();
                    }
                    for (int i = 0; i < numChoices; i++) {
                        Integer delta = Integer.parseInt(command.nextArgument());
                        String label = labelJumps[i];
                        handlers[i] = (event -> {
                            incrementScore(0, (int)delta);
                            removeOverlay();
                            jumpToLabel(label);
                            nextDialog(true);
                        });
                    }
                    Question curQuestion = new Question(question, choices, handlers);
                    setOverlay(initBasicOverlay(curQuestion.getFormattedQuestion(), curQuestion.getFormattedChoices()));
                    return false;
                }
                break;
            default: break;
        }
        return true;
    }

    /**
     * Handles the next dialog command
     * @param  position The index of the dialog command
     * @return          
     */
    private boolean handleDialog(int position) {
        return handleDialog(position, false);
    }

    /**
     * Jump to a label in the dialog commands
     * @param  label The label name
     * @return       Whether the jump was sucessful (The label exists)
     */
    private boolean jumpToLabel(String label) {
        if (dialogCommandsLabelMap.containsKey(label)) {
            this.dialogPosition = dialogCommandsLabelMap.get(label);
            return true;
        }
        return false;
    }

    /**
     * Runs the next dialog group
     * @param force Whether to run the dialog even if an overlay is visible
     */
    private void nextDialog(boolean force) {
        if (!force) {
            if (overlayVisible()) {
                return;
            }
        }
        if (dialogPosition < dialogCommands.length) {
            while (dialogPosition < dialogCommands.length && handleDialog(dialogPosition++));
        } else {
            dialogPosition++;
            onFinish();
        }
    }

    /**
     * Runs the next dialog group
     */
    public void nextDialog() {
        nextDialog(false);
    }

    /**
     * A fade transition to/from black
     * @param onFinished Handler to call once the transition is finished
     * @param duration   The duration of the transition
     * @param direction  Fade to black, or fade from black
     */
    private void transitionFade(EventHandler onFinished, double duration, boolean direction) {
        Util.fade(transitionOverlay, duration, direction ? 1 : 0, direction ? 0 : 1, onFinished);
    }

    /**
     * Fade from black
     * @param onFinished Handler to call once the transition is finished
     * @param duration   The duration of the transition
     */
    private void fadeIn(EventHandler onFinished, double duration) {
        transitionFade(onFinished, duration, true);
    }

    /**
     * Fade from black with a default duration of 1 second
     * @param onFinished Handler to call once the transition is finished
     */
    private void fadeIn(EventHandler onFinished) {
        fadeIn(onFinished, 1);
    }

    /**
     * Fade to black
     * @param onFinished Handler to call once the transition is finished
     * @param duration   The duration of the transition
     */
    private void fadeOut(EventHandler onFinished, double duration) {
        transitionFade(onFinished, duration, false);
    }

    /**
     * Fade to black with a default duration of 1 second
     * @param onFinished Handler to call once the transition is finished
     */
    private void fadeOut(EventHandler onFinished) {
        fadeOut(onFinished, 1);
    }

    /**
     * {@inheritDoc}
     */
    protected BaseGameSave save() {
        return new StoryGameSave(dialogPosition, scores, levelComplete);
    }

    /**
     * {@inheritDoc}
     */
    protected void load(BaseGameSave baseSave) {
        super.load(baseSave);
        StoryGameSave save = (StoryGameSave)baseSave;
        this.dialogPosition = 0;
        while (this.dialogPosition < save.dialogPosition && this.dialogPosition < dialogCommands.length) {
            handleDialog(this.dialogPosition++, true);
        }
        if (this.dialogPosition == dialogCommands.length) {
            onFinish();
        }
    }
}
