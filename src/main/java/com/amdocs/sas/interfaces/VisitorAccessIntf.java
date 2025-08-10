package com.amdocs.sas.interfaces;

import com.amdocs.sas.bean.VisitorAccess;
import com.amdocs.sas.exceptions.AccessAlreadyGrantedException;
public interface VisitorAccessIntf {
    void grantAccess(VisitorAccess access) throws AccessAlreadyGrantedException;
    void viewAccess(int visitorId);
}