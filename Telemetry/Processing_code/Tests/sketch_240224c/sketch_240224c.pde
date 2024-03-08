float angle = 0;
PShape Skyfall;
PGraphics canvas3D;

void setup() {
  
  size(1000, 700, P3D);//define the size of the window
  surface.setTitle("Skyfall Ground Control");//create the title of the window
  surface.setResizable(true);//define as resizable
  surface.setLocation(500,0);//define the loading location of the window
  background(100);
  canvas3D = createGraphics(int(width*0.5), int(height*0.72), P3D);//create a graphic area for the 3D model
  //load 3D model, resize and rotate
  Skyfall = loadShape("Skyfall.obj");
  Skyfall.scale(0.3,0.3,0.3);
  Skyfall.rotateX(PI);
}

void draw() {
  stroke(100);     //stroke color
  strokeWeight(10);   
  rect(0,0,width*0.1,height*0.24,10);
  rect(width*0.1,0,width*0.2,height*0.24,10);
  rect(width*0.3,0,width*0.2,height*0.24,10);
  rect(width*0.5,0,width*0.2,height*0.24,10);
  rect(width*0.7,0,width*0.3,height*0.24,10);
  rect(width*0.7,height*0.24,width*0.3,height*0.36,10);
  rect(width*0.7,height*0.6,width*0.3,height*0.36,10);
  rect(0,height*0.24,width*0.2,height*0.36,10);
  rect(0,height*0.6,width*0.2,height*0.36,10);
  
  
  // 3D shape on a separate PGraphics
  canvas3D.beginDraw();
  canvas3D.background(3);
  canvas3D.translate(width*0.25, height*0.6);
  canvas3D.rotateX(QUARTER_PI * sin(angle));
  canvas3D.rotateY(PI / 3 * cos(angle));
  canvas3D.rotateZ(QUARTER_PI * sin(angle));
  
  canvas3D.fill(0, 255, 0);
  canvas3D.shape(Skyfall);
  
  canvas3D.endDraw();
  
  // Display 3D buffer
  image(canvas3D, width*0.2, height*0.24);
  
  // 2D shapes
  //fill(255, 0, 0);
  translate(width / 2, height / 2);
  /*circle(-50, 0, 50);
  circle(50, 0, 50);
  circle(0, -50, 50);
  circle(0, 50, 50);*/
  
  angle += 0.05;
}
