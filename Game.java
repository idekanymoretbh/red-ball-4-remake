//TODO: Somehow Figure Out How To Keep Two Total Images From Going To FPS 1
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
/**The Class Where The Game Takes Place */
public class Game extends JPanel implements KeyListener, MouseListener {
    public static int newAlpha = 255;
    public static int ACHIEVEMENT_DELAY = 150;
    public static volatile boolean isDisplayAchievement = false;
    public static Achievement achievementToDraw = null;
    public static int displayTimer = 0;
    public static volatile boolean isPlayerHit = false;
    public static volatile boolean isPlayerInv = false;
    public static String GLOBAL_TEXTURE_SHEET = "Images/AssetSheet.png";
    public static HashMap<String, Boolean> keys = new HashMap<>();
    public double GRAVITY = 2;
    public double xVel = 0;
    public Timer timer;
    public JFrame frame = new JFrame("Red Ball 4: Remake");
    public Platform[] platforms = {new Platform(0, 907, 875, 50, 0, "null"), new Platform(875, 907, 600, 50, 0, "null"), new Platform(875, 857, 50, 50, 0, "null")};
    public Player player = new Player(25, 200, 50, 50, 0, "player", true);
    public static Enemy[] testEnemies = {new Enemy(600, 857, 50, 50, 0, "enemy", true, Enemy.MATRIX)};
    public static Graphics2D g2d;
    public static Achievement[] achievements = {new Achievement("Play The Game...", "Your First Ever. Cherish It.")};
    public Game() {
        frame.setUndecorated(false);
        frame.setResizable(false);
        this.addKeyListener(this);
        this.addMouseListener(this);
        frame.add(this);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported()) {
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
        } else {
            frame.setSize(screenSize);
        }
        this.requestFocus();
        this.setFocusTraversalKeysEnabled(false);
        timer = new Timer(16, e -> {
            repaint();
            for (Enemy enemy : testEnemies) {
                checkHitboxCollision(player, enemy.getHitbox());
                checkHurtboxCollision(player, enemy.getHurtbox());
            }
        });
        timer.start();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Game();
            }
        });
    }
    @Override
    public void keyPressed(KeyEvent evt) {
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } if(evt.getKeyCode() == KeyEvent.VK_A) {
            keys.put("Keys.A", true);
        } if(evt.getKeyCode() == KeyEvent.VK_D) {
            keys.put("Keys.D", true);
        } if(evt.getKeyCode() == KeyEvent.VK_W || evt.getKeyCode() == KeyEvent.VK_SPACE) {
            keys.put("Keys.W", true);
        } if(evt.getKeyCode() == KeyEvent.VK_TAB) {
            keys.put("Keys.TAB", true);
        }
    }
    @Override
    public void keyReleased(KeyEvent evt) {
        if(evt.getKeyCode() == KeyEvent.VK_A) {
            keys.put("Keys.A", false);
        } if(evt.getKeyCode() == KeyEvent.VK_D) {
            keys.put("Keys.D", false);
        } if(evt.getKeyCode() == KeyEvent.VK_W || evt.getKeyCode() == KeyEvent.VK_SPACE) {
            keys.put("Keys.W", false);
        } if(evt.getKeyCode() == KeyEvent.VK_TAB) {
            keys.put("Keys.TAB", false);
        } if(evt.getKeyCode() == KeyEvent.VK_T) {
            if(achievements.length > 0) achievements[0].setAchieved(true);
        }
    }
    @Override
    public void keyTyped(KeyEvent evt) {}
    @Override
    public void mousePressed(MouseEvent evt) {}
    @Override
    public void mouseReleased(MouseEvent evt) {}
    @Override
    public void mouseClicked(MouseEvent evt) {}
    @Override
    public void mouseEntered(MouseEvent evt) {}
    @Override
    public void mouseExited(MouseEvent evt) {}
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(player.getHealth() <= 0) {
            player.reset();
        }
        g2d = (Graphics2D)g.create();
        boolean playerOnGround = false;
        //Draw Platforms
        for(Platform plat : platforms) {
            //Image Logic
            if(plat.getImageName().equals("null")) {
                g.setColor(Color.RED);
                g.fillRect(plat.getPosition()[0], plat.getPosition()[1], plat.getSize()[0], plat.getSize()[1]);
            } else if(plat.getImageName().equals("grass")) {
                //Add Image Stuff
            }
            //Ground Detection
            if(player.getPosition()[1] + player.getSize()[1] >= plat.getPosition()[1] && player.getPosition()[1] < plat.getPosition()[1] + plat.getSize()[1] && (player.getPosition()[0] + player.getSize()[0] > plat.getPosition()[0]) && (player.getPosition()[0] < plat.getPosition()[0] + plat.getSize()[0])) {
                playerOnGround = true;
                player.update(player.getPosition()[0], plat.getPosition()[1] - player.getSize()[1], player.getSize()[0], player.getSize()[1], player.getDirectionDegrees(), player.getImageName(), player.getWalkDir());
            }
        }
        //Handles The Player Sprite
        if(player.getImageName().equals("null")) {
            g2d.setColor(Color.BLUE);
            g2d.fillRect(player.getPosition()[0], player.getPosition()[1], player.getSize()[0], player.getSize()[1]);
        } else {
            g2d.setColor(Color.BLUE);
            g2d.rotate(Math.toRadians(player.getDirectionDegrees()), player.getPosition()[0] + 25, player.getPosition()[1] + 25);
            g2d.fillRect(player.getPosition()[0], player.getPosition()[1], player.getSize()[0], player.getSize()[1]);
            g2d = (Graphics2D)g.create();
            //g.drawImage(ImageRetriever.rotateImage(playerImage, player.getDirectionDegrees()), player.getPosition()[0], player.getPosition()[1] + 10, null);
        }
        //Handles Enemy Sprite
        for(Enemy enemy : testEnemies) {
            if(enemy.getImageName().equals("null")) {
                g2d.setColor(Color.GRAY);
                g2d.fillRect(enemy.getPosition()[0], enemy.getPosition()[1], enemy.getSize()[0], enemy.getSize()[1]);
            } else {
                g2d.setColor(Color.GRAY);
                g2d.rotate(Math.toRadians(enemy.getDirectionDegrees()), enemy.getPosition()[0] + 25, enemy.getPosition()[1] + 25);
                g2d.fillRect(enemy.getPosition()[0], enemy.getPosition()[1], enemy.getSize()[0], enemy.getSize()[1]);
                g2d.dispose();
            }
        }
        //Showing Hitboxes And Hurtboxes
        if(keys.containsKey("Keys.TAB") && keys.get("Keys.TAB")) {
            for(Enemy testEnemy : testEnemies) {
                g.setColor(Color.GREEN);
                g.fillRect(testEnemy.getHitbox().getPosition()[0], testEnemy.getHitbox().getPosition()[1], testEnemy.getHitbox().getSize()[0], testEnemy.getHitbox().getSize()[1]);
                g.setColor(Color.RED);
                g.fillRect(testEnemy.getHurtbox().getPosition()[0], testEnemy.getHurtbox().getPosition()[1], testEnemy.getHurtbox().getSize()[0], testEnemy.getHurtbox().getSize()[1]);
            }
        }
        g.drawString(Integer.toString(player.getHealth()), 20, 20);
        g.drawString(Integer.toString(player.getLives()), 20, 40);
        //Update Achievements
        g.setColor(new Color(0, 0, 0, newAlpha));
        for (Achievement achievement : achievements) {
            if(achievement.getAchieved()) {
                isDisplayAchievement = true;
                displayTimer = 0;
                achievementToDraw = achievement;
                achievements = Arrays.stream(achievements).filter(val -> val != achievement).toList().toArray(Achievement[]::new);
            }
        }
        if(isDisplayAchievement) {
            if(displayTimer < ACHIEVEMENT_DELAY) {
                g.drawString("Achievement Unlocked: " + achievementToDraw.getTitle(), (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (g.getFontMetrics().stringWidth("Achievement Unlocked: " + achievementToDraw.getTitle()) / 2), 500);
                g.drawString(achievementToDraw.getDescription(), (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (g.getFontMetrics().stringWidth(achievementToDraw.getDescription()) / 2), 520);
                displayTimer++;
                newAlpha -= 3;
                newAlpha = Math.clamp(newAlpha, 0, 255);
                System.out.println(displayTimer);
            }
        }
        //Handles Movement Logic
        double newX = player.getPosition()[0] < 0 ? 0 : player.getPosition()[0];
        double newY = player.getPosition()[1];
        boolean playerBlockLeft = false;
        boolean playerBlockRight = false;
        for(Platform plat : platforms) {
            if(newX > plat.getPosition()[0] + plat.getSize()[0] && newX - 6.25 < plat.getPosition()[0] + plat.getSize()[0] && !(newY + player.getSize()[1] < plat.getPosition()[1])) {
                playerBlockLeft = true;
            } if(newX + player.getSize()[0] < plat.getPosition()[0] && newX + player.getSize()[0] + 6.25 > plat.getPosition()[0] && !(newY + player.getSize()[1] < plat.getPosition()[1])) {
                playerBlockRight = true;
            }
        }
        int newDeg = 0;
        if(isPlayerHit) {
            playerOnGround = false;
            xVel *= -5;
            GRAVITY = -14;
            isPlayerHit = false;
        }
        if(keys.containsKey("Keys.W") && keys.get("Keys.W") && playerOnGround) {
            GRAVITY = -14;
            playerOnGround = false;
        } if(keys.containsKey("Keys.A") && keys.get("Keys.A")) {
            if(!playerBlockLeft) {
                xVel -= 0.25;
            } else {
                xVel = 0;
            }
            xVel = Math.clamp(xVel, -6, 6);
        } if(keys.containsKey("Keys.D") && keys.get("Keys.D")) {
            if(!playerBlockRight) {
                xVel += 0.25;
            } else {
                xVel = 0;
            }
            xVel = Math.clamp(xVel, -6, 6);
        } if((!(keys.containsKey("Keys.A") && keys.get("Keys.A"))) && (!(keys.containsKey("Keys.D") && keys.get("Keys.D"))) && playerOnGround) {
            xVel /= 1.125;
        } if(xVel < 0.25 && xVel > 0) {
            xVel = 0;
        } if(xVel < 0) {
            newDeg -= -xVel;
        } else if(xVel > 0) {
            newDeg += xVel;
        }
        newX += xVel;
        if(!playerOnGround) {
            newY += GRAVITY;
            GRAVITY += 0.5;
        } else {
            GRAVITY = 1;
        }
        if(player.getPosition()[0] + newX < 0 || player.getPosition()[0] < 0) {
            xVel = 0;
            player.update(0, (int)Math.round(newY), player.getSize()[0], player.getSize()[1], player.getDirectionDegrees() + newDeg, player.getImageName(), player.getWalkDir());
        } else {
            player.update((int)Math.round(newX), (int)Math.round(newY), player.getSize()[0], player.getSize()[1], player.getDirectionDegrees() + newDeg, player.getImageName(), player.getWalkDir());
        }
        //Handle Enemy Logic
        for(Enemy enemy : testEnemies) {
            if(enemy.getType().equals(Enemy.REGULAR)) {
                int nx = enemy.getPosition()[0];
                for(Platform plat : platforms) {
                    if(nx > plat.getPosition()[0] + plat.getSize()[0] && nx - 6.25 < plat.getPosition()[0] + plat.getSize()[0] && !(enemy.getPosition()[1] + enemy.getSize()[1] < plat.getPosition()[1])) {
                        enemy.walkDir = true;
                    } if(nx + enemy.getSize()[0] < plat.getPosition()[0] && nx + enemy.getSize()[0] + 6.25 > plat.getPosition()[0] && !(enemy.getPosition()[1] + enemy.getSize()[1] < plat.getPosition()[1])) {
                        enemy.walkDir = false;
                    }
                }
                int newD = enemy.getDirectionDegrees();
                if(enemy.getWalkDir()) {
                    nx += 1.25;
                    newD += 2.5;
                } else {
                    nx -= 1.25;
                    newD -= 2.5;
                }
                enemy.update(nx, enemy.getPosition()[1], enemy.getSize()[0], enemy.getSize()[1], newD, enemy.getImageName(), enemy.getWalkDir());
            } else if(enemy.getType().equals(Enemy.DASH)) {
                int nx = enemy.getPosition()[0];
                for(Platform plat : platforms) {
                    if(nx > plat.getPosition()[0] + plat.getSize()[0] && nx - 6.25 < plat.getPosition()[0] + plat.getSize()[0] && !(enemy.getPosition()[1] + enemy.getSize()[1] < plat.getPosition()[1])) {
                        enemy.walkDir = true;
                    } if(nx + enemy.getSize()[0] < plat.getPosition()[0] && nx + enemy.getSize()[0] + 6.25 > plat.getPosition()[0] && !(enemy.getPosition()[1] + enemy.getSize()[1] < plat.getPosition()[1])) {
                        enemy.walkDir = false;
                    }
                }
                int newD = enemy.getDirectionDegrees();
                if(enemy.getWalkDir()) {
                    if(player.getPosition()[0] > enemy.getPosition()[0]) {
                        nx += 2.5;
                        newD += 5;
                    } else {
                        nx += 1.25;
                        newD += 2.5;
                    }
                } else {
                    if(player.getPosition()[0] < enemy.getPosition()[0]) {
                        nx -= 2.5;
                        newD -= 5;
                    } else {
                        nx -= 1.25;
                        newD -= 2.5;
                    }
                }
                enemy.update(nx, enemy.getPosition()[1], enemy.getSize()[0], enemy.getSize()[1], newD, enemy.getImageName(), enemy.getWalkDir());
            }
        }
    }
    public void checkHitboxCollision(Player player, EnemyHitbox hitbox) {
        if(hitbox == null) return;
        int playerX = player.getPosition()[0];
        int playerY = player.getPosition()[1];
        int playerW = player.getSize()[0];
        int playerH = player.getSize()[1];
        int hitboxX = hitbox.getPosition()[0];
        int hitboxY = hitbox.getPosition()[1];
        int hitboxW = hitbox.getSize()[0];
        if(playerX + playerW > hitboxX && playerX < hitboxX + hitboxW && playerY + playerH > hitboxY) {
            hitbox.getAttachedEnemy().destroy();
            GRAVITY = -14;
        }
    }
    public void checkHurtboxCollision(Player player, EnemyHurtbox hurtbox) {
        if(hurtbox == null) return;
        int playerX = player.getPosition()[0];
        int playerY = player.getPosition()[1];
        int playerW = player.getSize()[0];
        int playerH = player.getSize()[1];
        int hurtboxX = hurtbox.getPosition()[0];
        int hurtboxY = hurtbox.getPosition()[1];
        int hurtboxW = hurtbox.getSize()[0];
        if(playerX + playerW > hurtboxX && playerX < hurtboxX + hurtboxW && playerY + playerH > hurtboxY && !isPlayerInv) {
            player.setHealth(player.getHealth() - 1);
            isPlayerInv = true;
            isPlayerHit = true;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500L);
                    } catch(InterruptedException ie) {}
                    isPlayerInv = false;
                    return;
                }
            });
            t.start();
        }
    }
}
/**Super Class For All Objects That Exist In The World (Besides UI Related Stuff) 
 * @author IDEKanymoreTBH On Github
*/
class GameObject {
    /**The X Position Of An Object */
    protected int x;
    /**The Y Position Of An Object */
    protected int y;
    /**The Width Of An Object */
    protected int width;
    /**The Height Of An Object */
    protected int height;
    /**The Direction (In Degrees) An Object Is At */
    protected int direction;
    /**The Name Of The File Where The Image Is Made */
    protected String fileName;
    /**The Direction The Object Is Walking In. Not Applicable To All Objects. True Is Right, False Is Left */
    protected boolean walkDir;
    /**Is The Walking Direction Variable Not Allocated (Because The Constructor Did Not Call It) */
    protected boolean isWalkDirUninitialized;
    /**
     * Constructs A Game Object With Position, Size, And Other Attributes. Use This For Non-Moving Objects
     * @param x The X Position
     * @param y The Y Position
     * @param width The Width
     * @param height The Height
     * @param direction The Direction (In Degrees)
     * @param fileName The Name Of The Image File
     */
    protected GameObject(int x, int y, int width, int height, int direction, String fileName) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
        while(this.direction > 360) {
            this.direction -= 360;
        }
        this.fileName = fileName;
        this.isWalkDirUninitialized = true;
    }
    /**
     * Constructs A Game Object With Position, Size, And Other Attributes. Use This For Moving Objects.
     * @param x The X Position
     * @param y The Y Position
     * @param width The Width
     * @param height The Height
     * @param direction The Direction (In Degrees) Of The Object
     * @param fileName The Image File's Name
     * @param walkDir The Direction To Walk In. True For Right, False For Left.
     */
    protected GameObject(int x, int y, int width, int height, int direction, String fileName, boolean walkDir) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
        while(this.direction > 360) {
            this.direction -= 360;
        }
        this.fileName = fileName;
        this.walkDir = walkDir;
        this.isWalkDirUninitialized = false;
    }
    /**
     * Returns An Array That Acts As A Coordinate Pair. First Element Is X, Second Element Is Y
     * @return The Coordinate Pair
     */
    protected int[] getPosition() {
        return new int[]{this.x, this.y};
    }
    /**
     * Returns The Image Name Of This Object
     * @return The Image Name
     */
    protected String getImageName() {
        return this.fileName;
    }
    /**
     * Returns The Direction (In Degrees) Of The Object
     * @return The Direction
     */
    protected int getDirectionDegrees() {
        return this.direction;
    }
    /**
     * Returns An Array That Goes (Width, Height) For This Object. Usually Both Are The Same
     * @return The Pair Of Values
     */
    protected int[] getSize() {
        return new int[]{this.width, this.height};
    }
    /**
     * Gets The Walking Direction Of An Object
     * @return The Walking Direction, True If Right, False If Left.
     * @throws NullPointerException If This Object Was Not Initialized With A Walking Direction
     */
    protected boolean getWalkDir() throws NullPointerException {
        if(isWalkDirUninitialized) {
            throw new NullPointerException("Error: This Class Does Not Initialize A WalkDir Value.");
        }
        return this.walkDir;
    }
    /**
     * Updates All Values In The Object
     * @param x The New X Position
     * @param y The New Y Position
     * @param width The New Width
     * @param height The New Height
     * @param direction The New Direction (In Degrees)
     * @param fileName The New File Name
     * @param walkDir The New Walking Direction (For Moving Objects)
     */
    protected void updateAll(int x, int y, int width, int height, int direction, String fileName, boolean walkDir) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.fileName = fileName;
        if(this.isWalkDirUninitialized) {
            throw new NullPointerException("This Class Did Not Initialize The WalkDir Value.");
        }
        this.walkDir = walkDir;
    }
    /**
     * The Same As {@code updateAll(x, y, width, height, direction, fileName, walkDir)} But Without The Walking Direction.
     * Use This For Any Non-Moving Objects
     * @param x The New X Position
     * @param y The New Y Position
     * @param width The New Width
     * @param height The New Height
     * @param direction The New Direction (In Degrees)
     * @param fileName The New Image File Name
     */
    protected void updateAll(int x, int y, int width, int height, int direction, String fileName) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.fileName = fileName;
    }
}
/**The Class For A Player Object. Only Make One, Some Attributes Are Statically Kept In {@code Game.class}
 * @author IDEKanymoreTBH On Github
*/
class Player extends GameObject {
    /**The Amount Of Health The Player Has */
    private int health;
    /**The Amount Of Lives A Player Has */
    private int lives;
    /**
     * Constructs A Player. Do This Only Once
     * @param x The X Pos
     * @param y The Y Pos
     * @param width The Width
     * @param height The Height
     * @param direction The Direction (In Degrees)
     * @param fileName The File Name
     * @param walkDir The Walking Direction (Doesn't Matter As Of Now)
     */
    public Player(int x, int y, int width, int height, int direction, String fileName, boolean walkDir) {
        super(x, y, width, height, direction, fileName, walkDir);
        this.health = 3;
        this.lives = 5;
    }
    public int[] getPosition() {
        return super.getPosition();
    }
    public String getImageName() {
        return super.getImageName();
    }
    public int getDirectionDegrees() {
        return super.getDirectionDegrees();
    }
    public int[] getSize() {
        return super.getSize();
    }
    public boolean getWalkDir() {
        return super.getWalkDir();
    }
    /**
     * Updates The Player Object. Calls {@code GameObject.updateAll(x, y, width, height, direction, filename, walkDir)}.
     * @param newX The New X Position
     * @param newY The New Y Position
     * @param newW The New Width
     * @param newH The New Height
     * @param newDir The New Direction (In Degrees)
     * @param newFileName The New File Name
     * @param newWalkDir The New Walking Direction
     */
    public void update(int newX, int newY, int newW, int newH, int newDir, String newFileName, boolean newWalkDir) {
        super.updateAll(newX, newY, newW, newH, newDir, newFileName, newWalkDir);
    }
    /**
     * Sets The Health Of The Player
     * @param newHealth The New Health Value
     */
    public void setHealth(int newHealth) {
        this.health = newHealth;
    }
    /**
     * Gets The Player's Current Health
     * @return The Health Of The Player
     */
    public int getHealth() {
        return this.health;
    }
    /**
     * Sets The Amount Of Lives The Player Has
     * @param newLives The New Amount Of Lives
     */
    public void setLives(int newLives) {
        this.lives = newLives;
    }
    /**
     * Gets The Amoutn Of Lives The Player Has
     * @return The Amount Of Lives
     */
    public int getLives() {
        return this.lives;
    }
    /**Resets The Players Health And Value, Symbolizing The Loss Of A Live.*/
    public void reset() {
        this.health = 3;
        this.lives--;
    }
}
/**The Class For A Platform
 * @author IDEKanymoreTBH On Github
*/
class Platform extends GameObject {
    /**
     * Constructs A Platform With A Position, Size, And Other Stuff
     * @param x The X Position For The Platform
     * @param y The Y Position Of The Platform
     * @param width The Width Of The Platform
     * @param height The Height Of The Platform
     * @param direction The Direction (In Degrees) Of The Platform
     * @param fileName The Name Of The Image File
     */
    public Platform(int x, int y, int width, int height, int direction, String fileName) {
        super(x, y, width, height, direction, fileName);
    }
    public int[] getPosition() {
        return super.getPosition();
    }
    public String getImageName() {
        return super.getImageName();
    }
    public int getDirectionDegrees() {
        return super.getDirectionDegrees();
    }
    public int[] getSize() {
        return super.getSize();
    }
    /**
     * Updates This Platform. Calls The {@code GameObject.updateAll()} Function.
     * @param nx The New X Position
     * @param ny The New Y Position
     * @param nw The New Width
     * @param nh The New Height
     * @param nd The New Direction
     * @param nfName The New File Name
     */
    public void update(int nx, int ny, int nw, int nh, int nd, String nfName) {
        super.updateAll(nx, ny, nw, nh, nd, nfName);
    }
}
/**The Class Of An Enemy
 * @author IDEKanymoreTBH On Github
*/
class Enemy extends GameObject {
    /**The Representation Of A Regular Enemy */
    public static String REGULAR = "1";
    /**The Representation Of A Dashing Enemy */
    public static String DASH = "2";
    /**The Representation Of The Special Enemy */
    public static String MATRIX = "3";
    /**The Representation Of The Final Boss Enemy */
    public static String FINALBOSS = "4";
    /**The Hitbox Of The Enemy */
    private EnemyHitbox hitbox;
    /**The Hurtbox Of The Enemy */
    private EnemyHurtbox hurtbox;
    /**The Type Of The Enemy */
    private String type;
    /**
     * Constructs An Enemy With A Position, Size, Collision, And Other Attributes
     * @param x The X Position Of The Enemy
     * @param y The Y Position Of The Enemy
     * @param width The Width Of The Enemy
     * @param height The Height Of The Enemy
     * @param direction The Direction (In Degrees) That The Enemy Is In.
     * @param fileName The Image File's Name
     * @param walkDir The Walking Direction Of The Enemy
     * @param type The Type Of The Enemy
     * @see Enemy#REGULAR
     * @see Enemy#DASH
     * @see Enemy#MATRIX
     * @see Enemy#FINALBOSS
     */
    public Enemy(int x, int y, int width, int height, int direction, String fileName, boolean walkDir, String type) {
        super(x, y, width, height, direction, fileName, walkDir);
        this.hitbox = new EnemyHitbox(this, "null");
        this.hurtbox = new EnemyHurtbox(this, "null");
        this.type = type;
    }
    public int[] getPosition() {
        return super.getPosition();
    }
    public String getImageName() {
        return super.getImageName();
    }
    public int getDirectionDegrees() {
        return super.getDirectionDegrees();
    }
    public int[] getSize() {
        return super.getSize();
    }
    public boolean getWalkDir() {
        return super.getWalkDir();
    }
    public void update(int nx, int ny, int nw, int nh, int nd, String nfName, boolean nwalkDir) {
        super.updateAll(nx, ny, nw, nh, nd, nfName, nwalkDir);
        this.hitbox.update(super.getPosition()[0] + 5, super.getPosition()[1] - 5, super.getSize()[0] - 10, 5, super.getDirectionDegrees(), "null");
        this.hurtbox.update(super.getPosition()[0], super.getPosition()[1], super.getSize()[0], super.getSize()[1], super.getDirectionDegrees(), "null");
    }
    public EnemyHitbox getHitbox() {
        return this.hitbox;
    }
    public EnemyHurtbox getHurtbox() {
        return this.hurtbox;
    }
    public void destroy() {
        for(int i = 0; i < Game.testEnemies.length; i++) {
            if(Game.testEnemies[i] == this) {
                //Takes The Array, Makes A Stream, Filters Out Object That Are This Object, Makes A List, Then Fills A New Array With The List Items.
                Game.testEnemies = Arrays.stream(Game.testEnemies).filter(val -> val != this).toList().toArray(Enemy[]::new);
            }
            //So The Hitboxes Don't Show Up
            this.hitbox = null;
            this.hurtbox = null;
        }
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }
}
class EnemyHitbox extends GameObject {
    private Enemy enemyToFollow;
    public EnemyHitbox(Enemy enemyToFollow, String fileName) {
        super(enemyToFollow.getPosition()[0] + 5, enemyToFollow.getPosition()[1] - 5, enemyToFollow.getSize()[0] - 10, 5, enemyToFollow.getDirectionDegrees(), fileName);
        this.enemyToFollow = enemyToFollow;
    }
    public int[] getPosition() {
        return super.getPosition();
    }
    public String getImageName() {
        return super.getImageName();
    }
    public int getDirectionDegrees() {
        return super.getDirectionDegrees();
    }
    public int[] getSize() {
        return super.getSize();
    }
    public void update(int nx, int ny, int nw, int nh, int nd, String nfName) {
        super.updateAll(nx, ny, nw, nh, nd, nfName);
    }
    public Enemy getAttachedEnemy() {
        return this.enemyToFollow;
    }
}
class EnemyHurtbox extends GameObject {
    private Enemy enemyToFollow;
    public EnemyHurtbox(Enemy enemy, String fileName) {
        super(enemy.getPosition()[0], enemy.getPosition()[1], enemy.getSize()[0], enemy.getSize()[1], enemy.getDirectionDegrees(), fileName);
        this.enemyToFollow = enemy;
    }
    public int[] getPosition() {
        return super.getPosition();
    }
    public String getImageName() {
        return super.getImageName();
    }
    public int getDirectionDegrees() {
        return super.getDirectionDegrees();
    }
    public int[] getSize() {
        return super.getSize();
    }
    public void update(int nx, int ny, int nw, int nh, int nd, String nfName) {
        super.updateAll(nx, ny, nw, nh, nd, nfName);
    }
    public Enemy getAttachedEnemy() {
        return this.enemyToFollow;
    }
}
class Boss extends GameObject {
    private BossHitbox hitbox;
    private BossHurtbox hurtbox;
    public Boss(int x, int y, int w, int h, int initDir, String filename) {
        super(x, y, w, h, initDir, filename);
        this.hitbox = new BossHitbox(this, "null");
    }
    public int[] getPosition() {
        return super.getPosition();
    }
    public int[] getSize() {
        return super.getSize();
    }
    public int getDirectionDegrees() {
        return super.getDirectionDegrees();
    }
    public String getImageName() {
        return super.getImageName();
    }
    public boolean getWalkDir() {
        return super.getWalkDir();
    }
    public void update(int nx, int ny, int nw, int nh, int nd, String nfName, boolean nWd) {
        super.updateAll(nx, ny, nw, nh, nd, nfName, nWd);
    }
}
class BossHitbox extends GameObject {
    private Boss bossToFollow;
    public BossHitbox(Boss bossToFollow, String filename) {
        super(bossToFollow.getPosition()[0], bossToFollow.getPosition()[1], bossToFollow.getSize()[0], bossToFollow.getSize()[1], bossToFollow.getDirectionDegrees(), filename);
        this.bossToFollow = bossToFollow;
    }
    public int[] getPosition() {
        return super.getPosition();
    }
    public int[] getSize() {
        return super.getSize();
    }
    public int getDirectionDegrees() {
        return super.getDirectionDegrees();
    }
    public String getImageName() {
        return super.getImageName();
    }
    public Boss getAttachedBoss() {
        return this.bossToFollow;
    }
    public void update(int nx, int ny, int nw, int nh, int nd, String nfName) {
        super.updateAll(nx, ny, nw, nh, nd, nfName);
    }
}
class BossHurtbox extends GameObject {
    private Boss bossToFollow;
    public BossHurtbox(Boss bossToFollow, String filename) {
        super(bossToFollow.getPosition()[0], bossToFollow.getPosition()[1], bossToFollow.getSize()[0], bossToFollow.getSize()[1], bossToFollow.getDirectionDegrees(), filename);
        this.bossToFollow = bossToFollow;
    }
    public int[] getPosition() {
        return super.getPosition();
    }
    public int[] getSize() {
        return super.getSize();
    }
    public int getDirectionDegrees() {
        return super.getDirectionDegrees();
    }
    public String getImageName() {
        return super.getImageName();
    }
    public Boss getAttachedBoss() {
        return this.bossToFollow;
    }
    public void update(int nx, int ny, int nw, int nh, int nd, String nfName) {
        super.updateAll(nx, ny, nw, nh, nd, nfName);
    }
}
/**Represents The Boss Bar That Appears With A Boss */
class BossBar {
    /**The X Position Of The Boss Bar*/
    private int x;
    /**The Y Position Of The Boss Bar */
    private int y;
    /**The Width Of The Boss Bar */
    private int w;
    /**The Height Of The Boss Bar */
    private int h;
    /**The Amount Of Health In The Boss Bar */
    private int amtOfHealth;
    /**
     * Constructs A Boss Bar With A Specific Position And Health
     * @param x The X Position
     * @param y The Y Position
     * @param w The Width Of The Bar
     * @param h The Height Of The Bar
     * @param amtOfHealth The Amount Of Health
     */
    public BossBar(int x, int y, int w, int h, int amtOfHealth) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.amtOfHealth = amtOfHealth;
    }
    /**
     * Gets A Specific Property
     * @param propertyName The Name Of The Property
     * @return The Amount Listed For That Property.
     */
    public int getProperty(String propertyName) {
        return switch(propertyName) {
            case "Bar.xPos" -> this.x;
            case "Bar.yPos" -> this.y;
            case "Bar.Width" -> this.w;
            case "Bar.Height" -> this.h;
            case "Bar.Health" -> this.amtOfHealth;
            default -> -1;
        };
    }
    /**
     * Sets A Specific Property
     * @param propertyName The Name Of The Property
     * @param content The Content To Set For That Property
     */
    public void setProperty(String propertyName, int content) {
        switch(propertyName) {
            case "Bar.xPos":
                this.x = content;
                break;
            case "Bar.yPos":
                this.y = content;
                break;
            case "Bar.Width":
                this.w = content;
                break;
            case "Bar.Height":
                this.h = content;
                break;
            case "Bar.Health":
                this.amtOfHealth = content;
                break;
            default:
                return;
        }
    }
}
/**This Class Represents An Achievement. An Achievement Can Either Be Achieved Or Not. They All Have A Title
 * And Description.
 * @author IDEKanymoreTBH On Github
 * @see Achievement#Achievement(String, String)
*/
class Achievement {
    /**The Title For The Achievement */
    private String title;
    /**The Description Of The Achievement */
    private String desc;
    /**If The Achievement Is Done */
    private boolean done;
    /**
     * Constructs An Achievement With A Title And Description
     * @param title The Title Of The Achievement
     * @param desc The Description Of The Achievement
     */
    public Achievement(String title, String desc) {
        this.title = title;
        this.desc = desc;
        this.done = false;
    }
    /**
     * Gets The Title Of A Specific Achievement
     * @return The Title
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * Gets The Description Of A Specific Achievement
     * @return The Description
     */
    public String getDescription() {
        return this.desc;
    }
    /**
     * Sets If The Achievement Is Done
     * @param done A Boolean Indicating If The Achievement Is Done
     */
    public void setAchieved(boolean done) {
        this.done = done;
    }
    /**
     * Gets The Achieved Status Of The Achievement
     * @return A Boolean Indicating If The Achievement Is Done
     */
    public boolean getAchieved() {
        return this.done;
    }
    /**Returns A String That Describes The Achievement Well
     * @returns A String That Tells The Title And Description
    */
    @Override
    public String toString() {
        return String.format("Achievement: %s; Description: %s;", this.title, this.desc);
    }
}