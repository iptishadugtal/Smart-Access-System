package com.amdocs.sas.interfaces;

import java.util.List;
import com.amdocs.sas.bean.AccessPoint;

public interface AccessPointIntf {
    void addAccessPoint(AccessPoint point);
    List<AccessPoint> getAllAccessPoints();
}
