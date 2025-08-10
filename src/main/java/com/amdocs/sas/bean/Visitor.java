package com.amdocs.sas.bean;

public class Visitor {
    private int visitorId;
    private String name;
    private String contact;
    private String email;
    private String password; 

    public Visitor() {
        super();
    }

    public Visitor(int visitorId, String name, String contact, String email, String password) {
        this.visitorId = visitorId;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.password = password;
    }

    public int getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(int visitorId) {
        this.visitorId = visitorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {                 
        return password;
    }

    public void setPassword(String password) {   
        this.password = password;
    }
}
