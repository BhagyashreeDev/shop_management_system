package com.MyProjects.HibernateSwingProject.view;

import java.awt.event.*;
import javax.swing.*;
import org.hibernate.Session;
import com.MyProjects.HibernateSwingProject.model.User;
import com.MyProjects.HibernateSwingProject.model.Role;
import com.MyProjects.HibernateSwingProject.util.HibernateUtil;

public class LoginFrame extends JFrame {
    private String mode; 
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame(String mode) {
        this.mode = mode;
        setTitle("Login - " + mode);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 250);
        getContentPane().setLayout(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(50, 50, 100, 25);
        getContentPane().add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, 50, 150, 25);
        getContentPane().add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(50, 90, 100, 25);
        getContentPane().add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 90, 150, 25);
        getContentPane().add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(150, 140, 100, 30);
        getContentPane().add(btnLogin);
        
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, 140, 100, 30); 
        getContentPane().add(btnBack);

        btnBack.addActionListener(e -> {
            new MainDashboard().setVisible(true);
            dispose();
        });

        // Action Listener for Login Button
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createQuery("from User where username = :u and password = :p", User.class)
                               .setParameter("u", username)
                               .setParameter("p", password)
                               .uniqueResult();

            if (user != null) {
                handleRedirect(user);
            } else {
                // FAIL LOGIC: Show error and return to Main Dashboard
                JOptionPane.showMessageDialog(this, 
                    "Invalid credentials! Returning to Dashboard.", 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);
                
                new MainDashboard().setVisible(true);
                this.dispose(); 
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error!");
        }
    }

    private void handleRedirect(User user) {
        if (mode.equals("USER_MODE")) {
            if (user.getRole() == Role.ADMIN) {
                new UserCRUDFrame().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Only Admins allowed!");
                new MainDashboard().setVisible(true);
                this.dispose();
            }
        } else if (mode.equals("PRODUCT_MODE")) {
            new ProductCRUDFrame().setVisible(true);
            this.dispose();
        }
    }
}