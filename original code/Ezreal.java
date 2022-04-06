import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PGraphics;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import java.io.*;

//Eric Ngor, 2016-2017 Side project
//many statements in draw() could have been reorganized as their own methods
public class Ezreal extends PApplet {
    private PImage cursor;
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
    private List<ezAA> ezAA;

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
    public static void main(String[] args) {
        PApplet.main(new String[] {"Ezreal"});
    }

    public void settings() {
        size(1400,1000);
        xDest = width / 2;
        yDest = height / 2;
        box = new Box(this);
        ship = new SpaceShip(this);
        speed = 3.25;
        cursor = loadImage("cursor3.png");
        cursorAlpha = loadImage("alphaAlpha.png");
        cursor.mask(cursorAlpha);
        cursor2 = loadImage("cursor2.png");
        cursorAttack = loadImage("cursorAttack.png");
        cursorAttackAlpha = loadImage("cursorAttackAlpha.png");
        cursorAttack.mask(cursorAttackAlpha);
        markers = new ArrayList<Marker>();
        destLine = new DestLine(this);
        dummy = new Dummy(this);

        //Auto Attack
        ezAA = new ArrayList<ezAA>();
        attackSpeed = 1.2;
        attackReady = true;

        //Mystic Shot
        q_mis = new ArrayList<MysticShotProjectile>();

        //Landing
        arcaneParticles = new ArrayList<ArcaneLandingParticles>();
        arcaneShiftFile = new File("arcaneShift.wav");
        try {
            AudioInputStream fileIn = AudioSystem.getAudioInputStream(arcaneShiftFile);
            arcaneShiftSound = AudioSystem.getClip();
            arcaneShiftSound.open(fileIn);
        }
        catch (Exception e) {
            System.out.println("Unsupported file type");
        }

        //Projectile impact
        arcaneProjectile = new ArrayList<ArcaneProjectile>();
        arcaneImpact = new ArrayList<ArcaneProjectileImpact>();
        arcaneImpactFile = new File("arcaneImpact.wav");
        try {
            AudioInputStream fileIn = AudioSystem.getAudioInputStream(arcaneImpactFile);
            arcaneImpactSound = AudioSystem.getClip();
            arcaneImpactSound.open(fileIn);
        }
        catch (Exception e) {
            System.out.println("Unsupported file type");
        }

        //Flash
        flash1File = new File("flash1.wav");
        try {
            AudioInputStream fileIn = AudioSystem.getAudioInputStream(flash1File);
            flash1Sound = AudioSystem.getClip();
            flash1Sound.open(fileIn);
        }
        catch (Exception e) {
            System.out.println("Unsupported file type");
        }
        flash2File = new File("flash2.wav");
        try {
            AudioInputStream fileIn = AudioSystem.getAudioInputStream(flash2File);
            flash2Sound = AudioSystem.getClip();
            flash2Sound.open(fileIn);
        }
        catch (Exception e) {
            System.out.println("Unsupported file type");
        }
        flash = loadImage("flash.png");
        flashAlpha = loadImage("flashAlpha.png");
        flash.mask(flashAlpha);
    }

    public void draw() {
        background(0);
        ship.show();
        ship.move();
        //Cursor
        if (dist(mouseX, mouseY, dummy.getX(), dummy.getY()) < 30) {
            cursor(cursorAttack, 0, 0);
            dummy.targetted();
            targetting = true;
        }
        else {
            cursor(cursor, 0, 0);
            dummy.untargetted();
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
        if (System.currentTimeMillis() - ba_timer > 750 / attackSpeed) {
            updateMove = true;
        }
        else {
            updateMove = false;
        }
        if (System.currentTimeMillis() - ba_timer > 1000 / attackSpeed) {
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
            if (System.currentTimeMillis() - castTimeStart <= 250) {
                rect((width / 2) - 125, 800, System.currentTimeMillis() - castTimeStart, 10); // Casting time bar
            }
            stroke(255);
            strokeWeight(1);
            stroke(82, 50, 53);
            noFill();
            rect((width / 2) - 125, 800, 250, 10);
            fill(255);
            if (System.currentTimeMillis() - castTimeStart < 250) { // Cast time display
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
                        markers.add(new Marker(this, xDest, yDest, "green"));

                        destLine.show(ship, xDest, yDest, towardsTarget);
                    }

                    //If in range, fire projectile
                    if (dist(ship.getX(), ship.getY(), dummy.getX(), dummy.getY()) < ARCANE_PROJECTILE_RANGE) {
                        arcaneProjectile.add(new ArcaneProjectile(this, ship, dummy));
                        if (attackReady) {
                            ba_timer = System.currentTimeMillis() - 1000;
                            attackReady = false;
                        }
                    }

                    playArcaneShift();
                    arcaneShift = false;

                }
                if (mysticShot) {
                    q_mis.add(new MysticShotProjectile(this, ship, aimLocX, aimLocY));
                    q_mis.get(q_mis.size() - 1).setStart(System.currentTimeMillis());
                    if (attackReady) {
                        ba_timer = System.currentTimeMillis() - 1000;
                        attackReady = false;
                    }
                    mysticShot = false;
                }
                casting = false;
            }

        }

        //Mystic Shot Disp
        for (int i = 0; i < q_mis.size(); i++) {
            q_mis.get(i).show();
            q_mis.get(i).move();
            if (System.currentTimeMillis() - q_mis.get(i).getStart() > 150) {
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
        for (int i = 0; i < ezAA.size(); i++) {
            ezAA.get(i).show();
            ezAA.get(i).move();
            ezAA.get(i).homing(dummy);

            //BA Collision
            if (dist(ezAA.get(i).getX(), ezAA.get(i).getY(), dummy.getX(), dummy.getY()) < 10) {
                ezAA.remove(i);
                i--;
                //temp impact
                arcaneImpact.add(new ArcaneProjectileImpact(this, dummy.getX(), dummy.getY()));
                playArcaneImpact();
            }
        }

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
            markers.add(new Marker(this, xDest, yDest, "green"));
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
        if (key == 'd') {
            if ((int)(Math.random() * 2) + 1 == 1) {
                playFlash1();
                System.out.println("1");
            }
            else {
                playFlash2();
                System.out.println("2");
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
                markers.add(new Marker(this, xDest, yDest, "green"));
                destLine.show(ship, xDest, yDest, towardsTarget);
            }
            flashParticles = true;
        }
        if (key == 'x') {
            xDest = dummy.getX();
            yDest = dummy.getY();
            markers.add(new Marker(this, mouseX, mouseY, "red"));
            towardsTarget = true;
            //below is for testing only
            //ezAA.add(new ezAA(this, ship, dummy));
        }
        if (key == 'c') {
            ezAA.get(0).setX(width / 2);
            ezAA.get(0).setY(height / 2);
        }
    }

    public void directionToMouse() {
        ship.setPointDirection(((mouseX < ship.getX()) ? 180 : 0) + (180 / Math.PI) * (Math.atan((ship.getY() - mouseY) / (ship.getX() - mouseX))));
    }

    public void startCast() {
        castTimeStart = System.currentTimeMillis();
        casting = true;
    }

    public void attack() {
        if (attackReady) {
            ezAA.add(new ezAA(this, ship, dummy));
            ba_timer = System.currentTimeMillis();
            attackReady = false;
        }

    }

    public void arcaneLandingParticles(int xGrave, int yGrave) {
        for (int i = 0; i < (int)(Math.random() * 6) + 10; i++) {
            arcaneParticles.add(new ArcaneLandingParticles(this, xGrave, yGrave));
            arcaneParticles.get(arcaneParticles.size() - 1).setStartTime(System.currentTimeMillis());
        }
    }

    public void playArcaneShift() {
        arcaneShiftSound.stop();
        arcaneShiftSound.setFramePosition(0);
        arcaneShiftSound.start();
    }

    public void playArcaneImpact() {
        arcaneImpactSound.stop();
        arcaneImpactSound.setFramePosition(0);
        arcaneImpactSound.start();
    }

    public void playFlash1() {
        flash1Sound.stop();
        flash1Sound.setFramePosition(0);
        flash1Sound.start();
    }

    public void playFlash2() {
        flash2Sound.stop();
        flash2Sound.setFramePosition(0);
        flash2Sound.start();
    }
    //     public void setGradient(int x, int y, float w, float h, color c1, color c2, int axis ) {
    //         
    //         noFill();
    // 
    //         if (axis == Y_AXIS) {  // Top to bottom gradient
    //             for (int i = y; i <= y+h; i++) {
    //                 float inter = map(i, y, y+h, 0, 1);
    //                 color c = lerpColor(c1, c2, inter);
    //                 stroke(c);
    //                 line(x, i, x+w, i);
    //             }
    //         }  
    //         else if (axis == X_AXIS) {  // Left to right gradient
    //             for (int i = x; i <= x+w; i++) {
    //                 float inter = map(i, x, x+w, 0, 1);
    //                 color c = lerpColor(c1, c2, inter);
    //                 stroke(c);
    //                 line(i, y, i, y+h);
    //             }
    //         }
    //     }

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
}