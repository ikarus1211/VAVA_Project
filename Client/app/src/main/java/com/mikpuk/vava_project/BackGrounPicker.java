package com.mikpuk.vava_project;

import android.widget.TextView;


import java.security.SecureRandom;
import java.util.Random;


/**
 * Class which only purpose is to pick random background for distance text field
 */
public class BackGrounPicker {


    public void randomBackground(TextView textDistance)
    {

        SecureRandom random = new SecureRandom();
        int result = random.nextInt()%1200;
        if (result <= 200)
        {
            textDistance.setBackgroundResource(R.drawable.fullcircle2);
        }
        else if (result > 200 && result <= 400)
        {
            textDistance.setBackgroundResource(R.drawable.fullcircle);
        }
        else if(result > 400 && result <= 600)
        {
            textDistance.setBackgroundResource(R.drawable.fullcircle3);
        }
        else if(result > 600 && result <= 800)
        {
            textDistance.setBackgroundResource(R.drawable.fullcircle6);
        }
        else if(result > 800 && result <= 1000)
        {
            textDistance.setBackgroundResource(R.drawable.fullcircle5);
        }
        else if(result > 1000 && result <= 1200)
        {
            textDistance.setBackgroundResource(R.drawable.fullcircle4);
        }
    }
}
