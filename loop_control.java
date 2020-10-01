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
    private static long prev_telemetry_millis = 0;
    private static long SENSOR_PERIOD = 25; // milliseconds
    private static long NAV_PERIOD = 25;
    private static long ACTUATOR_PERIOD = 25;
    private static long TELEMETRY_PERIOD = 500;
    private static long stateTimer = 0;
    float leftMotor = 0;
    float rightMotor = 0;
    float liftMotor = 0;
    float clampServo = 0;
    private static int sensor_ctr = 0;
    private static int nav_ctr = 0;
    private static int actuator_ctr = 0;
    final int UP = 1;
    final int DN = -1;
    final int INIT = -1;
    final int CLMP = 0; // clamp: uses clamping, power, timeLim
    final int MOVE = 1; // move: compass, forward, crab, power, timeLim
    final int LIFT = 2; // lifter: uses lifter, power, timeLim
    final int WAIT = 3;  // wait: uses timeLim
    int autoState = CLMP;
    String[] strAction = {"CLMP","LIFT","MOVE","MOVE","CLMP","MOVE", "WAIT", "WAIT"};
    int[] action =   {CLMP,  LIFT, MOVE,  MOVE,  CLMP,  MOVE,  WAIT, WAIT};
    float[] clamp =   { 20,     0,    0,     0,   -10,     0,   0,     0};
    float[] lifter = {  0,     UP,    0,     0,     0,     0,   0,     0};
    float[] pwrLeft = { 0,     25,   40,    30,     0,    25,   0,     0};
    float[] pwrRight = {0,    -25,  -40,    30,     0,    25,   0,     0};
    long[] timeLim = {300,   2000,  900,   500,  1000,  2000, 500,    50};
    
    public double motor_stick_relation(double stick_position) {
        //double motor_command = sp * sp * sp * sp * sp;
        double motor_command = Math.pow(stick_position,5);
        return motor_command;
    }
    
    public double first_order_filter(double current, double previous, double fc) {
        double filtered_value = (fc * previous) + ((1-fc)*current);
        /*  -- formula: filtered value = (filter constant * previous filtered
         *    value) + (1-filter constant * raw value)
         */
        return filtered_value;
    }
 
    public double pretend_stick_input(double in_radians) {
        /* pretty much every signal in the world is going to act like
         * a combination of a sine wave, white noise, and a step function,
         * so may as well combine those things into a pretend driver input.
         * We can mess around with how fast it moves, but this should represent 
         * some theoretical stick movement pretty well
           */
        double stick_position = 0;
        double stick_noise = -0.08 + 0.16 * Math.random();
        /* Math.random() should return a value between 0 and 1
         * we want a small amount of noise, say +/- 0.08, so 
         * first let's "throttle" that 0-1 value down to 0.16
         * which is 0.08 * 2, then offset it by -0.08.   This
         * should give us a random number between -0.08 and 0.08, 
         * that SHOULD be a useful amount of noise for experimentation.
         */
        //stick_position = in_radians;
        if (in_radians < Math.PI) {
           stick_position = Math.sin(in_radians);
        }
        else if (in_radians < 1.2*Math.PI){
            stick_position = 1.0;
        }
        else if (in_radians < 1.3*Math.PI) {
            stick_position = -0.6;
        }
        else {
            stick_position = Math.cos(in_radians)*2;
        }
        stick_position = stick_position + stick_noise; 
        
        stick_position = Math.min(stick_position, 1);
        stick_position = Math.max(stick_position, -1);
        /* here we have ensured that stick position will be 
         * between -1 and 1, inclusive */
        //System.out.println(stick_position);
        return stick_position;
    }

    
    
    
    public int loop_control()
    {
        
        // states for the NAV switch statement

        long millis = System.currentTimeMillis();
        if (millis - prev_sensor_millis >= SENSOR_PERIOD)
        {
            prev_sensor_millis = millis;
            sensor_ctr++;

            //
        }
        if (millis - prev_nav_millis > NAV_PERIOD) 
        {
            stateTimer += NAV_PERIOD;
            int j = 5;
            boolean stageComplete = false;
            prev_nav_millis = millis;
            stageComplete = (stateTimer > timeLim[autoState]);

            switch (action[autoState])
            {   
                case CLMP:  // operate the clamp
                    clampServo = clamp[autoState];
                    break;
                case LIFT:
                    liftMotor = lifter[autoState];
                    break;
                case MOVE:   // move the bot, drive it somewhere
                    rightMotor = pwrRight[autoState];
                    leftMotor = pwrLeft[autoState];
                case WAIT:
                    break;
                default:
                    break;
            }
            if (stageComplete)
            {
                stateTimer= 0;
                if (autoState <=6) autoState++;
                liftMotor = 0;
                leftMotor = 0;
                rightMotor = 0;
                clampServo = 0;
            }

        }
        //j = j /2;
        if (millis - prev_actuator_millis >= ACTUATOR_PERIOD) 
        {
            
            prev_actuator_millis = millis;
            actuator_ctr++;
        }
        if (millis - prev_telemetry_millis >= TELEMETRY_PERIOD) 
        {
            prev_telemetry_millis = millis;

            //System.out.println(action[autoState]);
            System.out.print(strAction[autoState]);
            System.out.printf("\tLeft: %f,   Right:   %f \n", leftMotor, rightMotor);
            System.out.printf("\tLifter: %f, Clamper: %f\n", liftMotor, clampServo);
        }
        // initialise instance variables
        return autoState;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void main_stub()
    {
        double radians = -1;
        double prev_stick = 0;
        final double FILT_CONST = 0.5;
        final double FIRST_FILT = 0;
        double fc = FIRST_FILT;

        // put your code here
        //int i = 0;
        //for (i = 0; i <= 300; i++) {
        //for (radians = 0; radians <= 3*Math.PI; radians = radians + Math.PI/60) {
        //for (radians = -1; radians < 1; radians = radians + .05){
        //while(radians < 1) {
            //int loop_passed = loop_control();
        while(action[autoState] != WAIT) {
            double stick = pretend_stick_input(radians);
            stick = first_order_filter(stick, prev_stick, fc);
            prev_stick = stick;
            fc = FILT_CONST;

            double mtr_out = motor_stick_relation(stick);
            int currentState = loop_control();

            //System.out.printf("%f,%f, %f\n",radians, stick, mtr_out);
            radians = radians + 0.05;
        }

    }
}
