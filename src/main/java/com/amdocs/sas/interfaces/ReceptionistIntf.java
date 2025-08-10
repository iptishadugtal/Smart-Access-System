package com.amdocs.sas.interfaces;

public interface ReceptionistIntf {
    boolean loginReceptionist(String username, String password);
    int getReceptionistId(String username, String password);
    void viewAllVisitRequests();
}
