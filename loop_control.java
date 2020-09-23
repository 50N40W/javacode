import java.util.concurrent.ThreadLocalRandom;
/**
 * Write a description of class loop_control here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class loop_control
{
    // instance variables - replace the example below with your own
    private int x;
    private static long prev_sensor_millis= 0;
    private static long prev_nav_millis= 0;
    private static long prev_actuator_millis= 0;
    private static long SENSOR_PERIOD = 25; // milliseconds
    private static long NAV_PERIOD = 25;
    private static long ACTUATOR_PERIOD = 25;
    private static long TELEMETRY_PERIOD = 200;
    
    private static int sensor_ctr = 0;
    private static int nav_ctr = 0;
    private static int actuator_ctr = 0;
 
    public double pretend_stick_input(double in_radians) {
        double stick_position = Math.sin(in_radians);
        // question, what does that (double) statement do?
        //stick_position = stick_position + Math.random()/2 - Math.random()/2;
        System.out.println(stick_position);
        return stick_position;
    }

    public int loop_control()
    {
        //long millis = System.currentTimeMillis() % 1000;
        long millis = System.currentTimeMillis();
        if (millis - prev_sensor_millis >= SENSOR_PERIOD)
        {
            prev_sensor_millis = millis;
            sensor_ctr++;
            System.out.print("sense.");
            System.out.print(sensor_ctr);
            System.out.print(" ");
            System.out.println(millis);
            //
        }
        if (millis - prev_nav_millis > NAV_PERIOD) 
        {
            int j = 5;
            prev_nav_millis = millis;
            nav_ctr++;
            System.out.print("  navig.");
            System.out.print(nav_ctr);
            System.out.print("\t");
            System.out.println(millis);
            j++;
            System.out.println(j);
        }
        //j = j /2;
        if (millis - prev_actuator_millis >= ACTUATOR_PERIOD) 
        {
            
            prev_actuator_millis = millis;
            actuator_ctr++;
            System.out.print("     actuat.");
            System.out.print(actuator_ctr);
            System.out.print("\t");
            System.out.println(millis);
        }
        // if (millis - prev_telemetry_millis >= TELEMETRY_PERIOD) 
        // {
            // prev_actuator_millis = millis;
            // actuator_ctr++;
            // System.out.print("     actuat.");
            // System.out.print(actuator_ctr);
            // System.out.print("\t");
            // System.out.println(millis);
        // }
        // initialise instance variables
        return 0;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int main_stub()
    {
        double radians;
        // put your code here
        //int i = 0;
        //for (i = 0; i <= 300; i++) {
        for (radians = 0; radians <= 2*Math.PI; radians = radians + Math.PI/60) {
            //int loop_passed = loop_control();
            double stick = pretend_stick_input(radians);
        }
        return 0;
    }
}
