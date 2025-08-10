package com.amdocs.sas.services;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.amdocs.sas.dao.JDBC;
import com.amdocs.sas.bean.AccessPoint;
import com.amdocs.sas.interfaces.AccessPointIntf;

public class AccessPointImpl implements AccessPointIntf {

    @Override
    public void addAccessPoint(AccessPoint point) {
        try {
            Connection con = JDBC.getConnection();
            // Insert into access_point
            String insertAccessPoint = "INSERT INTO access_point (location_name) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(insertAccessPoint, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, point.getLocationName());
            ps.executeUpdate();
            // Step 2: Get generated access_point_id
            ResultSet rs = ps.getGeneratedKeys();
            int accessPointId = -1;
            if (rs.next()) {
                accessPointId = rs.getInt(1);
            }
            //Insert into visit_request using the new access_point_id
            if (accessPointId != -1) {
                String insertVisitRequest = "INSERT INTO visit_request (visitor_id, visit_date, status, access_point_id) VALUES (?, ?, ?, ?)";
                PreparedStatement ps2 = con.prepareStatement(insertVisitRequest);
                ps2.setInt(1, 1); 
                ps2.setDate(2, java.sql.Date.valueOf(LocalDate.now())); 
             // Default status
                ps2.setString(3, "PENDING"); 
                ps2.setInt(4, accessPointId);
                ps2.executeUpdate();
            }
            System.out.println("Access Point and Visit Request added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<AccessPoint> getAllAccessPoints() {
        List<AccessPoint> list = new ArrayList<>();
        try {
            Connection con = JDBC.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM access_point");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AccessPoint ap = new AccessPoint();
                ap.setAccessPointId(rs.getInt("access_point_id"));
                ap.setLocationName(rs.getString("location_name"));
                list.add(ap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
