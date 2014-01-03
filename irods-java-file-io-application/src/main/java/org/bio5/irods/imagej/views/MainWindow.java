package org.bio5.irods.imagej.views;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
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

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;

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
	private DirectoryContentsPane directoryContents;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
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
*/
/*	public void run(String arg0) {
		// TODO Auto-generated method stub
	}*/



	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setTitle("iRODS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 656, 466);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu_File = new JMenu("File");
		mnNewMenu_File.setMnemonic('F');
		menuBar.add(mnNewMenu_File);
		
		JMenuItem mntmNewMenuItem_Open = new JMenuItem("Open");
		mntmNewMenuItem_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnNewMenu_File.add(mntmNewMenuItem_Open);
		
		JMenuItem mntmNewMenuItem_Exit = new JMenuItem("Exit");
		mntmNewMenuItem_Exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mntmNewMenuItem_Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnNewMenu_File.add(mntmNewMenuItem_Exit);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('H');
		menuBar.add(mnHelp);
		
		JMenuItem mntm_About = new JMenuItem("About");
		mntm_About.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		mntm_About.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "iRODS File Operations", "About iRODS ImageJ Plugin", 1);
			}
		});
		mnHelp.add(mntm_About);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		textbox_LoginId = new JTextField();
		textbox_LoginId.setToolTipText("User Id");
		textbox_LoginId.setColumns(13);

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

						/*IRODSFileSystem irodsFileSystem= IRODSFileSystem.instance();

						UserAO  userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);*/

						List<String> dirList= FileOperations.getDirectoryContents(irodsAccount);

						IRODSFile irodsAccountFile =FileOperations.getiRodsFile();
						
						/*Display Directory contents*/
						/*Iterator itr =dirList.iterator();
						String list = "Directory List: \n";
						while(itr.hasNext())
						{
							list +=(String) itr.next();
							list+="\n";
						}
						JOptionPane.showMessageDialog(null,list);*/

						directoryContents  =new DirectoryContentsPane(dirList,irodsAccountFile,irodsAccount);
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
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(textField_Port, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(textField_Zone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(313))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(button_Login)
										.addGap(18)
										.addComponent(button_Cancel)
										.addGap(109))
									.addComponent(textField_passwordField, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE)
									.addComponent(textbox_LoginId, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE)
									.addComponent(textField_Host, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE))
								.addGap(107)))))
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
					.addGap(15)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField_Host, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_Host))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE, false)
						.addComponent(button_Cancel)
						.addComponent(button_Login))
					.addGap(134))
		);
		contentPane.setLayout(gl_contentPane);
	}
}