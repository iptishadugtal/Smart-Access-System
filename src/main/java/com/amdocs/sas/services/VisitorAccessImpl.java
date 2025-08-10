package com.amdocs.sas.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.amdocs.sas.bean.VisitorAccess;
import com.amdocs.sas.dao.JDBC;
import com.amdocs.sas.exceptions.AccessAlreadyGrantedException;
import com.amdocs.sas.interfaces.VisitorAccessIntf;

public class VisitorAccessImpl implements VisitorAccessIntf {
	@Override
	public void grantAccess(VisitorAccess access) throws AccessAlreadyGrantedException {
	    try {
	        Connection connection = JDBC.getConnection();
	        int accessPointId = -1;

	        // Step 1: Check if access area exists
	        PreparedStatement ps1 = connection.prepareStatement(
	            "SELECT access_point_id FROM access_point WHERE location_name = ?");
	        ps1.setString(1, access.getAccessArea());
	        ResultSet rs1 = ps1.executeQuery();

	        if (rs1.next()) {
	            accessPointId = rs1.getInt("access_point_id");
	        } else {
	            // Insert new access point
	            PreparedStatement insertAccessPoint = connection.prepareStatement(
	                "INSERT INTO access_point (location_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
	            insertAccessPoint.setString(1, access.getAccessArea());
	            insertAccessPoint.executeUpdate();
	            ResultSet generatedKeys = insertAccessPoint.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                accessPointId = generatedKeys.getInt(1);
	            }
	        }

	        // Step 2: Check if access already exists
	        PreparedStatement checkAccess = connection.prepareStatement(
	            "SELECT * FROM visitor_access WHERE visitor_id = ? AND access_point_id = ?");
	        checkAccess.setInt(1, access.getVisitorId());
	        checkAccess.setInt(2, accessPointId);
	        ResultSet rs2 = checkAccess.executeQuery();

	        if (rs2.next()) {
	            throw new AccessAlreadyGrantedException(
	                "Access is already granted to this visitor for the selected area.");
	        }

	        // Step 3: Insert access record
	        PreparedStatement insertAccess = connection.prepareStatement(
	            "INSERT INTO visitor_access (visitor_id, access_point_id, granted_by) VALUES (?, ?, ?)");
	        insertAccess.setInt(1, access.getVisitorId());
	        insertAccess.setInt(2, accessPointId);
	        insertAccess.setInt(3, access.getGrantedBy());

	        int rowsInserted = insertAccess.executeUpdate();
	        if (rowsInserted > 0) {
	            System.out.println("Access granted successfully.");

	            // ✅ Generate QR Code using your utility
	            String qrData = "Request ID: " + access.getRequestId() + "\nVisitor ID: " + access.getVisitorId();
	            String fileName = "QR_" + access.getRequestId() + ".png";
	            String path = com.amdocs.sas.util.QRCodeGenerator.generateQRCode(qrData, 250, 250, fileName);

	            System.out.println(" QR Code generated at: " + path);
	        }

	    } catch (AccessAlreadyGrantedException e) {
	        throw e; // pass it to caller
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}



    @Override
    public void viewAccess(int visitorId) {
        try {
            Connection con = JDBC.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT va.access_id, ap.location_name, va.granted_on " +
                "FROM visitor_access va JOIN access_point ap ON va.access_point_id = ap.access_point_id " +
                "WHERE va.visitor_id = ?");
            ps.setInt(1, visitorId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Visitor Access Records ---");
            String format = "| %-10s | %-25s | %-20s |%n";
            System.out.format("+------------+---------------------------+----------------------+%n");
            System.out.format("| Access ID  | Access Area               | Granted On           |%n");
            System.out.format("+------------+---------------------------+----------------------+%n");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.format(format,
                    rs.getInt("access_id"),
                    rs.getString("location_name"),
                    rs.getTimestamp("granted_on").toString());
            }

            if (!found) {
                System.out.println("| No access records found.                                             |");
            } else {
                System.out.format("+------------+---------------------------+----------------------+%n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewVisitorAccess(int visitorId) {
        viewAccess(visitorId);
    }

    public void viewAllVisitRequests() {
        try {
            Connection con = JDBC.getConnection();
            String query = "SELECT vr.request_id, v.visitor_id, v.name, vr.visit_date, vr.status " +
                           "FROM visit_request vr JOIN visitor v ON vr.visitor_id = v.visitor_id";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- All Visit Requests ---");
            String format = "| %-11s | %-10s | %-22s | %-12s | %-10s |%n";
            System.out.format("+-------------+------------+------------------------+--------------+------------+%n");
            System.out.format("| Request ID  | Visitor ID | Name                   | Visit Date   | Status     |%n");
            System.out.format("+-------------+------------+------------------------+--------------+------------+%n");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.format(format,
                    rs.getInt("request_id"),
                    rs.getInt("visitor_id"),
                    rs.getString("name"),
                    rs.getDate("visit_date"),
                    rs.getString("status"));
            }

            if (!found) {
                System.out.println("| No visit requests found.                                                         |");
            } else {
                System.out.format("+-------------+------------+------------------------+--------------+------------+%n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
