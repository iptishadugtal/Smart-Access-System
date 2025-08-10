package com.amdocs.sas.bean;
public class VisitRequest {
    private int requestId;
    private int visitorId;
    private String visitDateAndTime;
    private String visitDate;
    private String status;
    private String purpose;

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }
    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVisitDateAndTime() {
        return visitDateAndTime;
    }

    public void setVisitDateAndTime(String visitDateAndTime) {
        this.visitDateAndTime = visitDateAndTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}