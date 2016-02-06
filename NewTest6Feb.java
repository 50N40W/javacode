package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Authors - Nate, Jack, Carlos, and Helen
 * Instructed by - Jeremy
 * 10/10/15 - Created initial drive code
 * 10/13/15 - added arm. fixed joystick mapping issue
 * 10/31/15 - Happy Halloween, we created a new Version!!!!!!!!!!!!!!!!!!!!!!
 * Renamed by Carlos
 * Authors of the new Version - Carlos, Nate, Jack, and Chase.
 * Instructed by - Howard
 */
public class New_Test2 extends OpMode
{
    public enum unloader_state {Center, Left, Right}
    DcMotor left_Drive;
    DcMotor right_Drive;
    DcMotor esca_Drive;

    Servo Unload;

    Servo Right_Climber_Releaser;
    Servo Left_Climber_Releaser;

    DcMotor whinch;

    Servo whinch_pos;
    public static double whinch_position = .5;

    public static float prev_leftStickPos = 0;
    public static float prev_rightStickPos = 0;
    //public static final float HI_FILT_CONST = 0.96;
    public static final double HI_FILT_CONST = 0.96;
    public static final double MID_FILT_CONST = 0.5;
    public static final double LOW_FILT_CONST = 0.1;
    public static double whinch_extend = 0;
    public static float right_drive_gain = 1;
    public static float left_drive_gain = 1;
    public static double max_whinch_pos = .9;
    public static double max_whinch_effort = 1;
    public static double whinch_extend_time = 0;
    public static double whinch_retract_time = 0;

    /*final static double unload_Up = .25;
    final static double unload_Down = .78;
    double unload_Target = unload_Up;
/*/

    unloader_state cur_state;

    @Override
    public void init()
    {
        //get references to the drive motor from the hardware map
        left_Drive = hardwareMap.dcMotor.get("left_drive");
        right_Drive = hardwareMap.dcMotor.get("right_drive");

        Unload = hardwareMap.servo.get("Unload");

        //DcMotor whinch  = hardwareMap.dcmotor.get("whinch");
        whinch = hardwareMap.dcMotor.get("whinch");

        whinch_pos = hardwareMap.servo.get("whinch_pos");

        //DcMotor esca_Drive = hardwareMap.dcMotor.get("esca_drive");
        esca_Drive = hardwareMap.dcMotor.get("esca_drive");

        Right_Climber_Releaser = hardwareMap.servo.get("Right_Climber_Releaser");
        Left_Climber_Releaser = hardwareMap.servo.get("Left_Climber_Releaser");


        right_Drive.setDirection(DcMotor.Direction.REVERSE);

        max_whinch_pos = .9;
        

        cur_state = unloader_state.Center;

        // set the mode
        // Nxt devices start up in "write" mode by default, so no need to switch device modes here.
        left_Drive.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        right_Drive.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);


    }


    /* filter_input                                             */
	/*  Purpose is to adjust the stick input for better control */
	/*  The basic method could be included in the controller    */

    public static float filter_input(float instick, float prevStickPos) {
        float filter_constant = 0;  /*init to zero just in case */
        if (instick < 0) {
            instick = instick * instick * (-1);
        }
        else
        {
            instick = instick * instick;
        }

        //  using -0.05 allows the return spring to overshoot a little.
        if ((instick > -.05) && (instick < prevStickPos)) {

            if (prevStickPos > 0.85) {
                //filter_constant = (double)HI_FILT_CONST;
                filter_constant = (float)0.95;
            }
            else if (prevStickPos > 0.6) {
                //filter_constant = MID_FILT_CONST;
                filter_constant = (float)0.65;

            }
            else if (prevStickPos > 0.4) {
                filter_constant = (float)0.35;

                //filter_constant = LOW_FILT_CONST;
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

    @Override
    public void loop() {
        //Code to control Drive motors
        right_drive_gain = 1;
        left_drive_gain = 1;
        float left_drive_effort = filter_input(gamepad1.left_stick_y, prev_leftStickPos);
        prev_leftStickPos = left_drive_effort;
        float right_drive_effort = filter_input(gamepad1.right_stick_y, prev_rightStickPos);
        prev_rightStickPos = right_drive_effort;

        max_whinch_effort = 1;

        if (gamepad1.right_bumper) {
            left_drive_gain = Math.min(left_drive_gain, .25f);
            right_drive_gain = Math.min(right_drive_gain, .25f);
        }

        if (whinch_extend < 0) {
            if (whinch_retract_time > 2) {
                left_drive_gain = Math.min(left_drive_gain, .05f);
                right_drive_gain = Math.min(right_drive_gain, .05f);
            }
        } else {
            if (whinch_retract_time > 4) {
                left_drive_gain = Math.min(left_drive_gain, .10f);
                right_drive_gain = Math.min(right_drive_gain, .10f);
            }
        }

        left_Drive.setPower(left_drive_effort * left_drive_gain);
        right_Drive.setPower(right_drive_effort * right_drive_gain);


        //Code to control unload
        double unload_target = .5;

        if (gamepad2.dpad_right) {
            unload_target = 0;
        }
        if (gamepad2.dpad_left) {
            unload_target = 1;
        }

        Unload.setPosition(unload_target);
        //Unload.setPosition(unload_target);
       /* float unload_pos = left_Drive.getCurrentPosition();
        unload_Target = 0;
        switch (cur_state)
        {
            case Center:
                if (gamepad1.right_bumper)
                {
                    cur_state = unload_state.Right;
                }
                else if (gamepad1.left_bumper)
                {
                    cur_state = unload_state.Left;
                }
                else
                {
                    unload_Target = Math.max(-1, Math.min(1, -(unload_pos * .1)));
                }

                break;
            case Left:
                if (unload_pos < -100)
                {
                    cur_state = unload_state.Center;
                }
                else
                {
                    unload_Target = -1;
                }

                break;
            case Right:
                if (unload_pos > 100)
                {
                    cur_state = unload_state.Center;
                }
                else
                {
                    unload_Target = 1;
                }
                break;
            default:
                cur_state = unload_state.Center;
                break;
        }
        Unload.setPosition(unload_Target);
/*/

        //Code to control the escalator drive
        float right_trigger = gamepad1.right_trigger;
        float left_trigger = gamepad1.left_trigger;
        float esca_drive_effort = 0;

        if (right_trigger > .1) {
            esca_drive_effort = -1 * right_trigger;
        } else if (left_trigger > .1) {
            esca_drive_effort = left_trigger;
        }

        esca_Drive.setPower(esca_drive_effort);

        //code to control whinch position


        if (gamepad2.right_bumper) {
            whinch_position = whinch_position + 0.001;
        } else if (gamepad2.left_bumper) {
            whinch_position = whinch_position - 0.001;
        }
        if (whinch_extend_time >= 1000) {
            if (whinch_position > 0.75) {
                whinch_position = whinch_position - 0.01;
            }
        }

            if (whinch_position < 0) {
                whinch_position = 0;
            }
            if (whinch_position > max_whinch_pos) {
                whinch_position = max_whinch_pos;
            }

            whinch_pos.setPosition(whinch_position);


            //code to control whinch extend
            whinch_extend = 0;

            if (gamepad2.b) {
                whinch_extend = .25;
                max_whinch_pos = .75;
                whinch_extend_time += 1;
            } else {
                whinch_extend_time = 0;
            }

            if (gamepad2.a) {
                whinch_extend = -.95;
                whinch_retract_time += 1;
            } else {
                whinch_retract_time = 0;
            }

            if (left_drive_effort >= 0.05) {
                if (right_drive_effort >= 0.05) {
                    max_whinch_effort = 0.4;
                }
            }

            if (whinch_extend > max_whinch_effort) {
                whinch_extend = max_whinch_effort;
            }

            whinch.setPower(whinch_extend);

            double Right_Flip_pos = 0.45;
            double Left_Flip_pos = 0.45;

            if (gamepad2.x) {
                Right_Flip_pos = 1;
            }
            if (gamepad2.y) {
                Left_Flip_pos = 0;
            }

            Right_Climber_Releaser.setPosition(Right_Flip_pos);
            Left_Climber_Releaser.setPosition(Left_Flip_pos);

            //Push Gamepad commands to console
            telemetry.addData("01", "Gamepad1 info" + gamepad1.toString());
            telemetry.addData("02", "Gamepad2 info" + gamepad2.toString());
        }
    }
