package com.amdocs.sas.interfaces;
import com.amdocs.sas.bean.VisitRequest;
public interface VisitRequestIntf {
    void createVisitRequest(VisitRequest request);
    void updateRequestStatus(int requestId, String status);
    public void viewVisitorRequests(int visitorId);

}
