package com.MyProjects.HibernateSwingProject.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainDashboard extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainDashboard frame = new MainDashboard();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainDashboard() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("User Management");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Pass "USER_MODE" so the LoginFrame knows where to redirect
		        LoginFrame login = new LoginFrame("USER_MODE");
		        login.setVisible(true);
		        dispose(); // Closes the dashboard
			}
		});
		btnNewButton.setBounds(70, 95, 135, 20);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Product Management");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Pass "USER_MODE" so the LoginFrame knows where to redirect
		        LoginFrame login = new LoginFrame("PRODUCT_MODE");
		        login.setVisible(true);
		        dispose(); // Closes the dashboard
			}
		});
		btnNewButton_1.setBounds(243, 95, 135, 20);
		contentPane.add(btnNewButton_1);

	}
}
