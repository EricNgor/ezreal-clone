import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;
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