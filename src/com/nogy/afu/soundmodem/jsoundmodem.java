/*
 *  Simple demo application sending testdata
 */
package com.nogy.afu.soundmodem;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nogy.afu.soundmodem.R;

public class jsoundmodem extends Activity {
    /** Called when the activity is first created. */
	
	private Afsk a;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        a = new Afsk();
    }
    
    public void do_TX(View v)
    {
    	Message msg;
    	APRSFrame frame = new APRSFrame("NOCALL", "APA110", "WIDE1-1", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/*-+%&etc...", 20);
    	msg = frame.getMessage();
//    	for (i=0; i<msg.numberOfBits; i++)
//    		Log.d("msg", "["+Integer.toString(i/8)+"]="+Integer.toString((msg.data[i/8] & (1<<i%8))>>i%8));
    	a.sendMessage(msg);
    }
    
    public void do_RX(View v)
    {
    	a.debug = (TextView)findViewById(R.id.Status);
    	a.readPCM();
    	
    	
    }
    
}