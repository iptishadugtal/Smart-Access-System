package com.amdocs.sas.services;

import java.sql.*;
import com.amdocs.sas.dao.JDBC;
import com.amdocs.sas.interfaces.ReceptionistIntf;
import com.amdocs.sas.util.DateUtil;

public class ReceptionistImpl implements ReceptionistIntf {

    public boolean loginReceptionist(String username, String password) {
        try {
            Connection con = JDBC.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM receptionist WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getReceptionistId(String username, String password) {
        int id = -1;
        try {
            Connection con = JDBC.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT receptionist_id FROM receptionist WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("receptionist_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public void viewAllVisitRequests() {
        try {
            Connection con = JDBC.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT vr.request_id, vr.visit_date, vr.status, v.visitor_id, v.name " +
                "FROM visit_request vr JOIN visitor v ON vr.visitor_id = v.visitor_id");
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
                    DateUtil.convertToDisplayFormat(rs.getDate("visit_date")),
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

