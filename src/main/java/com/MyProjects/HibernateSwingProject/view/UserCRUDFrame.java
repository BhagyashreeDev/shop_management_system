package com.MyProjects.HibernateSwingProject.view;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.MyProjects.HibernateSwingProject.model.User;
import com.MyProjects.HibernateSwingProject.model.Role;
import com.MyProjects.HibernateSwingProject.util.HibernateUtil;

public class UserCRUDFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsername, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<Role> comboRole;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Buttons declared here so they can be accessed by the "Foolproof" method
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public UserCRUDFrame() {
        setTitle("User Management (Admin)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 10));

        // --- UI Setup ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        inputPanel.add(txtUsername);

        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);

        inputPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        inputPanel.add(txtPassword);

        inputPanel.add(new JLabel("Role:"));
        comboRole = new JComboBox<>(Role.values()); // get enum constants i.e USER,ADMIN
        inputPanel.add(comboRole);

        contentPane.add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Email", "Role"}, 0);
        table = new JTable(tableModel);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Add User");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");
        JButton btnBack = new JButton("Back to Dashboard");
        
        	// APPLY THE STYLES (Call the method)
        styleButton(btnAdd, new Color(46, 204, 113));    // Green
        styleButton(btnUpdate, new Color(52, 152, 219)); // Blue
        styleButton(btnDelete, new Color(231, 76, 60));  // Red
        styleButton(btnClear, new Color(149, 165, 166)); // Gray
        styleButton(btnBack, Color.BLACK);
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // --- Events ---
        btnAdd.addActionListener(e -> saveUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> {
            new MainDashboard().setVisible(true); // Open Dashboard
            dispose();                            // Close current frame
        });
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtUsername.setText(tableModel.getValueAt(row, 1).toString());
                    txtEmail.setText(tableModel.getValueAt(row, 2).toString());
                    comboRole.setSelectedItem((Role) tableModel.getValueAt(row, 3));
                    txtPassword.setText(""); 
                    
                    // ROW CLICKED: Enter Edit Mode (Add disabled, Update/Delete enabled)
                    setButtonsFoolProof(true);
                }
            }
        });
        
        loadTableData();
        setButtonsFoolProof(false); // Initial State: Add mode
    }

    /**
     * Logic to prevent user mistakes. 
     * If isEditMode is true, we disable 'Add' to prevent duplicates.
     */
    private void setButtonsFoolProof(boolean isEditMode) {
        btnAdd.setEnabled(!isEditMode);
        btnUpdate.setEnabled(isEditMode);
        btnDelete.setEnabled(isEditMode);
    }

    // --- Hibernate Operations ---

    private void saveUser() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // Check for empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole((Role) comboRole.getSelectedItem());
            
            session.persist(user);
            tx.commit();
            loadTableData();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("from User", User.class).list();
            for (User u : users) {
                tableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getEmail(), u.getRole()});
            }
        }
    }

    private void updateUser() {
        int row = table.getSelectedRow();
        int id = (int) tableModel.getValueAt(row, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                user.setUsername(txtUsername.getText());
                user.setEmail(txtEmail.getText());
                
                String newPass = new String(txtPassword.getPassword());
                if (!newPass.isEmpty()) user.setPassword(newPass);
                
                user.setRole((Role) comboRole.getSelectedItem());
                session.merge(user); 
                tx.commit();
                loadTableData();
                clearFields();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        int id = (int) tableModel.getValueAt(row, 0);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                tx.commit();
                loadTableData();
                clearFields();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void clearFields() {
        txtUsername.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        comboRole.setSelectedIndex(0);
        table.clearSelection();
        // RESET: Back to Add mode
        setButtonsFoolProof(false);
    }
    
    // for styling buttons
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.BOLD, 12));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }
}