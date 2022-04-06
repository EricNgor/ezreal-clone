import processing.core.PApplet;
public class ArcaneProjectile extends Floater {
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

    @Override
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