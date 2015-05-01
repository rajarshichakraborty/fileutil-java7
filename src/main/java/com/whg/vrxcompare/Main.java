package com.whg.vrxcompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whg.vrxcompare.FileUtil.FileType;

public class Main {

	private static final String INPUT_PROPERTY_LIST = "C:/Rajarshi/MyLab/VRXDAMCOMPARE/proplist.csv";
	private static final String OUTPUT_FILE = "C:/Rajarshi/MyLab/VRXDAMCOMPARE/output.csv";
	private static final String LDRIVE_IMAGE_PATH = "//hotelgroup.com/newjersey/HIT/Rajarshi/LDrive/Images";
	private static final String LDRIVE_VRX_PATH = "//hotelgroup.com/newjersey/HIT/Rajarshi/LDrive/VRX";

	private static final String[] LDRIVE_IMAGES_BRANDS = new String[] {
			"Days Inn", "Baymont", "Dream-Night", "Hawthorn", "Howard Johnson",
			"Knights", "Microtel", "Ramada", "Super 8", "Travelodge", "TRYP",
			"Wingate", "Wyndham Garden", "Wyndham Grand",
			"Wyndham Hotels and Resorts" };
	private static final String[] LDRIVE_VRX_BRANDS = new String[] { "Baymont",
			"Days Inn", "Hawthorn", "Howard Johnson", "Knights Inn",
			"Microtel", "Ramada", "Super 8", "Travelodge", "Wyndham" };
	private static final HashMap<String, String> propNamevsId;

	static {
		propNamevsId = new HashMap<String, String>();
		propNamevsId.put("Baymont", "BU");
		propNamevsId.put("Days Inn", "DI");
		propNamevsId.put("Ramada", "RA");
		propNamevsId.put("Howard Johnson", "HJ");
		propNamevsId.put("Super 8", "SE");
		propNamevsId.put("Wyndham", "WY");
		propNamevsId.put("Wingate By Wyndham", "WG");
		propNamevsId.put("Knights Inn", "KI");
		propNamevsId.put("Travelodge", "TL");
		propNamevsId.put("Microtel", "MT");
		propNamevsId.put("Hawthorn", "BH");
		propNamevsId.put("Dream-Night", "PX");
		propNamevsId.put("Knights", "KI");
		propNamevsId.put("TRYP", "WT");
		propNamevsId.put("Wingate", "WG");
		propNamevsId.put("Wyndham Garden", "WY");
		propNamevsId.put("Wyndham Grand", "WY");
		propNamevsId.put("Wyndham Hotels and Resorts", "WY");

	}

	private List<FileMeta> finallist = new ArrayList<FileMeta>();

	public static void main(String[] args) {

		// Fetch the input brand and property ids
		List<String> inputdata = new FileUtil(INPUT_PROPERTY_LIST).readFile();

		List<FileMeta> filesetinput = new ArrayList<FileMeta>();
		if (inputdata != null && inputdata.size() > 0) {
			for (String brandpropline : inputdata) {
				FileMeta fileMeta = new FileMeta();
				String[] bparr = brandpropline.split(",");
				String brandId = bparr[0], propertyId = padzero(bparr[1]);
				fileMeta.setBrandId(brandId);
				fileMeta.setPropertyId(propertyId);
				filesetinput.add(fileMeta);
			}
		}

		// cumilitive set of files
		List<FileMeta> filesetcomplete = new ArrayList<FileMeta>();
		List<FileMeta> filesetcompleteImages = new ArrayList<FileMeta>();
		List<FileMeta> filesetcompleteVRX = new ArrayList<FileMeta>();
		// Read L drive image folder
		for (String brandfolders : LDRIVE_IMAGES_BRANDS) {
			String path = LDRIVE_IMAGE_PATH + '/' + brandfolders;
			List<FileMeta> ilist = new FileUtil(path).findFiles(FileType.IMAGE);
			for (FileMeta fileMeta : ilist) {
				fileMeta.setBrandId(propNamevsId.get(brandfolders));
				fileMeta.setPropertyId(getProp(fileMeta.getFilePath()));
				fileMeta.setInLDVRX("FALSE");
				fileMeta.setInLDImages("TRUE");
				System.out.println(fileMeta.getPropertyId());
			}
			filesetcompleteImages.addAll(ilist);
		}

		// Read L drive VRX folder
		for (String brandfolders : LDRIVE_VRX_BRANDS) {
			String path = LDRIVE_VRX_PATH + '/' + brandfolders;
			List<FileMeta> ilist = new FileUtil(path).findFiles(FileType.IMAGE);
			for (FileMeta fileMeta : ilist) {
				fileMeta.setBrandId(propNamevsId.get(brandfolders));
				fileMeta.setPropertyId(getProp(fileMeta.getFilePath()));
				fileMeta.setInLDVRX("TRUE");
				fileMeta.setInLDImages("FALSE");
				System.out.println(fileMeta.getPropertyId());
			}
			filesetcompleteVRX.addAll(ilist);
		}

		filesetcomplete.addAll(filesetcompleteImages);
		filesetcomplete.addAll(filesetcompleteVRX);

		/*
		 * for (FileMeta fileMeta : filesetcompleteImages) {
		 * if(fileMeta.getFilePath().contains(s)) }
		 */

		// Generate the output
		System.out.println(filesetcomplete.size());

		StringBuffer sb = new StringBuffer();
		for (FileMeta fileMetaOut : filesetcomplete) {
			for (FileMeta fileMetaInp : filesetinput) {
				if(fileMetaInp.getBrandId().equalsIgnoreCase(fileMetaOut.getBrandId())
						&& fileMetaInp.getPropertyId().equalsIgnoreCase(fileMetaOut.getPropertyId())) {
					fileMetaOut.setInPIMT("TRUE");
					sb.append(fileMetaOut.toString()).append(System.getProperty("line.separator"));
				}
			}
		}
		
		new FileUtil(OUTPUT_FILE).writeFile(sb.toString());
	}

	private static String padzero(String id) {
		return ("00000" + id).substring(id.length());
	}

	private static String getProp(String path) {
		Matcher m = Pattern.compile("([0-9]{1,5}+)").matcher(path);
		if (m.find()) {
			return padzero(m.group());
		}
		return "00000";
	}
	
	class ReadThread extends Thread {
		 
		@Override
		public void run() {
	 
			System.out.println(getName() + " is running");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(getName() + " is running");
		}
	 
	}

}
