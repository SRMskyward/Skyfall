float [] x = new float[100];
float [] y = new float [100];
int add = 0;
int speed = 100;
int temps = millis();
void setup()
    {
      size(300,300);
      /*for (int i=0; i<x.length; i++)
      {
        x[i] = i*10;
        y[i] = random(-10,10);
        
      }*/
      
    }
    
void draw()
    {
      background(200);
   

    for (int k = 0; k<x.length-1; k++){
      strokeWeight(2);
      line((x[k]/x.length)*width,-y[k]+height/2,(x[k+1]/x.length)*width,-y[k+1]+height/2);
 
      
      //y[k]=y[k+1];
      
    }
    
    //if (x.length < width){
    x = append(x, add);
    y = append(y, y[y.length-1]+random(-10,10));
    add++;
    //}
    /*else{
    y[y.length-1] = y[y.length-1]+random(-50,50);
    }*/
     
      //y[y.length-1] = y[y.length-1]+random(-50,50);
      
    
    delay(0);
    println(x.length);
    }
/* line(xPos+50,yPos+0.85*Height,xPos+50,yPos+20);                        // y-axis line 
          line(xPos+50,yPos+0.85*Height,xPos+Width-20,yPos+0.85*Height);           // x-axis line
          */

    
