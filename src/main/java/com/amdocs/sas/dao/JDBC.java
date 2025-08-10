package com.amdocs.sas.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBC {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/db2",
                "root",                           
                "root"                     // root
            );
        } catch (Exception e) {
            System.out.println(" Failed to connect to DB: " + e.getMessage());
        }
        return con;
    }
}
