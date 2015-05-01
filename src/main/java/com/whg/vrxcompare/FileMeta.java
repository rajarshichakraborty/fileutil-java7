package com.whg.vrxcompare;

public class FileMeta {
	private String propertyId;
	private String brandId;
	private String inLDVRX;
	private String inLDImages;
	private String inPIMT;
	private String createdDate;
	private String modifiedDate;
	private String shootDate;
	private String filename;
	private String filePath;
	private String isVRXLatest;

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getShootDate() {
		return shootDate;
	}

	public void setShootDate(String shootDate) {
		this.shootDate = shootDate;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getInLDVRX() {
		return inLDVRX;
	}

	public void setInLDVRX(String inLDVRX) {
		this.inLDVRX = inLDVRX;
	}

	public String getInLDImages() {
		return inLDImages;
	}

	public void setInLDImages(String inLDImages) {
		this.inLDImages = inLDImages;
	}

	public String getInPIMT() {
		return inPIMT;
	}

	public void setInPIMT(String inPIMT) {
		this.inPIMT = inPIMT;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(propertyId);
		builder.append(", ");
		builder.append(brandId);
		builder.append(", ");
		builder.append(inLDVRX);
		builder.append(", ");
		builder.append(inLDImages);
		builder.append(", ");
		builder.append(inPIMT);
		builder.append(", ");
		builder.append(createdDate);
		builder.append(", ");
		builder.append(modifiedDate);
		builder.append(", ");
		builder.append(shootDate);
		builder.append(", ");
		builder.append(isVRXLatest);
		builder.append(", ");
		builder.append(filename);
		builder.append(", ");
		builder.append(filePath);
		
		return builder.toString();
	}

	public String getIsVRXLatest() {
		return isVRXLatest;
	}

	public void setIsVRXLatest(String isVRXLatest) {
		this.isVRXLatest = isVRXLatest;
	}
}
