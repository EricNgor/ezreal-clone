import processing.core.PApplet;
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