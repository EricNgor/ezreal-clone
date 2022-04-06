import processing.core.PApplet;
public class ArcaneLandingParticles extends Floater {
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

    @Override
    public void show() {
        applet.fill(230, 230, 230);
        applet.stroke(255, 255, 51);
        applet.ellipse(getX(), getY(), size, size);
    }

    public boolean despawn() {
        if (System.currentTimeMillis() - startTime > (int)(Math.random() * 1000) + 1000) {
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