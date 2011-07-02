/*
 * APRS Frame generator
 * 
 * this class generates an AX.25 compliant UI Frame incl. bit stuffing.
 * 
 * 08/29/2010
 * Bastian Mueller
 * 
 */
package com.nogy.afu.soundmodem;

public class APRSFrame {
	private String srca; // Source Address
	private String desta; // Destination Address
	private String digia; // Digipeater Addresses
	private byte cf; // controll field
	private byte protoId;
	private String data;
	@SuppressWarnings("unused")
	private byte flag;
	private int framelength;
	
	public APRSFrame (String source, String destination, String digipeaters, String data, int framelength)
	{
		this.cf = 0x03;
		this.protoId = -16; // = 0xf0
		this.flag = 0x7e;
		this.srca = source;
		this.desta = destination;
		this.digia = digipeaters;
		this.data = data;
		this.framelength = framelength;
	}
		
	public static byte[] crc16(byte[] d)
	{
		byte[] out = new byte[2];
		int crc = 0xFFFF;
		int crcpoly = 0x8408;
		int i,k=0;
		for (i=0; i<d.length-2; i++)
			for (k=0; k<8; k++)
			{
				if ((crc & 1) != ((d[i] & (1 << k))>>k))
					crc = ((crc >> 1) ^ crcpoly) & 0xFFFF;					
				else
					crc >>= 1;
			}
		crc ^= 0xFFFF;
		out[1] = (byte)(((crc & 0xff00) >> 8)-255-1);   // high byte
		out[0] = (byte)((crc & 0xff)-255-1);	        // low byte
		return out;
	}
	
	public static Message bitStuffAndFrame(byte[] d, int framelength)
	{
		byte[] out = new byte[d.length+framelength+(int)Math.ceil(d.length/5)+1];
		int i,k=0;
		int ones;//,bits,bytes;
//		bits =0; 
//		bytes =0;
		ones = 0;
		for (i=0; i<out.length; i++)
			out[i] = 0;
		for (i=0; i<framelength; i++)
			out[i] = 0x7e;
		i=0;	
		k=framelength*8;
		while (i<d.length*8)
		{
			if ((d[i/8] & (1<<(i%8))) > 0)	// current bit == 1
			{
				
				out[k/8] |= ((1 << (k%8))>127)? (byte)((1 << (k%8))-256): (byte)(1 << (k%8));	// insert 
				if (ones++ == 4)			// going to stuff a 0
				{
					k++;
					ones = 0;					
				}
			}
			else
			{
				ones = 0;
			}
			i++;
			k++;
		}
		i=0;
		while ((i)<8)
		{
			if ((0x7e & (1<<(i%8))) > 0)	// current bit == 1
			{
				out[k/8] |= ((1 << (k%8))>127)? (byte)((1 << (k%8))-256): (byte)(1 << (k%8));	// insert 
			}
			k++;			
			i++;
		}
		Message m = new Message();
		m.numberOfBits = k+1;
		m.data = out;
		return m;
	}
	
	private byte[] parseCall(String call, boolean isDest)
	{
		byte[] out = new byte[7];	// Always 7 Byte long
		String[] mycall;
		mycall = call.split("-");
		char[] c = mycall[0].toUpperCase().toCharArray();
		
		int i=0;
		
		for (i=0; i<6; i++)
			out[i] = 0x40;
		
		i=0;
		
		while (i<c.length)
		{
			out[i] = (byte)(((int)(c[i])) << 1);
			i++;
		}
		while (i<6)
		{
			out[i++] = 0x40; // Fill with Spaces			
		}
		if (mycall.length>1)
		{
			switch (Integer.parseInt(mycall[1]))
			{
				case 0: out[6] = 0x60; break;
				case 1: out[6] = 0x62; break;
				case 2: out[6] = 0x64; break;
				case 3: out[6] = 0x66; break;
				case 4: out[6] = 0x68; break;
				case 5: out[6] = 0x6A; break;
				case 6: out[6] = 0x6C; break;
				case 7: out[6] = 0x6E; break;
				case 8: out[6] = 0x70; break;
				case 9: out[6] = 0x72; break;
				case 10: out[6] = 0x74; break;
				case 11: out[6] = 0x76; break;
				case 12: out[6] = 0x78; break;
				case 13: out[6] = 0x7A; break;
				case 14: out[6] = 0x7C;  break;
				default: out[6] = 0x60;  break;
			}
		}
		else 
			out[6] = 0x60; // SSID 0 as default;
		if (isDest)			// set ssid-msb to 1
		{
			out[6] |= 0x80;
		}
		return out;
	}
	
	public Message getMessage()
	{
		String[] digis = this.digia.split(",");
		byte[] out = new byte[14+digis.length*7+2+this.data.length()+2];
		byte[] temp;
		int k,i=0;
		temp = parseCall(this.desta, true);
		while(i<7)		// Fill in source
		{
			out[i] = temp[i];
			i++;
		}
		temp = parseCall(this.srca, false);
		while(i<14)		// Fill in dest
		{
			out[i] = temp[i-7];
			i++;
		}
		for (k=0; k<digis.length; k++)	// Parse an fill in Digis
		{
			temp = parseCall(digis[k],false);
			while(i<21+k*7)	
			{
				out[i] = temp[i-(14+k*7)];
				i++;
			}
		}
		out[i-1] |= 0x01;
		out[i++] = this.cf;
		out[i++] = this.protoId;
		k=0;
		while (k<this.data.length())
		{
			out[i++] = (byte)(((int)(data.charAt(k++))) -256);
		}
		temp = crc16(out);
		out[i++] = temp[0];
		out[i] = temp[1];
		return bitStuffAndFrame(out, this.framelength);
	}
	
	
		
}
