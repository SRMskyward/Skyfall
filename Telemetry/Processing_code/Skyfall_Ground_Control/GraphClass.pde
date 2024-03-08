  
/*   =================================================================================       
     The Graph class contains functions and variables that have been created to draw 
     graphs. Here is a quick list of functions within the graph class:
          
       Graph(int x, int y, int w, int h,color k)
       DrawAxis()
       Bar([])
       smoothLine([][])
       DotGraph([][])
       Altitude([][]) 
     
     =================================================================================*/   

    
    class Graph 
    {
      
      boolean Dot=true;            // Draw dots at each data point if true
      boolean RightAxis;            // Draw the next graph using the right axis if true
      boolean ErrorFlag=false;      // If the time array isn't in ascending order, make true  
      boolean ShowMouseLines=true;  // Draw lines and give values of the mouse position
    
      int     xDiv=5,yDiv=5;            // Number of sub divisions
      int     xPos,yPos;        // location of the top left corner of the graph  
      int     Width,Height;         // Width and height of the graph
     




      color   GraphColor;
      color   BackgroundColor=color(255);  
      color   StrokeColor=color(180);     
      
      String  Title="Title";          // Default titles
      String  xLabel="x - Label";
      String  yLabel="y - Label";

      float   yMax=1024, yMin=-100;      // Default axis dimensions
      float   xMax=10, xMin=0;
      float   yMaxRight=1024,yMinRight=0;
  
      PFont   Font;                   // Selected font used for text 
     
      
  //    int Peakcounter=0,nPeakcounter=0;
     
      Graph(int x, int y, int w, int h,color k) {  // The main declaration function
        xPos = x;
        yPos = y;
        Width = w;
        Height = h;
        GraphColor = k;      
      }
    
     
       void DrawAxis(){
       
   /*  =========================================================================================
        Main axes Lines, Graph Labels, Graph Background
       ==========================================================================================  */
        fill(0); color(0);stroke(0);strokeWeight(0);
        fill(0);
        rectMode(CENTER);
        rect(xPos+Width/2,yPos+Height/2,Width,Height,10);    
        fill(BackgroundColor); color(255);stroke(255);strokeWeight(1);
        fill(35);
        rectMode(CENTER);
        rect(xPos+Width/2,yPos+Height/2,Width-10,Height-10,10);            // outline
        textAlign(CENTER);textSize(14);
        
       
                          
        fill(255);
        text(Title,xPos+Width/2+15,yPos+40);  // Heading Title
        
        textAlign(CENTER);textSize(10);
        text(xLabel,xPos+Width/2+15,yPos+Height-15);                     // x-axis Label 
        
        rotate(-PI/2);                                               // rotate -90 degrees
        text(yLabel,-yPos-Height/2,xPos+15);                   // y-axis Label  
        rotate(PI/2);                                                // rotate back
        
        textSize(10); noFill(); stroke(255); smooth();strokeWeight(1);
          //Edges
          line(xPos+50,yPos+0.85*Height,xPos+50,yPos+20);                        // y-axis line 
          line(xPos+50,yPos+0.85*Height,xPos+Width-20,yPos+0.85*Height);           // x-axis line 
          
           stroke(255);
           if (int(max(AltitudeY))<0){
             line(xPos+50,                                       // zero line 
                         yPos+20,   // 
                         xPos+Width-20,
                         yPos+20
                         );
          }
          else if (int(min(AltitudeY))>0){
             line(xPos+50,                                       // zero line 
                         yPos+0.85*Height,   // 
                         xPos+Width-20,
                         yPos+0.85*Height
                         );
          }
          else{
           line(xPos+50,                                       // zero line 
                         yPos+Height-(abs(yMin)/(yMax-yMin))*Height,   // 
                         xPos+Width-20,
                         yPos+Height-(abs(yMin)/(yMax-yMin))*Height
                         );
          }
          if(yMin<0){
                    line(xPos+50,                                       // zero line 
                         yPos+Height-(abs(yMin)/(yMax-yMin))*Height,   // 
                         xPos+Width-20,
                         yPos+Height-(abs(yMin)/(yMax-yMin))*Height
                         );
          
                    
          }
          
          if(RightAxis){                                       // Right-axis line   
              stroke(0);
              line(xPos+Width+3,yPos+Height,xPos+Width+3,yPos);
            }
            
           /*  =========================================================================================
                Sub-devisions for both axes, left and right
               ==========================================================================================  */
            
            stroke(255);
            
           for(int x=0; x<=xDiv; x++){
       
            /*  =========================================================================================
                  x-axis
                ==========================================================================================  */
            float xpos = (Width-70);
            line(float(x)/xDiv*xpos+xPos+50,yPos+0.85*Height,       //  x-axis Sub devisions    
                 float(x)/xDiv*xpos+xPos+50,yPos+0.85*Height+5);     
                 
            textSize(10);                                      // x-axis Labels
            String xAxis=str(xMin+float(x)/xDiv*(xMax-xMin));  // the only way to get a specific number of decimals 
            String[] xAxisMS=split(xAxis,'.');                 // is to split the float into strings 
            text(xAxisMS[0]+"."+xAxisMS[1].charAt(0),          // ...
                 float(x)/xDiv*xpos+xPos+50,yPos+0.85*Height+15);   // x-axis Labels
          }
          
          
           /*  =========================================================================================
                 left y-axis
               ==========================================================================================  */
       
          for(int y=0; y<=yDiv; y++){
            
            float ypos = 0.85*Height-20;
            line(xPos+50,float(y)/yDiv*ypos+yPos+20,                // ...
                  xPos+46,float(y)/yDiv*ypos+yPos+20);              // y-axis lines 
            
            textAlign(RIGHT);fill(255);
            
            String yAxis=str(yMin+float(y)/yDiv*(yMax-yMin));     // Make y Label a string
            String[] yAxisMS=split(yAxis,'.');                    // Split string
           
            text(yAxisMS[0]+"."+yAxisMS[1].charAt(0),             // ... 
                 xPos+40,float(yDiv-y)/yDiv*ypos+yPos+24);       // y-axis Labels 
                        
                        
            /*  =========================================================================================
                 right y-axis
                ==========================================================================================  */
            
            if(RightAxis){
             
              color(GraphColor); stroke(GraphColor);fill(255);
            
              line(xPos+Width+3,float(y)/yDiv*Height+yPos,             // ...
                   xPos+Width+7,float(y)/yDiv*Height+yPos);            // Right Y axis sub devisions
                   
              textAlign(LEFT); 
            
              String yAxisRight=str(yMinRight+float(y)/                // ...
                                yDiv*(yMaxRight-yMinRight));           // convert axis values into string
              String[] yAxisRightMS=split(yAxisRight,'.');             // 
           
               text(yAxisRightMS[0]+"."+yAxisRightMS[1].charAt(0),     // Right Y axis text
                    xPos+Width+15,float(yDiv-y)/yDiv*Height+yPos+3);   // it's x,y location
            
              noFill();
            }stroke(255);
            
          
          }
          
 
      }

   /*  =========================================================================================
       Streight line graph 
       ==========================================================================================  */
       
           void Altitude(float[] x1 ,float[] AltitudeY) {
          
        //yPos+Height-(abs(yMin)/(yMax-yMin))*Height
        for (int k = 0; k<x1.length-1; k++){
      strokeWeight(2);fill(255);
      
      line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -AltitudeY[k]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -AltitudeY[k+1]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      AltitudeY[k]=AltitudeY[k+1];
      //AltitudeY[k] = 5;
      //AltitudeY[0] = -k;
      
    }
    
        /*printArray(y);
         for (int i=0; i<(x.length-1); i++){
                    strokeWeight(2);stroke(GraphColor);noFill();smooth();
           line((x[i]-x[0])/(x[x.length-1]-x[0]+200),
                                            yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height,
                                            (x[i]-x[0])/(x[x.length-1]-x[0])+200,
                                            yPos+Height-(y[i+1]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height);
                                           
                             
      }*/
      }
     
     
       void Rotation(float[] x1 ,float[] RotationY) {
      /*if (int(max(RotationY))<0){
           Rotation.yMin=int((min(RotationY))); 
           Rotation.yMax=int(0);
          }
          else if (int(min(RotationY))>0){
           Rotation.yMin= 0; 
           Rotation.yMax=int((max(RotationY)));
          }
          else{
          Rotation.yMin=int(1.5*min(RotationY)); 
           Rotation.yMax=int(1.5*max(RotationY));
          }*/
        //yPos+Height-(abs(yMin)/(yMax-yMin))*Height
        for (int k = 0; k<x1.length-1; k++){
      strokeWeight(2);stroke(GraphColor);noFill();smooth();
      /*if (int(max(RotationY))<0){
           Rotation.yMin=int((min(RotationY))); 
           Rotation.yMax=int(0);
          }
          else if (int(min(RotationY))>0){
           Rotation.yMin= 0; 
           Rotation.yMax=int((max(RotationY)));
          }
          else{
          Rotation.yMin=int(1.5*min(RotationY)); 
           Rotation.yMax=int(1.5*max(RotationY));
          }*/
      line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -RotationY[k]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -RotationY[k+1]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      RotationY[k]=RotationY[k+1];
      //y1[k] = 5;
      //y1[0] = -k;
                             
      }}
      
      
      void OrientationX(float[] x1 ,float[]OrientationxY ) {
          
      strokeWeight(2);fill(255);
     
     for (int k = 0; k<x1.length-1; k++){
      line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -OrientationxY[k]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -OrientationxY[k+1]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      OrientationxY[k]=OrientationxY[k+1];
      //y1[k] = 5;
      //y1[0] = -k;
      }
      }
      
      
      
        void OrientationY(float[] x1 ,float[] OrientationyY) {
 
        for (int k = 0; k<x1.length-1; k++){
      strokeWeight(2);fill(255);
      
      line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -OrientationyY[k]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -OrientationyY[k+1]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      OrientationyY[k]=OrientationyY[k+1];
      //y1[k] = 5;
      //y1[0] = -k;
                             
      }}
      
      
      
      
        void OrientationZ(float[] x1 ,float[] OrientationzY) {
        
        //yPos+Height-(abs(yMin)/(yMax-yMin))*Height
        for (int k = 0; k<x1.length-1; k++){
      strokeWeight(2);fill(255);
      
      line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -OrientationzY[k]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -OrientationzY[k+1]*(0.85*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      OrientationzY[k]=OrientationzY[k+1];
      //y1[k] = 5;
      //y1[0] = -k;
                             
      }

       
 
       
    }
    

      
    }
 
