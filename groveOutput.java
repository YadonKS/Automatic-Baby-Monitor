//all needed classes imported
import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import edu.princeton.cs.introcs.StdDraw;
import	org.firmata4j.Pin;
import	org.firmata4j.ssd1306.SSD1306;

public class groveOutput extends TimerTask {
    private SSD1306 display;
    private Pin temp_sensor;
    private Pin sound_sensor;
    private Pin redLed;
    private Pin greenLed;
    private Pin poten;
    private Pin fan;
    private Pin buzzer;
    static int time = 0;
    static ArrayList<Integer> tempVoltage = new ArrayList<>();
    static int tooHot = 65; //sensor voltage when it is too hot for a baby. Can simply be changed at any time.
    static int tooLoud = 47; //sound sensor voltage when it is too loud for the baby(scale 0-1023)
    public groveOutput(SSD1306 oled, Pin soundSensePin, Pin redLEDPin, Pin tempSensePin, Pin grnLEDPin, Pin potentiometer, Pin fanPin, Pin buzzerPin){
        this.display = oled;
        this.sound_sensor = soundSensePin;
        this.redLed = redLEDPin;
        this.temp_sensor = tempSensePin;
        this.greenLed = grnLEDPin;
        this.poten = potentiometer;
        this.fan = fanPin;
        this.buzzer = buzzerPin;
    }//this is the constructor ^

    @Override//overriding the run() method below to make it do what is needed for this project instead
    public void run(){
        display.getCanvas().setTextsize(1);//setting the size of text that will appear on OLED

        //user can press button to display information on device
        display.getCanvas().clear();//clearing the memory before printing anything //clearing the canvas with each task execution
        display.getCanvas().drawString(0,45,"PRESS BUTTON TO SEE  DETAILED INFO");
        display.display();//printing on the oled

        //sound related outputs
        if (sound_sensor.getValue()>tooLoud){
            //flickering led to notify parent
            try {
                greenLed.setValue(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                greenLed.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //sound warning printed on oled if input voltage is over 700(0-1023 scale)
            display.getCanvas().clear();//clearing the memory before printing anything
            display.getCanvas().drawString(0,0,"TOO LOUD FOR BABY");
            display.display();//printing on the oled
        }else

        //temperature and humidity related outputs
        if(temp_sensor.getValue() < tooHot){
            try {//turning on fan to cool down the baby
                fan.setValue(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //visual signal being given by blinking the green LED really quickly
            try {
                redLed.setValue(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                redLed.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // turning on buzzer for sound notification
            try {
                buzzer.setValue(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //temperature warning printed on oled if input voltage is over 100(0-1023 scale)
            display.getCanvas().clear();//clearing the memory before printing anything
            display.getCanvas().drawString(0,25,"TOO HOT FOR BABY");
            display.display();//printing on the oled
        }else{//turning off fan if its not too hot
            try {
                fan.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {//turning off LED if temperature is fine
                redLed.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // turning off buzzer if temperature is fine
            try {
                buzzer.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        //Potentiometer related outputs and actions

        //calculating comfort levels and saving values in arrayList
        BabyMonitor.comfort_levels = 2000- ((2*(int)sound_sensor.getValue()+3*(int)temp_sensor.getValue()));//calculating and saving to variable in BabyMOnitor class
        BabyMonitor.comfortLevArray.add(BabyMonitor.comfort_levels);//appening to array list
        BabyMonitor.sound_voltage = (int)sound_sensor.getValue();
        BabyMonitor.temp_voltage = (int)temp_sensor.getValue();

        //printing info just for the sake of debugging...usually needed since my electronic components can be defective sometimes
        System.out.println("Temp: " + temp_sensor.getValue());
        System.out.println("Sound: " + sound_sensor.getValue());

    }
}
