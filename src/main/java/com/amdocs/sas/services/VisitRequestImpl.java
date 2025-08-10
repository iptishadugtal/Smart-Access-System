package com.amdocs.sas.services;

import java.sql.*;

import com.amdocs.sas.dao.JDBC;
import com.amdocs.sas.bean.VisitRequest;
import com.amdocs.sas.interfaces.VisitRequestIntf;
import com.amdocs.sas.util.DateUtil;
import com.amdocs.sas.util.EmailUtil;
import com.amdocs.sas.util.QRCodeGenerator;

public class VisitRequestImpl implements VisitRequestIntf {

	public void createVisitRequest(VisitRequest req) {
		try {
			Connection con = JDBC.getConnection();
			PreparedStatement ps = con.prepareStatement(
				"INSERT INTO visit_request (visitor_id, visit_date, purpose ,status) VALUES (?,?, ?, ?)");
			ps.setInt(1, req.getVisitorId());
			ps.setString(2, req.getVisitDate());
			ps.setString(3, req.getPurpose());
			ps.setString(4, req.getStatus());
			ps.executeUpdate();
			System.out.println("Visit Request Created.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendQRCodeByEmail(int requestId) {
	    try {
	        Connection con = JDBC.getConnection();

	        PreparedStatement ps = con.prepareStatement(
	            "SELECT v.email, v.name, vr.qr_code_path " +
	            "FROM visitor v JOIN visit_request vr ON v.visitor_id = vr.visitor_id " +
	            "WHERE vr.request_id = ?");
	        ps.setInt(1, requestId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            String email = rs.getString("email");
	            String name = rs.getString("name");
	            String qrPath = rs.getString("qr_code_path");

	            if (qrPath != null && !qrPath.isEmpty()) {
	                EmailUtil.sendEmailWithQR(
	                    email,
	                    "Your Visit QR Code",
	                    "Dear " + name + ",\n\nYour visit has been approved. Please find your QR code attached.\n\nSmart Access System",
	                    qrPath
	                );
	            } else {
	                System.out.println("❌ QR path not available in DB.");
	            }
	        } else {
	            System.out.println("❌ No visitor found for request ID: " + requestId);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void updateRequestStatus(int requestId, String status) {
	    try {
	        Connection con = JDBC.getConnection();

	        // Step 1: Update visit request status
	        PreparedStatement ps = con.prepareStatement(
	            "UPDATE visit_request SET status = ? WHERE request_id = ?");
	        ps.setString(1, status);
	        ps.setInt(2, requestId);
	        ps.executeUpdate();
	        System.out.println("✅ Visit Request Status Updated.");

	        // Step 2: If status is APPROVED, generate QR
	        if (status.equalsIgnoreCase("APPROVED")) {
	            PreparedStatement infoStmt = con.prepareStatement(
	                "SELECT v.visitor_id, v.name, v.email, vr.visit_date " +
	                "FROM visitor v JOIN visit_request vr ON v.visitor_id = vr.visitor_id " +
	                "WHERE vr.request_id = ?");
	            infoStmt.setInt(1, requestId);
	            ResultSet rs = infoStmt.executeQuery();

	            if (rs.next()) {
	                int visitorId = rs.getInt("visitor_id");
	                String visitorName = rs.getString("name");
	                String visitorEmail = rs.getString("email");
	                String visitDate = rs.getString("visit_date");

	                // Step 3: Generate QR data and file name
	                String qrData = "Visitor ID: " + visitorId +
	                        "\nName: " + visitorName +
	                        "\nRequest ID: " + requestId +
	                        "\nVisit Date: " + visitDate;

	                String fileName = "QR_" + requestId + ".png";
	                String qrPath = QRCodeGenerator.generateQRCode(qrData, 300, 300, fileName);

	                if (qrPath != null) {
	                    System.out.println("📌 QR Code generated at: " + qrPath);

	                    // Step 4 (Optional): Save QR path to DB for future reference
	                    PreparedStatement qrStmt = con.prepareStatement(
	                        "UPDATE visit_request SET qr_code_path = ? WHERE request_id = ?");
	                    qrStmt.setString(1, qrPath);
	                    qrStmt.setInt(2, requestId);
	                    qrStmt.executeUpdate();
	                    System.out.println("🗃️ QR Code path saved in database.");

	                    sendQRCodeByEmail(requestId);
	                } else {
	                    System.out.println("❌ QR Code generation failed.");
	                }
	            } else {
	                System.out.println("❌ Visitor info not found for request ID: " + requestId);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public void viewVisitorRequests(int visitorId) {
		try {
			Connection con = JDBC.getConnection();
			PreparedStatement ps = con.prepareStatement(
				"SELECT request_id, visit_date, purpose, status FROM visit_request WHERE visitor_id = ?");
			ps.setInt(1, visitorId);
			ResultSet rs = ps.executeQuery();

			System.out.println("\n--- Your Visit Requests ---");
			String format = "| %-11s | %-12s | %-25s | %-10s |%n";
			System.out.format("+-------------+--------------+---------------------------+------------+%n");
			System.out.format("| Request ID  | Visit Date   | Purpose                   | Status     |%n");
			System.out.format("+-------------+--------------+---------------------------+------------+%n");

			boolean hasResults = false;
			while (rs.next()) {
				hasResults = true;
				System.out.format(format,
					rs.getInt("request_id"),
					DateUtil.convertToDisplayFormat(rs.getDate("visit_date")),
					rs.getString("purpose"),
					rs.getString("status"));
			}
			if (!hasResults) {
				System.out.println("| You have no visit requests yet.                                         |");
			} else {
				System.out.format("+-------------+--------------+---------------------------+------------+%n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteRequest(int requestId) {
		try {
			Connection con = JDBC.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM visit_request WHERE request_id = ?");
			ps.setInt(1, requestId);
			ps.executeUpdate();
			System.out.println("Visit Request Deleted.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void grantAccess(int visitorId, String locationName) {
		try {
			Connection con = JDBC.getConnection();
			PreparedStatement checkStmt = con.prepareStatement(
				"SELECT access_point_id FROM access_point WHERE location_name = ?");
			checkStmt.setString(1, locationName);
			ResultSet rs = checkStmt.executeQuery();
			int accessPointId;
			if (rs.next()) {
				accessPointId = rs.getInt("access_point_id");
			} else {
				PreparedStatement insertStmt = con.prepareStatement(
					"INSERT INTO access_point (location_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
				insertStmt.setString(1, locationName);
				insertStmt.executeUpdate();
				ResultSet keys = insertStmt.getGeneratedKeys();
				keys.next();
				accessPointId = keys.getInt(1);
			}
			PreparedStatement ps = con.prepareStatement(
				"INSERT INTO visitor_access (visitor_id, access_point_id) VALUES (?, ?)");
			ps.setInt(1, visitorId);
			ps.setInt(2, accessPointId);
			ps.executeUpdate();
			System.out.println(" Access granted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void viewPendingRequestsWithVisitorInfo() {
		try {
			Connection con = JDBC.getConnection();
			String query = "SELECT vr.request_id, v.visitor_id, v.name, vr.visit_date, vr.status " +
			               "FROM visit_request vr JOIN visitor v ON vr.visitor_id = v.visitor_id " +
			               "WHERE vr.status = 'PENDING'";
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			System.out.println("\n--- Pending Visit Requests ---");
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
					DateUtil.convertToDisplayFormat(rs.getDate("visit_date")),
					rs.getString("status"));
			}
			if (!found) {
				System.out.println("| No pending visit requests found.                                               |");
			} else {
				System.out.format("+-------------+------------+------------------------+--------------+------------+%n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
