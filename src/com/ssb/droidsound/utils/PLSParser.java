package com.ssb.droidsound.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class PLSParser implements PlaylistParser {
	private static final String TAG = M3UParser.class.getSimpleName();
	private List<String> songs;
	private List<String> descs;

	public PLSParser(File file) {
		BufferedReader reader;
		
		songs = new ArrayList<String>();
		descs = new ArrayList<String>();
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while(line != null) {				
				Log.v(TAG, line);
				line = line.trim();
				
				if(line.length() > 0) {
					
					String args [] = line.split("=");
					if(args.length >= 2) {
						if(line.startsWith("File")) {
							songs.add(args[1]);
						} if(line.startsWith("Title")) {
							descs.add(args[1]);
						}
					}
					Log.v(TAG, String.format("Line:%s", line));
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMedia(int i) { return songs.get(i); }
	public String getDescription(int i) { return descs.get(i); }
	public int getMediaCount() { return songs.size(); }
	public List<String> getMediaList() { return songs; }
}