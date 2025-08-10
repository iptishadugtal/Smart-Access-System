package com.amdocs.sas.util;

import com.amdocs.sas.exceptions.*;

public class InputValidator {

    public static void validateName(String name) throws EmptyFieldException {
        if (name == null || name.trim().isEmpty()) {
            throw new EmptyFieldException("Name cannot be empty.");
        }
    }

    public static void validateContact(String contact) throws InvalidContactException {
        if (contact == null || !contact.matches("\\d{10}")) {
            throw new InvalidContactException("Contact must be exactly 10 digits.");
        }
    }

    public static void validateEmail(String email) throws InvalidEmailException {
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidEmailException("Email must be valid and contain '@' and domain.");
        }
    }

    public static void validatePassword(String password) throws WeakPasswordException {
        if (password == null ||
            !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            throw new WeakPasswordException("Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.");
        }
    }
}
