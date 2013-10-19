package org.bio5.irods.sampleapplications.irods_java_file_io_application;

import ij.IJ;
import ij.WindowManager;
import ij.gui.GUI;
import ij.plugin.frame.PlugInFrame;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class LoginWindow_AWT extends PlugInFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Frame instance;
	private Panel panel;
	private TextField Username;
	private TextField Password;
	private Button LoginButton;
	
	public LoginWindow_AWT(String title) {
		super("Login Window");

		if (instance!=null) {
			instance.toFront();
			return;
		}
		instance =this;
		addKeyListener(IJ.getInstance());
		
		setLayout(new FlowLayout());
		panel = new Panel();
		
		
		addTextBox("Username");
		addTextBoxPassword("Password");
		addButton("Login");
		add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(Username);
		panel.add(Password);
		panel.add(LoginButton);
		
		pack();
		GUI.center(this);
		setVisible(true);
	}
	
	
	void addButton(String label) {
		LoginButton = new Button(label);
		LoginButton.addActionListener(this);
		LoginButton.addKeyListener(IJ.getInstance());
	}
	
	void addTextBox(String label){
		Username = new TextField();
		Username.setText(label);
		Username.addActionListener(IJ.getInstance());
	}
	void addTextBoxPassword(String label){
		Password = new TextField();
		Password.setText(label);
		Password.addActionListener(IJ.getInstance());
	}
	
	
	

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	
		IJ.beep();
		IJ.showStatus("Error");
		
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	
		LoginWindow_AWT lw = new LoginWindow_AWT("iRODS Application");
		lw.pack();
		lw.setVisible(true);
		
	}

	/*
	 * Closing event*/
		public void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID()==WindowEvent.WINDOW_CLOSING) {
			instance = null;	
		}
	}

	public void run(String arg0) {

		IJ.showMessage("iRODS Application", "Hello iRODS!");
	
	}
	

	
	/*
	private JPanel contentPane;
	private JTextField textbox_LoginId;
	private JPasswordField passwordField;*/
	
	/**
	 * Create the frame.
	 */
	/*public LoginWindow() {
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

				if(username==null || password==null)
				{	
				 

					JOptionPane.showMessageDialog(null, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					IRODSAccount irodsAccount = IrodsConnection.irodsConnection(textbox_LoginId.getText(), passwordField.getSelectedText());
					irodsAccount.getUserName();
					JOptionPane.showMessageDialog(null, "Connection Established! and your Home directory is" +irodsAccount.getUserName());
				}

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

		JLabel label_MessageBox = new JLabel("");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
										.addGap(164)
										.addComponent(button_Login)
										.addGap(18)
										.addComponent(button_Cancel)
										.addPreferredGap(ComponentPlacement.RELATED, 86, Short.MAX_VALUE))
										.addGroup(gl_contentPane.createSequentialGroup()
												.addContainerGap(48, Short.MAX_VALUE)
												.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
														.addGroup(gl_contentPane.createSequentialGroup()
																.addComponent(Label_Username)
																.addGap(18)
																.addComponent(textbox_LoginId, GroupLayout.PREFERRED_SIZE, 268, GroupLayout.PREFERRED_SIZE))
																.addGroup(gl_contentPane.createSequentialGroup()
																		.addComponent(Label_Password)
																		.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
																				.addComponent(label_MessageBox, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																				.addComponent(passwordField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))))))
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
												.addGap(36)
												.addComponent(label_MessageBox, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
												.addContainerGap(25, Short.MAX_VALUE))
				);
		contentPane.setLayout(gl_contentPane);
	}*/
}
