import processing.core.PApplet;
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

    @Override
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