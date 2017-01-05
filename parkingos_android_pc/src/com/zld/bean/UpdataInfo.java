package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UpdataInfo implements Serializable {
	private String version;
	private String description;
	private String apkurl;
	private String versionBeta;
	private String descriptionBeta;
	private String apkurlBeta;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApkurl() {
		return apkurl;
	}

	public void setApkurl(String apkurl) {
		this.apkurl = apkurl;
	}

	public String getVersionBeta() {
		return versionBeta;
	}

	public void setVersionBeta(String versionBeta) {
		this.versionBeta = versionBeta;
	}

	public String getDescriptionBeta() {
		return descriptionBeta;
	}

	public void setDescriptionBeta(String descriptionBeta) {
		this.descriptionBeta = descriptionBeta;
	}

	public String getApkurlBeta() {
		return apkurlBeta;
	}

	public void setApkurlBeta(String apkurlBeta) {
		this.apkurlBeta = apkurlBeta;
	}

	@Override
	public String toString() {
		return "UpdataInfo [version=" + version + ", description="
				+ description + ", apkurl=" + apkurl + ", versionBeta="
				+ versionBeta + ", descriptionBeta=" + descriptionBeta
				+ ", apkurlBeta=" + apkurlBeta + "]";
	}
	
}
