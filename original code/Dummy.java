import processing.core.PApplet;
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
        setX((float)Math.random() * (applet.width / 2) + 250);
        setY((float)Math.random() * (applet.height / 2) + 250);
        setPointDirection(180);
    }
    
    public void targetted() {
        targetted = true;
    }
    
    public void untargetted() {
        targetted = false;
    }
         
    public float getRadius() {
        return radius;
    }
    
    @Override
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