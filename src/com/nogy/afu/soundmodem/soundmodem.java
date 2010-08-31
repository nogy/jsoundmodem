/*
 *  Simple demo application sending testdata
 */
package com.nogy.afu.soundmodem;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class soundmodem extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void do_TX(View v)
    {
    	Message msg;
    	APRSFrame frame = new APRSFrame("NOCALL", "APA110", "WIDE1-1", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/*-+%&etc...", 20);
    	msg = frame.getMessage();
//    	for (i=0; i<msg.numberOfBits; i++)
//    		Log.d("msg", "["+Integer.toString(i/8)+"]="+Integer.toString((msg.data[i/8] & (1<<i%8))>>i%8));
    	Afsk a = new Afsk();
    	a.sendMessage(msg);
    }
}