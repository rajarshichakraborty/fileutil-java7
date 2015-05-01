package com.whg.vrxcompare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompareSets {

	private static String COMPARE_FILE_AEM = "C:/Rajarshi/MyLab/VRXDAMCOMPARE/comparetest/aemlist.txt";
	private static String COMPARE_FILE_LD = "C:/Rajarshi/MyLab/VRXDAMCOMPARE/comparetest/output.csv";
	private static String OUTPUT_FILE = "C:/Rajarshi/MyLab/VRXDAMCOMPARE/comparetest/finaloutput.csv";

	public static void main(String[] args) {

		// Fetch the input brand and property ids
		List<String> aeminputlist = new FileUtil(COMPARE_FILE_AEM).readFile();

		// Fetch the input brand and property ids
		List<String> ldinputlist = new FileUtil(COMPARE_FILE_LD).readFile();

		// aem man
		List<FileMeta> aemlist = new ArrayList<FileMeta>();
		for (String aeminline : aeminputlist) {
			String[] aemlineattrs = aeminline.split(",");
			FileMeta fileMeta = new FileMeta();
			fileMeta.setPropertyId(padzero(aemlineattrs[0].trim()));
			fileMeta.setBrandId(aemlineattrs[1].trim());
			fileMeta.setShootDate(aemlineattrs[2].trim());
			aemlist.add(fileMeta);
		}

		// ld man
		List<FileMeta> ldlist = new ArrayList<FileMeta>();
		for (String ldinline : ldinputlist) {
			String[] ldinlineattrs = ldinline.split(",");
			FileMeta fileMeta = new FileMeta();
			fileMeta.setPropertyId(padzero(ldinlineattrs[0].trim()));
			fileMeta.setBrandId(ldinlineattrs[1].trim());
			fileMeta.setInLDVRX(ldinlineattrs[2].trim());
			fileMeta.setInLDImages(ldinlineattrs[3].trim());
			fileMeta.setInPIMT(ldinlineattrs[4].trim());
			fileMeta.setCreatedDate(ldinlineattrs[5].trim());
			fileMeta.setModifiedDate(ldinlineattrs[6].trim());
			fileMeta.setShootDate(ldinlineattrs[7].trim());
			fileMeta.setFilename(ldinlineattrs[8].trim());
			fileMeta.setFilePath(ldinlineattrs[9].trim());
			ldlist.add(fileMeta);
		}
		
		//compare and list final
		StringBuffer sb = new StringBuffer();
		sb.append("propertyId, brandId, inLDVRX, inLDImages, inPIMT, createdDate, modifiedDate, shootDate, isVRXLatest, filename, filePath").append(System.getProperty("line.separator"));
		for (FileMeta aemfileMeta : aemlist) {
			//System.out.println("aemfileMeta:" + aemfileMeta.getPropertyId());
			for (FileMeta ldfileMeta : ldlist) {
				//System.out.println("ldfileMeta:" + ldfileMeta.getPropertyId());
				if(aemfileMeta.getBrandId().equalsIgnoreCase(ldfileMeta.getBrandId())
						&& aemfileMeta.getPropertyId().equalsIgnoreCase(ldfileMeta.getPropertyId())) {
					System.out.println("Matched!!!!" + aemfileMeta.getPropertyId());
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		        	Date aemdate=null,lddate=null;
					try {
						aemdate = sdf.parse(aemfileMeta.getShootDate());
						lddate = sdf.parse(ldfileMeta.getModifiedDate());
					} catch (ParseException e) {
						e.printStackTrace();
					}
		 
		        	if(aemdate.after(lddate)){
		        		ldfileMeta.setIsVRXLatest("TRUE");
		        	}
		 
		        	if(aemdate.before(lddate)){
		        		ldfileMeta.setIsVRXLatest("FALSE");
		        	}
		 
		        	if(aemdate.equals(lddate)){
		        		ldfileMeta.setIsVRXLatest("TRUE");
		        	}
					ldfileMeta.setShootDate(aemfileMeta.getShootDate());
					sb.append(ldfileMeta.toString()).append(System.getProperty("line.separator"));
				}
			}
		}
		
		
		new FileUtil(OUTPUT_FILE).writeFile(sb.toString());
	}
	
	private static String padzero(String id) {
		return ("00000" + id).substring(id.length());
	}
}
