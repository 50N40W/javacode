
package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by admin on 1/16/2016.
 */
public class Auto_Test_2 extends LinearOpMode {
    DcMotor left_Drive;
    DcMotor right_Drive;

    @Override
    public void runOpMode() throws InterruptedException {
        left_Drive = hardwareMap.dcMotor.get("left_drive");
        right_Drive = hardwareMap.dcMotor.get("right_drive");
        right_Drive.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        for(int i=0; i<1; i++){
            right_Drive.setPower(0.8);
            left_Drive.setPower(0.8);
            sleep(1000);

            right_Drive.setPower(0);
            left_Drive.setPower(0.4);
            sleep(1000);

            right_Drive.setPower(0.2);
            left_Drive.setPower(0.2);
            sleep(1000);
        }
    }
}
