import processing.core.PApplet;
public class Marker {
    private PApplet applet;
    private long startTime;
    private float x;
    private float y;
    private String color;
    protected float opacity;
    public Marker(PApplet applet, float xLoc, float yLoc, String color) {
        this.applet = applet;
        opacity = 100;
        x = xLoc;
        y = yLoc;
        this.color = color;
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
        switch (color)  {
            case("green"): applet.fill(0,255,0,opacity); break;
            case("red"): applet.fill(255,0,0,opacity); break;
            default: applet.fill(0,0,0,opacity);            
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