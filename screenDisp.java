//all needed classes imported
import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import edu.princeton.cs.introcs.StdDraw;

public class screenDisp implements IODeviceEventListener{//implementing one interface and extending the Canvas class for plotting
    private Pin soundSensor;
    private Pin button;
    private Pin tempSensor;
    static String message;
    public screenDisp(Pin buttonPin, Pin soundSensePin, Pin tempSensePin){
        this.soundSensor = soundSensePin;
        this.button = buttonPin;
        this.tempSensor = tempSensePin;
    }//this is the constructor ^

    @Override//overriding the onPinChange method below to make it do what is needed for this project instead
    public void onPinChange(IOEvent event) {
        //returning if the event is not from the button pin
        if (event.getPin().getIndex() != button.getIndex()) {
            return;
        }

        StdDraw.clear(StdDraw.WHITE);//clearing the previous screen for new info
        //outputting info onto screen using the StdLib library
        StdDraw.setXscale(-50 ,2000);//setting x axis scale
        StdDraw.setYscale(-50, 2000);//setting y axis scale

        StdDraw.setPenRadius(0.002);
        StdDraw.setPenColor(StdDraw.BLUE);//pen radius and color set
        StdDraw.text(60, 50, "Heat");//Temperature label
        StdDraw.text(490, 50, "Loudness");//Temperature label
        StdDraw.text(60, 1100, "HIGH");//Temperature level label
        StdDraw.text(490, 1100, "HIGH");//Temperature level label
        StdDraw.text(60, 200, "LOW");//Temperature level label
        StdDraw.text(490, 200, "LOW");//Temperature level label

        StdDraw.setPenRadius(0.007);
        StdDraw.setPenColor(StdDraw.BLUE);//pen radius and color set


        StdDraw.filledRectangle(250, 0, 50, 1000-(7*BabyMonitor.temp_voltage));//temperature bar
        StdDraw.filledRectangle(700, 0, 50, 1000-(3*BabyMonitor.sound_voltage));//loudness bar

        StdDraw.line(900,-20, 1900,-20);//x-axis line
        StdDraw.line(900,-20,900,1900);//y-axis line
        StdDraw.text(1400, 1850, "Today's comfort levels");//graph label

        for (int j=0; j<BabyMonitor.comfortLevArray.size(); j ++){//for loop for plotting comfort levels
            StdDraw.text(j+950, (int)BabyMonitor.comfortLevArray.get(j)/2, "*");//plotting points on graph
        }

        if(BabyMonitor.comfort_levels >= 1700){//changing pen color for the upcoming message and deciding the message based on calculated comfort level
            StdDraw.setPenColor(StdDraw.RED);
            message = "BAD";
        }else if(BabyMonitor.comfort_levels >= 1600 && BabyMonitor.comfort_levels < 1700){
            StdDraw.setPenColor(StdDraw.ORANGE);
            message = "MODERATE";
        }else{
            StdDraw.setPenColor(StdDraw.GREEN);
            message = "GOOD";
        }

        StdDraw.setPenRadius(0.015);
        StdDraw.circle(50, 1910, 50);
        StdDraw.text(50,1770,message);

    }
    // Methods below are part of IODeviceEventListener but I do not want them to do anything specific so I leave them empty
    @Override
    public void onStart(IOEvent event) {}
    @Override
    public void onStop(IOEvent event) {}
    @Override
    public void onMessageReceive(IOEvent event, String message) {}
}