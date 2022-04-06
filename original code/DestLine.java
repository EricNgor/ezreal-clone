import processing.core.PApplet;
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