package com.ssb.droidsound.plugins;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.util.Log;

public class SidplayPlugin extends DroidSoundPlugin {

	public SidplayPlugin(Context ctx) {
		super(ctx);
	}

	private static final String TAG = SidplayPlugin.class.getSimpleName();


	static {
		System.loadLibrary("sidplay2");
	}
	
	private static class Info {
		String name;
		String composer;
		String copyright;
	};
	
	final byte [] header = new byte [128];
	//private byte[] lenData;
	private byte[] mainHash;
	private short[] extraLengths;
	private int hashLen;
	
	
	private int songLengths[] = new int [256];
	private int currentSong;

	@Override
	public boolean canHandle(String name) { 
		//return N_canHandle(name);
		return name.toLowerCase().endsWith(".sid");
	}

	@Override
	public void unload(Object song) {
		if(song instanceof Info) {
			return;
		}
		N_unload((Long)song);
	}
	
	// Expects Stereo, 44.1Khz, signed, big-endian shorts
	@Override
	public int getSoundData(Object song, short [] dest, int size) { return N_getSoundData((Long)song, dest, size); }	
	@Override
	public boolean seekTo(Object song, int seconds) { return N_seekTo((Long)song, seconds); }
	@Override
	public boolean setTune(Object song, int tune) {
		boolean ok = N_setTune((Long)song, tune);
		if(ok) {
			currentSong = tune;
		}
		return ok;
	}
	@Override
	public int getIntInfo(Object song, int what) {
		if(song instanceof Info) {
			return 0;
		}
		
		if(what == INFO_LENGTH) {
			return songLengths[currentSong];
		}
		
		return N_getIntInfo((Long)song, what);
	}

	native public long N_load(byte [] module, int size);
	native public void N_unload(long song);
	
	// Expects Stereo, 44.1Khz, signed, big-endian shorts
	native public int N_getSoundData(long song, short [] dest, int size);
	native public boolean N_seekTo(long song, int seconds);
	native public boolean N_setTune(long song, int tune);
	native public String N_getStringInfo(long song, int what);
	native public int N_getIntInfo(long song, int what);

	
	
	@Override
	public Object load(byte [] module, int size) {
		
		currentSong = 0;
		
		long rc = N_load(module, size);
		
		if(rc == 0) {
			return null;
		}

		currentSong = getIntInfo(rc, DroidSoundPlugin.INFO_STARTTUNE);
		
		for(int i=0; i<256; i++) {
			songLengths[i] = 60*60*1000;
		}
		
		Context context = getContext();
		if(context != null) {		
			if(mainHash == null) {
				try {
					InputStream is = context.getAssets().open("songlengths.dat");
					if(is != null) {						
						DataInputStream dis = new DataInputStream(is);
						hashLen = dis.readInt();
						mainHash = new byte [hashLen * 6];
						dis.read(mainHash);
						int l = is.available()/2;
						Log.v(TAG, String.format("We have %d lengths and %d hashes", l, hashLen));
						extraLengths = new short [l];
						for(int i=0; i<l; i++) {
							extraLengths[i] = dis.readShort();
						}
						is.close();
						
					}
				} catch (IOException e) {
				}
			}
		}
		
		if(mainHash != null) {		
			byte [] md5 = findMD5(module, size);
			int first = 0;
			int upto = hashLen;
			
			int found = -1;
			
			long key = ((md5[0]&0xff) << 24) | ((md5[1]&0xff) << 16) | ((md5[2]&0xff) << 8) | (md5[3] & 0xff);
			key &= 0xffffffffL;
			
			//short lens [] = new short [128];
			
	    	Log.v(TAG, String.format("MD5 %08x", key));
			while (first < upto) {
		        int mid = (first + upto) / 2;  // Compute mid point.
	    		long hash = ((mainHash[mid*6]&0xff) << 24) | ((mainHash[mid*6+1]&0xff) << 16) | ((mainHash[mid*6+2]&0xff) << 8) | (mainHash[mid*6+3] & 0xff);
	    		hash &= 0xffffffffL;
	
	        	//Log.v(TAG, String.format("offs %x, hash %08x", mid, hash));
		        if (key < hash) {
		            upto = mid;     // repeat search in bottom half.
		        } else if (key > hash) {
		            first = mid + 1;  // Repeat search in top half.
		        } else {
		        	found = mid;
		        	int len = ((mainHash[mid*6+4]&0xff)<<8) | (mainHash[mid*6+5]&0xff);
	    			Log.v(TAG, String.format("LEN: %x", len));
		        	if((len & 0x8000) != 0) {
		        		len &= 0x7fff;
		        		int xl = 0;
		        		int n = 0;
		        		while((xl & 0x8000) == 0) {
		        			xl = extraLengths[len++] & 0xffff;
		        			songLengths[n++] = ((xl & 0x7fff)-1) * 1000;
		        		}
		        		
		        		//for(int i=0; i<n; i++) {
		        		//	Log.v(TAG, String.format("LEN: %02d:%02d", songLengths[i]/60, songLengths[i]%60));
		        		//}
		        	} else {
		        		Log.v(TAG, String.format("SINGLE LEN: %02d:%02d", len/60, len%60));
		        		songLengths[0] = (len-1) * 1000;
		        	}
		        	break;
		        }
		    }
			
			Log.v(TAG, String.format("Found md5 at offset %d", found));
		}		
		return rc; 
	}

	@Override
	public String getStringInfo(Object song, int what) {
		if(song instanceof Info) {
			Info info = (Info)song;
			switch(what) {
			case INFO_AUTHOR:
				return info.composer;
			case INFO_COPYRIGHT:
				return info.copyright;
			case INFO_TITLE:
				return info.name;
			}
			return null;
		}
		return N_getStringInfo((Long)song, what);
	}
	
	@Override
	public String[] getDetailedInfo(Object song) {

		final String sids [] = { "UNKNOWN", "6581", "8580", "6581 & 8580" };
		
		String[] info = new String [4];
		info[0] = "Copyright";
		info[1] = N_getStringInfo((Long)song, INFO_COPYRIGHT);
		int m = N_getIntInfo((Long)song, 101);
		Log.v(TAG, "Sid model " + m);
		if(m <= 3 && m >= 0) {
			info[2] = "SID Model";
			info[3] = sids[m];
		}
		return info;
	}
	
	/* 					if(currentSongInfo.md5 != null) {					
						final String HEX = "0123456789abcdef";
						
						StringBuilder sb = new StringBuilder(16);
						for(int i=0; i<16; i++) {
							byte x = currentSongInfo.md5[i];
							sb.append(HEX.charAt((x>>4)&0xf)).append(HEX.charAt(x&0xf));
						}
						
						info[SONG_MD5] = sb.toString();
						Log.v(TAG, "MD5: " + info[SONG_MD5]);

					} else {
						Log.v(TAG, "No MD5!");
					}

	 */
	private byte [] findMD5(byte[] module, int size) {

		ByteBuffer src = ByteBuffer.wrap(module, 0, size);		
		src.order(ByteOrder.BIG_ENDIAN);
		
		byte [] id = new byte [4];
		src.get(id);
		int version = src.getShort();
		src.position(8);
		short loadAdress = src.getShort();
		short initAdress = src.getShort();
		short playAdress = src.getShort();
		short songs = src.getShort();
		short startSong = src.getShort();
		int speedBits = src.getInt();
		src.position(0x76);
		int flags = src.getShort();
		
		int offset = 120;
		if(version == 2) {
			offset = 126;
		}
		
		int speed = 0;
		if(id[0] == 'R') {
			speed = 60;
		}
		
		Log.v(TAG, String.format("speed %08x, flags %04x left %d songs %d init %x", speed, flags, size - offset, songs, initAdress));
		
		byte[] md5 = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			md.update(module, offset, size - offset);
			
			ByteBuffer dest = ByteBuffer.allocate(128);
			dest.order(ByteOrder.LITTLE_ENDIAN);
			
			dest.putShort(initAdress);
			dest.putShort(playAdress);
			dest.putShort(songs);
			
			for(int i=0; i<songs; i++) {
				if((speedBits & (1<<i)) != 0) {
					dest.put((byte)60);
				} else {
					dest.put((byte)speed);
				}
			}
			
			if((flags & 0x8) != 0) {
				dest.put((byte)2);
			}

			byte [] d = dest.array();
			md.update(d, 0, dest.position());
			
			md5 = md.digest();
			Log.v(TAG, String.format("%d %02x %02x DIGEST %02x %02x %02x %02x", d.length, d[0], d[1], md5[0], md5[1], md5[2], md5[3]));
			return md5;
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public Object loadInfo(InputStream is, int size) throws IOException {
		
		Info info = new Info();
		
		is.read(header);
		
		String s = new String(header, 0, 4);
		if(!(s.equals("PSID") || s.equals("RSID"))) {
			return null;
		}

		int o = 0x35;
		while(o >= 0x16 && header[o] == 0) {
			o -= 1;
		}
		info.name = new String(header, 0x16, o-0x15, "ISO-8859-1");

		o = 0x55;
		while(o >= 0x36 && header[o] == 0) {
			o -= 1;
		}
		info.composer = new String(header, 0x36, o-0x35, "ISO-8859-1");

		o = 0x75;
		while(o >= 0x56 && header[o] == 0) {
			o -= 1;
		}
		info.copyright = new String(header, 0x56, o-0x55, "ISO-8859-1");

		return info;	
	}
	
	@Override
	public boolean isSilent(Object song) {
		
		return (N_getIntInfo((Long)song, 100) != 0);
	}
	
/*
	@Override
	public Object load(File file) throws IOException {
		
		//if(loadInfo(file) == null) {
		//	return null;
		//}
		
		int l = (int)file.length();
		byte [] songBuffer = new byte [l];
		FileInputStream fs = new FileInputStream(file);
		fs.read(songBuffer);
		fs.close();

		
		long rc = N_load(songBuffer, l);		
		return rc != 0 ? rc : null; 
	} */
}
