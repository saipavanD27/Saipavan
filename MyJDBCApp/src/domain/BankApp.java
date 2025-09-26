package domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Scanner;

public class BankApp {
	static final String url="jdbc:mysql://localhost:3306/bankdb";
	static final String uname="root";
	static final String password="1234";
	static int userId=-1;
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		try(Connection conn = DriverManager.getConnection(url,uname,password)){
			if(!login(conn,sc)) {
				System.out.println("Login failed. Exiting");
				return;
			}
			boolean run =true;
			 while (run) {
	                System.out.println("\nPlease select the option");
	                System.out.println("1. Deposit");
	                System.out.println("2. Withdrawal");
	                System.out.println("3. Check balance");
	                System.out.println("4. Edit profile");
	                System.out.println("5. Change password");
	                System.out.println("6. EXIT");
	                int choice = sc.nextInt();
	                switch (choice) {
	                    case 1: // Deposit
	                        System.out.println("Enter the amount");
	                        double dep = sc.nextDouble();
	                        updateBalance(conn, dep, true);
	                        break;
	                    case 2: // Withdrawal
	                        System.out.println("Enter the amount");
	                        double wit = sc.nextDouble();
	                        updateBalance(conn, wit, false);
	                        break;
	                    case 3: // Check balance
	                        showBalance(conn);
	                        break;
	                    case 4: // Edit profile
	                        editProfile(conn, sc);
	                        break;
	                    case 5: // Change password
	                        changePassword(conn, sc);
	                        break;
	                    case 6: // EXIT
	                        run = false;
	                        System.out.println("Exiting...");
	                        break;
	                    default:
	                        System.out.println("Invalid option!");
	                }
	                if (run) {
	                    System.out.println("Do you want to continue");
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        sc.close();
	    }
	    // --- LOGIN ---
	    private static boolean login(Connection conn, Scanner sc) throws SQLException {
	        System.out.println("===== LOGIN =====");
	        System.out.print("Enter username: ");
	        String uname = sc.next();
	        System.out.print("Enter password: ");
	        String pwd = sc.next();
	        String sql = "SELECT id FROM users WHERE username=? AND password=?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, uname);
	        ps.setString(2, pwd);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            userId = rs.getInt("id");
	            System.out.println("Login successful! Welcome " + uname);
	            return true;
	        } else {
	            return false;
	        }
	    }
	    // --- UPDATE BALANCE ---
	    private static void updateBalance(Connection conn, double amount, boolean deposit) throws SQLException {
	        String sql = "SELECT balance FROM users WHERE id=?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            double balance = rs.getDouble("balance");
	            if (deposit) {
	                balance += amount;
	            } else {
	                if (amount > balance) {
	                    System.out.println("Insufficient balance!");
	                    return;
	                }
	                balance -= amount;
	            }
	            sql = "UPDATE users SET balance=? WHERE id=?";
	            ps = conn.prepareStatement(sql);
	            ps.setDouble(1, balance);
	            ps.setInt(2, userId);
	            ps.executeUpdate();
	            System.out.println("balance updated " + balance);
	        }
	    }
	    // --- SHOW BALANCE ---
	    private static void showBalance(Connection conn) throws SQLException {
	        String sql = "SELECT balance FROM users WHERE id=?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            System.out.println("Current balance: " + rs.getDouble("balance"));
	        }
	    }
	    // --- EDIT PROFILE ---
	    private static void editProfile(Connection conn, Scanner sc) throws SQLException {
	        System.out.println("Please select the field you want to update");
	        System.out.println("1. Name");
	        System.out.println("2. Address");
	        System.out.println("3. Email");
	        System.out.println("4. Phone");
	        int field = sc.nextInt();
	        sc.nextLine(); // consume newline
	        System.out.println("Enter new value:");
	        String value = sc.nextLine();
	        String column = "";
	        switch (field) {
	            case 1: column = "name"; break;
	            case 2: column = "address"; break;
	            case 3: column = "email"; break;
	            case 4: column = "phone"; break;
	            default:
	                System.out.println("Invalid option!");
	                return;
	        }
	        String sql = "UPDATE users SET " + column + "=? WHERE id=?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, value);
	        ps.setInt(2, userId);
	        ps.executeUpdate();
	        System.out.println(column + " updated successfully!");
	    }
	    // --- CHANGE PASSWORD ---
	    private static void changePassword(Connection conn, Scanner sc) throws SQLException {
	        System.out.print("Enter old password: ");
	        String oldPwd = sc.next();
	        System.out.print("Enter new password: ");
	        String newPwd = sc.next();
	        String sql = "SELECT password FROM users WHERE id=?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next() && rs.getString("password").equals(oldPwd)) {
	            sql = "UPDATE users SET password=? WHERE id=?";
	            ps = conn.prepareStatement(sql);
	            ps.setString(1, newPwd);
	            ps.setInt(2, userId);
	            ps.executeUpdate();
	            System.out.println("Password updated successfully!");
	        } else {
	            System.out.println("Old password is incorrect!");
	        }
	    }
	}