package com.whg.vrxcompare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CompareImages {

	private static String PIMT_File = "C:/Dev/Documents/Content/VRX/Test_Workspace/PIMT.csv";

	private static String OUTPUT_FILE = "C:/Dev/Documents/Content/VRX/Test_Workspace/OUTPUT2.csv";

	private static String L_Drive_Image_Path = "C:/Rajarshi/MyLab/VRXDAMCOMPARE/LDrive/Images";
	private static String L_Drive_VRX_Path = "C:/LDrive/VRX/";

	private static int notindam = 0;

	private static ArrayList<Data2> fullData = new ArrayList<Data2>();
	private static HashMap<String, Data2> pimtProperties = new HashMap<String, Data2>();

	private static String[] LDrive_Images_Brands = new String[] { "Days Inn",
			"Baymont", "Dream-Night", "Hawthorn", "Howard Johnson", "Knights",
			"Microtel", "Ramada", "Super 8", "Travelodge", "TRYP", "Wingate",
			"Wyndham Garden", "Wyndham Grand", "Wyndham Hotels and Resorts" };
	private static String[] LDrive_VRX_Brands = new String[] { "Baymont",
			"Days Inn", "Hawthorn", "Howard Johnson", "Knights Inn",
			"Microtel", "Ramada", "Super 8", "Travelodge", "Wyndham"
	};
	private static HashMap<String, String> propNamevsId = new HashMap<String, String>();

	static {
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

	public static void main(String[] args) throws Exception {
		readPIMTPropertyIds();
		checkLDriveImages();
		checkLDriveVRX();
		createOutput();
	}

	private static void readPIMTPropertyIds() throws Exception {
		Scanner scanner = new Scanner((new File(PIMT_File)));
		int count = 0;
		while (scanner.hasNext()) {
			String temp = scanner.next();
			String brandId = temp.substring(0, temp.indexOf(","));
			String propId = appendZero(temp.substring(temp.indexOf(",") + 1));
			// System.out.print(brandId+"=="+ propId);
			Data2 dt = new Data2(brandId, propId);
			dt.Pimt_Flag = true;
			pimtProperties.put(propId, dt);
			fullData.add(dt);
			count++;
		}
		scanner.close();
		System.out.println("Total Properties in PIMT=" + count);
	}

	private static void checkLDriveImages() throws Exception {
		int count = 0;
		int count1 = 0, tp = 0, count3 = 0;
		for (count = 0; count < LDrive_Images_Brands.length; count++) {
			File brandFolder = new File(L_Drive_Image_Path
					+ LDrive_Images_Brands[count]);
			if (brandFolder.exists()) {
				String[] propIds = brandFolder.list(new LDriveImageFilter2());
				for (count1 = 0; count1 < propIds.length; count1++) {
					String propId = appendZero(propIds[count1]);
					Data2 dt = pimtProperties.get(propId);
					if (dt != null) {
						dt.addImage("test2.jpeg", "01/22/2014", "01/22/2014",
								"01/22/2014", true);
					}
					tp++;
				}
			}
		}
		notindam += count3;
		System.out.println("Total Properties from L-Drive/Images=" + (tp));
		System.out
				.println("Total Properties from L-Drive/Images Not found in PIMT="
						+ (count3));

	}

	private static void checkLDriveVRX() throws Exception {

		int count = 0;
		int count1 = 0, tp = 0, count3 = 0;
		for (count = 0; count < LDrive_VRX_Brands.length; count++) {
			File brandFolder = new File(L_Drive_VRX_Path
					+ LDrive_VRX_Brands[count]);
			if (brandFolder.exists()) {
				String[] propIds = brandFolder.list(new LDriveImageFilter2());
				for (count1 = 0; count1 < propIds.length; count1++) {
					tp++;
					String propId = appendZero(propIds[count1]);
					Data2 dt = pimtProperties.get(propId);
					if (dt != null) {
						dt.addImage("test2.jpeg", "01/22/2014", "01/22/2014",
								"01/22/2014", false);

					}
				}
			}
		}
		notindam += count3;
		System.out.println("Total Properties from L-Drive/VRX=" + (tp));
		System.out
				.println("Total Properties from L-Drive/VRX Not found in PIMT="
						+ (count3));
	}

	private static void createOutput() throws Exception {

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				OUTPUT_FILE)));
		System.out.println("Total Properties in Output=" + fullData.size());
		bw.write("BrandId, PropertyId, Exist in Pimt, Exist in L-Drive/Images, Exist in L-Drive/VRX, ImagePath, createdDate, ModifiedDate, ShootDate\n");
		int count = 0, noImages = 0;
		while (count < fullData.size()) {
			bw.write(fullData.get(count).toString());
			count++;
		}
		bw.close();
		System.out
				.println("Total Properties (L-Drive-Images/VRX, VRX Deliverable) Not found in PIMT="
						+ (notindam));
		System.out
				.println("Total PIMT Properties which will not have any images="
						+ (noImages));
	}

	private static String appendZero(String p) {
		if (p.length() >= 5) {
			return p;
		}
		return appendZero(p = "0" + p);
	}

}

class LDriveImageFilter2 implements FilenameFilter {
	public boolean accept(File directory, String fileName) {
		return directory.isDirectory() && fileName.matches("[0-9]{1,5}");
	}
}

class Data2 {
	public String brandId = "";
	public String propertyId = "";
	public String Status_In_Shopper = "Not In Shopper";
	public boolean Pimt_Flag = false;

	public boolean VRX_Flag = false;
	public String VRX_ImageCount = "";
	public boolean bwsStatus = false;
	ArrayList images = new ArrayList();

	public Data2(String brandId, String propertyId) {
		this.propertyId = propertyId;
		this.brandId = brandId;

	}

	public String toString() {
		String test = brandId + "," + propertyId + "," + Pimt_Flag + ",";
		for (int i = 0; i < images.size(); i++) {
			test += images.get(i).toString();
		}
		if (images.size() == 0) {
			test += "\n";
		}

		return test;
	}

	public void addImage(String path, String createdDate, String modifiedDate,
			String dateTaken, boolean fromLDriveImagesFolder) {
		Image im = new Image();
		im.path = path;
		im.createdDate = createdDate;
		if (fromLDriveImagesFolder)
			im.LDrive_Image_Flag = true;
		else
			im.LDrive_VRX_Flag = true;
		im.shootDate = dateTaken;
		im.ModifiedDate = modifiedDate;

		images.add(im);
	}

}

class Image {
	String path = "XXXX.jpeg";
	public String createdDate = "";
	public String shootDate = "";
	public String ModifiedDate = "";
	public boolean LDrive_Image_Flag = false;
	public boolean LDrive_VRX_Flag = false;

	public String toString() {
		String test = LDrive_Image_Flag + "," + LDrive_VRX_Flag + "," + path
				+ "," + createdDate + "," + ModifiedDate + "," + shootDate;

		return test + "\n";
	}

}
