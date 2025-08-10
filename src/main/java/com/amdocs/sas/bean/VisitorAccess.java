package com.amdocs.sas.bean;

public class VisitorAccess {
	private int visitorId;
	private String accessArea;
	private int grantedBy;
	private int requestId;

	public int getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(int visitorId) {
		this.visitorId = visitorId;
	}

	public String getAccessArea() {
		return accessArea;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public void setAccessArea(String accessArea) {
		this.accessArea = accessArea;
	}

	public int getGrantedBy() {
		return grantedBy;
	}

	public void setGrantedBy(int grantedBy) {
		this.grantedBy = grantedBy;
	}
}
