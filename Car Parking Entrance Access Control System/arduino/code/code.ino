/* 
 *  From Java to Arduino (Control SERVO Motor)
 *  Programmer: Tryfon Theodorou
 *  Date: 10/5/2014
 *  Desc: Java sends integer data from 0 to 180 
 *        Arduino turns the servo motor to that angle
*/
#include<Servo.h>

Servo servo; // create servo object
const int servoPin = 9;
int greenLed = 12;                // the pin that the LED is atteched to
int redLed = 13;                // the pin that the LED is atteched to
int sensor = 2;               // choose the input pin (for PIR sensor)
int pirState = LOW;             // we start, assuming no motion detected
int val = 0;                    // variable for reading the pin status

void setup() {
  pinMode(greenLed, OUTPUT);      // initalize LED as an output
  pinMode(redLed, OUTPUT);      // initalize LED as an output
  pinMode(sensor, INPUT);     // declare sensor as input
  Serial.begin(9600);   // initialize serial communication at 9600 bits per second:
  servo.attach(servoPin);  // attaches the servo object on pin 
  servo.write(160);
}

void loop() {
  val = digitalRead(sensor);  // read input value
  if (val == HIGH) {            // check if the input is HIGH
    Serial.println(val);
    digitalWrite(redLed, HIGH);   // turn LED ON
    delay(100);                // delay 100 milliseconds 
    
    if (pirState == LOW) {
      // we have just turned on
      //Serial.println("Motion detected!");
      // We only want to print on the output change, not state
      pirState = HIGH;
    }
  } else {
     digitalWrite(redLed, LOW); // turn LED OFF
     delay(200);             // delay 200 milliseconds
    Serial.println(val);
    if (pirState == HIGH){
      // we have just turned of
      //Serial.println("Motion ended!");
      // We only want to print on the output change, not state
      pirState = LOW;
    }
  }
  
  int d = Serial.read();  // Read from serial
  if(d >= 0 && d <= 180) {
    digitalWrite(greenLed, HIGH);
    servo.write(d);
     delay(3000); 
     servo.write(160);
     digitalWrite(greenLed, LOW);
  }
  
  delay(3000);        // delay in between reads for stability
}
