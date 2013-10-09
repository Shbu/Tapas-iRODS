package org.bio5.irods.sampleapplications.irods_java_file_io_application;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class LoginWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textbox_LoginId;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow frame = new LoginWindow();
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
	public LoginWindow() {
		setTitle("iRODS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setToolTipText("Password");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		textbox_LoginId = new JTextField();
		textbox_LoginId.setToolTipText("User Id");
		textbox_LoginId.setColumns(13);

		JLabel label_2 = new JLabel("");

		final JButton button_Login = new JButton("Login");
		button_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username= textbox_LoginId.getText();
				String password= passwordField.getSelectedText();

				if(username!=null || password!=null)
				/*
				 * Do something*/

					JOptionPane.showMessageDialog(null, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		JButton button_Cancel = new JButton("Cancel");
		button_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		JLabel Label_Username = new JLabel("User Name:");

		JLabel Label_Password = new JLabel("Password:");

		passwordField = new JPasswordField();
		passwordField.setToolTipText("Password");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(164)
						.addComponent(button_Login)
						.addGap(18)
						.addComponent(button_Cancel)
						.addContainerGap(120, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
								.addContainerGap(48, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
										.addGroup(gl_contentPane.createSequentialGroup()
												.addComponent(Label_Password)
												.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 268, GroupLayout.PREFERRED_SIZE))
												.addGroup(gl_contentPane.createSequentialGroup()
														.addComponent(Label_Username)
														.addGap(18)
														.addComponent(textbox_LoginId, GroupLayout.PREFERRED_SIZE, 268, GroupLayout.PREFERRED_SIZE)))
														.addGap(34))
				);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(54)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(textbox_LoginId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(Label_Username))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(Label_Password))
										.addGap(18)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
												.addComponent(button_Cancel)
												.addComponent(button_Login))
												.addContainerGap(106, Short.MAX_VALUE))
				);
		contentPane.setLayout(gl_contentPane);
	}
}
