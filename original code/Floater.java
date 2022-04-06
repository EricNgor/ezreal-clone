import processing.core.PApplet;
public abstract class Floater //Do modify the Floater class! Make changes in the SpaceShip class 
{   
    protected PApplet applet;
    protected int corners;  //the number of corners, a triangular floater has 3   
    protected int[] xCorners;   
    protected int[] yCorners;
    protected int myColor;   
    protected float myCenterX, myCenterY; //holds center coordinates   
    protected double myDirectionX, myDirectionY; //holds x and y coordinates of the vector for direction of travel   
    protected double myPointDirection; //holds current direction the ship is pointing in degrees   

    public Floater(PApplet applet_)
    {
        applet = applet_;
    }

    public void setX(float x) {
        myCenterX = x;
    }

    public float getX() {
        return myCenterX;
    }

    public void setY(float y) {
        myCenterY = y;
    }

    public float getY() {
        return myCenterY;
    }

    public void setDirectionX(double x) {
        myDirectionX = x;
    }

    public double getDirectionX() {
        return myDirectionX;
    }

    public void setDirectionY(double y) {
        myDirectionY = y;
    }

    public double getDirectionY() {
        return myDirectionY;
    }

    public void setPointDirection(double degrees) {
        myPointDirection = degrees;
    }

    public double getPointDirection() {
        return myPointDirection;
    }

    //Accelerates the floater in the direction it is pointing (myPointDirection)   
    public void accelerate (double dAmount)   
    {          
        //convert the current direction the floater is pointing to radians    
        double dRadians = myPointDirection * (Math.PI / 180);     
        //change coordinates of direction of travel    
        myDirectionX += ((dAmount) * Math.cos(dRadians));    
        myDirectionY += ((dAmount) * Math.sin(dRadians));       
    }   

    public void rotate (double nDegreesOfRotation)   
    {     
        //rotates the floater by a given number of degrees    
        myPointDirection += nDegreesOfRotation;   
    }   
       
    public void move ()   //move the floater in the current direction of travel
    {      
        //change the x and y coordinates by myDirectionX and myDirectionY       
        myCenterX += myDirectionX;    
        myCenterY += myDirectionY;     

        //wrap around screen    
        if(myCenterX > applet.width)
        {     
            myCenterX = 0;    
        }    
        else if (myCenterX < 0)
        {     
            myCenterX = applet.width;    
        }    
        if(myCenterY > applet.height)
        {    
            myCenterY = 0;    
        }   
        else if (myCenterY < 0)
        {     
            myCenterY = applet.height;    
        }   
    }   

    public void show ()  //Draws the floater at the current position  
    {         
        applet.fill(myColor);   
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
    }   
} 
