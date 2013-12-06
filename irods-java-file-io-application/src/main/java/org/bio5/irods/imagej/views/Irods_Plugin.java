package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.gui.GUI;
import ij.gui.GenericDialog;
import ij.plugin.frame.PlugInFrame;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.bio5.irods.imagej.connection.IrodsConnection;
import org.bio5.irods.imagej.utilities.Constants;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public class Irods_Plugin extends PlugInFrame /*implements PlugIn*/ {


	/*Declare static variables*/
	private static final int PANEL_HEIGHT = 500;
	private static final int PANEL_WIDTH = 500;
	private static Frame instance;
	private Panel panel;
	private TextField textFieldUsername, textFieldPort,textFieldZone,textFieldHost, testFieldPassword;
	private Label label_username, label_password,label_port, label_Zone,label_Host;
	private String username=""; 
	private String password="";
	private String port="";
	private String zone="";
	private String host="";

	private IRODSAccount iRODSAccount;
	private IRODSFileSystem irodsFileSystem;
	private UserAO  userAccount;
	private IRODSSession iRODSSession;
	private IRODSFileFactory iRODSFileFactory;
	private IRODSFileSystemAOImpl iRODSFileSystemAOImpl;
	
	private static final long serialVersionUID = 3225639715931294038L;

	public Irods_Plugin() {
		super("iRODS");
		// TODO Auto-generated constructor stub
	}

	public void run(String arg) {

		panel = new Panel();
		panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		panel.setLayout(new GridLayout(6,2,7,7));

		label_username = new Label("User Name :");
		textFieldUsername =new TextField(30);
		label_password = new Label("Password :");
		testFieldPassword = new TextField(30);
		label_port = new Label("Port :");
		textFieldPort= new TextField(30);
		textFieldPort.setText("1247");
		label_Zone = new Label("Zone :");
		textFieldZone= new TextField(30);
		textFieldZone.setText("iPlant");
		label_Host= new Label("Host :");
		textFieldHost= new TextField(30);
		textFieldHost.setText("data.iplantcollaborative.org");

		char c=0;
		testFieldPassword.setEchoChar(c);

		Button button_Login=new Button();
		button_Login.setLabel("Login");

		button_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				/*Collect details from login page*/
				username=textFieldUsername.getText();
				password=testFieldPassword.getText();
				port=textFieldPort.getText();
				zone=textFieldZone.getText();
				host=textFieldHost.getText();
				
				JOptionPane.showMessageDialog(null, username +password);
				
				/*Establish Connection*/
				irodsConnection();
				/*Perform Action after clicking Login button*/
			}
		});

		Button button_Cancel=new Button();
		button_Cancel.setLabel("Close");
		button_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		/*Adding contents to panel*/
		panel.add(label_username);
		panel.add(textFieldUsername);

		panel.add(label_password);
		panel.add(testFieldPassword);

		panel.add(label_port);
		panel.add(textFieldPort);

		panel.add(label_Zone);
		panel.add(textFieldZone);

		panel.add(label_Host);
		panel.add(textFieldHost);

		panel.add(button_Login);
		panel.add(button_Cancel);

		add(panel);
		pack();
		GUI.center(this);
		setVisible(true);
		
	}
	
	private void irodsConnection(){
		IRODSFile iRodsFile;

		iRODSAccount = new IRODSAccount (host, Integer.parseInt(port), username, password, Constants.HOME_DIR, zone, Constants.DEFAULT_STORAGE_RESOURCE);
		try {
			irodsFileSystem= IRODSFileSystem.instance();
			/*userAccount not required*/
			userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(iRODSAccount);
			
			iRODSFileFactory=irodsFileSystem.getIRODSFileFactory(iRODSAccount);
			
			iRODSSession=IrodsConnection.createDefaultiRodsSession();
			
			JOptionPane.showMessageDialog(null, userAccount.getJargonProperties());
			
			String parentFileName= iRODSAccount.getUserName();

			/*iRODSFileFactory */
			/*Change path to HOME_DIR in future to display other folders.
			 *Pull zone from iRODSAccount*/
			iRodsFile=iRODSFileFactory.instanceIRODSFile(Constants.HOME_DIR_IPLANT_HOME +parentFileName);
		}
		
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
