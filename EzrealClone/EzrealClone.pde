// import processing.core.PApplet;
// import processing.core.PImage;
// import processing.core.PGraphics;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import java.io.*;
// import java.lang.String;

//Eric Ngor, 2016-2017 Side project
// Edited in 2022 to work with HTML embedding via processing.js
// public class Ezreal extends PApplet {
    private PImage cursorImage;
    private PImage cursorAlpha;
    private PImage cursor2;
    private PImage cursorAttack;
    private PImage cursorAttackAlpha;
    private Box box;
    private SpaceShip ship;
    private List<Marker> markers;
    private DestLine destLine;
    private float xDest;
    private float yDest;
    private double direction;
    private double dRadians;
    private boolean moving;
    private double speed;
    private boolean targetting;
    private boolean towardsTarget;

    //Attacking
    private final int ATTACK_RANGE = 198;
    private boolean attacking;
    private double attackSpeed;
    private boolean attackReady;
    private boolean inRange;
    private long ba_timer;
    private boolean updateMove;
    private List<ezAA> ezAAList;

    //Casting
    //     private int cdr;
    private boolean casting;
    private long castTimeStart;
    private float aimLocX;
    private float aimLocY;
    //Rising Spell Force

    //Mystic Shot
    private List<MysticShotProjectile> q_mis;
    private boolean mysticShot;
    //Arcane Shift
    private final int ARCANESHIFT_RANGE = 171;
    private final int ARCANE_PROJECTILE_RANGE = 270;
    private double eCooldown;
    private boolean arcaneShift;
    private float arcaneX;
    private float arcaneY;
    private List<ArcaneLandingParticles> arcaneParticles;
    private List<ArcaneProjectile> arcaneProjectile;
    private List<ArcaneProjectileImpact> arcaneImpact; 
    private File arcaneShiftFile;
    private Clip arcaneShiftSound;
    private File arcaneImpactFile;
    private Clip arcaneImpactSound;

    //Impact Particles
    private PImage flare;
    private PImage flareAlpha;
    private PImage redLight;
    private PImage redLightAlpha;

    //Flash
    private final int FLASH_RANGE = 144;
    private File flash1File;
    private Clip flash1Sound;
    private File flash2File;
    private Clip flash2Sound;
    private PImage flash;
    private PImage flashAlpha;
    private boolean flashParticles;
    private boolean directionFlag;
    private int flashOpacity;
    private float flashX1;
    private float flashY1;
    private float flashX2;
    private float flashY2;

    private Dummy dummy;
    private boolean reachedDest;
    private float timer = 0f;
    // String dataPath = dataPath
    // public static void main(String[] args) {
    //     PApplet.main(new String[] {"Ezreal"});
    // }

    public void setup() {
        size(1400,900);
        xDest = width / 2;
        yDest = height / 2;
        box = new Box(this);
        ship = new SpaceShip(this);
        speed = 3.25;
        cursorImage = loadImage("data/cursor.png");
        cursorAlpha = loadImage("data/alphaAlpha.png");
        cursorImage.mask(cursorAlpha);
        cursorAttack = loadImage("data/cursorAttack.png");
        // cursorAttackAlpha = loadImage("cursorAttackAlpha.png");
        // cursorAttack.mask(cursorAttackAlpha);
        markers = new ArrayList<Marker>();
        destLine = new DestLine(this);
        dummy = new Dummy(this);

        //Auto Attack
        ezAAList = new ArrayList<ezAA>();
        attackSpeed = 1.2;
        attackReady = true;

        //Mystic Shot
        q_mis = new ArrayList<MysticShotProjectile>();
        // System.out.println(new File(".").getAbsolutePath());
        // System.out.println(dataPath(""));

        //Landing
        arcaneParticles = new ArrayList<ArcaneLandingParticles>();
        // arcaneShiftFile = new File(sketchPath(""), "arcaneShift.wav");
        // // System.out.println(arcaneShiftFile.exists());
        // try {
        //     AudioInputStream fileIn = AudioSystem.getAudioInputStream(arcaneShiftFile);
        //     arcaneShiftSound = AudioSystem.getClip();
        //     arcaneShiftSound.open(fileIn);
        // }
        // catch (Exception e) {
        //     System.out.println("Error loading file; " + e);
        // }

        //Projectile impact
        arcaneProjectile = new ArrayList<ArcaneProjectile>();
        arcaneImpact = new ArrayList<ArcaneProjectileImpact>();
        // arcaneImpactFile = new File(sketchPath(""), "arcaneImpact.wav");
        // arcaneImpactFile = new SoundFile(this, "arcaneImpact.wav");
        // try {
        //     AudioInputStream fileIn = AudioSystem.getAudioInputStream(arcaneImpactFile);
        //     arcaneImpactSound = AudioSystem.getClip();
        //     arcaneImpactSound.open(fileIn);
        // }
        // catch (Exception e) {
        //     System.out.println("Error loading file; " + e);
        // }

        //Flash
        // flash1File = new File(sketchPath(""), "flash1.wav");
        // try {
        //     AudioInputStream fileIn = AudioSystem.getAudioInputStream(flash1File);
        //     flash1Sound = AudioSystem.getClip();
        //     flash1Sound.open(fileIn);
        // }
        // catch (Exception e) {
        //     System.out.println("Error loading file; " + e);
        // }
        // flash2File = new File(sketchPath(""), "flash2.wav");
        // try {
        //     AudioInputStream fileIn = AudioSystem.getAudioInputStream(flash2File);
        //     flash2Sound = AudioSystem.getClip();
        //     flash2Sound.open(fileIn);
        // }
        // catch (Exception e) {
        //     System.out.println("Error loading file; " + e);
        // }
        flash = loadImage("data/flash.png");
        // flashAlpha = loadImage("flashAlpha.png");
        // flash.mask(flashAlpha);
    }

    public void draw() {
        background(0);
        ship.show();
        ship.move();
        //Cursor
        if (dist(mouseX, mouseY, dummy.getX(), dummy.getY()) < 30) {
            cursor(cursorAttack, 0, 0);
            dummy.target();
            targetting = true;
        }
        else {
            cursor(cursorImage, 0, 0);
            dummy.untarget();
            targetting = false;
        }

        if (towardsTarget && updateMove && !casting) {
            xDest = dummy.getX();
            yDest = dummy.getY();
            moveToDest();
        }

        //Set when in attacking range 
        if (towardsTarget && dist(dummy.getX(), dummy.getY(), ship.getX(), ship.getY()) < ATTACK_RANGE) {
            attacking = true;
            inRange = true;
        }
        if (dist(dummy.getX(), dummy.getY(), ship.getX(), ship.getY()) > ATTACK_RANGE) {
            inRange = false;
        }

        //AA
        if (attacking && inRange) {
            attack();
        }

        //AA Timer
        if (timer - ba_timer > 750 / attackSpeed) {
            updateMove = true;
        }
        else {
            updateMove = false;
        }
        if (timer - ba_timer > 1000 / attackSpeed) {
            attackReady = true;
        }

        dummy.show();
        dummy.move();
        dummy.setX(dummy.getX());
        direction = ((xDest < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - yDest) / (ship.getX() - xDest)));

        //Reach destination
        if (dist(ship.getX(), ship.getY(), xDest, yDest) < 7) {
            stopMoving();
            moving = false;
        }
        else if (!casting && moving) {
            moveToDest();
        }

        //Show marker
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).show(ship, xDest, yDest);
            markers.get(i).decrementOpacity();
            if (markers.get(i).getOpacity() < 0) {
                markers.remove(i);
                i--;
            }
        }

        //Show destLine
        if (destLine.getOpacity() > 0) {
            destLine.show(ship, xDest, yDest, towardsTarget);
        }
        destLine.decrementOpacity();

        //Stop moving when attacking
        if (attacking && inRange) {
            towardsTarget = true;
            stopMoving();
        }

        //Casting
        if (casting) {
            stopMoving();
            fill(41, 171, 210);
            strokeWeight(3);
            stroke(0, 153, 153);
            if (timer - castTimeStart <= 250) {
                rect((width / 2) - 125, 800, timer - castTimeStart, 10); // Casting time bar
            }
            stroke(255);
            strokeWeight(1);
            stroke(82, 50, 53);
            noFill();
            rect((width / 2) - 125, 800, 250, 10);
            fill(255);
            if (timer - castTimeStart < 250) { // Cast time display
                textSize(32);
                textAlign(CENTER);
                if (arcaneShift) {
                    text("Arcane Shift", width / 2, 750);
                }
                if (mysticShot) {
                    text("Mystic Shot", width / 2, 750);
                }
            }
            else { // Cast Spell
                if (arcaneShift) {
                    //Teleport
                    if (!(arcaneX == ship.getX() && arcaneY == ship.getY())) { //Do nothing on spot
                        arcaneLandingParticles((int)ship.getX(), (int)ship.getY());
                        ship.setPointDirection(((arcaneX < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - arcaneY) / (ship.getX() - arcaneX))));
                        ship.setX((int)arcaneX);
                        ship.setY((int)arcaneY);
                    }

                    arcaneLandingParticles((int)arcaneX, (int)arcaneY);
                    //Continue moving to Dest
                    if (xDest != arcaneX && yDest != arcaneY && moving) {
                        moveToDest();
                        destLine.resetOpacity();
                        markers.add(new Marker(this, xDest, yDest, true));

                        destLine.show(ship, xDest, yDest, towardsTarget);
                    }

                    //If in range, fire projectile
                    if (dist(ship.getX(), ship.getY(), dummy.getX(), dummy.getY()) < ARCANE_PROJECTILE_RANGE) {
                        arcaneProjectile.add(new ArcaneProjectile(this, ship, dummy));
                        if (attackReady) {
                            ba_timer = timer - 1000;
                            attackReady = false;
                        }
                    }

                    playArcaneShift();
                    arcaneShift = false;

                }
                if (mysticShot && aimLocX >= 0 && aimLocY >= 0) {
                    q_mis.add(new MysticShotProjectile(this, ship, aimLocX, aimLocY));
                    q_mis.get(q_mis.size() - 1).setStart(timer);
                    if (attackReady) {
                        ba_timer = timer - 1000;
                        attackReady = false;
                    }
                    mysticShot = false;
                    aimLocX = -1, aimLocY = -1;
                }
                casting = false;
            }

        }

        //Mystic Shot Disp
        for (int i = 0; i < q_mis.size(); i++) {
            q_mis.get(i).show();
            q_mis.get(i).move();
            if (timer - q_mis.get(i).getStart() > 150) {
                q_mis.get(i).fade();
            }
            if (dist(q_mis.get(i).getX(), q_mis.get(i).getY(), dummy.getX(), dummy.getY()) < 15 + dummy.getRadius()) {
                q_mis.remove(i);
                i--;
                //Temp
                arcaneImpact.add(new ArcaneProjectileImpact(this, dummy.getX(), dummy.getY()));
                playArcaneImpact();
            }
            else if (q_mis.get(i).getOpacity() <= 0) {
                q_mis.remove(i);
                i--;
            }
        }

        //Arcane Shift Landing Particles
        for (int i = 0; i < arcaneParticles.size(); i++) {
            if (arcaneParticles.get(i).despawn()) {
                arcaneParticles.remove(i);
                i--;
                continue;
            }
            arcaneParticles.get(i).show();
            arcaneParticles.get(i).move();
        }

        //Arcane Projectiles
        for (int i = 0; i < arcaneProjectile.size(); i++) {
            //Show Arcane Projectile
            arcaneProjectile.get(i).show();
            arcaneProjectile.get(i).move();
            arcaneProjectile.get(i).homing(dummy);

            //Arcane Particle Collision
            if (dist(arcaneProjectile.get(i).getX(), arcaneProjectile.get(i).getY(), dummy.getX(), dummy.getY()) < 8) {
                arcaneProjectile.remove(i);
                i--;
                arcaneImpact.add(new ArcaneProjectileImpact(this, dummy.getX(), dummy.getY()));
                playArcaneImpact();
            }
        }

        //Arcane Impact Particles
        for (int i = 0; i < arcaneImpact.size(); i++) {
            arcaneImpact.get(i).show();
            if (arcaneImpact.get(i).despawn()) {
                arcaneImpact.remove(i);
                i--;
            }
        }

        //Flash Disp
        tint(255, flashOpacity);
        image(flash, flashX1 - 64, flashY1 - 64);
        image(flash, flashX2 - 64, flashY2 - 64);
        if (flashParticles) {
            if (flashOpacity < 100 && !directionFlag) {
                flashOpacity += 33;
            }
            else if (flashOpacity > 0) {
                directionFlag = true;
                flashOpacity -= 3;
            }
            else {
                directionFlag = false;
                flashParticles = false;
            }
        }

        //Basic Attack
        tint(255, 255);
        //         rotate(radians(1));
        for (int i = 0; i < ezAAList.size(); i++) {
            ezAAList.get(i).show();
            ezAAList.get(i).move();
            ezAAList.get(i).homing(dummy);

            //BA Collision
            if (dist(ezAAList.get(i).getX(), ezAAList.get(i).getY(), dummy.getX(), dummy.getY()) < 10) {
                ezAAList.remove(i);
                i--;
                //temp impact
                arcaneImpact.add(new ArcaneProjectileImpact(this, dummy.getX(), dummy.getY()));
                playArcaneImpact();
            }
        }

        timer += 16 + (2f/3f); // 16.66667... ms/frame
        //         ellipse(width / 2, height / 2, 10, 10);
        //         if (ezAA.size() > 0) {
        //             fill(0, 0, 255);
        //             ellipse(ezAA.get(0).getX(), ezAA.get(0).getY(), 10, 10);
        //         }
        debug();
    }

    public void moveToDest() {
        if (xDest != ship.getX() && yDest != ship.getY()) {
            ship.setPointDirection(((xDest < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - yDest) / (ship.getX() - xDest))));
            dRadians = (ship.getPointDirection() * (Math.PI / 180));
            ship.setDirectionX(Math.cos(dRadians) * speed);
            ship.setDirectionY(Math.sin(dRadians) * speed);
        }
    }

    public void stopMoving() {
        ship.setDirectionX(0);
        ship.setDirectionY(0);
    }

    public void mousePressed() {
        //         if (!casting) {
        if (targetting) {
            xDest = dummy.getX();
            yDest = dummy.getY();
            towardsTarget = true;
        }
        else {
            xDest = mouseX;
            yDest = mouseY;
            attacking = false;
            towardsTarget = false;
            markers.add(new Marker(this, xDest, yDest, true));
        }

        destLine.resetOpacity();
        moving = true;
        if (!attacking && !casting) {
            moveToDest();
        }
        //         }
    }

    public void keyPressed() {
        if (!casting) {
            //Mystic Shot Cast
            if (key == 'q') {
                startCast();
                if (!(mouseX == ship.getX() && mouseY == ship.getY())) {
                    directionToMouse();
                }
                aimLocX = mouseX;
                aimLocY = mouseY;
                mysticShot = true;
            }
            //Arcane Shift Cast
            if (key == 'e') {
                startCast();
                if (!(mouseX == ship.getX() && mouseY == ship.getY())) {
                    directionToMouse();
                }
                dRadians = (ship.getPointDirection() * (Math.PI / 180));
                if (dist(ship.getX(), ship.getY(), mouseX, mouseY) < ARCANESHIFT_RANGE) {
                    arcaneX = mouseX;
                    arcaneY = mouseY;
                }
                else if (dist(ship.getX(), ship.getY(), mouseX, mouseY) >= ARCANESHIFT_RANGE) {
                    arcaneX = (float)((ARCANESHIFT_RANGE * Math.cos(dRadians)) + ship.getX()); 
                    arcaneY = (float)((ARCANESHIFT_RANGE * Math.sin(dRadians)) + ship.getY()); 
                }
                arcaneShift = true;
            }
        }
        if (key == 'd' || key == 'f') {
            if ((int)(Math.random() * 2) + 1 == 1) {
                playFlash1();
                // System.out.println("1");
            }
            else {
                playFlash2();
                // System.out.println("2");
            }

            flashX1 = ship.getX();
            flashY1 = ship.getY();
            //Direction
            if (!(mouseX == ship.getX() && mouseY == ship.getY())) {
                directionToMouse();
            }
            //Teleport Location
            dRadians = (ship.getPointDirection() * (Math.PI / 180));
            if (dist(ship.getX(), ship.getY(), mouseX, mouseY) < FLASH_RANGE) {
                ship.setX(mouseX);
                ship.setY(mouseY);
            }
            else if (dist(ship.getX(), ship.getY(), mouseX, mouseY) >= FLASH_RANGE) {
                ship.setX((float)((ARCANESHIFT_RANGE * Math.cos(dRadians)) + ship.getX())); 
                ship.setY((float)((ARCANESHIFT_RANGE * Math.sin(dRadians)) + ship.getY())); 
            }
            flashX2 = ship.getX();
            flashY2 = ship.getY();

            if (casting) {
                //                 if (mysticShot) {
                //                     ship.setPointDirection(((aimLocX < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - aimLocY) / (ship.getX() - aimLocY))));
                //                 }
                if (arcaneShift) {
                    if (dist(ship.getX(), ship.getY(), mouseX, mouseY) < ARCANESHIFT_RANGE) {
                        arcaneX = mouseX;
                        arcaneY = mouseY;
                    }
                    else if (dist(ship.getX(), ship.getY(), mouseX, mouseY) >= ARCANESHIFT_RANGE) {
                        arcaneX = (float)((ARCANESHIFT_RANGE * Math.cos(dRadians)) + ship.getX()); 
                        arcaneY = (float)((ARCANESHIFT_RANGE * Math.sin(dRadians)) + ship.getY()); 
                    }
                }
            }

            if (xDest != arcaneX && yDest != arcaneY && moving) {
                moveToDest();
                destLine.resetOpacity();
                markers.add(new Marker(this, xDest, yDest, true));
                destLine.show(ship, xDest, yDest, towardsTarget);
            }
            flashParticles = true;

            // Set aim direction if was casting during flash
            if (aimLocX >= 0 && aimLocY >= 0) {
                ship.setPointDirection(((aimLocX < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - aimLocY) / (ship.getX() - aimLocX))));
            }
        }
        if (key == 'x' || key == 'a') {
            xDest = dummy.getX();
            yDest = dummy.getY();
            markers.add(new Marker(this, mouseX, mouseY, false));
            towardsTarget = true;
            //below is for testing only
            //ezAA.add(new ezAA(this, ship, dummy));
        }
        if (key == 's') {
            stopMoving();
            moving = false;
        }
        // if (key == 'c') {
        //     ezAAList.get(0).setX(width / 2);
        //     ezAAList.get(0).setY(height / 2);
        // }
    }

    public void directionToMouse() {
        ship.setPointDirection(((mouseX < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - mouseY) / (ship.getX() - mouseX))));
    }

    public void startCast() {
        castTimeStart = timer;
        casting = true;
    }

    public void attack() {
        if (attackReady) {
            ezAAList.add(new ezAA(this, ship, dummy));
            ba_timer = timer;
            attackReady = false;
        }

    }

    public void arcaneLandingParticles(int xGrave, int yGrave) {
        for (int i = 0; i < (int)(Math.random() * 6) + 10; i++) {
            arcaneParticles.add(new ArcaneLandingParticles(this, xGrave, yGrave));
            arcaneParticles.get(arcaneParticles.size() - 1).setStartTime(timer);
        }
    }

    public void playArcaneShift() {
        // arcaneShiftSound.stop();
        // arcaneShiftSound.setFramePosition(0);
        // arcaneShiftSound.start();
    }

    public void playArcaneImpact() {
        // arcaneImpactFile.play();
        // arcaneImpactSound.stop();
        // arcaneImpactSound.setFramePosition(0);
        // arcaneImpactSound.start();
    }

    public void playFlash1() {
        // flash1Sound.stop();
        // flash1Sound.setFramePosition(0);
        // flash1Sound.start();
    }

    public void playFlash2() {
        // flash2Sound.stop();
        // flash2Sound.setFramePosition(0);
        // flash2Sound.start();
    }

    public void debug() {
        //         System.out.println("bool: " + (xDest != arcaneX && yDest != arcaneY));
        //         System.out.println("mouseX: " + mouseX);
        //         System.out.println("xDest: " + xDest);
        //         System.out.println("yDest: " + yDest);
        //         System.out.println("shipX: " + ship.getX());
        //         System.out.println("shipY: " + ship.getY());
        //         System.out.println("directionX: " + ship.getDirectionX());
        //         System.out.println("directionY: " + ship.getDirectionY());
        //         System.out.println("reachedDest: " + reachedDest);  
        //         System.out.println("moving: " + moving);
        //         System.out.println("arcaneX: " + arcaneX);
        //         System.out.println("arcaneY: " + arcaneY);
        //         System.out.println("pointDirection: " + ship.getPointDirection());
        //         System.out.println("direction: " + direction);
        //         System.out.println("mouseX(int): " + (int)mouseX);
        //         System.out.println("mouseY(int): " + (int)mouseY);
        //         System.out.println("keyDest: " + (((mouseX < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - mouseY) / (ship.getX() - mouseX)))));
        //         System.out.println("atan: " + Math.atan((yDest - box.getY()) / (xDest - box.getX())));
        //         System.out.println("diff: " + (yDest - box.getY()) / (xDest - box.getX()));
        //         dRadians = (ship.getPointDirection() * (Math.PI / 180));
        //         System.out.println("arcaneXPre: " + (float)((250 * Math.cos(dRadians)) + ship.getX()));
        //         System.out.println("arcaneYPre: " + (float)((250 * Math.sin(dRadians)) + ship.getY()));
        //         if (ezAA.size() > 0) {
        //             System.out.println("aaX: " + ezAA.get(0).getX());
        //             System.out.println("aaY: " + ezAA.get(0).getY());
        //         }
        //         System.out.println("dummyX: " + dummy.getX());
        //         System.out.println("dummyY: " + dummy.getY());
        //         System.out.println("towardsTarget: " + towardsTarget);
        //         System.out.println("attacking: " + attacking);
        //         System.out.println("inRange: " + inRange);
    }
// }

class ArcaneLandingParticles extends Floater {
    private PApplet applet;
    private long startTime;
    private int size;
    public ArcaneLandingParticles(PApplet applet, int xGrave, int yGrave) {
        super(applet);
        this.applet = applet;
        setX(xGrave + (int)(Math.random() * 4) - 2);
        setY(yGrave + (int)(Math.random() * 4) - 2);
        setDirectionX(randomPosNeg() * Math.random() / (5.0/3.0));
        setDirectionY(randomPosNeg() * Math.random() / (5.0/3.0));
        size = (int)(Math.random() * 6) + 2;
    }

    // @Override 
    public void show() {
        applet.fill(230, 230, 230);
        applet.stroke(255, 255, 51);
        applet.ellipse(getX(), getY(), size, size);
    }

    public boolean despawn() {
        if (timer - startTime > (int)(Math.random() * 1000) + 1000) {
            return true;
        }
        return false;
    }

    public void setStartTime(long time) {
        startTime = time;
    }

    public double randomPosNeg() {
        if ((int)(Math.random() * 2) == 0) {
            return 1.0;
        }
        return -1.0;
    }
}

class ArcaneProjectile extends Floater {
    private PApplet applet;
    private final int SPEED = 13;
    public ArcaneProjectile(PApplet applet, SpaceShip ship, Dummy dummy) {
        super(applet);
        this.applet = applet;
        corners = 4;
        xCorners = new int[corners];
        yCorners = new int[corners];
        xCorners[0] = -20; xCorners[1] = 2; xCorners[2] = 4; xCorners[3] = -2;
        yCorners[0] = 0; yCorners[1] = 4; yCorners[2] = 0; yCorners[3] = -4;
        setX(ship.getX());
        setY(ship.getY());
        setPointDirection(((dummy.getX() < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - dummy.getY()) / (ship.getX() - dummy.getX()))));
        double dRadians = myPointDirection * (Math.PI / 180);
        setDirectionX(Math.cos(dRadians) * SPEED);
        setDirectionY(Math.sin(dRadians) * SPEED);
    }

    public void homing(Dummy target) {
        setPointDirection(((target.getX() < myCenterX) ? 180 : 0) + (180 / Math.PI) * (Math.atan((myCenterY - target.getY()) / (myCenterX - target.getX()))));
        double dRadians = myPointDirection * (Math.PI / 180);
        setDirectionX(Math.cos(dRadians) * SPEED);
        setDirectionY(Math.sin(dRadians) * SPEED);
    }

    // @Override 
    public void show ()  //Draws the floater at the current position  
    {         
        applet.fill(230, 230, 230);   
        applet.stroke(230, 230, 0);    

        //convert degrees to radians for sin and cos         
        double dRadians = myPointDirection * (Math.PI / 180);                 
        int xRotatedTranslated, yRotatedTranslated;    
        applet.beginShape();         
        for(int nI = 0; nI < corners; nI++)    
        {     
            //rotate and translate the coordinates of the floater using current direction 
            xRotatedTranslated = (int)((xCorners[nI] * Math.cos(dRadians)) - (yCorners[nI] * Math.sin(dRadians)) + myCenterX);     
            yRotatedTranslated = (int)((xCorners[nI] * Math.sin(dRadians)) + (yCorners[nI] * Math.cos(dRadians)) + myCenterY);      
            applet.vertex(xRotatedTranslated, yRotatedTranslated);    
        }   
        applet.endShape(applet.CLOSE);  
        applet.fill(255);
    }   
}

class ArcaneProjectileImpact extends Floater {
    private PApplet applet;
    private PImage flare1;
    private PImage flareAlpha1;
    private PImage flare2;
    private PImage flareAlpha2;
    private PImage redLight;
    private PImage redLightAlpha;
    private float opacity;
    private boolean directionFlag;
    private float xLoc; 
    private float yLoc;
    public ArcaneProjectileImpact(PApplet applet, float xLoc, float yLoc) {
        super(applet);
        this.applet = applet;
        flare1 = applet.loadImage("data/flare.png");
        // flareAlpha1 = applet.loadImage("flareAlpha.png");
        // flare1.mask(flareAlpha1);
        flare2 = applet.loadImage("data/flareSun.png");
        // flareAlpha2 = applet.loadImage("flareSunAlpha.png");
        // flare2.mask(flareAlpha2);
        redLight = applet.loadImage("data/redLight.png");
        // redLightAlpha = applet.loadImage("redLightAlpha.png");
        // redLight.mask(redLightAlpha);
        this.xLoc = xLoc; this.yLoc = yLoc;
        opacity = 4;
        directionFlag = false;
    }

    // @Override 
    public void show() {
        if (opacity < 100 && !directionFlag) {
            opacity += 25;
        }
        else {
            directionFlag = true;
            opacity -= 10;
        }
        //         System.out.println("opacity: " + opacity);
        //         System.out.println("directionFlag: " + directionFlag);
        applet.tint(255, opacity);
        applet.image(flare1, xLoc - 64, yLoc - 64);
        applet.image(flare2, xLoc - 96, yLoc - 96);
        applet.image(redLight, xLoc - 64, yLoc - 64);
    }

    public boolean despawn() {
        if (opacity <= 0) {
            return true;
        }
        return false;
    }
    
    
}

public class Box extends Floater {
    private PApplet applet;
    public Box(PApplet applet) {
        super(applet);
        this.applet = applet;
        myColor = 255;
        corners = 4;
        xCorners = new int[corners];
        yCorners = new int[corners];
        xCorners[0] = -10; xCorners[1] = 10; xCorners[2] = 10; xCorners[3] = -10;
        yCorners[0] = 10; yCorners[1] = 10; yCorners[2] = -10; yCorners[3] = -10;
        setX(applet.width / 2);
        setY(applet.height / 2);
    }
}

public class Debris extends Floater {
    private PApplet applet;
    private long startTime;
    private int size;
    public Debris(PApplet applet, int xGrave, int yGrave) {
        super(applet);
        this.applet = applet;
        setX(xGrave + (int)(Math.random() * 4) - 2);
        setY(yGrave + (int)(Math.random() * 4) - 2);
        setDirectionX(randomPosNeg() * Math.random() / (5.0/3.0));
        setDirectionY(randomPosNeg() * Math.random() / (5.0/3.0));
        size = (int)(Math.random() * 6) + 2;
    }

    // @Override 
    public void show() {
        applet.fill(230, 230, 230);
        applet.stroke(255, 255, 51);
        applet.ellipse(getX(), getY(), size, size);
    }
    
    public boolean despawn() {
        if (timer - startTime > (int)(Math.random() * 1000) + 1250) {
            return true;
        }
        return false;
    }
    
    public void setStartTime(long time) {
        startTime = time;
    }
    
    public double randomPosNeg() {
        if ((int)(Math.random() * 2) == 0) {
            return 1.0;
        }
        return -1.0;
    }
}

public class DestLine extends Marker {
    private PApplet applet;
    public DestLine(PApplet applet) {
        super(applet);
        this.applet = applet;
    }

    public void show(SpaceShip ship, float xDest, float yDest, boolean towardsTarget) {
        if (towardsTarget) {
            applet.stroke(255, 0, 0, opacity);
        }
        else {
            applet.stroke(255, opacity);
        }
        applet.line(ship.getX(), ship.getY(), xDest, yDest);
        applet.stroke(255);
    }

    public void resetOpacity() {
        opacity = 100;
    }
}

public class Dummy extends Floater {
    private PApplet applet;
    private boolean targetted;
    private final int radius = 10;
    public Dummy(PApplet applet) {
        super(applet);
        this.applet = applet;
        myColor = 255;
        corners = 5;
        xCorners = new int[5];
        yCorners = new int[5];
        xCorners[0] = 0; xCorners[1] = 20; xCorners[2] = 12; xCorners[3] = -12; xCorners[4] = -20;
        yCorners[0] = 20; yCorners[1] = 4; yCorners[2] = -20; yCorners[3] = -20; yCorners[4] = 4;
        // float x = (float)Math.random() * (applet.width);
        // float y = (float)Math.random() * (applet.height);
        // setX(x);
        // setY(y);
        setX((float)Math.random() * (applet.width / 2) + (applet.width/4));
        setY((float)Math.random() * (applet.height / 2) + (applet.height/4));
        setPointDirection(180);
    }
    
    public void target() {
        targetted = true;
    }
    
    public void untarget() {
        targetted = false;
    }
         
    public float getRadius() {
        return radius;
    }
    
    // @Override 
    public void show ()  //Draws the floater at the current position  
    {         
        rotate(0);
        applet.fill(myColor);   
        if (targetted) {
           applet.strokeWeight(5);
        }
        applet.stroke(myColor);    

        //convert degrees to radians for sin and cos         
        double dRadians = myPointDirection * (Math.PI / 180);                 
        int xRotatedTranslated, yRotatedTranslated;    
        applet.beginShape();         
        for(int nI = 0; nI < corners; nI++)    
        {     
            //rotate and translate the coordinates of the floater using current direction 
            xRotatedTranslated = (int)((xCorners[nI] * Math.cos(dRadians)) - (yCorners[nI] * Math.sin(dRadians)) + myCenterX);     
            yRotatedTranslated = (int)((xCorners[nI] * Math.sin(dRadians)) + (yCorners[nI] * Math.cos(dRadians)) + myCenterY);      
            applet.vertex(xRotatedTranslated, yRotatedTranslated);    
        }   
        applet.endShape(applet.CLOSE);  
        applet.strokeWeight(1);
    }   
}

public class ezAA extends Floater {
    private PApplet applet;
    private PImage ezAAAlpha;
    private float x; private float y;
    private double directionX;
    private double directionY;
    private double rotation;
    private final int SPEED = 11;
    public ezAA(PApplet applet, SpaceShip ship, Dummy dummy) {
        super(applet);
        this.applet = applet;
        setX(ship.getX());
        setY(ship.getY());
        setPointDirection(ship.getPointDirection());
        corners = 47;
        xCorners = new int[corners];
        yCorners = new int[corners];
        xCorners[0] = -20; xCorners[1] = -16; xCorners[2] = -16; xCorners[3] = -12; xCorners[4] = -12; xCorners[5] = -2; xCorners[6] = -2; xCorners[7] = -12; xCorners[8] = -2; xCorners[9] = 2; xCorners[10] = -14; xCorners[11] = -14; xCorners[12] = -20; xCorners[13] = -14; xCorners[14] = -6; xCorners[15] = -6; xCorners[16] = -2; xCorners[17] = -2; xCorners[18] = 0; xCorners[19] = 0; 
        yCorners[0] = 4; yCorners[1] = 4; yCorners[2] = 2; yCorners[3] = 2; yCorners[4] = 4; yCorners[5] = 4; yCorners[6] = 2; yCorners[7] = 2; yCorners[8] = 2; yCorners[9] = 0; yCorners[10] = 0; yCorners[11] = -2; yCorners[12] = -2; yCorners[13] = -2; yCorners[14] = 0; yCorners[15] = -2; yCorners[16] = -2; yCorners[17] = 0; yCorners[18] = 0; yCorners[19] = 2; 
        xCorners[20] = 0; xCorners[21] = 2; xCorners[22] = 2; xCorners[23] = -2; xCorners[24] = 2; xCorners[25] = 2; xCorners[26] = 2; xCorners[27] = 20; xCorners[28] = 2; xCorners[29] = 2; xCorners[30] = 10; xCorners[31] = 10; xCorners[32] = 8; xCorners[33] = 10; xCorners[34] = 10; xCorners[35] = 18; xCorners[36] = 10; xCorners[37] = 10; xCorners[38] = 12; xCorners[39] = 12; 
        yCorners[20] = 0; yCorners[21] = 0; yCorners[22] = -2; yCorners[23] = -2; yCorners[24] = -2; yCorners[25] = 0; yCorners[26] = 4; yCorners[27] = 4; yCorners[28] = 4; yCorners[29] = 0; yCorners[30] = 0; yCorners[31] = 2; yCorners[32] = 2; yCorners[33] = 2; yCorners[34] = -2; yCorners[35] = -2; yCorners[36] = -2; yCorners[37] = 0; yCorners[38] = 0; yCorners[39] = 2;
        xCorners[40] = 18; xCorners[41] = 12; xCorners[42] = 12; xCorners[43] = 14; xCorners[44] = 10; xCorners[45] = 10; xCorners[46] = 18;
        yCorners[40] = 2; yCorners[41] = 2; yCorners[42] = 0; yCorners[43] = 0; yCorners[44] = 0; yCorners[45] = -2; yCorners[46] = -2; 
    }

    public void homing(Dummy target) {
        setPointDirection(((target.getX() < myCenterX) ? 180 : 0) + (180 / Math.PI) * (Math.atan((myCenterY - target.getY()) / (myCenterX - target.getX()))));
        double dRadians = myPointDirection * (Math.PI / 180);
        setDirectionX(Math.cos(dRadians) * SPEED);
        setDirectionY(Math.sin(dRadians) * SPEED);
    }

    // @Override  
    public void show ()  //Draws the floater at the current position  
    {         
        applet.stroke(200, 200, 150);    

        //convert degrees to radians for sin and cos         
        double dRadians = myPointDirection * (Math.PI / 180);                 
        int xRotatedTranslated, yRotatedTranslated;    
        applet.beginShape();         
        for(int nI = 0; nI < corners; nI++)    
        {     
            //rotate and translate the coordinates of the floater using current direction 
            xRotatedTranslated = (int)((xCorners[nI] * Math.cos(dRadians)) - (yCorners[nI] * Math.sin(dRadians)) + myCenterX);     
            yRotatedTranslated = (int)((xCorners[nI] * Math.sin(dRadians)) + (yCorners[nI] * Math.cos(dRadians)) + myCenterY);      
            applet.vertex(xRotatedTranslated, yRotatedTranslated);    
        }   
        applet.endShape(applet.CLOSE);  
    }   
}

// By Sean Fottrell and Mindy Holmes (APCS Teachers)
public abstract class Floater
{   
    protected PApplet applet;
    protected int corners;  //the number of corners, a triangular floater has 3   
    protected int[] xCorners;   
    protected int[] yCorners;
    protected int myColor;   
    protected float myCenterX, myCenterY; //holds center coordinates   
    protected double myDirectionX, myDirectionY; //holds x and y coordinates of the vector for direction of travel   
    protected double myPointDirection; //holds current direction the ship is pointing in degrees   

    public Floater(PApplet applet_)
    {
        applet = applet_;
    }

    public void setX(float x) {
        myCenterX = x;
    }

    public float getX() {
        return myCenterX;
    }

    public void setY(float y) {
        myCenterY = y;
    }

    public float getY() {
        return myCenterY;
    }

    public void setDirectionX(double x) {
        myDirectionX = x;
    }

    public double getDirectionX() {
        return myDirectionX;
    }

    public void setDirectionY(double y) {
        myDirectionY = y;
    }

    public double getDirectionY() {
        return myDirectionY;
    }

    public void setPointDirection(double degrees) {
        myPointDirection = degrees;
    }

    public double getPointDirection() {
        return myPointDirection;
    }

    //Accelerates the floater in the direction it is pointing (myPointDirection)   
    public void accelerate (double dAmount)   
    {          
        //convert the current direction the floater is pointing to radians    
        double dRadians = myPointDirection * (Math.PI / 180);     
        //change coordinates of direction of travel    
        myDirectionX += ((dAmount) * Math.cos(dRadians));    
        myDirectionY += ((dAmount) * Math.sin(dRadians));       
    }   

    public void rotate (double nDegreesOfRotation)   
    {     
        //rotates the floater by a given number of degrees    
        myPointDirection += nDegreesOfRotation;   
    }   
       
    public void move ()   //move the floater in the current direction of travel
    {      
        //change the x and y coordinates by myDirectionX and myDirectionY       
        myCenterX += myDirectionX;    
        myCenterY += myDirectionY;     

        //wrap around screen    
        if(myCenterX > applet.width)
        {     
            myCenterX = 0;    
        }    
        else if (myCenterX < 0)
        {     
            myCenterX = applet.width;    
        }    
        if(myCenterY > applet.height)
        {    
            myCenterY = 0;    
        }   
        else if (myCenterY < 0)
        {     
            myCenterY = applet.height;    
        }   
    }   

    public void show ()  //Draws the floater at the current position  
    {         
        applet.fill(myColor);   
        applet.stroke(myColor);    

        //convert degrees to radians for sin and cos         
        double dRadians = myPointDirection * (Math.PI / 180);                 
        int xRotatedTranslated, yRotatedTranslated;    
        applet.beginShape();         
        for(int nI = 0; nI < corners; nI++)    
        {     
            //rotate and translate the coordinates of the floater using current direction 
            xRotatedTranslated = (int)((xCorners[nI] * Math.cos(dRadians)) - (yCorners[nI] * Math.sin(dRadians)) + myCenterX);     
            yRotatedTranslated = (int)((xCorners[nI] * Math.sin(dRadians)) + (yCorners[nI] * Math.cos(dRadians)) + myCenterY);      
            applet.vertex(xRotatedTranslated, yRotatedTranslated);    
        }   
        applet.endShape(applet.CLOSE);  
    }   
} 

// String color;
class Marker {
    private PApplet applet;
    private long startTime;
    private float x;
    private float y;
    boolean isValid;
    // String color;
    protected float opacity;
    Marker(PApplet applet, float xLoc, float yLoc, boolean isValid) {
        this.applet = applet;
        opacity = 100;
        x = xLoc;
        y = yLoc;
        this.isValid = isValid;
        // this.color = color;
    }

    //DestLine constructor
    public Marker(PApplet applet) {
        this.applet = applet;
    }

    public float getOpacity() {
        return opacity;
    }

    public void decrementOpacity() {
        opacity -= (2f);
    }

    public void show(SpaceShip ship, float xDest, float yDest) {
        if (isValid) {
            applet.fill(0,255,0,opacity);
        } else {
            applet.fill(255,0,0,opacity);
        }
        applet.stroke(0, 255, 150, opacity);
        applet.ellipse(x, y, 10, 10);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}

public class MysticShotProjectile extends Floater {
    private PApplet applet;
    private int opacity;
    private final int SPEED = 19;
    private float xStart; private float yStart;
    private long startTime;
    public MysticShotProjectile(PApplet applet, SpaceShip ship, float aimLocX, float aimLocY) {
        super(applet);
        this.applet = applet;
        corners = 6;
        xCorners = new int[corners];
        yCorners = new int[corners];
        xCorners[0] = -16; xCorners[1] = 16; xCorners[2] = 18; xCorners[3] = 20; xCorners[4] = 18; xCorners[5] = 16; 
        yCorners[0] = 0; yCorners[1] = 6; yCorners[2] = 4; yCorners[3] = 0; yCorners[4] = -4; yCorners[5] = -6; 
        setX(ship.getX());
        setY(ship.getY());
        xStart = ship.getX();
        yStart = ship.getY();
        //         setPointDirection(((dummy.getX() < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - dummy.getY()) / (ship.getX() - dummy.getX()))));
        setPointDirection(ship.getPointDirection());
        double dRadians = myPointDirection * (Math.PI / 180);
        setDirectionX(Math.cos(dRadians) * SPEED);
        setDirectionY(Math.sin(dRadians) * SPEED);
        opacity = 255;
    }

    public int getOpacity() {
        return opacity;
    }

    public void fade() {
        opacity -= 15;
    }

    public long getStart() {
        return startTime;
    }

    public void setStart(long time) {
        startTime = time;
    }

    // @Override 
    public void show ()  //Draws the floater at the current position  
    {         
        applet.fill(230, 230, 230, opacity);   
        applet.stroke(230, 230, 100, opacity);    

        //convert degrees to radians for sin and cos         
        double dRadians = myPointDirection * (Math.PI / 180);                 
        int xRotatedTranslated, yRotatedTranslated;    
        applet.beginShape();         
        for(int nI = 0; nI < corners; nI++)    
        {     
            //rotate and translate the coordinates of the floater using current direction 
            xRotatedTranslated = (int)((xCorners[nI] * Math.cos(dRadians)) - (yCorners[nI] * Math.sin(dRadians)) + myCenterX);     
            yRotatedTranslated = (int)((xCorners[nI] * Math.sin(dRadians)) + (yCorners[nI] * Math.cos(dRadians)) + myCenterY);      
            applet.vertex(xRotatedTranslated, yRotatedTranslated);    
        }   
        applet.endShape(applet.CLOSE);  
        applet.fill(255);
    }  
}

public class SpaceShip extends Floater  
{   
    private long deathTime;
    private PApplet applet;
    public SpaceShip(PApplet applet) {
        super(applet);
        this.applet = applet;
        myColor = 255;
        corners = 4;
        xCorners = new int[corners];
        yCorners = new int[corners];
        xCorners[0] = -8; xCorners[1] = 16; xCorners[2] = -8; xCorners[3] = -2;
        yCorners[0] = -8; yCorners[1] = 0; yCorners[2] = 8; yCorners[3] = 0;
        //                 xCorners[4] = -4; xCorners[5] = -16; xCorners[6] = -4;
        //                yCorners[4] = -6; yCorners[5] = 0; yCorners[6] = 6;
        setX(applet.width / 2);
        setY(applet.height / 2);
    }

    //     public boolean deathTimer(List<Asteroids> asteroids) {
    //         for (int i = 0; i < asteroids.size(); i++) {
    //             if ((System.nanoTime() * Math.pow(10, -9)) - (deathTime * Math.pow(10, -9)) > 2 && applet.dist(asteroids.get(i).getX(), asteroids.get(i).getY(), getX(), getY()) > asteroids.get(i).getSize()) {
    //                 return true;
    //             }
    //         }
    //         return false;
    //     }

    public void deathTrack(long death) {
        deathTime = death;
    }
    }