package com.amdocs.sas.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.amdocs.sas.bean.Visitor;
import com.amdocs.sas.interfaces.VisitorIntf;
import com.amdocs.sas.dao.JDBC;

public class VisitorImpl implements VisitorIntf {
    @Override
    public void registerVisitor(Visitor visitor) {
        try {
            Connection con = JDBC.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO visitor (name, contact, email, password) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, visitor.getName());
            ps.setString(2, visitor.getContact());
            ps.setString(3, visitor.getEmail());
            ps.setString(4, visitor.getPassword());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                visitor.setVisitorId(id);
                System.out.println(" Visitor registered successfully! Your Visitor ID is: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Visitor loginVisitor(int id, String password) {
        Visitor visitor = null;
        try {
            Connection con = JDBC.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM visitor WHERE visitor_id = ? AND password = ?");
            ps.setInt(1, id);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                visitor = new Visitor();
                visitor.setVisitorId(rs.getInt("visitor_id"));
                visitor.setName(rs.getString("name"));
                visitor.setContact(rs.getString("contact"));
                visitor.setEmail(rs.getString("email"));
                visitor.setPassword(rs.getString("password"));
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed: Visitor not found or password incorrect.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return visitor;
    }
}
