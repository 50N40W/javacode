
/**
 * Write a description of class Hello here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

public class Hello
{
    // instance variables - replace the example below with your own
    private int x;
    private float i;

    /**
     * Constructor for objects of class Hello
     */
    public  Hello()
    {
        // initialise instance variables
        System.out.println("Hello world\n");
        x = 0;
    }

    public float First_Order_Filter(float prev, float input, float filt_const) {
        if (input < 0.4f) filt_const = 0;
        float new_value = (filt_const*prev) + (1 - filt_const)*input;
        //new_value = new_value + different_value;
        //i = 6f;
        return new_value;
        
    }
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int sampleMethod()
    {
        float[] stick={0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f};
        //float i;
        float different_value = 6f;
        float prev_value = stick[0];
        System.out.println("before loops");
        i = 1f;
        while (i < 1f) 
        {
            System.out.println("in while loop");
            // to make Bart happy
            i = i + 0.02f;
        }
        for (i = 1f; i <= 1f; i += 1f) {
           //float newValue = First_Order_Filter(prev_value, stick[i], 0.9f);
           //float newValue = First_Order_Filter(prev_value, i, 0.9f);
           float newValue = .9f;
           prev_value = newValue;
           System.out.print(i);
           System.out.print(",");
           System.out.println(newValue);
        }

        System.out.println("hello\n");
        return x;
    }
}
