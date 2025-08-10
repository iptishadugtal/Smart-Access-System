package com.amdocs.sas.interfaces;

import com.amdocs.sas.bean.Visitor;

public interface VisitorIntf {
    void registerVisitor(Visitor visitor);
    Visitor loginVisitor(int id, String email);
}
