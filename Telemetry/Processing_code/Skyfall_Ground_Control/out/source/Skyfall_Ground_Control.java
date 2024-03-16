import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.Frame; 
import java.awt.BorderLayout; 
import controlP5.*; 
import processing.serial.*; 
import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Skyfall_Ground_Control extends PApplet {

// import libraries


 // http://www.sojamo.de/libraries/controlP5/



/* SETTINGS BEGIN */

// Serial port to connect to
String serialPortName = "/dev/tty.usbmodem1411";

// If you want to debug the plotter without using a real serial port set this to true
boolean mockupSerial = true;

/* SETTINGS END */
//test
Serial serialPort;// Serial port object
SoundFile countdown;

// interface stuff
ControlP5 cp5;
Button launchButton;
Textfield securityCode;
Button Pray;
Button Abort;



// Settings for the plotter are saved in this file
JSONObject plotterConfigJSON;
float angle = 0;
PShape Skyfall;
PFont font,font1;
PGraphics canvas3D;
Table table;
PImage icon = loadImage("icon.png");



int x = 100;
int y = 200;
int SizeX = 1920;
int SizeY = 1080;
int scale = 10;
int add = 0;
int speed = 100;
int millisecs;
int seconds;
int minutes;
int GCT;
int altitudeMax;
int abortTime;
int timeLeft = 10;

boolean start = false;
boolean clicked = false;
boolean RightCode = false;
boolean aborted = false;
boolean launched = false;
boolean connectionFailed;
boolean starter;
float valueY;

// plots
Graph Altitude = new Graph(0,PApplet.parseInt(SizeY*0.24f), PApplet.parseInt(SizeX*0.2f), PApplet.parseInt(SizeY*0.36f), color (20, 20, 200));
Graph Rotation = new Graph(0,PApplet.parseInt(SizeY*0.6f), PApplet.parseInt(SizeX*0.2f), PApplet.parseInt(SizeY*0.36f), color (20, 20, 200));
Graph OrientationX = new Graph(PApplet.parseInt(SizeX*0.1f),0, PApplet.parseInt(SizeX*0.2f), PApplet.parseInt(SizeY*0.24f), color (20, 20, 200));
Graph OrientationY = new Graph(PApplet.parseInt(SizeX*0.3f),0, PApplet.parseInt(SizeX*0.2f), PApplet.parseInt(SizeY*0.24f), color (20, 20, 200));
Graph OrientationZ = new Graph(PApplet.parseInt(SizeX*0.5f),0, PApplet.parseInt(SizeX*0.2f), PApplet.parseInt(SizeY*0.24f), color (20, 20, 200));

float [] x1 = new float[1];
float [] AltitudeY = new float [1];
float [][] RotationY = new float [3][1];
float [] OrientationxY = new float [1];
float [] OrientationyY = new float [1];
float [] OrientationzY = new float [1];

float[] barChartValues = new float[6];
float[][] AltitudeValues = new float[1][1000];
float[] AltitudeSampleNumbers = new float[1000];
int[] graphColors = new int[6];



// helper for saving the executing path
String topSketchPath = "";
String code = "1402";

public void settings() {
  size(SizeX, SizeY, P3D);//define the size of the window

}

public void setup() {
  
  
  
  
  cp5 = new ControlP5(this);
  surface.setTitle("Skyfall Ground Control");//create the title of the window
  surface.setResizable(true);//define as non-resizable
  surface.setLocation(0,0);//define the loading location of the window
  surface.setIcon(icon);
  background(0);
  font = createFont("CircularStd-Book.otf",30);
  font1 = createFont("CircularStd-Book.otf",15);
  
  table = new Table();
  
  table.addColumn("Time");
  table.addColumn("Altitude");
  table.addColumn("OrientationX");
  table.addColumn("OrientationY");
  table.addColumn("OrientationZ");
  
    
  
  
  // set line graph colors
  graphColors[0] = color(131, 255, 20);
  graphColors[1] = color(232, 158, 12);
  graphColors[2] = color(255, 0, 0);
  graphColors[3] = color(62, 12, 232);
  graphColors[4] = color(13, 255, 243);
  graphColors[5] = color(200, 46, 232);

  // settings save file
  topSketchPath = sketchPath();
  plotterConfigJSON = loadJSONObject(topSketchPath+"/plotter_config.json");

  // gui
  
  launchButton = cp5.addButton("Launch")
      .setPosition(0.785f*width,0.33f*height)
      .setSize(PApplet.parseInt(0.22f*width-5),PApplet.parseInt(0.1f*height))
      .setColorBackground(color(179,192,164))
      .setColorForeground(color(209,222,194))
      .setColorActive(color(50,50,50))
      .setColorLabel(color(80,81,104))
      .setFont(font);

  
  // init charts
  setChartSettings();

  
 
  
  // start serial communication
    if (!mockupSerial) {
    //String serialPortName = Serial.list()[3];
    serialPort = new Serial(this, serialPortName, 115200);
  }
  else
    serialPort = null;

  // build the gui

  
  canvas3D = createGraphics(PApplet.parseInt(width), PApplet.parseInt(height), P3D);//create a graphic area for the 3D model
  //load 3D model, resize and rotate
  Skyfall = loadShape("Skyfall.obj");
  Skyfall.scale(0.3f,0.3f,0.3f);
  Skyfall.rotateX(PI);
  countdown = new SoundFile(this, "countdown.MP3");
}

byte[] inBuffer = new byte[100]; // holds serial message
int i = 0; // loop variable


public void draw() {
  
    background(0);
  /* Read serial and update values */
    if (mockupSerial || serialPort.available() > 0) {
      String myString = "";
      if (!mockupSerial) {
        try {
        serialPort.readBytesUntil('\r', inBuffer);
            }
        catch (Exception e) {
        }
        myString = new String(inBuffer);
        }
      else {
     //AltitudeY[AltitudeY.length-1] = AltitudeY[AltitudeY.length-1]+random(-10,10);
     x1 = append(x1, add);
     AltitudeY =append(AltitudeY, AltitudeY[AltitudeY.length-1]+random(-10,10));
    //for (int i = 0; i < RotationY.length; i++){
    for(int k=0; k<RotationY.length; k++){
    RotationY[k] = append(RotationY[k],RotationY[k][RotationY[k].length-1]+random(-1,1));
    //printArray(RotationY[k]);
    }
    
      //  }

    OrientationxY = append(OrientationxY, OrientationxY[OrientationxY.length-1]+random(-1,1));
    OrientationyY = append(OrientationyY, OrientationyY[OrientationyY.length-1]+random(-1,1));
    OrientationzY = append(OrientationzY, OrientationzY[OrientationzY.length-1]+random(-1,1));
        myString = mockupSerialFunction();
        
        add ++;
           }
           
           
           
     if(altitudeMax < PApplet.parseInt(max(AltitudeY)))
     {altitudeMax = PApplet.parseInt(max(AltitudeY));
   }
     textFont(font);
    textSize(20);smooth();
    textAlign(CENTER);
    text("Alt. max : " + str(altitudeMax)+" m",0.0625f*width,0.297f*height);    

    //println(myString);

    // split the string at delimiter (space)
    String[] nums = split(myString, ' ');
  
    
    GCT = millis();
    int s = second();  
    int m = minute();  
    int h = hour(); 
    
    //textSize(30);
    textFont(font);
    textSize(40);smooth();
    textAlign(CENTER);
    text("SkyFall",0.895f*width,50);
    textSize(25);
    textAlign(CENTER);
    text("Ground Control",0.895f*width,80);
    textSize(20);
    textAlign(CENTER);
    text("CLT"+" : " + str(h)+":"+str(m)+":"+str(s)+" CT",0.895f*width,120);
    
    if (GCT/100  % 10 != millisecs){
    millisecs++;
    }
    if (millisecs >= 10){
    millisecs -= 10;
    seconds++;
    }
    if (seconds >= 60){
    seconds -= 60;
    minutes++;
    }
    textSize(20);
    textAlign(CENTER);
    text("CLT"+" : " + nf(minutes, 2) + ":" + nf(seconds, 2) + "." + nf(millisecs, 1) ,0.895f*width,150);



    setChartSettings();
  
  canvas3D.beginDraw();
  canvas3D.background(0);
  //canvas3D.setSize(int(width*0.25), int(height*0.6));
  canvas3D.translate(width*0.25f, height*0.6f);
  //canvas3D.rotateX(xangle/(PI/180)/1000);
  //canvas3D.rotateY(yangle/(PI/180)/1000);
  //canvas3D.rotateZ(/(PI/180));
  canvas3D.rotateX((OrientationxY[OrientationxY.length-1]*PI)/180);
  canvas3D.rotateY((OrientationyY[OrientationyY.length-1]*PI)/180);
  canvas3D.rotateZ((OrientationzY[OrientationzY.length-1]*PI)/180);
  canvas3D.shape(Skyfall);
  canvas3D.endDraw();
  pushMatrix();
  image(canvas3D, width*0.2f, height*0.24f);
  
  
  popMatrix();
  stroke(100);     //stroke color
  strokeWeight(10);   
  


    
    // count number of bars and line graphs to hide
    int numberOfInvisibleBars = 0;
    for (i=0; i<6; i++) {
      if (PApplet.parseInt(getPlotterConfigString("bcVisible"+(i+1))) == 0) {
        numberOfInvisibleBars++;
      }
    }
    int numberOfInvisibleAltitudes = 0;
    for (i=0; i<6; i++) {
      if (PApplet.parseInt(getPlotterConfigString("lgVisible"+(i+1))) == 0) {
        numberOfInvisibleAltitudes++;
      }
    }
    // build a new array to fit the data to show
    barChartValues = new float[6-numberOfInvisibleBars];

    // build the arrays for bar charts and line graphs
    int barchartIndex = 0;
    for (i=0; i<nums.length; i++) {

      // update barchart
      try {
        if (PApplet.parseInt(getPlotterConfigString("bcVisible"+(i+1))) == 1) {
          if (barchartIndex < barChartValues.length)
            barChartValues[barchartIndex++] = PApplet.parseFloat(nums[i])*PApplet.parseFloat(getPlotterConfigString("bcMultiplier"+(i+1)));
        }
        else {
        }
      }
      catch (Exception e) {
      }

      // update line graph
      try {
        if (i<AltitudeValues.length) {
          for (int k=0; k<AltitudeValues[i].length-1; k++) {
            AltitudeValues[i][k] = AltitudeValues[i][k+1];
          }

          AltitudeValues[i][AltitudeValues[i].length-1] = PApplet.parseFloat(nums[i])*PApplet.parseFloat(getPlotterConfigString("lgMultiplier"+(i+1)));
        }
      }
      catch (Exception e) {
      }
    }
  }


  // draw the line graphs
  
  //Graph.Altitude(0,0,100,100,color (20, 20, 200));
 
  Altitude.DrawAxis();
  for (int i=0;i<AltitudeValues.length; i++) {
    //Altitude.yMin=int(min(AltitudeY)); 
    //Altitude.yMax=int(max(AltitudeY)); 
    Altitude.GraphColor = graphColors[i];
    if (PApplet.parseInt(getPlotterConfigString("lgVisible"+(i+1))) == 1)
      Altitude.Altitude(x1, AltitudeY);
    
  }
 Rotation.DrawAxis();
  for (int i=0;i<RotationY.length; i++) {
    Rotation.GraphColor = graphColors[i];
      Rotation.Rotation(x1, RotationY[i]);
     
  }
  
  OrientationX.DrawAxis();
  for (int i=0;i<AltitudeValues.length; i++) {
    OrientationX.GraphColor = graphColors[i];
    if (PApplet.parseInt(getPlotterConfigString("lgVisible"+(i+1))) == 1)
      OrientationX.OrientationX(x1, OrientationxY);
     
  }
  
  OrientationY.DrawAxis();
  for (int i=0;i<AltitudeValues.length; i++) {
    OrientationY.GraphColor = graphColors[i];
    if (PApplet.parseInt(getPlotterConfigString("lgVisible"+(i+1))) == 1)
      OrientationY.OrientationY(x1, OrientationyY);
     
  }
  
  OrientationZ.DrawAxis();
  for (int i=0;i<AltitudeValues.length; i++) {
    OrientationZ.GraphColor = graphColors[i];
    if (PApplet.parseInt(getPlotterConfigString("lgVisible"+(i+1))) == 1)
      OrientationZ.OrientationZ(x1, OrientationzY);
     
  }
        
    if(RightCode){
     Pray.remove();
     securityCode.remove();
     launchButton.remove();
      if (PApplet.parseInt(millis()/100)  % 10 != abortTime && launched==false){
      abortTime++;
      println(abortTime);
      }
      if (abortTime >= 10){
      abortTime -= 10;
      timeLeft = timeLeft-1;
      Abort.setLabel("Abort : " + str(timeLeft));
      }
      if(timeLeft == 0){
        launched = true;
        Abort.remove();
        rectMode(CORNER);fill(color(197,222,205));noStroke();
        rect(0.785f*width,0.33f*height,0.22f*width-5,0.1f*height);
         fill(color(80,81,104));textFont(font);
        textAlign(CENTER, CENTER);
        text("LAUNCHED",0.895f*width,0.38f*height); 
    
        
      }
     
    }
     else{
     launchButton.setPosition(0.785f*width,0.33f*height)
                .setSize(PApplet.parseInt(0.22f*width-5),PApplet.parseInt(0.1f*height))
                .listenerSize();
     }
     
    if(aborted){
   
    rectMode(CORNER);fill(color(154,3,30));noStroke();
    rect(0.785f*width,0.33f*height,0.22f*width-5,0.1f*height);
     fill(color(80,81,104));textFont(font);
    textAlign(CENTER, CENTER);
    text("ABORTED",0.895f*width,0.38f*height); 
    noLoop();
    
    }

   
}
// called each time the chart settings are changed by the user 
public void setChartSettings() {
  
  if (PApplet.parseInt(max(AltitudeY))<0){
           Altitude.yMin=PApplet.parseInt((min(AltitudeY))); 
           Altitude.yMax=PApplet.parseInt(0);
          }
          else if (PApplet.parseInt(min(AltitudeY))>0){
           Altitude.yMin= 0; 
           Altitude.yMax=PApplet.parseInt((max(AltitudeY)));
          }
          else{
          Altitude.yMin=PApplet.parseInt(1.5f*min(AltitudeY)); 
           Altitude.yMax=PApplet.parseInt(1.5f*max(AltitudeY));
          }
          
 if (PApplet.parseInt(max(OrientationxY))<0){
           OrientationX.yMin=PApplet.parseInt((min(OrientationxY))); 
           OrientationX.yMax=PApplet.parseInt(0);
          }
          else if (PApplet.parseInt(min(OrientationxY))>0){
           OrientationX.yMin= 0; 
           OrientationX.yMax=PApplet.parseInt((max(OrientationxY)));
          }
          else{
          OrientationX.yMin=PApplet.parseInt(1.5f*min(OrientationxY)); 
           OrientationX.yMax=PApplet.parseInt(1.5f*max(OrientationxY));
          }
          
if (PApplet.parseInt(max(OrientationyY))<0){
           OrientationY.yMin=PApplet.parseInt((min(OrientationyY))); 
           OrientationY.yMax=PApplet.parseInt(0);
          }
          else if (PApplet.parseInt(min(OrientationyY))>0){
           OrientationY.yMin= 0; 
           OrientationY.yMax=PApplet.parseInt((max(OrientationyY)));
          }
          else{
          OrientationY.yMin=PApplet.parseInt(1.5f*min(OrientationyY)); 
           OrientationY.yMax=PApplet.parseInt(1.5f*max(OrientationyY));
          }
          
if (PApplet.parseInt(max(OrientationzY))<0){
           OrientationZ.yMin=PApplet.parseInt((min(OrientationzY))); 
           OrientationZ.yMax=PApplet.parseInt(0);
          }
          else if (PApplet.parseInt(min(OrientationzY))>0){
           OrientationZ.yMin= 0; 
           OrientationZ.yMax=PApplet.parseInt((max(OrientationzY)));
          }
          else{
          OrientationZ.yMin=PApplet.parseInt(1.5f*min(OrientationzY)); 
           OrientationZ.yMax=PApplet.parseInt(1.5f*max(OrientationzY));
          }
  Altitude.xLabel=" Time ";
  Altitude.yLabel="Altitude";
  Altitude.Title="Altitude";  
  Altitude.yDiv = 10;
  Altitude.xDiv=6;  
  Altitude.xMax=0; 
  Altitude.xMin=-100; 
  Altitude.xPos=0;
  Altitude.yPos=PApplet.parseInt(height*0.33f);
  Altitude.Width=PApplet.parseInt(width*0.25f);
  Altitude.Height=PApplet.parseInt(height*0.33f);
  //Altitude.yMin=int(min(AltitudeY)); 
  //Altitude.yMax=int(max(AltitudeY)); 

  //Altitude.yMin = -1000;
  //Altitude.yMax = 1000;
  
  //Altitude.yMin=int(min(AltitudeY));
  
  
  Rotation.xLabel=" Time ";
  Rotation.yLabel="Rotation";
  Rotation.Title="Rotation";  
  Rotation.yDiv = 10;
  Rotation.xDiv=6;  
  Rotation.xMax=0; 
  Rotation.yMax=100;
  Rotation.yMin=-100;
  Rotation.xMin=-100; 
  Rotation.xPos=0;
  Rotation.yPos=PApplet.parseInt(height*0.66f);
  Rotation.Width=PApplet.parseInt(width*0.25f);
  Rotation.Height=PApplet.parseInt(height*0.33f);

  OrientationX.yLabel="Orientation X";
  OrientationX.Title="Orientation";  
  OrientationX.yDiv = 10;
  OrientationX.xDiv=6;  
  OrientationX.xMax=100; 
  OrientationX.xMin=0; 
  OrientationX.xPos=PApplet.parseInt(width*0.125f);
  OrientationX.yPos=0;
  OrientationX.Width=PApplet.parseInt(width*0.22f);
  OrientationX.Height=PApplet.parseInt(height*0.33f);

  
  OrientationY.yLabel="Orientation Y";
  OrientationY.Title="Orientation";  
  OrientationY.yDiv = 10;
  OrientationY.xDiv=6;  
  OrientationY.xMax=0; 
  OrientationY.xMin=-100; 
  OrientationY.xPos=PApplet.parseInt(width*0.345f);
  OrientationY.yPos=0;
  OrientationY.Width=PApplet.parseInt(width*0.22f);
  OrientationY.Height=PApplet.parseInt(height*0.33f);

  
  OrientationZ.yLabel="Orientation Y";
  OrientationZ.Title="Orientation";  
  OrientationZ.yDiv = 10;
  OrientationZ.xDiv=6;  
  OrientationZ.xMax=0; 
  OrientationZ.xMin=-100; 
  OrientationZ.xPos=PApplet.parseInt(width*0.565f);
  OrientationZ.yPos=0;
  OrientationZ.Width=PApplet.parseInt(width*0.22f);
  OrientationZ.Height=PApplet.parseInt(height*0.33f);

 

}

// handle gui actions
public void controlEvent(ControlEvent theEvent) {
  if (theEvent.isAssignableFrom(Textfield.class) || theEvent.isAssignableFrom(Toggle.class) || theEvent.isAssignableFrom(Button.class)) {
    String parameter = theEvent.getName();
    String value = "";
    if (theEvent.isAssignableFrom(Textfield.class))
      value = theEvent.getStringValue();
    else if (theEvent.isAssignableFrom(Toggle.class) || theEvent.isAssignableFrom(Button.class))
      value = theEvent.getValue()+"";

    plotterConfigJSON.setString(parameter, value);
    saveJSONObject(plotterConfigJSON, topSketchPath+"/plotter_config.json");
  }
  setChartSettings();
}

// get gui settings from settings file
public String getPlotterConfigString(String id) {
  String r = "";
  try {
    r = plotterConfigJSON.getString(id);
  } 
  catch (Exception e) {
    r = "";
  }
  return r;
}

public void Launch(){
    println("clicked");
    
      if (clicked && aborted == false){
      
      securityCode.remove();
      Pray.remove();
      clicked=false;
      launchButton.setLabel("Launch").setColorBackground(color(179,192,164)).setColorForeground(color(209,222,194));
      }
      else{
       securityCode = cp5.addTextfield("Security Code")
      .setPosition(0.785f*width,0.43f*height)
      .setSize(PApplet.parseInt(0.15f*width),PApplet.parseInt(0.05f*height))
      //.setColorBackground( color( 255,255,255 ) )
      .setFont(font1);
      //.setLabel("");
     Pray = cp5.addButton("Pray")
      .setPosition(0.935f*width,0.43f*height)
      .setSize(PApplet.parseInt(0.065f*width-5),PApplet.parseInt(0.05f*height))
      .setColorBackground( color( 80,81,104 ) )
      .setFont(font1);
      launchButton.setLabel("Cancel").setColorBackground(color(220,196,142)).setColorForeground(color(250,226,172));
      clicked=true;
      
      }
    }
    
public void Pray(){
    
    String tryCode = cp5.get(Textfield.class,"Security Code").getText();
    
    if(tryCode.equals(code) && aborted == false){
      Abort = cp5.addButton("Abort")
      .setPosition(0.785f*width,0.33f*height)
      .setSize(PApplet.parseInt(0.22f*width-5),PApplet.parseInt(0.1f*height))
      .setColorBackground(color(154,3,30))
      .setColorForeground(color(184,33,60))
      .setColorActive(color(50,50,50))
      .setColorLabel(color(80,81,104))
      .setFont(font);
                 
                 
      println("Code OK");
      RightCode = true;
      println("Launch in 10s");
      countdown.play();
      
      println(tryCode);
     
        
    }
    
    }
    
    
public void Abort(){
    aborted = true;
    countdown.stop();
    Abort.remove();
    }

// Settings for the control panel are saved in this file
JSONObject robotConfigJSON;

// the ControlFrame class extends PApplet, so we 
// are creating a new processing applet inside a
// new frame with a controlP5 object loaded
public class ControlFrame extends PApplet {

  int w, h;

  int abc = 100;

  public void settings() {
    size(w, h);
    
  }

  public void setup() {
    surface.setLocation(100, 100);
    surface.setResizable(false);
    surface.setVisible(true);
    frameRate(25);
    cp5 = new ControlP5(this);

    robotConfigJSON = loadJSONObject(topSketchPath+"/robot_config.json");
    //printArray(json.getJSONObject("sensors2").getJSONObject("1"));

    // speed PID
    int x;
    int y;
    cp5.addTextlabel("label").setText("Speed PID (outer loop)").setPosition(x=5, y=5).setFont(createFont("Georgia", 12));
    cp5.addTextfield("speed-PID Kp").setPosition(x=x+5, y=y+20).setText(getConfigString("speedPIDKp")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("speed-PID Ki").setPosition(x, y=y+40).setText(getConfigString("speedPIDKi")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("speed-PID Kd").setPosition(x, y=y+40).setText(getConfigString("speedPIDKd")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("speed-PID Output LowerLimit").setPosition(x, y=y+60).setText(getConfigString("speedPIDOutputLowerLimit")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("speed-PID Output HigherLimit").setPosition(x, y=y+40).setText(getConfigString("speedPIDOutputHigherLimit")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("speed-PID Sampling").setPosition(x, y=y+40).setText(getConfigString("speedPIDSampling")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("motor Speed SensorSampling").setPosition(x, y=y+40).setText(getConfigString("motorSpeedSensorSampling")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("speed Kalman FilterR").setPosition(x, y=y+40).setText(getConfigString("speedKalmanFilterR")).setWidth(40).setAutoClear(false);

    // angple PID
    x = x+150;
    y = 5;
    cp5.addTextlabel("Angle PID (outer loop)").setText("Angle PID (outer loop)").setPosition(x, y).setFont(createFont("Georgia", 12));
    x = x + 5;
    y = y+20;
    cp5.addTextlabel("Conservative").setText("Conservative").setPosition(x, y).setFont(createFont("Georgia", 12));
    x = x + 100;
    cp5.addTextlabel("Aggressive").setText("Aggressive").setPosition(x, y).setFont(createFont("Georgia", 12));
    x = x - 95;
    y = y+20;
    cp5.addTextfield("angle-PID ConKp").setPosition(x, y).setText(getConfigString("anglePIDConKp")).setWidth(40).setAutoClear(false);
    x = x + 100;
    cp5.addTextfield("angle-PID AggKp").setPosition(x, y).setText(getConfigString("anglePIDAggKp")).setWidth(40).setAutoClear(false);
    x = x - 100;
    y = y+40;
    cp5.addTextfield("angle-PID ConKi").setPosition(x, y).setText(getConfigString("anglePIDConKi")).setWidth(40).setAutoClear(false);
    x = x + 100;
    cp5.addTextfield("anglePIDAggKi").setPosition(x, y).setText(getConfigString("anglePIDAggKi")).setWidth(40).setAutoClear(false);
    x = x - 100;
    y = y+40;
    cp5.addTextfield("angle-PID ConKd").setPosition(x, y).setText(getConfigString("anglePIDConKd")).setWidth(40).setAutoClear(false);
    x = x + 100;
    cp5.addTextfield("angle-PID AggKd").setPosition(x, y).setText(getConfigString("anglePIDAggKd")).setWidth(40).setAutoClear(false);

    // angle general      
    cp5.addTextfield("angle-PID LowerLimit").setPosition(x=x-100, y=y+60).setText(getConfigString("anglePIDLowerLimit")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("calibrated Zero Angle").setPosition(x, y=y+40).setText(getConfigString("calibratedZeroAngle")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("angle-PID Sampling").setPosition(x, y=y+40).setText(getConfigString("anglePIDSampling")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("angle Sensor Sampling").setPosition(x, y=y+40).setText(getConfigString("angleSensorSampling")).setWidth(40).setAutoClear(false);
    cp5.addTextfield("angle Kalman FilterR").setPosition(x, y=y+40).setText(getConfigString("angleKalmanFilterR")).setWidth(40).setAutoClear(false);

    // Debug
    cp5.addTextlabel("Debug").setText("Debug").setPosition(x=x+200, y=5).setFont(createFont("Georgia", 12));
    cp5.addToggle("debug Level").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("debugLevel"))).setMode(ControlP5.SWITCH);
    cp5.addTextfield("debug Sample Rate").setPosition(x, y=y+40).setText(getConfigString("debugSampleRate")).setWidth(40).setAutoClear(false);
    cp5.addToggle("speed-PID OutputDebug").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("speedPIDOutputDebug"))).setMode(ControlP5.SWITCH);
    cp5.addToggle("speed-PID InputDebug").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("speedPIDInputDebug"))).setMode(ControlP5.SWITCH);
    cp5.addToggle("speed Kalman FilterDebug").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("speedKalmanFilterDebug"))).setMode(ControlP5.SWITCH);
    cp5.addToggle("angle-PID SetpointDebug").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("anglePIDSetpointDebug"))).setMode(ControlP5.SWITCH);
    cp5.addToggle("angle-PID InputDebug").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("anglePIDInputDebug"))).setMode(ControlP5.SWITCH);
    cp5.addToggle("angle-PID OutputDebug").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("anglePIDOutputDebug"))).setMode(ControlP5.SWITCH);
    cp5.addToggle("speed RawDebug").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("speedRawDebug"))).setMode(ControlP5.SWITCH);
    cp5.addToggle("angle RawDebug").setPosition(x, y=y+40).setValue(PApplet.parseInt(getConfigString("angleRawDebug"))).setMode(ControlP5.SWITCH);

    PImage[] imgs = {
      loadImage(topSketchPath+"/images/button_a.png"), loadImage(topSketchPath+"/images/button_b.png"), loadImage(topSketchPath+"/images/button_c.png")
    };
    
    x = 200;
    cp5.addButton("moveForwards").setValue(1).setPosition(x, y=y+60).setImages(imgs).updateSize();
    cp5.addButton("moveBackwards").setValue(1).setPosition(x, y=y+60).setImages(imgs).updateSize();
    cp5.addButton("turnLeft").setValue(1).setPosition(x=x-60, y).setImages(imgs).updateSize();
    cp5.addButton("turnRight").setValue(1).setPosition(x=x+120, y).setImages(imgs).updateSize();
    
    cp5.addButton("start").setValue(1).setPosition(x=x-250, y=y-60);
    cp5.addButton("stop1").setValue(1).setPosition(x, y=y+40); // Do not run when setting  "stop"
    cp5.addButton("calibrate").setValue(1).setPosition(x, y=y+40);
  }



  public void controlEvent(ControlEvent theEvent) {
    print(theEvent);
    if (theEvent.isAssignableFrom(Textfield.class) || theEvent.isAssignableFrom(Toggle.class) || theEvent.isAssignableFrom(Button.class)) {
      String parameter = theEvent.getName();
      String value = "";
      if (theEvent.isAssignableFrom(Textfield.class))
        value = theEvent.getStringValue();
      else if (theEvent.isAssignableFrom(Toggle.class) || theEvent.isAssignableFrom(Button.class))
        value = theEvent.getValue()+"";

      robotConfigJSON.setString(parameter, value);
      saveJSONObject(robotConfigJSON, topSketchPath+"/robot_config.json");
      if (!mockupSerial) {
        serialPort.write("set "+parameter+" "+value+";");
        serialPort.clear();
      }
      print("set "+parameter+" "+value+";\n");
      /*for (int i=0; i<inBuffer.length; i++) {
       inBuffer[i] = 0;  
       }*/
    }
  }

  public void draw() {
    background(abc);
  }

  private ControlFrame() {
  }

  public ControlFrame(PApplet theParent, int theWidth, int theHeight, String _name) {
    parent = theParent;
    w = theWidth;
    h = theHeight;
    PApplet.runSketch(new String[]{this.getClass().getName()}, this);
  }


  public ControlP5 control() {
    return cp5;
  }


  ControlP5 cp5;

  PApplet parent;
}
public String getConfigString(String id) {
  String r = "";
  try {
    r = robotConfigJSON.getString(id);
  } 
  catch (Exception e) {
    r = "";
  }
  return r;
}
public void addRowsToTable(Table table, int numRows, float[] x1,
                                                        float[] AltitudeY,float[] RotationxY,
                                                        float[] RotationyY, float[] RotationzY) 
  {
  for (int i = 0; i < x1.length; i++) {
    TableRow newRow = table.addRow();
    newRow.setFloat("Time", x1[i]);
    newRow.setFloat("Altitde", AltitudeY[i]);
    newRow.setFloat("RotationX", RotationxY[i]);
    newRow.setFloat("RotationY", RotationyY[i]);
    newRow.setFloat("RotationZ", RotationzY[i]);
  }
}
  
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
     




      int   GraphColor;
      int   BackgroundColor=color(255);  
      int   StrokeColor=color(180);     
      
      String  Title="Title";          // Default titles
      String  xLabel="x - Label";
      String  yLabel="y - Label";

      float   yMax=1024, yMin=-100;      // Default axis dimensions
      float   xMax=10, xMin=0;
      float   yMaxRight=1024,yMinRight=0;
  
      PFont   Font;                   // Selected font used for text 
     
      
  //    int Peakcounter=0,nPeakcounter=0;
     
      Graph(int x, int y, int w, int h,int k) {  // The main declaration function
        xPos = x;
        yPos = y;
        Width = w;
        Height = h;
        GraphColor = k;      
      }
    
     
       public void DrawAxis(){
       
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
          line(xPos+50,yPos+0.85f*Height,xPos+50,yPos+20);                        // y-axis line 
          line(xPos+50,yPos+0.85f*Height,xPos+Width-20,yPos+0.85f*Height);           // x-axis line 
          
           stroke(255);
           if (PApplet.parseInt(max(AltitudeY))<0){
             line(xPos+50,                                       // zero line 
                         yPos+20,   // 
                         xPos+Width-20,
                         yPos+20
                         );
          }
          else if (PApplet.parseInt(min(AltitudeY))>0){
             line(xPos+50,                                       // zero line 
                         yPos+0.85f*Height,   // 
                         xPos+Width-20,
                         yPos+0.85f*Height
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
            line(PApplet.parseFloat(x)/xDiv*xpos+xPos+50,yPos+0.85f*Height,       //  x-axis Sub devisions    
                 PApplet.parseFloat(x)/xDiv*xpos+xPos+50,yPos+0.85f*Height+5);     
                 
            textSize(10);                                      // x-axis Labels
            String xAxis=str(xMin+PApplet.parseFloat(x)/xDiv*(xMax-xMin));  // the only way to get a specific number of decimals 
            String[] xAxisMS=split(xAxis,'.');                 // is to split the float into strings 
            text(xAxisMS[0]+"."+xAxisMS[1].charAt(0),          // ...
                 PApplet.parseFloat(x)/xDiv*xpos+xPos+50,yPos+0.85f*Height+15);   // x-axis Labels
          }
          
          
           /*  =========================================================================================
                 left y-axis
               ==========================================================================================  */
       
          for(int y=0; y<=yDiv; y++){
            
            float ypos = 0.85f*Height-20;
            line(xPos+50,PApplet.parseFloat(y)/yDiv*ypos+yPos+20,                // ...
                  xPos+46,PApplet.parseFloat(y)/yDiv*ypos+yPos+20);              // y-axis lines 
            
            textAlign(RIGHT);fill(255);
            
            String yAxis=str(yMin+PApplet.parseFloat(y)/yDiv*(yMax-yMin));     // Make y Label a string
            String[] yAxisMS=split(yAxis,'.');                    // Split string
           
            text(yAxisMS[0]+"."+yAxisMS[1].charAt(0),             // ... 
                 xPos+40,PApplet.parseFloat(yDiv-y)/yDiv*ypos+yPos+24);       // y-axis Labels 
                        
                        
            /*  =========================================================================================
                 right y-axis
                ==========================================================================================  */
            
            if(RightAxis){
             
              color(GraphColor); stroke(GraphColor);fill(255);
            
              line(xPos+Width+3,PApplet.parseFloat(y)/yDiv*Height+yPos,             // ...
                   xPos+Width+7,PApplet.parseFloat(y)/yDiv*Height+yPos);            // Right Y axis sub devisions
                   
              textAlign(LEFT); 
            
              String yAxisRight=str(yMinRight+PApplet.parseFloat(y)/                // ...
                                yDiv*(yMaxRight-yMinRight));           // convert axis values into string
              String[] yAxisRightMS=split(yAxisRight,'.');             // 
           
               text(yAxisRightMS[0]+"."+yAxisRightMS[1].charAt(0),     // Right Y axis text
                    xPos+Width+15,PApplet.parseFloat(yDiv-y)/yDiv*Height+yPos+3);   // it's x,y location
            
              noFill();
            }stroke(255);
            
          
          }
          
 
      }

   /*  =========================================================================================
       Streight line graph 
       ==========================================================================================  */
       
           public void Altitude(float[] x1 ,float[] AltitudeY) {
          
        //yPos+Height-(abs(yMin)/(yMax-yMin))*Height
        for (int k = 0; k<x1.length-1; k++){
          strokeWeight(2);fill(255);
      
          line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -AltitudeY[k]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -AltitudeY[k+1]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

          //AltitudeY[k]=AltitudeY[k+1];
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
     
     
       public void Rotation(float[] x1 ,float[] RotationY) {
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
                                          -RotationY[k]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -RotationY[k+1]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      RotationY[k]=RotationY[k+1];
      //y1[k] = 5;
      //y1[0] = -k;
                             
      }}
      
      
      public void OrientationX(float[] x1 ,float[]OrientationxY ) {
          
      strokeWeight(2);fill(255);
     
     for (int k = 0; k<x1.length-1; k++){
      line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -OrientationxY[k]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -OrientationxY[k+1]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      OrientationxY[k]=OrientationxY[k+1];
      //y1[k] = 5;
      //y1[0] = -k;
      }
      }
      
      
      
        public void OrientationY(float[] x1 ,float[] OrientationyY) {
 
        for (int k = 0; k<x1.length-1; k++){
      strokeWeight(2);fill(255);
      
      line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -OrientationyY[k]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -OrientationyY[k+1]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      OrientationyY[k]=OrientationyY[k+1];
      //y1[k] = 5;
      //y1[0] = -k;
                             
      }}
      
      
      
      
        public void OrientationZ(float[] x1 ,float[] OrientationzY) {
        
        //yPos+Height-(abs(yMin)/(yMax-yMin))*Height
        for (int k = 0; k<x1.length-1; k++){
      strokeWeight(2);fill(255);
      
      line(x1[k]*(Width-70)/x1.length+xPos+50,
                                          -OrientationzY[k]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height,
                                           x1[k+1]*(Width-70)/x1.length+xPos+50,
                                           -OrientationzY[k+1]*(0.85f*Height-20)/(yMax-yMin)+yPos+Height-(abs(yMin)/(yMax-yMin))*Height);

      OrientationzY[k]=OrientationzY[k+1];
      //y1[k] = 5;
      //y1[0] = -k;
                             
      }

       
 
       
    }
    

      
    }
 
// If you want to debug the plotter without using a real serial port

int mockupValue = 0;
int mockupDirection = 10;
public String mockupSerialFunction() {
  mockupValue = (mockupValue + mockupDirection);
  if (mockupValue > 100)
    mockupDirection = -10;
  else if (mockupValue < -100)
    mockupDirection = 10;
  String r = "";
  for (int i = 0; i<6; i++) {
    switch (i) {
    case 0:
      r += mockupValue+" ";
      
      break;
    case 1:
      r += cos(mockupValue*(2*3.14f)/1000)+" ";
      break;
    case 2:
      r += mockupValue/4+" ";
      break;
    case 3:
      r += mockupValue/8+" ";
      break;
    case 4:
      r += mockupValue/16+" ";
      break;
    case 5:
      r += mockupValue/32+" ";
      break;
    }
    if (i < 7)
      r += '\r';
  }
  delay(100);
  return r;
}
   /*  =========================================================================================
       Bar graph
       ==========================================================================================     
      
      void Bar(float[] a ,int from, int to) {
        
         
          stroke(GraphColor);
          fill(GraphColor);
          
          if(from<0){                                      // If the From or To value is out of bounds 
           for (int x=0; x<a.length; x++){                 // of the array, adjust them 
               rect(int(xPos+x*float(Width)/(a.length)),
                    yPos+Height-2,
                    Width/a.length-2,
                    -a[x]/(yMax-yMin)*Height);
                 }
          }
          
          else {
          for (int x=from; x<to; x++){
            
            rect(int(xPos+(x-from)*float(Width)/(to-from)),
                     yPos+Height-2,
                     Width/(to-from)-2,
                     -a[x]/(yMax-yMin)*Height);
                     
    
          }
          }
          
      }
  void Bar(float[] a ) {
  
              stroke(GraphColor);
          fill(GraphColor);
    
  for (int x=0; x<a.length; x++){                 // of the array, adjust them 
               rect(int(xPos+x*float(Width)/(a.length)),
                    yPos+Height-2,
                    Width/a.length-2,
                    -a[x]/(yMax-yMin)*Height);
                 }
          }
  
  
   /*  =========================================================================================
       Dot graph
       ==========================================================================================  
       
        void DotGraph(float[] x ,float[] y) {
          
         for (int i=0; i<x.length; i++){
                    strokeWeight(2);stroke(GraphColor);noFill();smooth();
           ellipse(
                   xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width,
                   yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height,
                   2,2
                   );
         }
                             
      }
        
      
      /*  =========================================================================================
             smoothLine
          ==========================================================================================  
    
      void smoothLine(float[] x ,float[] y) {
         
        float tempyMax=yMax, tempyMin=yMin;
        
        if(RightAxis){yMax=yMaxRight;yMin=yMinRight;} 
         
        int counter=0;
        int xlocation=0,ylocation=0;
         
//         if(!ErrorFlag |true ){    // sort out later!
          
          beginShape(); strokeWeight(2);stroke(GraphColor);noFill();smooth();
         
            for (int i=0; i<x.length; i++){
              
           /* ===========================================================================
               Check for errors-> Make sure time array doesn't decrease (go back in time) 
              ===========================================================================
              if(i<x.length-1){
                if(x[i]>x[i+1]){
                   
                  ErrorFlag=true;
                
                }
              }
         
         /* =================================================================================       
             First and last bits can't be part of the curve, no points before first bit, 
             none after last bit. So a streight line is drawn instead   
            =================================================================================  

              if(i==0 || i==x.length-2)line(xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width,
                                            yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height,
                                            xPos+(x[i+1]-x[0])/(x[x.length-1]-x[0])*Width,
                                            yPos+Height-(y[i+1]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height);
                                            
          /* =================================================================================       
              For the rest of the array a curve (spline curve) can be created making the graph 
              smooth.     
             =================================================================================  
                            
              curveVertex( xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width,
                           yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height);
                           
           /* =================================================================================       
              If the Dot option is true, Place a dot at each data point.  
             =================================================================================     
           
             if(Dot)ellipse(
                             xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width,
                             yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height,
                             2,2
                             );
                             
         /* =================================================================================       
             Highlights points closest to Mouse X position   
            =================================================================================
                          
              if( abs(mouseX-(xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width))<5 ){
                
                 
                  float yLinePosition = yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height;
                  float xLinePosition = xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width;
                  strokeWeight(1);stroke(240);
                 // line(xPos,yLinePosition,xPos+Width,yLinePosition);
                  strokeWeight(2);stroke(GraphColor);
                  
                  ellipse(xLinePosition,yLinePosition,4,4);
              }
              
     
              
            }  
       
          endShape(); 
          yMax=tempyMax; yMin=tempyMin;
                float xAxisTitleWidth=textWidth(str(map(xlocation,xPos,xPos+Width,x[0],x[x.length-1])));
          
           
       if((mouseX>xPos&mouseX<(xPos+Width))&(mouseY>yPos&mouseY<(yPos+Height))){   
        if(ShowMouseLines){
              // if(mouseX<xPos)xlocation=xPos;
            if(mouseX>xPos+Width)xlocation=xPos+Width;
            else xlocation=mouseX;
            stroke(200); strokeWeight(0.5);fill(255);color(50);
            // Rectangle and x position
            line(xlocation,yPos,xlocation,yPos+Height);
            rect(xlocation-xAxisTitleWidth/2-10,yPos+Height-16,xAxisTitleWidth+20,12);
            
            textAlign(CENTER); fill(160);
            text(map(xlocation,xPos,xPos+Width,x[0],x[x.length-1]),xlocation,yPos+Height-6);
            
           // if(mouseY<yPos)ylocation=yPos;
             if(mouseY>yPos+Height)ylocation=yPos+Height;
            else ylocation=mouseY;
          
           // Rectangle and y position
            stroke(200); strokeWeight(0.5);fill(255);color(50);
            
            line(xPos,ylocation,xPos+Width,ylocation);
             int yAxisTitleWidth=int(textWidth(str(map(ylocation,yPos,yPos+Height,y[0],y[y.length-1]))) );
            rect(xPos-15+3,ylocation-6, -60 ,12);
            
            textAlign(RIGHT); fill(GraphColor);//StrokeColor
          //    text(map(ylocation,yPos+Height,yPos,yMin,yMax),xPos+Width+3,yPos+Height+4);
            text(map(ylocation,yPos+Height,yPos,yMin,yMax),xPos -15,ylocation+4);
           if(RightAxis){ 
                          
                           stroke(200); strokeWeight(0.5);fill(255);color(50);
                           
                           rect(xPos+Width+15-3,ylocation-6, 60 ,12);  
                            textAlign(LEFT); fill(160);
                           text(map(ylocation,yPos+Height,yPos,yMinRight,yMaxRight),xPos+Width+15,ylocation+4);
           }
            noStroke();noFill();
         }
       }
            
   
      }

       
          void smoothLine(float[] x ,float[] y, float[] z, float[] a ) {
           GraphColor=color(188,53,53);
            smoothLine(x ,y);
           GraphColor=color(193-100,216-100,16);
           smoothLine(z ,a);
   
       }*/
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Skyfall_Ground_Control" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
