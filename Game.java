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
public class Game extends JPanel implements KeyListener, MouseListener{
    public static String GLOBAL_TEXTURE_SHEET = "Images/AssetSheet.png";
    public HashMap<String, Boolean> keys = new HashMap<>();
    public double GRAVITY = 2;
    public double xVel = 0;
    public Timer timer;
    public JFrame frame = new JFrame("Red Ball 4: Remake");
    public Platform[] platforms = {new Platform(0, 907, 875, 50, 0, "null"), new Platform(875, 907, 600, 50, 0, "null")};
    public Player player = new Player(25, 200, 50, 50, 0, "player", true);
    public static Enemy[] testEnemies = {new Enemy(600, 857, 50, 50, 0, "enemy", true)};
    public static Graphics2D g2d;
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
            }
            //checkHurtboxCollision(player, testEnemy.getHurtbox());
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
            if(player.getPosition()[1] + player.getSize()[1] >= plat.getPosition()[1] && player.getPosition()[1] < plat.getPosition()[1] + plat.getSize()[1]) {
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
            g2d.dispose();
            //g.drawImage(ImageRetriever.rotateImage(playerImage, player.getDirectionDegrees()), player.getPosition()[0], player.getPosition()[1] + 10, null);
        }
        //Handles Enemy Sprite
        for(Enemy enemy : testEnemies) {
            if(enemy.getImageName().equals("null") || false) {
                g.setColor(Color.GRAY);
                g.fillRect(enemy.getPosition()[0], enemy.getPosition()[1], enemy.getSize()[0], enemy.getSize()[1]);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(enemy.getPosition()[0], enemy.getPosition()[1], enemy.getSize()[0], enemy.getSize()[1]);
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
        //Handles Movement Logic
        double newX = player.getPosition()[0];
        double newY = player.getPosition()[1];
        int newDeg = 0;
        if(keys.containsKey("Keys.W") && keys.get("Keys.W") && playerOnGround) {
            GRAVITY = -14;
            playerOnGround = false;
        } if(keys.containsKey("Keys.A") && keys.get("Keys.A")) {
            xVel -= 0.25;
            xVel = Math.clamp(xVel, -6, 6);
        } if(keys.containsKey("Keys.D") && keys.get("Keys.D")) {
            xVel += 0.25;
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
        }
        player.update((int)Math.round(newX), (int)Math.round(newY), player.getSize()[0], player.getSize()[1], player.getDirectionDegrees() + newDeg, player.getImageName(), player.getWalkDir());
    }
    public void checkHitboxCollision(Player player, EnemyHitbox hitbox) {
        int playerX = player.getPosition()[0];
        int playerY = player.getPosition()[1];
        int playerW = player.getSize()[0];
        int playerH = player.getSize()[1];
        int hitboxX = hitbox.getPosition()[0];
        int hitboxY = hitbox.getPosition()[1];
        int hitboxW = hitbox.getSize()[0];
        if(playerX + playerW > hitboxX && playerX < hitboxX + hitboxW && playerY + playerH > hitboxY) {
            hitbox.getAttachedEnemy().destroy();
        }
    }
}
class GameObject {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int direction;
    protected String fileName;
    protected boolean walkDir;
    protected boolean isWalkDirUninitialized;
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
    protected int[] getPosition() {
        return new int[]{this.x, this.y};
    }
    protected String getImageName() {
        return this.fileName;
    }
    protected int getDirectionDegrees() {
        return this.direction;
    }
    protected int[] getSize() {
        return new int[]{this.width, this.height};
    }
    protected boolean getWalkDir() throws NullPointerException {
        if(isWalkDirUninitialized) {
            throw new NullPointerException("Error: This Class Does Not Initialize A WalkDir Value.");
        }
        return this.walkDir;
    }
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
    protected void updateAll(int x, int y, int width, int height, int direction, String fileName) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.fileName = fileName;
    }
}
class Player extends GameObject {
    public Player(int x, int y, int width, int height, int direction, String fileName, boolean walkDir) {
        super(x, y, width, height, direction, fileName, walkDir);
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
    public void update(int newX, int newY, int newW, int newH, int newDir, String newFileName, boolean newWalkDir) {
        super.updateAll(newX, newY, newW, newH, newDir, newFileName, newWalkDir);
    }
}
class Platform extends GameObject {
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
    public void update(int nx, int ny, int nw, int nh, int nd, String nfName) {
        super.updateAll(nx, ny, nw, nh, nd, nfName);
    }
}
class Enemy extends GameObject {
    private EnemyHitbox hitbox;
    private EnemyHurtbox hurtbox;
    public Enemy(int x, int y, int width, int height, int direction, String fileName, boolean walkDir) {
        super(x, y, width, height, direction, fileName, walkDir);
        this.hitbox = new EnemyHitbox(this, "null");
        this.hurtbox = new EnemyHurtbox(this, "null");
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
        hitbox.update(super.getPosition()[0], super.getPosition()[1], super.getSize()[0], super.getPosition()[1], super.getDirectionDegrees(), "null");
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