package org.bio5.irods.sampleapplications.irods_java_file_io_application;

import ij.gui.GenericDialog;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

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
import javax.swing.ProgressMonitor;
import javax.swing.border.EmptyBorder;

import org.bio5.irods.sampleapplications.irods_connection.IrodsConnection;
import org.bio5.irods.sampleapplications.irods_file_operations.FileOperations;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidUserException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;

import java.awt.Canvas;

import javax.swing.JProgressBar;

//import org.bio5.irods.sampleapplications.irods_connection.IrodsConnection;
//import org.irods.jargon.core.connection.IRODSAccount;

public class Login_Window extends JFrame {

	public Login_Window(String title) {
		super("LoginWindow");
		// TODO Auto-generated constructor stub
	}

	private JPanel contentPane;
	private JTextField textbox_LoginId;
	private JPasswordField textField_passwordField;
	private JTextField textField_Port;
	private JTextField textField_Zone;
	private JTextField textField_Host;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login_Window frame = new Login_Window();
					frame.setVisible(true);


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void run(String arg0) {
		// TODO Auto-generated method stub

		GenericDialog gd = new GenericDialog("LoginWindow");
		gd.addNumericField("Frame width:",200.0,3);

		/*IJ.showMessage("iRODS Application", "Hello iRODS!");

                String URL  ="http://godatdesign.com/sites/default/files/styles/large/public/Bio5_1.jpg";

                System.out.println("Result of URL" +IJ.openUrlAsString(URL));

                IJ.showMessage(IJ.openUrlAsString(URL));

                PlugInFrame pif = new PlugInFrame(null);
                pif.getBackground();
                Opener opener= new Opener();
                ImagePlus imageplus =opener.openURL("http://godatdesign.com/sites/default/files/styles/large/public/Bio5_1.jpg");
		 */
	}



	/**
	 * Create the frame.
	 */
	public Login_Window() {
		setTitle("iRODS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 615, 405);
		contentPane = new JPanel();
		contentPane.setToolTipText("");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		textbox_LoginId = new JTextField();
		textbox_LoginId.setToolTipText("User Id");
		textbox_LoginId.setColumns(13);

		JLabel label_2 = new JLabel("");

		final JButton button_Login = new JButton("Login");
		button_Login.setToolTipText("Click to Login");
		button_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username= textbox_LoginId.getText();
				char[] password= textField_passwordField.getPassword();
				String password_full= "";

				for (char chars: password)
					password_full += chars;
				int port=Integer.parseInt(textField_Port.getText());
				String host=textField_Host.getText();
				String zone=textField_Zone.getText();
				//JOptionPane.showMessageDialog(contentPane, password_full);

				/*if(username==null || password==null)
                                {        
                                       JOptionPane.showMessageDialog(null, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
                                }*/
				//else
				{

					try{
						IRODSAccount irodsAccount =IrodsConnection.irodsConnection(username, password_full, zone, host, port);

						IRODSFileSystem irodsFileSystem= IRODSFileSystem.instance();

						UserAO  userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);

						JOptionPane.showMessageDialog(null,"Login Successful!");

						//ProgressMonitor pm  = new ProgressMonitor(parentComponent, "Loading", "Loading", min, max)
						List<String> dirList= FileOperations.DirectoryContentsList(irodsAccount);
						Iterator itr =dirList.iterator();
						String list = "Directory List: \n";
						while(itr.hasNext())
						{
							list +=(String) itr.next();
							list+="\n";
						}
						JOptionPane.showMessageDialog(null,list);

					}
					catch (InvalidUserException invalidUserException)
					{
						JOptionPane.showMessageDialog(null, "Invalid Username or password!");
					}
					catch(Exception e1)
					{
						e1.printStackTrace();
					}

					//	JOptionPane.showMessageDialog(null, "Connection Established! and your Home directory is" +irodsAccount.getHost());
				}

			}
		});

		JButton button_Cancel = new JButton("Cancel");
		button_Cancel.setToolTipText("Click to close application");
		button_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		JLabel Label_Username = new JLabel("User Name:");

		JLabel Label_Password = new JLabel("Password:");

		textField_passwordField = new JPasswordField();
		textField_passwordField.setToolTipText("Password");

		JLabel label_MessageBox = new JLabel("");

		textField_Port = new JTextField();
		textField_Port.setText("1247");
		textField_Port.setToolTipText("Port No.");
		textField_Port.setColumns(10);

		JLabel Label_Port = new JLabel("Port:");

		JLabel label_Zone = new JLabel("Zone:");

		textField_Zone = new JTextField();
		textField_Zone.setText("iplant");
		textField_Zone.setToolTipText("Zone");
		textField_Zone.setColumns(10);

		textField_Host = new JTextField();
		textField_Host.setToolTipText("Host Address");
		textField_Host.setText("data.iplantcollaborative.org");
		textField_Host.setColumns(10);

		JLabel label_Host = new JLabel("Host:");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(81, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(Label_Password)
						.addComponent(Label_Username)
						.addComponent(Label_Port)
						.addComponent(label_Host)
						.addComponent(label_Zone))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(textField_Port, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(textField_Zone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(313))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(button_Login)
										.addGap(18)
										.addComponent(button_Cancel))
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(textField_Host, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED, 255, Short.MAX_VALUE))
									.addComponent(textField_passwordField, 341, 341, 341)
									.addComponent(textbox_LoginId, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
								.addComponent(label_MessageBox)
								.addGap(34)))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(54)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(Label_Username)
						.addComponent(textbox_LoginId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(17)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(Label_Password)
						.addComponent(textField_passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(Label_Port)
						.addComponent(textField_Port, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField_Zone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_Zone))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(label_MessageBox, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(4)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(textField_Host, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(label_Host))
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(button_Login, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(button_Cancel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addContainerGap(94, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}