import processing.core.PApplet;
import processing.core.PImage;
public class ezAA extends Floater {
    private PApplet applet;
    private PImage ezAA;
    private PImage ezAAAlpha;
    private float x; private float y;
    private double directionX;
    private double directionY;
    private double rotation;
    private final int SPEED = 11;
    public ezAA(PApplet applet, SpaceShip ship, Dummy dummy) {
        //         this.applet = applet;
        // 
        //         ezAA = applet.loadImage("ezAA.png");
        //         ezAAAlpha = applet.loadImage("ezAAAlpha.png");
        //         ezAA.mask(ezAAAlpha);
        // 
        //         float dRadians = applet.radians((float)ship.getPointDirection());
        //         x = ((float)Math.cos(dRadians) + ship.getX() - (ezAA.width / 2));
        //         y = ((float)Math.sin(dRadians) + ship.getY() - (ezAA.height / 2));
        // 
        //         //         applet.translate(x + (ezAA.width / 2), y + (ezAA.height / 2));
        //         //         rotation = (float)(((dummy.getX() < x) ? 180 : 0) + (180 / Math.PI) * (Math.atan((y - dummy.getY()) - (x - dummy.getY()))));
        //         //         applet.rotate(rotation);
        //         System.out.println("rotation: " + rotation);
        super(applet);
        this.applet = applet;
        //         setX((float)Math.cos(ship.getPointDirection()) * 20 + ship.getX());
        //         setY((float)Math.sin(ship.getPointDirection()) * 20 + ship.getY());
        setX(ship.getX());
        setY(ship.getY());
        setPointDirection(ship.getPointDirection());
        corners = 47;
        xCorners = new int[corners];
        yCorners = new int[corners];
        xCorners[0] = -20; xCorners[1] = -16; xCorners[2] = -16; xCorners[3] = -12; xCorners[4] = -12; xCorners[5] = -2; xCorners[6] = -2; xCorners[7] = -12; xCorners[8] = -2; xCorners[9] = 2; xCorners[10] = -14; xCorners[11] = -14; xCorners[12] = -20; xCorners[13] = -14; xCorners[14] = -6; xCorners[15] = -6; xCorners[16] = -2; xCorners[17] = -2; xCorners[18] = 0; xCorners[19] = 0; 
        yCorners[0] = 4; yCorners[1] = 4; yCorners[2] = 2; yCorners[3] = 2; yCorners[4] = 4; yCorners[5] = 4; yCorners[6] = 2; yCorners[7] = 2; yCorners[8] = 2; yCorners[9] = 0; yCorners[10] = 0; yCorners[11] = -2; yCorners[12] = -2; yCorners[13] = -2; yCorners[14] = 0; yCorners[15] = -2; yCorners[16] = -2; yCorners[17] = 0; yCorners[18] = 0; yCorners[19] = 2; 
        xCorners[20] = 0; xCorners[21] = 2; xCorners[22] = 2; xCorners[23] = -2; xCorners[24] = 2; xCorners[25] = 2; xCorners[26] = 2; xCorners[27] = 20; xCorners[28] = 2; xCorners[29] = 2; xCorners[30] = 10; xCorners[31] = 10; xCorners[32] = 8; xCorners[33] = 10; xCorners[34] = 10; xCorners[35] = 18; xCorners[36] = 10; xCorners[37] = 10; xCorners[38] = 12; xCorners[39] = 12; 
        yCorners[20] = 0; yCorners[21] = 0; yCorners[22] = -2; yCorners[23] = -2; yCorners[24] = -2; yCorners[25] = 0; yCorners[26] = 4; yCorners[27] = 4; yCorners[28] = 4; yCorners[29] = 0; yCorners[30] = 0; yCorners[31] = 2; yCorners[32] = 2; yCorners[33] = 2; yCorners[34] = -2; yCorners[35] = -2; yCorners[36] = -2; yCorners[37] = 0; yCorners[38] = 0; yCorners[39] = 2;
        xCorners[40] = 18; xCorners[41] = 12; xCorners[42] = 12; xCorners[43] = 14; xCorners[44] = 10; xCorners[45] = 10; xCorners[46] = 18;
        yCorners[40] = 2; yCorners[41] = 2; yCorners[42] = 0; yCorners[43] = 0; yCorners[44] = 0; yCorners[45] = -2; yCorners[46] = -2; 
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
        applet.stroke(200, 200, 150);    

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
    }   
    //     public void show(Dummy target) {
    //         System.out.println("x: " + x);
    //         System.out.println("y: " + y);
    //         applet.translate(x, y);
    //         rotation = (((target.getX() < x) ? 180 : 0) + (180 / Math.PI) * (Math.atan((y - target.getY()) - (x - target.getY()))));
    //         //         applet.rotate(applet.radians(rotation));
    //         System.out.println("rotation: " + rotation);
    //         System.out.println("rotationRadians: " + applet.radians(rotation));
    //         applet.image(ezAA, 0, 0);
    //         applet.fill(255);
    //         applet.line(0, 0, (float)Math.cos(applet.radians(rotation)) * 30, (float)Math.sin(applet.radians(rotation)) * 30);
    //         //         applet.redraw();
    //         //         applet.rotate(0);
    //     }
    // 
    //     public void move() {
    // 
    //         directionX = (Math.cos(rotation) * SPEED);
    //         directionY = (Math.sin(rotation) * SPEED);
    //         x += directionX;
    //         y += directionY;
    // 
    //         if(x > applet.width)
    //         {     
    //             x = 0;    
    //         }    
    //         else if (x < 0)
    //         {     
    //             x = applet.width;    
    //         }    
    //         if(y > applet.height)
    //         {    
    //             y = 0;    
    //         }   
    //         else if (y < 0)
    //         {     
    //             y = applet.height;    
    //         }   
    //     }
    // 
    //     public float getX() {
    //         return x;
    //     }
    // 
    //     public float getY() {
    //         return y;
    //     }
    // 
    //     public void setX(float val) {
    //         x = val;
    //     }
    // 
    //     public void setY(float val) {
    //         y = val;
    //     }
}