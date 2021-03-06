/**
 * Evan Zhang and Max Li
 * Mrs Krasteva
 * Due: June 10, 2019
 * Base class for platformer levels.
 */
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;

/**
 * Base class for platformer levels.
 * <pre>
 * Revision history:
 *  - May 13, 2019: Created ~Evan Zhang
 *  - May 14, 2019: Updated ~Evan Zhang
 *  - May 15, 2019: Updated ~Evan Zhang
 *  - May 16, 2019: Updated ~Evan Zhang
 *  - May 17, 2019: Updated ~Evan Zhang
 *  - May 18, 2019: Updated ~Evan Zhang
 *  - May 19, 2019: Updated ~Evan Zhang
 *  - May 21, 2019: Updated ~Evan Zhang
 *  - May 22, 2019: Updated ~Evan Zhang
 *  - May 25, 2019: Updated ~Evan Zhang
 *  - May 26, 2019: Updated ~Evan Zhang
 *  - May 27, 2019: Commented ~Evan Zhang
 *  - May 27, 2019: Updated ~Evan Zhang
 *  - May 28, 2019: Updated ~Evan Zhang
 *  - May 29, 2019: Updated ~Evan Zhang
 *  - May 30, 2019: Updated ~Evan Zhang
 *  - May 31, 2019: Updated ~Evan Zhang
 *  - Jun 1, 2019: Commented ~Evan Zhang
 *  - Jun 2, 2019: Commented ~Evan Zhang
 *  - Jun 2, 2019: Updated ~Evan Zhang
 *  - Jun 3, 2019: Updated ~Evan Zhang
 *  - Jun 3, 2019: Commented ~Evan Zhang
 *  - Jun 4, 2019: Updated ~Evan Zhang
 *  - Jun 6, 2019: Commented ~Evan Zhang
 *  - Jun 6, 2019: Updated ~Evan Zhang
 *  - Jun 7, 2019: Finished ~Evan Zhang
 * </pre>
 * @author Evan Zhang
 * @version 1
*/
public abstract class BasePlatformer extends BaseLevel {
    /** The player sprite */
    protected Sprite player;
    /** The progress bar */
    protected ProgressBar progress;
    /** The level data manager */
    protected Level level;
    /** The reference point of the screen */
    protected double referencePoint;
    /** The background image */
    protected ImageView background;
    /** The list of removed nodes from the screen */
    protected List<Sprite> removedNodes = new ArrayList<Sprite>();
    /** The amount of times the update function has been called */
    protected int updateCount = 0;

    /**
     * Constructor
     * @param  game The current game
     */
    public BasePlatformer(Game game) {
        super(game);
        level = new Level(getLevelFile());
        referencePoint = this.level.screenLength() - Constants.SCREEN_HEIGHT;
        root = new Group();
    }

    /**
     * {@inheritDoc}
     */
    public void initScene() {
        background = new ImageView(ResourceLoader.loadImage("platform-background.png"));
        background.setFitWidth(Constants.SCREEN_WIDTH);
        background.setPreserveRatio(true);
        updateBackground();
        root.getChildren().add(background);
        /** Add blocks */
        for (Sprite s : level.getAllSprites()) {
            root.getChildren().add(s);
            s.setTranslateY(getScreenY(s.getTranslateY()));
            s.setVisible(0 <= s.getTranslateY() + s.getHeight() && s.getTranslateY() < Constants.SCREEN_HEIGHT);
        }

        /** Add the player */
        player = new Sprite(30, Constants.SCREEN_HEIGHT - Constants.PLATFORM_BLOCK_HEIGHT - 26,
                            25, 25, ResourceLoader.loadImage("player/still.png"));
        root.getChildren().add(player);

        /** Add the progress bar */
        progress = new ProgressBar();
        progress.setTranslateX(Constants.SCREEN_WIDTH / 3);
        progress.setMinWidth(Constants.SCREEN_WIDTH / 3);
        progress.setTranslateY(20);
        progress.setMinHeight(20);
        progress.getStylesheets().add(ResourceLoader.loadCSS("game-progress-bar.css"));
        root.getChildren().add(progress);
        updateProgress();

        /** Add the pause button */
        ImageButton pause = Util.getMainImageButton("pause", event -> handleKeyPressed(KeyCode.ESCAPE));
        pause.setTranslateX(Constants.SCREEN_WIDTH - 50);
        pause.setFitWidth(40);
        pause.setTranslateY(10);
        pause.setFitHeight(40);
        root.getChildren().add(pause);

        setScene(root);
        start();
    }

    /**
     * Updates the location of the background
     */
    private void updateBackground() {
        background.setTranslateY(-referencePoint / 2);
    }

    /**
     * Updates the player position
     */
    private void updatePlayer() {
        double yy = getRealY(player.getTranslateY());
        BoundingBox box = new BoundingBox(player.getTranslateX() + Constants.EPS, yy + Constants.EPS,
                                          player.getWidth() - 2 * Constants.EPS,
                                          player.getHeight() - 2 * Constants.EPS);

        double ground = getScreenY(this.level.getLowerBound(box) - player.getHeight());
        double ceiling = getScreenY(this.level.getUpperBound(box));

        boolean moveLeft = pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.A);
        boolean moveRight = pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D);
        boolean moveUp = pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.W);
        boolean onGround = player.onGround(ground);

        if (moveLeft || moveRight || !onGround) {
            if (onGround || updateCount % 40 / 10 != 0) {
                updateCount++;
            }
            player.setImage(ResourceLoader.loadImage("player/00" + (updateCount % 80 / 10) + ".png"));
        } else {
            player.setImage(ResourceLoader.loadImage("player/still.png"));
        }

        if (moveUp && onGround) {
            player.jump();
        }

        player.fall(ground, ceiling);
        if (moveLeft) {
            player.setScaleX(-1);
            player.moveLeft(this.level.getLeftBound(box));
        }
        if (moveRight) {
            player.setScaleX(1);
            player.moveRight(this.level.getRightBound(box) - player.getWidth());
        }

        if (player.onGround(ground) &&
                this.level.aboveEndPlatform(player.getCenterX(), getRealY(player.getCenterY()))) {
            onFinish();
        }
    }

    /**
     * Calculates how much to shift the screen location by
     * @param  cur The current value
     * @param  max The max value
     * @return     The amount to shift by
     */
    private double calcSlide(double cur, double max) {
        double ret = 10 * (max - cur) / max;
        return ret < 0 ? 10 : ret;
    }

    /**
     * Updates the screen position based on the player position
     */
    private void updateScreen() {
        double mod = 0;
        double posY = player.getTranslateY();
        double screenY = Constants.SCREEN_HEIGHT;
        if (posY <= screenY / 3) {
            mod -= calcSlide(posY, screenY / 3);
        } else if (posY >= screenY * 2 / 3) {
            mod += calcSlide(screenY - posY, screenY / 3);
        }
        if (Math.abs(mod) > Constants.EPS &&
                0 <= referencePoint + mod && referencePoint + mod + screenY < this.level.screenLength()) {
            referencePoint += mod;
            for (Sprite obj : this.level.getAllSprites()) {
                obj.setTranslateY(obj.getTranslateY() - mod);
                obj.setVisible(0 <= obj.getTranslateY() + obj.getHeight() && obj.getTranslateY() < screenY);
            }
            player.setTranslateY(player.getTranslateY() - mod);
            updateBackground();
        }
    }

    /**
     * Checks whether the player is touching a special block and update accordingly
     */
    private void updateSpecial() {
        for (Sprite s : this.level.getSpecialSprites()) {
            if (s.getBoundsInParent().intersects(player.getBoundsInParent()) && root.getChildren().remove(s)) {
                removedNodes.add(s);
                handleSpecial(-this.level.getPosition(s.getCenterX(), getRealY(s.getCenterY())));
                break;
            }
        }
    }

    /**
     * Updates the progress bar
     */
    private void updateProgress() {
        /**
         * Calculating the progress bar.
         * The challenge with calculating the progress bar is, which position do you use?
         * * Using the top of the screen means the bar can never be empty (at the beginning of the level).
         * * Using the bottom of the screen means the bar can never full (at the end of the level).
         * * Using the middle of the screen means the bar can never be full *nor* empty.
         *
         * Thus, the solution is to calculate the position of the screen to use *based* on the actual position.
         * This is calculated with the "add" variable.
         *
         *  add = (referencePoint + add * Constants.SCREEN_HEIGHT) / this.level.screenLength();
         *
         *  Note that the answer depends on the answer. Fortunately, the answer converges, so we can just approximate
         *  by recursing 3 times.
         */
        int n = 3;
        double add = 0;
        for (int x = n - 1; x >= 0; x--) {
            add += referencePoint * Math.pow(this.level.screenLength(), x) * Math.pow(Constants.SCREEN_HEIGHT, n - x - 1);
        }
        add /= Math.pow(this.level.screenLength(), n);
        add *= Constants.SCREEN_HEIGHT;

        progress.setProgress(1 - (referencePoint + add) / this.level.screenLength());
    }

    /**
     * {@inheritDoc}
     */
    protected void update() {
        if (overlayVisible()) {
            return;
        }
        updateScreen();
        updatePlayer();
        updateSpecial();
        updateProgress();
    }

    /**
     * {@inheritDoc}
     */
    protected void load(BaseGameSave baseSave) {
        super.load(baseSave);
        PlatformerGameSave save = (PlatformerGameSave)baseSave;

        this.player.setCenterPosition(save.player);

        double mod = save.referencePoint - this.referencePoint;
        this.referencePoint = save.referencePoint;
        for (Sprite obj : this.level.getAllSprites()) {
            obj.setTranslateY(obj.getTranslateY() - mod);
            obj.setVisible(0 <= obj.getTranslateY() + obj.getHeight() && obj.getTranslateY() < Constants.SCREEN_HEIGHT);
        }

        this.removedNodes.clear();
        for (Point2D p : save.removedNodes) {
            double x = p.getX();
            double y = getRealY(p.getY());
            Sprite s = this.level.getSprite(x, y);
            this.removedNodes.add(s);
            root.getChildren().remove(s);
        }

        updateBackground();
    }

    /**
     * {@inheritDoc}
     */
    protected PlatformerGameSave save() {
        ArrayList<Point2D> nodes = new ArrayList<Point2D>();
        for (Sprite s : removedNodes) {
            nodes.add(s.getCenterPosition());
        }
        return new PlatformerGameSave(referencePoint, player.getCenterPosition(), nodes, scores, levelComplete);
    }

    /**
     * Gets the actual y value given the screen y
     * @param  y The screen y
     * @return   The actual y
     */
    protected double getRealY(double y) {
        return y + referencePoint;
    }

    /**
     * Gets the screen y value given the actual y
     * @param  y The actual y
     * @return   The screen y
     */
    protected double getScreenY(double y) {
        return y - referencePoint;
    }

    /**
     * Called when the player touches a special block
     * @param specialType The unique id of the special block
     */
    protected abstract void handleSpecial(int specialType);
}
