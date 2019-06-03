/**
 * Revision history:
 *  - May 13, 2019: Created ~Evan Zhang
 *  - May 16, 2019: Updated ~Evan Zhang
 *  - May 17, 2019: Updated ~Evan Zhang
 *  - May 22, 2019: Updated ~Evan Zhang
 *  - May 29, 2019: Updated ~Evan Zhang
 *  - May 30, 2019: Updated ~Evan Zhang
 *  - May 31, 2019: Updated ~Evan Zhang
 *  - Jun 1, 2019: Commented ~Evan Zhang
 * @author Evan Zhang
 * @version 1
 */
public abstract class BaseScene {
    /** Instance variables */
    protected Game game;

    /**
     * Constructor
     * @param  game The game to link with
     */
    public BaseScene(Game game) {
        this.game = game;
    }

    /**
     * Initializes the scene
     */
    public abstract void initScene();

    /**
     * Called on enter to the scene
     */
    public void onEnter() {
        /** Nothing needs to be done by default */
    }

    /**
     * Called on exit from the scene
     */
    public void onExit() {
        /** Nothing needs to be done by default */
    }
}
