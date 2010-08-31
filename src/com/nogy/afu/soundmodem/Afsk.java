/*
 * AFSK modulator
 * 
 * This class helps sending out AFSK modulated data packets for e.g. hamradio use
 * 
 * 08/27/2010
 * Bastian Mueller
 * 
 */

package com.nogy.afu.soundmodem;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Afsk {
	public static int f_low = 1200;
	public static int f_high = 2200;
	public static int bps = 1200;
	public static int samplerate = 12000;
	public static int pcmBits = 16;
	
	public short[] pcmData;
	
	private AudioTrack a;

	private float volume;
	
	public Afsk()
	{
		volume = AudioTrack.getMaxVolume()/2;		
	}
	
	public void setVolume(float vol)	
	{
		this.volume = vol;
	}
	
	public void sendMessage(Message m)
	{
		int i,k=0;
		int t=0;
		int datapoint=0;
		double cospos=0;
		int lasttone=f_low;
		pcmData = new short[(m.numberOfBits*samplerate)/bps];
		for (i=0; i<m.numberOfBits; i++)
		{
			
			
				t=0;
				if ((m.data[i/8] & (1<<(i%8)))==0) // bit to transmit is 0
				{
					lasttone = (lasttone==f_low)?f_high:f_low;
				}
				while(t++<samplerate/bps)
				{
					pcmData[datapoint++]=(short) Math.round(Math.cos(cospos)*((1 << (pcmBits-1))-1));

					cospos += 2*Math.PI*lasttone/samplerate;
					if (cospos > 2*Math.PI)
						cospos -= 2*Math.PI;					
				}
			
		}
		sendPCM();
		
	}
	
	private void sendPCM()
	{
		a = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				samplerate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				pcmData.length*2,
				AudioTrack.MODE_STATIC
				);
		a.setPlaybackRate(samplerate);
		a.write(pcmData, 0, pcmData.length);
		a.setStereoVolume(this.volume, this.volume);
		a.play();
		//a.g
		
	}	
	

}
