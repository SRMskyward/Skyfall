int transistor = 4;
int button = 3;

void setup() {
  pinMode(button, INPUT);
  pinMode(transistor, OUTPUT);
  digitalWrite(4, LOW);
  
}

void loop() {
  int read=digitalRead(button);
  if (read == HIGH){
    digitalWrite(transistor, HIGH);    
  }
  else{
    digitalWrite(transistor, LOW);
  }
  

  // put your main code here, to run repeatedly:

}
