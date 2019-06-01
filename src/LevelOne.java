import java.io.*;
import java.util.*;

import javafx.animation.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;

/**
 * @version 1
 * @author Evan Zhang
 * Revision history:
 *  - May 13, 2019: Created ~Evan Zhang
 *  - May 17, 2019: Updated ~Evan Zhang
 *  - May 21, 2019: Updated ~Evan Zhang
 *  - May 22, 2019: Updated ~Evan Zhang
 *  - May 27, 2019: Commented ~Evan Zhang
 *  - May 29, 2019: Updated ~Evan Zhang
 *  - May 30, 2019: Updated ~Evan Zhang
 *  - May 31, 2019: Updated ~Evan Zhang
 */

public class LevelOne extends BaseLevel {
    private class Command {
        private String command;
        private String[] arguments;
        private int argumentPointer = 0;
        public Command(String line) {
            String[] tokens = line.trim().split(":");
            command = tokens[0].trim();
            arguments = new String[tokens.length - 1];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = tokens[i + 1].trim();
            }
        }
        public Command(Command other) {
            this.command = other.command;
            this.arguments = new String[other.arguments.length];
            for (int i = 0; i < this.arguments.length; i++) {
                this.arguments[i] = other.arguments[i];
            }
        }
        public String getCommand() {
            return command;
        }
        public String[] getArguments() {
            return arguments;
        }
        public String nextArgument() {
            return arguments[argumentPointer++];
        }
        public boolean isComment() {
            return command.startsWith("#");
        }
    }

    final int TEXT_OVERLAY_WIDTH = Constants.SCREEN_WIDTH - 150;
    final int TEXT_OVERLAY_HEIGHT = 200;
    final int TEXT_PADDING = 50;

    private Rectangle transitionOverlay = new Rectangle(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Color.BLACK);
    private ImageView background;
    private StackPane textOverlay;
    private int dialogPosition = 0;
    private Command[] dialogCommands;
    private TreeMap<String, Integer> dialogCommandsLabelMap = new TreeMap();

    /**
     * Constructor
     * @param  game The current game
     */
    public LevelOne(Game game) {
        super(game);
        root = new Group();
        ArrayList<String> lines = new ArrayList();
        try (BufferedReader in = new BufferedReader(ResourceLoader.loadLevel(getLevelFile()))) {
            for (String line; (line = in.readLine()) != null;) {
                if (line.length() > 0) {
                    lines.add(line);
                }
            }
        } catch (Exception e) {
        }
        dialogCommands = new Command[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            dialogCommands[i] = new Command(lines.get(i));
            if (dialogCommands[i].getCommand().equals("LABEL")) {
                dialogCommandsLabelMap.put(dialogCommands[i].getArguments()[0], i);
            }
        }
    }

    /**
     * Get the level number that this class represents
     * @return The level
     */
    protected int getLevel() {
        return 1;
    }
    /**
     * Get the number of scores to save
     * @return The score count
     */
    protected int getScoreCount() {
        return 1;
    }

    /**
     * Initializes the scene
     */
    public void initScene() {
        background = new ImageView();
        background.setFitWidth(Constants.SCREEN_WIDTH);
        background.setFitHeight(Constants.SCREEN_HEIGHT);
        root.getChildren().add(background);

        textOverlay = new StackPane();
        textOverlay.setTranslateX((Constants.SCREEN_WIDTH - TEXT_OVERLAY_WIDTH) / 2);
        textOverlay.setMinWidth(TEXT_OVERLAY_WIDTH);
        textOverlay.setTranslateY(Constants.SCREEN_HEIGHT - TEXT_OVERLAY_HEIGHT);
        textOverlay.setMinHeight(TEXT_OVERLAY_HEIGHT);
        textOverlay.setBackground(new Background(
            new BackgroundFill(Color.web("#ff9900"), new CornerRadii(20, 20, 0, 0, false), Insets.EMPTY),
            new BackgroundFill(Color.web("#ffd640"), new CornerRadii(15, 15, 0, 0, false), new Insets(25, 25, 0, 25))
        ));
        textOverlay.setPadding(new Insets(TEXT_PADDING));
        textOverlay.setAlignment(Pos.TOP_LEFT);

        root.setOnMouseClicked(e -> nextDialog());

        root.getChildren().add(textOverlay);

        root.getChildren().add(transitionOverlay);
        transitionOverlay.setOpacity(0);
        setScene(root);
        start();
    }

    /**
     * Update method called every game tick
     */
    protected void update() {
    }

    protected void handleFinish() {
        fadeOut(e -> {
            StackPane nextLevel = initBasicOverlay(Util.getMainButton("Proceed to the next level",
                                                                      event -> this.game.updateState(State.LEVEL_TWO), 15));
            setOverlay(nextLevel);
            nextLevel.setOpacity(0);
            Util.fade(nextLevel, 1, 0, 1);
        });
    }

    protected void onFirstEnter() {
        super.onFirstEnter();
        nextDialog();
    }

    protected boolean overlayVisible() {
        return super.overlayVisible() || transitionOverlay.getOpacity() > 0;
    }

    protected void handleKeyPressed(KeyCode key) {
        super.handleKeyPressed(key);
        switch(key) {
            case ENTER: nextDialog(); break;
        }
    }

    private Text getFormattedText(String text) {
        Text ret = new Text(text);
        ret.setFont(Util.getDefaultFont(15));
        ret.setFill(Color.RED);
        ret.setWrappingWidth(TEXT_OVERLAY_WIDTH - TEXT_PADDING * 2);
        return ret;
    }

    private boolean handleDialog(int position, boolean onlyMutatingCommands) {
        Command command = new Command(dialogCommands[position]);
        if (command.isComment()) {
            return true;
        }
        switch(command.getCommand()) {
            case "PAUSE":
                return false;
            case "TEXT":
                textOverlay.getChildren().clear();
                textOverlay.getChildren().add(getFormattedText(command.nextArgument()));
                break;
            case "NARRATION":
                textOverlay.getChildren().clear();
                Text text = getFormattedText(command.nextArgument());
                text.setStyle("-fx-font-style: italic");
                textOverlay.getChildren().add(text);
                break;
            case "BACKGROUND":
                switch(command.nextArgument()) {
                    case "IMAGE":
                        background.setImage(ResourceLoader.loadImage(command.nextArgument()));
                        break;
                    case "COLOR":
                        WritableImage colorImage = new WritableImage(1, 1);
                        colorImage.getPixelWriter().setColor(0, 0, Color.web(command.nextArgument()));
                        background.setImage(colorImage);
                        break;
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
                    } catch(Exception e) {}
                    fadeOut(event -> nextDialog(true), delay);
                    return false;
                }
                break;
            case "FADEIN":
                if (!onlyMutatingCommands) {
                    double delay = 0.5;
                    try {
                        delay = Double.parseDouble(command.nextArgument());
                    } catch(Exception e) {}
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
        }
        return true;
    }

    private boolean handleDialog(int position) {
        return handleDialog(position, false);
    }

    private boolean jumpToLabel(String label) {
        if (dialogCommandsLabelMap.containsKey(label)) {
            this.dialogPosition = dialogCommandsLabelMap.get(label);
            return true;
        }
        return false;
    }

    private void nextDialog(boolean force) {
        if (!force) {
            if (overlayVisible()) {
                return;
            }
        }
        if (dialogPosition < dialogCommands.length) {
            while(dialogPosition < dialogCommands.length && handleDialog(dialogPosition++));
        } else {
            dialogPosition++;
            onFinish();
        }
    }

    private void nextDialog() {
        nextDialog(false);
    }

    private void transitionFade(EventHandler onFinished, double duration, boolean direction) {
        Util.fade(transitionOverlay, duration, direction ? 1 : 0, direction ? 0 : 1, onFinished);
    }

    private void fadeIn(EventHandler onFinished, double duration) {
        transitionFade(onFinished, duration, true);
    }

    private void fadeIn(EventHandler onFinished) {
        fadeIn(onFinished, 1);
    }

    private void fadeOut(EventHandler onFinished, double duration) {
        transitionFade(onFinished, duration, false);
    }

    private void fadeOut(EventHandler onFinished) {
        fadeOut(onFinished, 1);
    }

    /**
     * Saves the level state
     * @return The GameSave object
     */
    protected GameSave save() {
        return new StoryGameSave(dialogPosition, scores, levelComplete);
    }

    /**
     * Load the level state from a GameSave
     * @param baseSave The game save to load from
     */
    protected void load(GameSave baseSave) {
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
