package pena.antoino.s107gcontrols.syma;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class MiniHelicopter_Controls extends Activity implements SeekBar.OnSeekBarChangeListener {

    public volatile  int[] pattern = new int[67];

    ConsumerIrManager mCIR;  //need to change this to handle different vendors as vendors now use their own wrapper class.
    SeekBar yawControl, pitchControl, throttleControl, trimControl;
    TextView yawDisplay, pitchDisplay, throttleDisplay, trimDisplay;
    //Switch sw;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mini_helicopter__controls);

        yawDisplay = (TextView)findViewById(R.id.tvYaw);
        pitchDisplay = (TextView)findViewById(R.id.tvPitch);
        throttleDisplay = (TextView)findViewById(R.id.tvThrottle);
        trimDisplay = (TextView)findViewById(R.id.tvTrim);

        yawControl =(SeekBar)findViewById(R.id.sbYaw);
        yawControl.setMax(127);
        yawControl.setProgress(63);
        yawControl.setOnSeekBarChangeListener(this);

        pitchControl =(SeekBar)findViewById(R.id.sbPitch);
        pitchControl.setMax(127);
        pitchControl.setProgress(63);
        pitchControl.setOnSeekBarChangeListener(this);

        throttleControl =(SeekBar)findViewById(R.id.sbThrottle);
        throttleControl.setMax(127);
        throttleControl.setProgress(0);
        throttleControl.setOnSeekBarChangeListener(this);
        
        trimControl = (SeekBar)findViewById(R.id.sbTrim);
        trimControl.setMax(127);
        trimControl.setProgress(63);
        trimControl.setOnSeekBarChangeListener(this);
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected int[] pulses(int yaw, int pitch, int throttle, int trim)
    {
        mCIR = (ConsumerIrManager)getSystemService(CONSUMER_IR_SERVICE);
       if(! mCIR.hasIrEmitter()){
           onPause();
       }

        int mask;

        // START PULSE
        pattern[0] = 2002;
        pattern[1] = 2000;

        // END PULSE
        pattern[66] = 270;

        // for yaw seekBar
        int y = 2;
        if( y < 18)
        {
            for(mask = 128; mask > 0; mask >>= 1)
            {
                pattern[y] = 250;
                y++;
                if((mask&yaw) == mask)
                {
                    pattern[y]=700;
                }
                else
                {
                    pattern[y]=300;
                }
                y++;
            }
        }

        //Pitch seekBar
        int p = 18;
        if( p < 34)
        {
            for(mask = 128; mask > 0; mask >>= 1)
            {
                pattern[p] = 250;
                p++;
                if((mask&pitch) == mask)
                {
                    pattern[p]=700;
                }
                else
                {
                    pattern[p]=300;
                }
                p++;
            }
        }

        // throttle seekBar
        int t = 34;
        if( t < 50)
        {
            for(mask = 128; mask > 0; mask >>= 1)
            {
                pattern[t] = 250;
                t++;
                if((mask&throttle) == mask)
                {
                    pattern[t]=700;
                }
                else
                {
                    pattern[t]=300;
                }
                t++;
            }
        }

        // for trim seekBar
        int r = 50;
        if( r < 66)
        {
            for(mask = 128; mask > 0; mask >>= 1)
            {
                pattern[r] = 250;
                r++;
                if((mask&trim) == mask)
                {
                    pattern[r]=700;
                }
                else
                {
                    pattern[r]=300;
                }
                r++;
            }
        }
        return pattern;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean b) {
        //TODO: need to correct contactination warnings
        yawDisplay.setText("YAW " + yawControl.getProgress());
        pitchDisplay.setText("PITCH " + pitchControl.getProgress());
        throttleDisplay.setText("THROTTLE " + throttleControl.getProgress());
        trimDisplay.setText("TRIM " +    trimControl.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        new Thread(new Send()).start();
        //TODO: need to correct contactination warnings
        yawDisplay.setText("YAW " + yawControl.getProgress());
        pitchDisplay.setText("PITCH " + pitchControl.getProgress());
        throttleDisplay.setText("THROTTLE " + throttleControl.getProgress());
        trimDisplay.setText("TRIM " +    trimControl.getProgress());
        yawControl.setProgress(63);
        pitchControl.setProgress(63);

    }

    class Send implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                while (throttleControl.getProgress()>0)
                  mCIR.transmit(38000,pulses(yawControl.getProgress(), pitchControl.getProgress(), throttleControl.getProgress() ,trimControl.getProgress()));
            }
            catch (Exception e)
            { e.printStackTrace();}
        }
    }

}
