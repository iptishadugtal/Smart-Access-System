package com.amdocs.sas.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import com.amdocs.sas.dao.JDBC;
import com.amdocs.sas.interfaces.AdminIntf;
import com.amdocs.sas.util.DateUtil;

public class AdminImpl implements AdminIntf {
 
    public boolean loginAdmin(String username, String password) {
        try {
            Connection connection = JDBC.getConnection();
            PreparedStatement prepareStatement = connection.prepareStatement("SELECT * FROM admin WHERE username=? AND password=?");
            prepareStatement.setString(1, username);
            prepareStatement.setString(2, password);
            ResultSet resultSet = prepareStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
   
    public void viewAllVisitRequests() {
        try {
            Connection connection = JDBC.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM visit_request");
            String format = "| %-10s | %-10s | %-12s | %-20s | %-10s |%n";
            System.out.format("+------------+------------+--------------+----------------------+------------+%n");
            System.out.format("| Request ID | Visitor ID | Visit Date   | Purpose              | Status     |%n");
            System.out.format("+------------+------------+--------------+----------------------+------------+%n");
            boolean hasResults = false;
            while (resultSet.next()) {
            	hasResults = true;
            	System.out.format(format,
            	        resultSet.getInt("request_id"),
            	        resultSet.getInt("visitor_id"),
            	        DateUtil.convertToDisplayFormat(resultSet.getDate("visit_date")),
            	        resultSet.getString("purpose"),
            	        resultSet.getString("status"));

            }
            if (!hasResults) {
                System.out.println("| No visit requests found.                                                        |");
            } else {
                System.out.format("+------------+------------+--------------+----------------------+------------+%n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewAllGrantedAccess() {
        try {
            Connection connection = JDBC.getConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT va.access_id, v.visitor_id, v.name AS visitor_name, " +
                           "ap.location_name AS access_area, va.granted_on " +
                           "FROM visitor_access va " +
                           "JOIN visitor v ON va.visitor_id = v.visitor_id " +
                           "JOIN access_point ap ON va.access_point_id = ap.access_point_id " +
                           "ORDER BY va.access_id";
            ResultSet resultSet = statement.executeQuery(query);
            String format = "| %-9s | %-10s | %-20s | %-22s | %-19s |%n";
            System.out.format("+-----------+------------+----------------------+------------------------+---------------------+%n");
            System.out.format("| Access ID | Visitor ID | Visitor Name         | Access Area            | Granted On          |%n");
            System.out.format("+-----------+------------+----------------------+------------------------+---------------------+%n");
            boolean hasResults = false;
            while (resultSet.next()) {
                hasResults = true;
                System.out.format(format,
                    resultSet.getInt("access_id"),
                    resultSet.getInt("visitor_id"),
                    resultSet.getString("visitor_name"),
                    resultSet.getString("access_area"),
                    resultSet.getTimestamp("granted_on"));
            }
            if (!hasResults) {
                System.out.println("| No granted access records found.                                                    |");
            } else {
                System.out.format("+-----------+------------+----------------------+------------------------+---------------------+%n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteVisitor(int visitorId) {
        try (Connection connection = JDBC.getConnection()) {
            PreparedStatement prepareStatement = connection.prepareStatement(
                "DELETE FROM visit_request WHERE visitor_id = ?");
            prepareStatement.setInt(1, visitorId);
            prepareStatement.executeUpdate();
            prepareStatement = connection.prepareStatement(
                "DELETE FROM visitor_access WHERE visitor_id = ?");
            prepareStatement.setInt(1, visitorId);
            prepareStatement.executeUpdate();
            prepareStatement = connection.prepareStatement(
                "DELETE FROM visitor WHERE visitor_id = ?");
            prepareStatement.setInt(1, visitorId);
            int result = prepareStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Visitor deleted successfully.");
                return true;
            } else {
                System.out.println("Visitor not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReceptionist(int receptionistId) {
        try (Connection connection = JDBC.getConnection()) {
            PreparedStatement prepareStatement = connection.prepareStatement("SELECT receptionist_id, name, username FROM receptionist");
            ResultSet resultSet = prepareStatement.executeQuery();
            System.out.println("\n--- Available Receptionists ---");
            boolean found = false;
            while (resultSet.next()) {
                found = true;
                int id = resultSet.getInt("receptionist_id");
                String name = resultSet.getString("name");
                String username = resultSet.getString("username");
                System.out.println("ID: " + id + ", Name: " + name + ", Username: " + username);
            }
            if (!found) {
                System.out.println("No receptionists found.");
                return false;
            }
            prepareStatement = connection.prepareStatement(
                    "DELETE FROM receptionist WHERE receptionist_id = ?");
            prepareStatement.setInt(1, receptionistId);
            int result = prepareStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Receptionist deleted successfully.");
                return true;
            } else {
                System.out.println("Receptionist not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addReceptionist(String name, String username, String password) {
        try (Connection connection = JDBC.getConnection()) {
            PreparedStatement prepareStatement = connection.prepareStatement(
                "INSERT INTO receptionist (name, username, password) VALUES (?, ?, ?)");
            prepareStatement.setString(1, name);
            prepareStatement.setString(2, username);
            prepareStatement.setString(3, password);
            int result = prepareStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Receptionist added successfully.");
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already exists. Please try a different one.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void showVisitorStatistics() {
        try (Connection connection = JDBC.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM visitor");
            resultSet.next();
            int total = resultSet.getInt(1);
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM visit_request WHERE status = 'PENDING'");
            resultSet.next();
            int pendingCount = resultSet.getInt(1);
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM visit_request WHERE status = 'APPROVED'");
            resultSet.next();
            int approvedCount = resultSet.getInt(1);
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM visit_request WHERE status = 'REJECTED'");
            resultSet.next();
            int rejectedCount = resultSet.getInt(1);
            System.out.println("\n--- Visitor Statistics ---");
            String format = "| %-20s | %-10s |%n";
            System.out.format("+----------------------+------------+%n");
            System.out.format("| Metric               | Count      |%n");
            System.out.format("+----------------------+------------+%n");
            System.out.format(format, "Total Visitors", total);
            System.out.format(format, "Pending Requests", pendingCount);
            System.out.format(format, "Approved Requests", approvedCount);
            System.out.format(format, "Rejected Requests", rejectedCount);
            System.out.format("+----------------------+------------+%n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
