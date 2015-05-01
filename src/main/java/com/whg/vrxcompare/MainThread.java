package com.whg.vrxcompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whg.vrxcompare.FileUtil.FileType;

public class MainThread {

	private static String INPUT_PROPERTY_LIST = "C:/Rajarshi/MyLab/VRXDAMCOMPARE/proplist.csv";
	private static String OUTPUT_FILE = "C:/Rajarshi/MyLab/VRXDAMCOMPARE/output.csv";
	private static String LDRIVE_IMAGE_PATH = "//hotelgroup.com/newjersey/HIT/Rajarshi/LDrive/Images/";
	private static String LDRIVE_VRX_PATH = "//hotelgroup.com/newjersey/HIT/Rajarshi/LDrive/VRX/";

	private static final String[] LDRIVE_IMAGES_BRANDS = new String[] {
			"Days Inn", "Baymont", "Dream-Night", "Hawthorn", "Howard Johnson",
			"Knights", "Microtel", "Ramada", "Super 8", "Travelodge", "TRYP",
			"Wingate", "Wyndham Garden", "Wyndham Grand",
			"Wyndham Hotels and Resorts" };
	private static final String[] LDRIVE_VRX_BRANDS = new String[] { "Baymont",
			"Days Inn", "Hawthorn", "Howard Johnson", "Knights Inn",
			"Microtel", "Ramada", "Super 8", "Travelodge", "Wyndham" };
	private static final HashMap<String, String> propNamevsId;

	public enum LocType {
		VRX, IMAGES, OTHER
	}
	
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

	private static List<FileMeta> filesetcompleteImages = new ArrayList<FileMeta>();

	public static void main(String[] args) {
		
		if(args.length > 0 ){
			if(args.length == 4) {
				INPUT_PROPERTY_LIST = args[0];
				OUTPUT_FILE = args[1];
				LDRIVE_IMAGE_PATH = args[2];
				LDRIVE_VRX_PATH = args[3];
			} else {
				System.out.println("USAGE:: " + " java -cp vrx-full.jar com.whg.vrxcompare.MainThread [INPUT_PROPERTY_LIST] [OUTPUT_FILE] [LDRIVE_IMAGE_PATH] [LDRIVE_VRX_PATH] ");
				System.exit(1);
			}
		}
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

		//Create thread framework
		ExecutorService executor = Executors.newFixedThreadPool(50);

		
		// Read L drive image folder
		for (String brandfolders : LDRIVE_IMAGES_BRANDS) {
			String path = LDRIVE_IMAGE_PATH + brandfolders;
			ReadThread rt = new MainThread().new ReadThread(path,brandfolders,LocType.IMAGES);
			executor.execute(rt);
		}

		// Read L drive VRX folder
		for (String brandfolders : LDRIVE_VRX_BRANDS) {
			String path = LDRIVE_VRX_PATH + brandfolders;
			ReadThread rt = new MainThread().new ReadThread(path,brandfolders,LocType.VRX);
			executor.execute(rt);
		}
		
		executor.shutdown();

		while (!executor.isTerminated()) {}

		// Generate the output
		System.out.println("COMPLETE FETCH SIZE:"+filesetcompleteImages.size());

		StringBuffer sb = new StringBuffer();
		sb.append("propertyId, brandId, inLDVRX, inLDImages, inPIMT, createdDate, modifiedDate, shootDate, filename, filePath").append(System.getProperty("line.separator"));
		for (FileMeta fileMetaOut : filesetcompleteImages) {
			for (FileMeta fileMetaInp : filesetinput) {
				//System.out.println(fileMetaInp.getBrandId() + " - " + fileMetaOut.getBrandId());
				//System.out.println(fileMetaInp.getPropertyId() + " - " + fileMetaOut.getPropertyId());
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
		//System.out.println("------>"+path);
		Matcher m = Pattern.compile("\\\\([0-9]{1,5}+)").matcher(path);
		if (m.find()) {
			return padzero((m.group()).substring(1));
		}
		return "00000";
	}
	
	class ReadThread implements Runnable {
		
		private String path;
		private String bid;
		private LocType type;
		ReadThread(String path, String bid, LocType type) {
			this.path = path;
			this.bid = bid;
			this.type = type;
		}
		public void run() {
			System.out.println(this.type + "[" + this.bid + "]" + " is running...");
			List<FileMeta> ilist = new FileUtil(this.path).findFiles(FileType.IMAGE);
			for (FileMeta fileMeta : ilist) {
				fileMeta.setBrandId(propNamevsId.get(this.bid));
				fileMeta.setPropertyId(getProp(fileMeta.getFilePath()));
				switch(this.type) {
					case VRX:
						fileMeta.setInLDVRX("TRUE");
						fileMeta.setInLDImages("FALSE");
						break;
					case IMAGES:
						fileMeta.setInLDVRX("FALSE");
						fileMeta.setInLDImages("TRUE");
						break;
					default:
						fileMeta.setInLDVRX("FALSE");
						fileMeta.setInLDImages("FALSE");
				}
				
				//System.out.println(fileMeta.getPropertyId());
			}
			filesetcompleteImages.addAll(ilist);
			System.out.println(this.type + "[" + this.bid + "]" + " is complete!!");
		}
		
	}

}
