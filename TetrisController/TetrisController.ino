#include <Keyboard.h>
#include <Wire.h>
#include "SFE_MMA8452Q.h"

//L3G gyro;
MMA8452Q accel;

char leftArrow = KEY_LEFT_ARROW; 
char rightArrow = KEY_RIGHT_ARROW;
char downArrow = KEY_DOWN_ARROW;
char spaceBar = ' ';

int inPin1 = 4;
int inPinVal1 = 0;
int prevInPinVal1 = 0;
int state = 1;
int inPin2 = 5;
int inPinVal2 = 0;
int prevInPinVal2 = 0;
int inPin3 = 6;
int inPinVal3 = 0;
int prevInPinVal3 = 0;
int inPin4 = 7;
int inPinVal4 = 0;
int prevInPinVal4 = 0;
//int gyroPin = 3;
//float gyroVoltage = 3.3;         //Gyro is running at 5V
//float gyroZeroVoltage = 2.5;   //Gyro is zeroed at 2.5V
//float gyroSensitivity = .007;  //Our example gyro is 7mV/deg/sec
//float rotationThreshold = 1;   //Minimum deg/sec to keep track of - helps with gyro drifting
//float currentAngle = 0;
boolean active = false;

// the setup function runs once when you press reset or power the board
void setup() {
  // initialize digital pin LED_BUILTIN as an output.
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(inPin1, INPUT);
  Keyboard.begin();
  //Serial.begin(9600);
  //while (!Serial);
  Wire.begin();
  digitalWrite(LED_BUILTIN, HIGH); // LED ON
  /*
  while (!gyro.init(gyro.device_4200D, gyro.sa0_high)) {
    Serial.println("Failed to autodetect gyro type");
    delay(250);
  }
  gyro.enableDefault();*/
  accel.init(SCALE_8G, ODR_6);
}

// the loop function runs over and over again forever
void loop() {
    inPinVal1 = digitalRead(inPin1);
    if (inPinVal1 != prevInPinVal1) {
      state++;
      delay(50);
    }
    if ((state / 2) % 2 == 0) {
      digitalWrite(LED_BUILTIN, HIGH); // LED ON
      active = true;
    } else {
      digitalWrite(LED_BUILTIN, LOW); // LED OFF
      active = false;
    }
    prevInPinVal1 = inPinVal1;

    if (active) {
      inPinVal2 = digitalRead(inPin2);
      inPinVal3 = digitalRead(inPin3);
      inPinVal4 = digitalRead(inPin4);
      if (inPinVal2 != prevInPinVal2 && inPinVal2 == HIGH) {
        Keyboard.press('d');
      } else if (inPinVal2 != prevInPinVal2 && inPinVal2 == LOW) {
        Keyboard.release('d');
      }
      if (inPinVal3 != prevInPinVal3 && inPinVal3 == HIGH) {
        Keyboard.press('a');
      } else if (inPinVal3 != prevInPinVal3 && inPinVal3 == LOW) {
        Keyboard.release('a');
      }
      if (inPinVal4 != prevInPinVal4 && inPinVal4 == HIGH) {
        Keyboard.press(spaceBar);
      } else if (inPinVal4 != prevInPinVal4 && inPinVal4 == LOW) {
        Keyboard.release(spaceBar);
      }
      prevInPinVal2 = inPinVal2;
      prevInPinVal3 = inPinVal3;
      prevInPinVal4 = inPinVal4;
    }

  // FOR ACCELEROMETER
  // Use the accel.available() function to wait for new data
  //  from the accelerometer.
  if (accel.available()) {
    // First, use accel.read() to read the new variables:
    accel.read();
    
    // accel.read() will update two sets of variables. 
    // * int's x, y, and z will store the signed 12-bit values 
    //   read out of the accelerometer.
    // * floats cx, cy, and cz will store the calculated 
    //   acceleration from those 12-bit values. These variables 
    //   are in units of g's.
    // Check the two function declarations below for an example
    // of how to use these variables.
    if (active) {
      if (accel.cz > 0.4) {
        //Serial.println("down");
        Keyboard.press(downArrow);
      }
      else {
        Keyboard.release(downArrow);
      }
      
      if (accel.cx > 0.2) {
        //Serial.println("left");
        Keyboard.press(leftArrow);
      }
      else if (accel.cx < -0.2) {
        //Serial.println("right");
        Keyboard.press(rightArrow);
      }
      else {
        //Serial.println("do nothing");
        Keyboard.release(leftArrow);
        Keyboard.release(rightArrow);
      }
    }
    //printAccels(); // Uncomment to print digital readings
    
    // The library also supports the portrait/landscape detection
    //  of the MMA8452Q. Check out this function declaration for
    //  an example of how to use that.
    //printOrientation();
    
    //Serial.println(); // Print new line every time.
  }

  // For gyro
  /*
  //This line converts the 0-1023 signal to 0-5V
  float gyroRate = (analogRead(gyroPin) * gyroVoltage) / 1023;
  Serial.println(analogRead(gyroRate));

  //This line finds the voltage offset from sitting still
  gyroRate -= gyroZeroVoltage;

  //This line divides the voltage we found by the gyro's sensitivity
  gyroRate /= gyroSensitivity;

  //Ignore the gyro if our angular velocity does not meet our threshold
  if (gyroRate >= rotationThreshold || gyroRate <= -rotationThreshold) {
    //This line divides the value by 100 since we are running in a 10ms loop (1000ms/10ms)
    gyroRate /= 100;
    currentAngle += gyroRate;
  }

  //Keep our angle between 0-359 degrees
  if (currentAngle < 0)
    currentAngle += 360;
  else if (currentAngle > 359)
    currentAngle -= 360;

  //DEBUG
  //Serial.println(currentAngle);

  delay(10);
  
  Serial.println("Starts read():");
  gyro.read();
  
  Serial.print("G ");
  Serial.print("X: ");
  Serial.print((int)gyro.g.x);
  Serial.print(" Y: ");
  Serial.print((int)gyro.g.y);
  Serial.print(" Z: ");
  Serial.println((int)gyro.g.z);

  delay(100);*/
}
