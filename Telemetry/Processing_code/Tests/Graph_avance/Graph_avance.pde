float [] x = new float[100];
float [] y = new float [100];
int add = 100;
int speed = 100;
int temps = millis();
void setup()
    {
      size(1000,1000);
      for (int i=0; i<x.length; i++)
      {
        x[i] = i*10;
        y[i] = random(-10,10);
        
      }
      
    }
    
void draw()
    {
      background(200);
   

    for (int k = 0; k<x.length-1; k++){
      strokeWeight(2);
      line(x[k],-y[k]+height/2,x[k+1],-y[k+1]+height/2);
      
      y[k]=y[k+1];
      
    }
    /*
    if (x.length < width){
    x = append(x, add);
    y = append(y, y[y.length-1]+random(-10,10));
    add++;
    }
    else{
    y[y.length-1] = y[y.length-1]+random(-50,50);
    }*/
     
      y[y.length-1] = y[y.length-1]+random(-50,50);
      
    
    delay(10);
    }
 
