package com.amdocs.sas.services;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import com.amdocs.sas.bean.Visitor;
import com.amdocs.sas.bean.AccessPoint;
import com.amdocs.sas.bean.VisitRequest;
import com.amdocs.sas.bean.VisitorAccess;
import com.amdocs.sas.dao.JDBC;
import com.amdocs.sas.exceptions.AccessAlreadyGrantedException;
import com.amdocs.sas.exceptions.EmptyFieldException;
import com.amdocs.sas.exceptions.InvalidContactException;
import com.amdocs.sas.exceptions.InvalidDateFormatException;
import com.amdocs.sas.exceptions.InvalidEmailException;
import com.amdocs.sas.exceptions.WeakPasswordException;
import com.amdocs.sas.util.DateUtil;
import com.amdocs.sas.util.InputValidator;
import com.amdocs.sas.util.SessionManager;

public class MainClass {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		VisitorImpl visitorImpl = new VisitorImpl();
		VisitRequestImpl visitRequestImpl = new VisitRequestImpl();
		VisitorAccessImpl visitorAccessImpl = new VisitorAccessImpl();
		ReceptionistImpl receptionistImpl = new ReceptionistImpl();
		AdminImpl adminImpl = new AdminImpl();
		while (true) {
			System.out.println("\n============== Smart Access System ===============");
			System.out.println("1. Register as Visitor");
			System.out.println("2. Login as Visitor");
			System.out.println("3. Login as Receptionist");
			System.out.println("4. Login as Admin");
			System.out.println("5. Exit");
			System.out.print("Enter your choice: ");
			int choice = scanner.nextInt();
			scanner.nextLine();
			switch (choice) {
			case 1:
				System.out.println("\n============ Visitor Registration =============");
				Visitor newVisitor = new Visitor();
				try {
				    System.out.print("Enter Name: ");
				    String nameInput = scanner.nextLine().trim();
				    InputValidator.validateName(nameInput);
				    newVisitor.setName(nameInput);

				    System.out.print("Enter Contact: ");
				    String contactInput = scanner.nextLine().trim();
				    InputValidator.validateContact(contactInput);
				    newVisitor.setContact(contactInput);

				    System.out.print("Enter Email: ");
				    String emailInput = scanner.nextLine().trim();
				    InputValidator.validateEmail(emailInput);
				    newVisitor.setEmail(emailInput);

				    System.out.print("Enter Password: ");
				    String passwordInput = scanner.nextLine().trim();
				    InputValidator.validatePassword(passwordInput);
				    newVisitor.setPassword(passwordInput);
				    
				    visitorImpl.registerVisitor(newVisitor);

				} catch (EmptyFieldException | InvalidContactException | InvalidEmailException | WeakPasswordException e) {
				    System.out.println("❌ " + e.getMessage());
				}
				break;
			case 2:
				System.out.print("Enter Visitor ID: ");
				int visitorId = scanner.nextInt();
				scanner.nextLine();
				System.out.print("Enter Password: ");
				String visitorPassword = scanner.nextLine();
				Visitor loggedVisitor = visitorImpl.loginVisitor(visitorId, visitorPassword);
				if (loggedVisitor != null) {
					SessionManager.getInstance().setSession("Visitor", loggedVisitor.getName(), visitorId);
					SessionManager.getInstance().displayCurrentUser();
					boolean visitorSession = true;
					while (visitorSession) {
						System.out.println("\n=========== Visitor Dashboard ================");
						System.out.println("1. Request a Visit");
						System.out.println("2. View My Visit Requests");
						System.out.println("3. View My Granted Access");
						System.out.println("4. Logout");
						System.out.print("Enter your choice: ");
						int visitorChoice = scanner.nextInt();
						scanner.nextLine();
						switch (visitorChoice) {
						case 1:
							VisitRequest request = new VisitRequest();
							request.setVisitorId(loggedVisitor.getVisitorId());
							System.out.print("Enter Visit Date (DD-MM-YYYY): ");
							String rawDate = scanner.nextLine().trim();
							String formattedDate = null;
							try {
								formattedDate = DateUtil.convertToDbFormat(rawDate);
							} catch (InvalidDateFormatException e) {
								System.out.println(e.getMessage());
								break;
							}
							request.setVisitDate(formattedDate);


							request.setStatus("PENDING");
							String purpose;
							do {
								System.out.print("Enter purpose for visit: ");
								purpose = scanner.nextLine().trim();
								if (purpose.isEmpty())
									System.out.println(" Purpose cannot be empty.");
							} while (purpose.isEmpty());
							request.setPurpose(purpose);
							visitRequestImpl.createVisitRequest(request);
							break;
						case 2:
							visitRequestImpl.viewVisitorRequests(loggedVisitor.getVisitorId());
							break;
						case 3:
							visitorAccessImpl.viewVisitorAccess(loggedVisitor.getVisitorId());
							break;
						case 4:
							SessionManager.getInstance().clearSession();
							visitorSession = false;
							break;
						}
					}
				} else {
					System.out.println(" Invalid Visitor credentials.");
				}
				break;
			case 3:
				System.out.print("Enter Receptionist Username: ");
				String receptionistUser = scanner.nextLine();
				System.out.print("Enter Password: ");
				String receptionistPass = scanner.nextLine();
				int receptionistId = receptionistImpl.getReceptionistId(receptionistUser, receptionistPass);
				if (receptionistId != -1) {
					SessionManager.getInstance().setSession("Receptionist", receptionistUser, receptionistId);
					SessionManager.getInstance().displayCurrentUser();
					boolean receptionistSession = true;
					while (receptionistSession) {
						System.out.println("\n=== Receptionist Dashboard ===");
						System.out.println("1. View All Visit Requests");
						System.out.println("2. Approve/Reject Visit Requests");
						System.out.println("3. View Access Points");
						System.out.println("4. Grant Access");
						System.out.println("5. Logout");
						System.out.print("Enter choice: ");
						int receptionistChoice = scanner.nextInt();
						scanner.nextLine();
						switch (receptionistChoice) {
						case 1:
							receptionistImpl.viewAllVisitRequests();
							break;
						case 2:
							System.out.print("Enter Request ID: ");
							int reqId = scanner.nextInt();
							scanner.nextLine();
							System.out.print("Enter new status (APPROVED/REJECTED): ");
							String status = scanner.nextLine();
							visitRequestImpl.updateRequestStatus(reqId, status);
							break;
						case 3:
							AccessPointImpl dao = new AccessPointImpl();
							List<AccessPoint> accessPoints = dao.getAllAccessPoints();
							if (accessPoints.isEmpty()) {
								System.out.println("No access points found.");
							} else {
								System.out.println("--- List of Access Points ---");
								for (AccessPoint ap : accessPoints) {
									System.out.println(
											"ID: " + ap.getAccessPointId() + ", Location: " + ap.getLocationName());
								}
							}
							break;

						case 4:
							VisitorAccess access = new VisitorAccess();
						    System.out.print("Enter Request ID: ");
						    int requestId = scanner.nextInt();
						    scanner.nextLine();

						    // Get visitor ID from request ID
						    int visitorIdFromRequest = -1;
						    String requestStatus = "";

						    try {
						        Connection con = JDBC.getConnection();
						        PreparedStatement ps = con.prepareStatement(
						            "SELECT visitor_id, status FROM visit_request WHERE request_id = ?");
						        ps.setInt(1, requestId);
						        ResultSet rs = ps.executeQuery();
						        if (rs.next()) {
						            visitorIdFromRequest = rs.getInt("visitor_id");
						            requestStatus = rs.getString("status");
						        } else {
						            System.out.println("Request ID not found.");
						            break;
						        }

						        if (!requestStatus.equalsIgnoreCase("APPROVED")) {
						            System.out.println("Cannot grant access. Request is not approved.");
						            break;
						        }
						     // important for QR
						        access.setRequestId(requestId); 
						        access.setVisitorId(visitorIdFromRequest);
						        System.out.print("Enter Access Area (e.g., Floor 2): ");
						        access.setAccessArea(scanner.nextLine());
						        access.setGrantedBy(receptionistId);

						        try {
						            visitorAccessImpl.grantAccess(access);
						        } catch (AccessAlreadyGrantedException e) {
						            System.out.println("! " + e.getMessage());
						        }

						    } catch (Exception e) {
						        e.printStackTrace();
						    }
						    break;
						case 5:
							SessionManager.getInstance().clearSession();
							receptionistSession = false;
							break;
						}
					}
				} else {
					System.out.println("Invalid receptionist credentials.");
				}
				break;
			case 4:
				System.out.print("Enter Admin Username: ");
				String aUser = scanner.nextLine();
				System.out.print("Enter Admin Password: ");
				String aPass = scanner.nextLine();
				if (adminImpl.loginAdmin(aUser, aPass)) {
					SessionManager.getInstance().setSession("Admin", aUser, 0);
					SessionManager.getInstance().displayCurrentUser();
					boolean adminSession = true;	
					while (adminSession) {
						System.out.println("\n=== Admin Dashboard ===");
						System.out.println("1. View All Visit Requests");
						System.out.println("2. View Access Points of Granted Requests");
						System.out.println("3. Delete Visitor");
						System.out.println("4. Delete Receptionist");
						System.out.println("5. Add Receptionist");
						System.out.println("6. Show Visitor Statistics");
						System.out.println("7. Logout");
						System.out.print("Enter choice: ");
						int adminChoice = scanner.nextInt();
						scanner.nextLine();
						switch (adminChoice) {
						case 1:
							adminImpl.viewAllVisitRequests();
							break;
						case 2:
							adminImpl.viewAllGrantedAccess();
							break;
						case 3:
						    try {
						        System.out.print("Enter Visitor ID to delete: ");
						        int delVisitorId = scanner.nextInt();
						        scanner.nextLine();
						        Connection con = JDBC.getConnection();
						        PreparedStatement ps = con.prepareStatement(
						            "SELECT v.visitor_id, v.name, vr.purpose " +
						            "FROM visitor v LEFT JOIN visit_request vr ON v.visitor_id = vr.visitor_id " +
						            "WHERE v.visitor_id = ?");
						        ps.setInt(1, delVisitorId);
						        ResultSet rs = ps.executeQuery();
						        if (rs.next()) {
						            String name = rs.getString("name");
						            String purpose = rs.getString("purpose");
						            System.out.println("\n Visitor Details:");
						            System.out.println("ID: " + delVisitorId);
						            System.out.println("Name: " + name);
						            System.out.println("Purpose: " + (purpose != null ? purpose : "N/A"));
						            System.out.print("\nAre you sure you want to delete this visitor? (Y/N): ");
						            String confirm = scanner.nextLine().trim();
						            if (confirm.equalsIgnoreCase("Y")) {
						                boolean result = adminImpl.deleteVisitor(delVisitorId);
						                if (!result)
						                    System.out.println("Visitor could not be deleted.");
						            } else {
						                System.out.println("Deletion cancelled.");
						            }
						        } else {
						            System.out.println("Visitor not found with ID: " + delVisitorId);
						        }
						    } catch (Exception e) {
						        e.printStackTrace();
						    }
						    break;

						case 4:
						    try {
						        Connection con = JDBC.getConnection();
						        PreparedStatement showAll = con.prepareStatement("SELECT receptionist_id, name, username FROM receptionist");
						        ResultSet rs = showAll.executeQuery();
						        System.out.println("\n---------- Available Receptionists --------");
						        boolean found = false;
						        while (rs.next()) {
						            found = true;
						            int id = rs.getInt("receptionist_id");
						            String name = rs.getString("name");
						            String username = rs.getString("username");
						            System.out.println("ID: " + id + ", Name: " + name + ", Username: " + username);
						        }
						        if (!found) {
						            System.out.println("No receptionists found.");
						            break;
						        }
						        System.out.print("\nEnter Receptionist ID to delete: ");
						        int delReceptionistId = scanner.nextInt();
						        scanner.nextLine();
						        System.out.print("Are you sure you want to delete this receptionist? (Y/N): ");
						        String confirm = scanner.nextLine().trim();
						        if (confirm.equalsIgnoreCase("Y")) {
						            boolean deleted = adminImpl.deleteReceptionist(delReceptionistId);
						            if (!deleted)
						                System.out.println("Receptionist could not be deleted.");
						        } else {
						            System.out.println("Deletion cancelled.");
						        }
						    } catch (Exception e) {
						        e.printStackTrace();
						    }
						    break;
						case 5:
							System.out.println("\n--- Add New Receptionist ---");
							System.out.print("Enter Name: ");
							String recName = scanner.nextLine();
							System.out.print("Enter Username: ");
							String recUsername = scanner.nextLine();
							System.out.print("Enter Password: ");
							String recPassword = scanner.nextLine();
							boolean added = adminImpl.addReceptionist(recName, recUsername, recPassword);
							if (!added)
								System.out.println("Receptionist could not be added.");
							break;

						case 6:
							System.out.println("\n--- Visitor Statistics ---");
							adminImpl.showVisitorStatistics();
							break;

						case 7:
							SessionManager.getInstance().clearSession();
							adminSession = false;
							break;

						default:
							System.out.println("Invalid option. Please choose again.");
							break;
						}
					}
				} else {
					System.out.println(" Invalid admin credentials.");
				}
				break;

			case 5:
				System.out.println("Exiting program. Goodbye!");
				scanner.close();
				System.exit(0);
				break;

			default:
				System.out.println(" Invalid option. Please choose again.");
			}
		}
	}
}