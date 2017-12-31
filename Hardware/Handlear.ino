#include<SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>

#define PIN 6
#define LEFTTOUCH 1
#define RIGHTTOUCH 2

int softpotLeft;
int softpotRight;
int blueRx = 2;
int blueTx = 3;
int latchPin = 8;
int clockPin = 12;
int dataPin = 11;

String meter = "";
String turnType = "";

boolean isTurnType = false;
boolean complete = false;

char inChar;

byte buffer[1024];
byte data;
byte touchData[] = { B00000000, B00000000}; //좌측6개89abcd00, 우측7개12345670 //총 13개 진동모

SoftwareSerial mySerial(blueRx, blueTx);
Adafruit_NeoPixel strip = Adafruit_NeoPixel(35, PIN, NEO_RGBW + NEO_KHZ800);

void SetVibration(int mode,int softpotReading)
{ 
  if(mode == RIGHTTOUCH) // 오른쪽터치
  {
    if(softpotReading >= 0 && softpotReading < 483)
    {
      touchData[1] = B11000000;
    }
    else if(softpotReading >= 483 && softpotReading < 630)
    {
      touchData[1] = B01100000; 
    }
    else if(softpotReading >= 630 && softpotReading < 768)
    {
      touchData[1] = B00110000;
    }
    else if(softpotReading >= 768 && softpotReading < 902)
    {
      touchData[1] = B00011000;
    }
    else if(softpotReading >= 902 && softpotReading < 972)
    {
      touchData[1] = B00001100;
    }
    else if(softpotReading >= 972 && softpotReading < 1023)
    {
      touchData[1] = B00000110;
    }
  }
  else // 왼쪽터치
  {
    if(softpotReading >= 0 && softpotReading < 508)
    {
      touchData[0] = B00001100;
    }
    else if(softpotReading >= 508 && softpotReading < 663)
    {
       touchData[0] = B00011000;
    }
    else if(softpotReading >= 663 && softpotReading < 788)
    {
      touchData[0] = B00110000;
    }
    else if(softpotReading >= 788 && softpotReading < 920)
    {
      touchData[0] = B01100000;
    }
    else if(softpotReading >= 920 && softpotReading < 985)
    {
      touchData[0] = B11000000;    
    }
    else if(softpotReading >= 985 && softpotReading < 1023)
    {
      touchData[0] = B10000000;
      touchData[1] = touchData[1] | B00000010;
    }
  }
}

void ActivateVibration(int vibrationTime, int stopTime)
{
  digitalWrite(latchPin, LOW);
  shiftOut(dataPin, clockPin, LSBFIRST, touchData[0]);
  shiftOut(dataPin, clockPin, LSBFIRST, touchData[1]);
  digitalWrite(latchPin, HIGH);
  delay(vibrationTime);
  StopVibration();
  delay(stopTime);
}

void StopVibration()
{
  digitalWrite(latchPin, LOW);
  shiftOut(dataPin, clockPin, LSBFIRST, B00000000);
  shiftOut(dataPin, clockPin, LSBFIRST, B00000000);
  digitalWrite(latchPin, HIGH);
}

void StartLight()
{
  for(int i=0; i<35; i++)
  {
      strip.setPixelColor(i,155,0,0,0);
      strip.show();
      delay(10);
  }
  for(int i=0; i<35; i++)
  {
      strip.setPixelColor(i,0,155,0,0);
      strip.show();
      delay(10);
  }
  for(int i=0; i<35; i++)
  {
      strip.setPixelColor(i,0,0,155,0);
      strip.show();
      delay(10);
  }
  for(int i=0; i<35; i++)
  {
      strip.setPixelColor(i,0,0,0,0);
      strip.show();
      delay(10);
  }
}

void StopLight()
{
  for(int i=0; i<35; i++)
  {
      strip.setPixelColor(i,0,0,0,0);
      strip.show();
  }
}

void SetLight(String alert)
{
  if(alert == "12") // 좌회전
  {
      if(touchData[0] != B10000000)
      {
        touchData[1] = B00000000;
      }
      for(int i=0;i<18;i++)
      {
          strip.setPixelColor(i,255,0,0,0);
          strip.show();
      }
  }
  else if(alert == "13") // 우회전
  {
      touchData[0] = B00000000;
      for(int i=17;i<35;i++)
      {
          strip.setPixelColor(i,255,0,0,0);
          strip.show();
      }
  }
  else if(alert == "14") // 유턴
  { 
      for(int i=0;i<9;i++)
      {
          strip.setPixelColor(i,255,0,0,0);
          strip.show();
      }
      for(int i=26;i<35;i++)
      {
          strip.setPixelColor(i,255,0,0,0);
          strip.show();
      }
   }
   else if(alert == "51" || alert == "11") // 직진
   {      
      for(int i=9;i<26;i++)
      {
          strip.setPixelColor(i,255,0,0,0);
          strip.show();
      }
   }
   else if(alert == "100") // 위험감지
   {
      for(int j=0;j<35;j++)
      {
         strip.setPixelColor(j,0,255,0,0);
         strip.show();
      }
   }
   else if(alert == "101") // 과속알림
   {
      for(int j=0;j<35;j++)
      {
         strip.setPixelColor(j,120,255,0,0);
         strip.show();
      }
   }
   else if(alert == "103") // 예비 우회전
   {
       for(int i=17;i<35;i++)
      {
          strip.setPixelColor(i,0,0,255,0);
          strip.show();
      }
   }
   else if(alert == "102") // 예비 좌회전
   {
      for(int i=0;i<18;i++)
      {
          strip.setPixelColor(i,0,0,255,0);
          strip.show();
      }
   }
   else if(alert == "104") // 예비 직진
   {
      for(int i=9;i<26;i++)
      {
          strip.setPixelColor(i,0,0,255,0);
          strip.show();
      }
   }
   else if(alert == "105") // 예비 유턴
   {
      for(int i=0;i<9;i++)
      {
          strip.setPixelColor(i,0,0,255,0);
          strip.show();
      }
      for(int i=26;i<35;i++)
      {
          strip.setPixelColor(i,0,0,255,0);
          strip.show();
      }
   }
   else if(alert == "200")  // 출발 도착
   {
      StartLight();
   }
  else 
  {
      touchData[0] = B00000000;
      touchData[1] = B00000000;
  }
}

void setup() 
{
  mySerial.begin(9600);
  Serial.begin(9600);

  softpotLeft = analogRead(A0);
  softpotRight = analogRead(A1);

  pinMode(latchPin, OUTPUT);
  pinMode(clockPin, OUTPUT);
  pinMode(dataPin, OUTPUT);
  
  digitalWrite(A0, HIGH);
  digitalWrite(A1, HIGH);
  
  strip.begin();
  strip.show(); //Initialize all pixels to 'off'

  StopVibration();
  StartLight(); //하드웨어 처음 시작 알림
}

void loop() 
{ 
    while(mySerial.available())
    {
      inChar = (char)mySerial.read();
  
      if(inChar == ' ')
      {
        isTurnType = true;
      }
    
      if(isTurnType)
      {
        if(inChar!=' '&&inChar!='.')
        {
           turnType += inChar;
        }
        if(inChar=='.')
        {
          isTurnType = false;
          Print();
          handlear(meter, turnType);
          meter = "";
          turnType = "";
        }
      }
      else
      {
        meter += inChar;
      }
  }
}

void handlear(String meter, String turnType)
{
      softpotLeft = analogRead(A0);
      softpotRight = analogRead(A1);
      delay(10);

      if (softpotRight < 1002 || softpotRight > 1007 || softpotLeft < 1002 || softpotLeft > 1007)
      { 
         SetVibration(RIGHTTOUCH, softpotRight);
         SetVibration(LEFTTOUCH, softpotLeft);
  
         for(int l=0; l<5; l++)
         {
            SetLight(turnType);
            digitalWrite(latchPin, LOW);
            shiftOut(dataPin, clockPin, LSBFIRST, touchData[0]);
            shiftOut(dataPin, clockPin, LSBFIRST, touchData[1]);
            digitalWrite(latchPin, HIGH);
            delay(500);
            StopVibration();
            delay(100);
         }     
         StopLight();
      }
}

void Print()
{
  Serial.println(meter);
  Serial.println("//");
  Serial.println(turnType);
}
