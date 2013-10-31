package org.bio5.irods.imagej.views;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.border.EmptyBorder;

import org.bio5.irods.imagej.connection.IrodsConnection;
import org.bio5.irods.imagej.fileoperations.FileOperations;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.AuthenticationException;
import org.irods.jargon.core.exception.CatalogSQLException;
import org.irods.jargon.core.exception.InvalidUserException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.IRODSFile;

//import org.bio5.irods.sampleapplications.irods_connection.IrodsConnection;
//import org.irods.jargon.core.connection.IRODSAccount;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 393011043100419159L;

	public MainWindow(String title) {
		super("iRODS File IO");
		// TODO Auto-generated constructor stub
	}

	private JPanel contentPane;
	private JTextField textbox_LoginId;
	private JPasswordField textField_passwordField;
	private JTextField textField_Port;
	private JTextField textField_Zone;
	private JTextField textField_Host;
	private DirectoryContents directoryContents;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void run(String arg0) {
		// TODO Auto-generated method stub
	}



	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setTitle("iRODS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 656, 466);
		contentPane = new JPanel();
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
				{

					try{
						IRODSAccount irodsAccount =IrodsConnection.irodsConnection(username, password_full, zone, host, port);

						IRODSFileSystem irodsFileSystem= IRODSFileSystem.instance();

						UserAO  userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);

						/*Pop-up after successful connection with iRODS*/
						//JOptionPane.showMessageDialog(null,"Login Successful!");

						List<String> dirList= FileOperations.getDirectoryContents(irodsAccount);

						IRODSFile irodsAccountFile =FileOperations.getIrodsAccountFile(irodsAccount);
						
						/*Display Directory contents*/
						/*Iterator itr =dirList.iterator();
						String list = "Directory List: \n";
						while(itr.hasNext())
						{
							list +=(String) itr.next();
							list+="\n";
						}
						JOptionPane.showMessageDialog(null,list);*/

						directoryContents  =new DirectoryContents(dirList,irodsAccountFile);
						setContentPane(directoryContents);
						repaint(); // optional
						revalidate(); 
						pack();

					}

					/*Exception when username/password is empty*/
					catch(CatalogSQLException catalogSQLException)
					{
						JOptionPane.showMessageDialog(null, "Invalid Username or password!");
						catalogSQLException.printStackTrace();
					}
					/*Exception when username is invalid*/
					catch (InvalidUserException invalidUserException)
					{
						JOptionPane.showMessageDialog(null, "Invalid Username!");
						invalidUserException.printStackTrace();
					}

					/*Exception when password is invalid*/
					catch(AuthenticationException authenticationException)
					{
						JOptionPane.showMessageDialog(null, "Invalid password!");
						authenticationException.printStackTrace();
					}
					catch(Exception e1)
					{
						e1.printStackTrace();
					}

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
						.addGap(81)
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
																						.addComponent(textField_passwordField, 341, 341, Short.MAX_VALUE)
																						.addComponent(textbox_LoginId, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
																						.addComponent(textField_Host, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
																						.addGap(73)
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