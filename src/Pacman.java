import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Pacman contains all relevant fields and methods related to the pacman moving and navigating on the map.
 */
public class Pacman {
    private final float normalSpeed = 1.5f;
    private final float arvindSpeed = 2.0f;
    private float speed;

    private Directions nextDir;

    private HashMap<Directions, Animation> pacmanAnimations = new HashMap<>();
    private Animation arvindAnimation;
    private boolean shouldShowArvind = false;

    private final float initialX;
    private final float initialY;

    private float x;
    private float y;

    private float centerX;
    private float centerY;

    private final float elementPixelUnit;
    private Directions dir;
    private Circle pacmanCircle;
    private final float pacmanCircleRadius;

    private boolean isAtIntersection = false;

    private float closestNonCollisionX;
    private float closestNonCollisionY;

    private final boolean isDebug;

    private HashMap<Directions, Integer> dirMapX;
    private HashMap<Directions, Integer> dirMapY;

    private boolean isColliding = false;
    private ArrayList<Shape> wallShapesAroundPacman;

    /**
     * Constructor for Pacman class taking in its parameters specified below.
     *
     * @param initialX         the initial x coordinate of pacman
     * @param initialY         the initial y coordinate of pacman
     * @param elementPixelUnit how many pixel (xy coordinate unit length) is one row/column equal to.
     * @param isDebug          boolean whether debug mode is on
     */
    public Pacman(float initialX, float initialY, float elementPixelUnit, boolean isDebug) {
        this.initialX = initialX;
        this.initialY = initialY;
        this.x = initialX;
        this.y = initialY;

        this.dir = Directions.STILL;
        this.isDebug = isDebug;
        this.elementPixelUnit = elementPixelUnit;
        nextDir = Directions.STILL;
        this.pacmanCircleRadius = (float) ((this.elementPixelUnit / 2) * 0.90);
        initDirMap();
    }

    /**
     * Initializes all direction animation of Pacman, associating animations with different directions.
     */
    public void init() {
        this.setCurrentSpeed();

        try {
            this.initializePacmanAnimations();
            this.initializeArvindAnimation();

            // This is the conversion between animation coordinate and circle coordinate so that they fully overlap.
            this.centerX = this.x + this.elementPixelUnit / 2;
            this.centerY = this.y + this.elementPixelUnit / 2;

            this.pacmanCircle = new Circle(
                    this.centerX,
                    this.centerY,
                    this.pacmanCircleRadius);

        } catch (SlickException e) {
            System.out.println("Cannot load Pacman images.");
        }

    }

    /**
     * Sets up the speed based on if Arvind status is turned on
     */
    private void setCurrentSpeed() {
        this.speed = this.shouldShowArvind ? this.arvindSpeed : this.normalSpeed;
    }

    /**
     * Initializes the animation for Arvind animation.
     */
    private void initializeArvindAnimation() throws SlickException {
        SpriteSheet arvindSpriteSheet = new SpriteSheet("images/pacman/arvind.jpg", 200, 200);
        Animation arvindAnimation = new Animation(arvindSpriteSheet, 100);

        this.arvindAnimation = arvindAnimation;
    }

    /**
     * Initializes animations for pacman in all directions and put them into the pacmanAnimation HashMap.
     */
    private void initializePacmanAnimations() throws SlickException {
        SpriteSheet pacmanLeftSpriteSheet = new SpriteSheet("images/pacman/pacman_left.jpg", 56, 56);
        Animation leftAnimation = new Animation(pacmanLeftSpriteSheet, 100);

        SpriteSheet pacmanRightSpriteSheet = new SpriteSheet("images/pacman/pacman_right.jpg", 56, 56);
        Animation rightAnimation = new Animation(pacmanRightSpriteSheet, 100);

        SpriteSheet pacmanUpSpriteSheet = new SpriteSheet("images/pacman/pacman_up.jpg", 56, 56);
        Animation upAnimation = new Animation(pacmanUpSpriteSheet, 100);

        SpriteSheet pacmanDownSpriteSheet = new SpriteSheet("images/pacman/pacman_down.jpg", 56, 56);
        Animation downAnimation = new Animation(pacmanDownSpriteSheet, 100);

        SpriteSheet pacmanStillSpriteSheet = new SpriteSheet("images/pacman/pacman_still.jpg", 56, 56);
        Animation stillAnimation = new Animation(pacmanStillSpriteSheet, 100);

        this.pacmanAnimations.put(Directions.UP, upAnimation);
        this.pacmanAnimations.put(Directions.DOWN, downAnimation);
        this.pacmanAnimations.put(Directions.LEFT, leftAnimation);
        this.pacmanAnimations.put(Directions.RIGHT, rightAnimation);
        this.pacmanAnimations.put(Directions.STILL, stillAnimation);
    }


    /**
     * update method is called every frame of the game by the governing update method in MainGameState class.
     * it updates the positions (x, y) and directions (dir) of the pacman with processed key inputs.
     * Update score based on whether location has dot.
     */
    public void update(
            int delta,
            ArrayList<Shape> closeByWallShapes,
            float closestNonCollisionX,
            float closestNonCollisionY
    ) {
        this.setWallShapesAroundPacman(closeByWallShapes);
        this.closestNonCollisionX = closestNonCollisionX;
        this.closestNonCollisionY = closestNonCollisionY;
        this.pacmanAnimations.values().forEach(animation -> animation.update(delta));
        this.arvindAnimation.update(delta);
        this.updatePacmanCirclePosition();
        this.setIsAtIntersectionAndCollidingWithWall();
        updatePosition();

        if (dirMovable(nextDir)) {
            dir = nextDir;
        } else if (isAtIntersection && isColliding) {
            replacePacmanToPathCenter();
            nextDir = Directions.STILL;
            dir = nextDir;
        }

        this.setCurrentSpeed();
    }


    /**
     * Method to rendering Pacman image.
     *
     * @param g Graphics
     */
    public void render(Graphics g) {
        if (this.shouldShowArvind) {
            this.arvindAnimation.draw(this.x, this.y, this.elementPixelUnit, this.elementPixelUnit);
        }
        else {
            this.pacmanAnimations.get(this.dir).draw(this.x, this.y, this.elementPixelUnit, this.elementPixelUnit);
        }
        if (isDebug) {
            g.draw(this.pacmanCircle);
        }
    }

    /**
     * Sets wallShapesAroundPacman
     */
    public void setWallShapesAroundPacman(ArrayList<Shape> wallShapesAroundPacman) {
        this.wallShapesAroundPacman = wallShapesAroundPacman;
    }

    /**
     * When collision is detected, the pacman circle would already be slightly off the center of its path.
     * This method moves it back to the center, resets its position to be right before the collision so that the
     * collision state is clear and the pacman could change direction.
     */
    public void replacePacmanToPathCenter() {
        this.x = this.closestNonCollisionX;
        this.y = this.closestNonCollisionY;
    }

    /**
     * Determine if wall shapes around Pacman is colliding with Pacman circle.
     *
     * @param circle Pacman circle
     * @return boolean whether collision happens
     */
    public boolean getIsCollidingWithCircle(Circle circle) {
        for (Shape wall : this.wallShapesAroundPacman) {
            if (circle.intersects(wall)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set isAtIntersection and isColliding according to whether Pacman circle intersects with wall shapes and available directions at current coordinate.
     */
    public void setIsAtIntersectionAndCollidingWithWall() {
        for (Shape wall : this.wallShapesAroundPacman) {
            if (this.pacmanCircle.intersects(wall)) {
                this.isAtIntersection = true;
                this.isColliding = true;
                return;
            }
        }
        // if the ghost is close enough to a nearest non collision location (path center) and the pacman temp circle
        // (placed at the nearest path center) has more than 2 available directions
        // (more than current direction and its reverse), it is also at intersection.
        if (Math.abs(this.closestNonCollisionX - this.x) < this.speed / 2 &&
                Math.abs(this.closestNonCollisionY - this.y) < this.speed / 2 &&
                this.getAvailableDirections(this.closestNonCollisionX, this.closestNonCollisionY).size() >= 2) {
            this.isAtIntersection = true;
            return;
        }

        this.isAtIntersection = false;
        this.isColliding = false;
    }

    /**
     * Setter for nextDir
     *
     * @param nextDir nextDir to be set
     */
    public void setNextDir(Directions nextDir) {
        this.nextDir = nextDir;
    }

    /**
     * Update Pacman position using direction maps.
     */
    public void updatePosition() {
        if (dir != Directions.STILL) {
            x += dirMapX.get(dir) * this.speed;
            y += dirMapY.get(dir) * this.speed;
        }
    }

    /**
     * Method to reset Pacman to its initial coordinates and directions to STILL.
     */
    public void reset() {
        this.x = this.initialX;
        this.y = this.initialY;
        this.dir = Directions.STILL;
        this.nextDir = Directions.STILL;
        this.shouldShowArvind = false;
    }

    /**
     * Getter for x.
     *
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * Getter for y.
     *
     * @return y
     */
    public float getY() {
        return y;
    }

    /**
     * Setter for x.
     *
     * @param x x coordinate of Pacman.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Setter for y.
     *
     * @param y y coordinate of Pacman.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Getter for center x.
     *
     * @return center x
     */
    public float getCenterX() {
        return centerX;
    }

    /**
     * Getter for center y.
     *
     * @return center y
     */
    public float getCenterY() {
        return centerY;
    }

    /**
     * Getter for direction.
     *
     * @return dir
     */
    public Directions getDir() {
        return dir;
    }

    /**
     * Setter for direction.
     *
     * @param d direction to be set.
     */
    public void setDir(Directions d) {
        dir = d;
    }

    /**
     * Getter for nextDir.
     *
     * @return nextDir
     */
    public Directions getNextDir() {
        return nextDir;
    }

    /**
     * Setter for next direction.
     *
     * @param dir direction to be set.
     */
    public void setNextDirection(Directions dir) {
        nextDir = dir;
    }

    /**
     * Getter for Pacman circle.
     *
     * @return Pacman circle
     */
    public Circle getPacmanCircle() {
        return this.pacmanCircle;
    }

    /**
     * Getter for direction map for x coordinate.
     *
     * @return Direction map for x coordinate
     */
    public HashMap<Directions, Integer> getDirMapX() {
        return this.dirMapX;
    }


    /**
     * Getter for boolean isAtIntersection.
     *
     * @return boolean isAtIntersection
     */
    public boolean getIsAtIntersection() {
        return this.isAtIntersection;
    }

    /**
     * Getter for boolean shouldShowArvind.
     *
     * @return boolean shouldShowArvind
     */
    public boolean getShouldShowArvind() {
        return this.shouldShowArvind;
    }

    /**
     * Toggle the boolean field for showing Arwind animation as easter egg
     */
    public void toggleShouldShowArvind() {
        this.shouldShowArvind = !this.shouldShowArvind;
    }

    /**
     * Initialize direction map, which maps different directions with their corresponding moves in x and y coordinate.
     */
    protected void initDirMap() {
        dirMapX = new HashMap<Directions, Integer>();
        dirMapY = new HashMap<Directions, Integer>();
        Directions[] dindex = {
                Directions.STILL,
                Directions.LEFT,
                Directions.RIGHT,
                Directions.UP,
                Directions.DOWN
        };
        int[] dx = {0, -1, 1, 0, 0};
        int[] dy = {0, 0, 0, -1, 1};
        for (int i = 0; i < dindex.length; i++) {
            dirMapX.put(dindex[i], dx[i]);
            dirMapY.put(dindex[i], dy[i]);
        }
    }

    /**
     * Return whether next position is accessible given dir.
     *
     * @param d direction for next position
     * @return boolean whether next position is accessible given dir.
     * @see Directions
     */
    private boolean dirMovable(Directions d) {
        float nextX = x + elementPixelUnit * dirMapX.get(d);
        float nextY = y + elementPixelUnit * dirMapY.get(d);

        this.pacmanCircle.setCenterX(nextX + this.elementPixelUnit / 2);
        this.pacmanCircle.setCenterY(nextY + this.elementPixelUnit / 2);
        boolean isNextPositionPacmanCircleColliding = this.getIsCollidingWithCircle(this.pacmanCircle);
        updatePacmanCirclePosition();
        return !isNextPositionPacmanCircleColliding;
    }

    /**
     * This method updates the center x,y of the circle based on the x, y of the pacman animation.
     */
    private void updatePacmanCirclePosition() {
        this.centerX = this.x + this.elementPixelUnit / 2;
        this.centerY = this.y + this.elementPixelUnit / 2;

        this.pacmanCircle.setCenterX(centerX);
        this.pacmanCircle.setCenterY(centerY);
    }

    /**
     * This method creates a temporary pacman circle that is placed one radius distance more than the
     * current actual pacman circle for each of the four directions. Then it populates available directions for those
     * that do not cause a collision between the temporary pacman circle and the wallShapesAroundPacman.
     */
    private ArrayList<Directions> getAvailableDirections(float x, float y) {
        ArrayList<Directions> availableDirections = new ArrayList<>();

        float currentCircleCenterX = x + this.elementPixelUnit / 2;
        float currentCircleCenterY = y + this.elementPixelUnit / 2;

        Circle tempPacmanCircle = new Circle(
                currentCircleCenterX,
                currentCircleCenterY,
                this.pacmanCircleRadius);

        // LEFT
        tempPacmanCircle.setCenterX(currentCircleCenterX - this.pacmanCircleRadius);
        tempPacmanCircle.setCenterY(currentCircleCenterY);
        if (this.wallShapesAroundPacman.stream().noneMatch(tempPacmanCircle::intersects)) {
            availableDirections.add(Directions.LEFT);
        }

        // RIGHT
        tempPacmanCircle.setCenterX(currentCircleCenterX + this.pacmanCircleRadius);
        tempPacmanCircle.setCenterY(currentCircleCenterY);
        if (this.wallShapesAroundPacman.stream().noneMatch(tempPacmanCircle::intersects)) {
            availableDirections.add(Directions.RIGHT);
        }
        // UP
        tempPacmanCircle.setCenterX(currentCircleCenterX);
        tempPacmanCircle.setCenterY(currentCircleCenterY - this.pacmanCircleRadius);
        if (this.wallShapesAroundPacman.stream().noneMatch(tempPacmanCircle::intersects)) {
            availableDirections.add(Directions.UP);
        }
        // DOWN
        tempPacmanCircle.setCenterX(currentCircleCenterX);
        tempPacmanCircle.setCenterY(currentCircleCenterY + this.pacmanCircleRadius);
        if (this.wallShapesAroundPacman.stream().noneMatch(tempPacmanCircle::intersects)) {
            availableDirections.add(Directions.DOWN);
        }

        return availableDirections;
    }
}