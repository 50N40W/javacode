/*
 * untitled.java
 * 
 * Copyright 2015 Howard <howard@Hugh>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */
//#include <stdio.h>



public class hello {
	public static double stickpos = 0;
	public static double prevStickPos = 0;
	public static double prevTime = 0;
	public static final double HI_FILT_CONST = 0.85;
	public static final double MID_FILT_CONST = 0.3;
	public static final double LOW_FILT_CONST = 0.1;
	
	public static double get_time_delta(double old_time) {
		double delta;
		delta = old_time - prevTime;
		prevTime = delta;
		return (delta);	
	}
	
	/* sim_game_controller                                     */
	/*  purpose: simulate inputs for one game controller input */
	/*  Obviously, the data types can be manipulated as needed */
	/*  This code WILL NOT BE IN AN OPMODE !!!!                */
	
	public static double sim_game_controller(double lcv) {
		double returnval = 0;
	    if (lcv < 60) {
		   returnval = Math.sin(((double)lcv)/8);
		}
		else {
			if (lcv < 70) {
				returnval = 1;
			}
			else if (lcv < 80) {
				returnval = 0;
			}
			else if (lcv < 90) {
				returnval = 1;
			}
			else {
				returnval = -1;
			}
		}
		if ((returnval < 0.05) && (returnval > -0.1)) {
			returnval = 0;
		}
		else if (returnval < -1) {
			returnval = -1;
		}
		else if (returnval > 1) {
			returnval = 1;
		}
		return (returnval);
	}
	
	
	/* filter_input                                             */
	/*  Purpose is to adjust the stick input for better control */
	/*  The basic method could be included in the controller    */
	
	public static double filter_input(double instick) {
		double filter_constant = 0;  /*init to zero just in case */
		if (instick < 0) {
			instick = instick * instick * (-1);
		}
		else
		{
			instick = instick * instick;
		}
		
		//  using -0.05 allows the return spring to overshoot a little.
		if ((instick > -.05) && (instick < prevStickPos)) {
			
			 if (prevStickPos > 0.8) {
			     filter_constant = HI_FILT_CONST;
			 }
			 else if (prevStickPos > 0.6) {
				 filter_constant = MID_FILT_CONST;
			 }
			 else if (prevStickPos > 0.4) {
				 filter_constant = LOW_FILT_CONST;
			 }
		}
		/*  Basic formula for a simple 1st order filter is:      */
		/*      ((1-FC)* input) + (FC * prev_value)              */
		/*  where FC is filter constant ref: wikipedia, et. al.  */
		/* this works because we set the filter const to 0 under */
		/* most conditions.  Only nonzero when decreasing & >0.4 */
		prevStickPos = ((1-filter_constant)* instick) + (filter_constant * prevStickPos);
		return (prevStickPos);
	}
	
	public static void main (String[] args) {
		
		for (int i = 0; i < 1000; i++) {
			stickpos = sim_game_controller(i);
		}
		/*System.out.println ("hello world");*/
		double i = 0;
		double new_time;
		double out_time;
		double raw_pos;
		double adjusted_pos;
		
		while (i < 100) {
			raw_pos = sim_game_controller(((double)i));
			adjusted_pos = filter_input(raw_pos);
			System.out.println(raw_pos + " " + adjusted_pos);
			new_time = System.currentTimeMillis();
			out_time = get_time_delta(new_time);
			//System.out.print(i , new_time, out_time);
			//System.out.print(i + " ");
			//System.out.println(out_time);
			if (i > 90) {
				int t = 5;
				for (int j = 0; j <= 1; j++) {
				   t++;
				   t = t/(j+1);
				   double pi = 22/7;
				   double euler = pi * pi * pi;
				   euler = euler * euler / pi;
				   pi = (double)i;
				   long nap = (long)pi;
				   try {
                       Thread.sleep(nap);
                   }
                   catch (InterruptedException ie) {
                        //Handle exception
                   }
			   }
		   }	
								
			i++;
		}
		
		/*String ruler1 = " 1 ";
        String ruler2 = ruler1 + "2" + ruler1;
        String ruler3 = ruler2 + "3" + ruler2;
        String ruler4 = ruler3 + "4" + ruler3;
        String ruler5 = ruler4 + "5" + ruler4;

        System.out.println(ruler1);
        System.out.println(ruler2);
        System.out.println(ruler3);
        System.out.println(ruler4);
        System.out.println(ruler5);*/ 
	}
}

