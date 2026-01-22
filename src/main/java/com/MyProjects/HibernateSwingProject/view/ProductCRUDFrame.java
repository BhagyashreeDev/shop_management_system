package com.MyProjects.HibernateSwingProject.view;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.MyProjects.HibernateSwingProject.model.*;
import com.MyProjects.HibernateSwingProject.util.HibernateUtil;

public class ProductCRUDFrame extends JFrame {

    private JTextField txtName, txtCategory, txtPrice, txtDesc, txtUrl, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public ProductCRUDFrame() {
        setTitle("Product Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 850, 650);
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // --- 1. INPUT PANEL (NORTH) ---
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        
        inputPanel.add(new JLabel("Product Name:"));
        txtName = new JTextField(); inputPanel.add(txtName);
        
        inputPanel.add(new JLabel("Category:"));
        txtCategory = new JTextField(); inputPanel.add(txtCategory);
        
        inputPanel.add(new JLabel("Price:"));
        txtPrice = new JTextField(); inputPanel.add(txtPrice);
        
        inputPanel.add(new JLabel("Description:"));
        txtDesc = new JTextField(); inputPanel.add(txtDesc);
        
        inputPanel.add(new JLabel("Image URL:"));
        txtUrl = new JTextField(); inputPanel.add(txtUrl);

        // --- 2. SEARCH PANEL (part of North) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by Name/Category:"));
        txtSearch = new JTextField(25);
        searchPanel.add(txtSearch);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(inputPanel, BorderLayout.CENTER);
        northContainer.add(searchPanel, BorderLayout.SOUTH);
        contentPane.add(northContainer, BorderLayout.NORTH);

        // --- 3. TABLE PANEL (CENTER) ---
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Price", "Image URL"}, 0);
        table = new JTable(tableModel);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 4. BUTTON PANEL (SOUTH) ---
        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("Add Product");
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
        
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); 
        btnPanel.add(btnDelete); btnPanel.add(btnClear);
        btnPanel.add(btnBack);
        contentPane.add(btnPanel, BorderLayout.SOUTH);
        

        // --- BUTTON ACTIONS ---
        btnAdd.addActionListener(e -> saveProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> {
            new MainDashboard().setVisible(true);
            dispose();
        });

        // --- REAL-TIME SEARCH ---
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadProducts(txtSearch.getText());
            }
        });

        // --- TABLE SELECTION (FOOLPROOF) ---
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtCategory.setText(tableModel.getValueAt(row, 2).toString());
                    txtPrice.setText(tableModel.getValueAt(row, 3).toString());
                    txtUrl.setText(tableModel.getValueAt(row, 4).toString());
                    setButtonsFoolProof(true);
                }
            }
        });

        loadProducts(""); // Initial data load
        setButtonsFoolProof(false);
    }

    private void setButtonsFoolProof(boolean isEditMode) {
        btnAdd.setEnabled(!isEditMode);
        btnUpdate.setEnabled(isEditMode);
        btnDelete.setEnabled(isEditMode);
    }

    private void clearFields() {
        txtName.setText(""); txtCategory.setText(""); txtPrice.setText("");
        txtDesc.setText(""); txtUrl.setText(""); txtSearch.setText("");
        table.clearSelection();
        setButtonsFoolProof(false);
        loadProducts("");
    }

    // --- HIBERNATE LOGIC: CREATE ---
    private void saveProduct() {
        if(txtName.getText().isEmpty() || txtPrice.getText().isEmpty() || txtUrl.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill all mandatory fields!");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            
            Product p = new Product();
            p.setName(txtName.getText());
            p.setCategory(txtCategory.getText());
            p.setDescription(txtDesc.getText());
            p.setPrice(Double.parseDouble(txtPrice.getText()));

            ProductImage img = new ProductImage();
            img.setImageUrl(txtUrl.getText());
            img.setProduct(p);
            p.setProductImage(img);

            session.persist(p);
            tx.commit();
            loadProducts("");
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- HIBERNATE LOGIC: READ & SEARCH ---
    private void loadProducts(String query) {
        tableModel.setRowCount(0);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from Product p where p.name LIKE :q or p.category LIKE :q";
            List<Product> list = session.createQuery(hql, Product.class)
                                        .setParameter("q", "%" + query + "%")
                                        .list();
            for (Product p : list) {
                String url = (p.getProductImage() != null) ? p.getProductImage().getImageUrl() : "";
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), url});
            }
        }
    }

    // --- HIBERNATE LOGIC: UPDATE ---
    private void updateProduct() {
        int row = table.getSelectedRow();
        int id = (int) tableModel.getValueAt(row, 0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Product p = session.get(Product.class, id);
            if (p != null) {
                p.setName(txtName.getText());
                p.setCategory(txtCategory.getText());
                p.setPrice(Double.parseDouble(txtPrice.getText()));
                if(p.getProductImage() != null) p.getProductImage().setImageUrl(txtUrl.getText());
                
                session.merge(p);
                tx.commit();
                loadProducts("");
                clearFields();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- HIBERNATE LOGIC: DELETE ---
    private void deleteProduct() {
        int row = table.getSelectedRow();
        int id = (int) tableModel.getValueAt(row, 0);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Product p = session.get(Product.class, id);
            if (p != null) {
                session.remove(p);
                tx.commit();
                loadProducts("");
                clearFields();
            }
        } catch (Exception e) { e.printStackTrace(); }
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