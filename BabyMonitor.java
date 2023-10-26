import java.util.Timer;
import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import	org.firmata4j.firmata.FirmataDevice;
import	org.firmata4j.ssd1306.SSD1306;
import	java.io.IOException;
import java.util.ArrayList;

public class BabyMonitor {
    //all static variables needed for main method are below
    static final int D7 = 7; //pin D7
    static final int D6 = 6; //pin D6
    static final int A1 = 15; //pin A1
    static final int POT = 14; //potentiometer
    static final int SOUND = 16; //sound sensor
    static final int GRN_LED = 13; //small green LED
    static final int BUTTON = 6; //the button
    static final int BUZZER = 5; //the buzzer
    static final int BIG_RED_LED = 4; //the big red LED
    static final int TEMP_HUMID = 15; //temperature and humidity sensor
    static final int LIGHT = 19; //light sensor
    static final String myPort = "COM4"; //computer port, Change name for your own machine
    static Integer comfort_levels;
    static ArrayList<Integer> comfortLevArray = new ArrayList<>();
    static int sound_voltage;
    static int temp_voltage;

    public static void main(String[] args) throws InterruptedException, IOException{
        Timer time = new Timer(); //instantiating timer object
        var	arduino = new FirmataDevice(myPort);	//making arduino object
        arduino.start();//starting arduino object
        arduino.ensureInitializationIsDone();//ensuring initialization

        I2CDevice i2cDevice = arduino.getI2CDevice((byte) 0x3C); // 0x3C is OLED's location
        SSD1306 my_oled = new SSD1306(i2cDevice, SSD1306.Size.SSD1306_128_64); // creating oled object

        var fanPin = arduino.getPin(D7);//getting pin that is connected to MOSFET
        var soundPin = arduino.getPin(SOUND);//sound sensor reference variable
        var potentiometer = arduino.getPin(POT);//potentiometer reference variable
        var lightPin = arduino.getPin(LIGHT);//light intensity sensor reference variable
        var button = arduino.getPin(BUTTON);//button reference variable
        var buzzer = arduino.getPin(BUZZER);//buzzer sensor reference variable
        var redLed = arduino.getPin(BIG_RED_LED);//big red LED reference variable
        var greenLed = arduino.getPin(GRN_LED);//green LED reference variable
        var tempHumidity = arduino.getPin(TEMP_HUMID);//temp and humidity sensor reference variable

        //setting their modes below
        fanPin.setMode(Pin.Mode.OUTPUT);//output to use MOSFET as a switch
        soundPin.setMode(Pin.Mode.ANALOG);//analog input sound levels so warnings can be given when the sound gets too loud
        potentiometer.setMode(Pin.Mode.ANALOG);//dial for controlling machine actions
        lightPin.setMode(Pin.Mode.ANALOG);//warnings will be given if a certain light intensity is reached
        buzzer.setMode(Pin.Mode.OUTPUT);//buzzer for sound output
        redLed.setMode(Pin.Mode.OUTPUT);//LED can be flashed when needed
        greenLed.setMode(Pin.Mode.OUTPUT);//LED can be flashed when needed
        tempHumidity.setMode(Pin.Mode.ANALOG);//temperature and humidity sensor
        button.setMode(Pin.Mode.INPUT);//button

        my_oled.init(); //Initializing oled before passing it to task method

        var task = new groveOutput(my_oled, soundPin, redLed, tempHumidity, greenLed, potentiometer, fanPin, buzzer);//initializing task object with input argument
        time.schedule(task,0,1000);//executing task each millisecond to get a constantly running state machine

        //Adding an event listener to the board. Pressing button displays info on device screen
        arduino.addEventListener(new screenDisp(button, soundPin, tempHumidity));

    }
}