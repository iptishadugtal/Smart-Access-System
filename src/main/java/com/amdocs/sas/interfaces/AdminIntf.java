package com.amdocs.sas.interfaces;

public interface AdminIntf {
    boolean loginAdmin(String username, String password);
    void viewAllVisitRequests();
    void viewAllGrantedAccess();
    boolean deleteVisitor(int visitorId);
    boolean deleteReceptionist(int receptionistId);
    boolean addReceptionist(String name, String username, String password);
    void showVisitorStatistics();
}