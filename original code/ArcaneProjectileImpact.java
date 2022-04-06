import processing.core.PApplet;
import processing.core.PImage;
public class ArcaneProjectileImpact extends Floater {
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
        flare1 = applet.loadImage("flare.png");
        flareAlpha1 = applet.loadImage("flareAlpha.png");
        flare1.mask(flareAlpha1);
        flare2 = applet.loadImage("flareSun.png");
        flareAlpha2 = applet.loadImage("flareSunAlpha.png");
        flare2.mask(flareAlpha2);
        redLight = applet.loadImage("redLight.png");
        redLightAlpha = applet.loadImage("redLightAlpha.png");
        redLight.mask(redLightAlpha);
        this.xLoc = xLoc; this.yLoc = yLoc;
        opacity = 4;
        directionFlag = false;
    }

    @Override
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