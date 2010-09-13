package com.ssb.droidsound;

import java.io.File;

import android.util.Log;

public class SongFile {

	private static final String TAG = SongFile.class.getSimpleName();
	private int subtune;
	private int length;
	String fileName;
	
	private File file;
	private String path;
	private String prefix;
	private String suffix;
	private String midfix;
	private String zipPath;
	private String zipName;
	
	//private String tuneString;
	//private String title;
	//private String composer;
	
	public SongFile(File f) {
		init(f.getPath());
	}
	
	public SongFile(String fname) {
		init(fname);
	}
	
	private void init(String fname) {
		subtune = -1;
		length = -1;
		
		if(fname.startsWith("file://")) {
			fname = fname.substring(7);
		}
		
		String s[] = fname.split(";");
		
		fileName = s[0];
		
		//int sc = fileName.lastIndexOf(';');
		if(s.length > 1) {
			if(s.length > 2) {
				try {
					length = Integer.parseInt(s[2]);
				} catch (NumberFormatException e) {}					
			}
			try {
				subtune = Integer.parseInt(s[1]);
			} catch (NumberFormatException e) {}					
			fileName = s[0];
		}

		
		int slash = fileName.lastIndexOf('/');
		if(slash > 0) {
			path = fileName.substring(0, slash);
			fileName = fileName.substring(slash+1);
		} else {
			path = "";
		}

		midfix = fileName;
		prefix = suffix = "";
		int firstDot = fileName.indexOf('.');
		int lastDot = fileName.lastIndexOf('.');
		if(firstDot > 0) {
			prefix = fileName.substring(0, firstDot);
			suffix = fileName.substring(lastDot+1);
			midfix = fileName.substring(firstDot, lastDot+1);
		}

		int zip = s[0].toUpperCase().indexOf(".ZIP/");
		if(zip > 0) {
			zipPath = s[0].substring(0, zip+4);
			zipName = s[0].substring(zip+5);
		}
		
		file = new File(path, fileName);
		
		Log.v(TAG, String.format("SONGFILE -%s-%s-%s-%s- %d,%d", path, prefix, midfix, suffix, subtune, length));
	}

	public String getPrefix() {
		return prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public File getFile() {
		return file;
	}
	
	public int getSubtune() {
		return subtune;
	}
	
	public int getLength() {
		return length;
	}
	
	public void changePrefix(String p) {
		prefix = p;
		fileName = prefix + midfix + "suffix"; 
	}
	
	public void changeSuffix(String s) {
		suffix = s;
		fileName = prefix + midfix + "suffix"; 
	}

	public SongFile(File f, int t, String title) {
		init(f.getPath());
		if(subtune < 0) {
			subtune = t;
		}
	}
	
	public SongFile(String song, int t) {
		init(song);
		if(t < 0) {
			subtune = t;
		}
	}

	public String getZipPath() {
		return zipPath;
	}

	public String getZipName() {
		return zipName;
	}

	public String getPath() {
		if(subtune >= 0) {
			if(length >= 0) {
				return path + "/" + fileName + ";" + subtune + ";" + length;
			}
			return path + "/" + fileName + ";" + subtune;
		} else {
			return path + "/" + fileName;
		}
	}

	public String getName() {
		//return file.getName();
		return fileName;
	}

	public boolean exists() {
		return file.exists();
	}
	

	public String getParent() {
		return file.getParent();
	}

	public boolean isDirectory() {
		return file.isDirectory();
	}

	public File[] listFiles() {
		return file.listFiles();
	}

	public SongFile[] listSongFiles() {
		File [] files = file.listFiles();
		SongFile [] sfiles = new SongFile [files.length];
		for(int i=0; i<files.length; i++) {
			sfiles[i] = new SongFile(files[i]);
		}
		return sfiles;
	}
}
