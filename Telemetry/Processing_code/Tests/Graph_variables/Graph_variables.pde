float[] a = {1,2,3,4};
int m = millis();
int x = 0;
int x1 = 0;
int y = height/2;
int y1 = height/2;
float rand = random(-10,10);
void setup(){
size(1000,1000);
printArray (a);
}
void draw(){
  rand = random(-10,10);
  if (x>width){
    x=0;
    x1 = x;
  
  }
  x = x+1;
  y = y+int(rand);
  line(x,y,x1,y1);
  x1 = x1+1;
  y1 = y;
  delay(10);
}
