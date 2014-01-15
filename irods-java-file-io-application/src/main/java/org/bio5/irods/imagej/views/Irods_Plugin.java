package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.frame.PlugInFrame;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.connection.IrodsConnection;
import org.bio5.irods.imagej.fileoperations.FileOperations;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.AuthenticationException;
import org.irods.jargon.core.exception.CatalogSQLException;
import org.irods.jargon.core.exception.InvalidUserException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;

public class Irods_Plugin extends PlugInFrame {


	/*Declare static variables*/
	private static final int PANEL_HEIGHT = 1000;
	private static final int PANEL_WIDTH = 1000;
	private static Frame instance;
	private Panel panel;
	private TextField textFieldUsername, textFieldPort,textFieldZone,textFieldHost, testFieldPassword;
	private Label label_username, label_password,label_port, label_Zone,label_Host,label_DirectoryList;
	private TextArea textarea;
	private String username=""; 
	private String password="";
	private String port="";
	private String zone="";
	private String host="";
	private static IRODSFileSystem irodsFileSystem;
	private static String HOME_DIR ="/iplant/home/";
	public IRODSAccount irodsAccount;
	private DirectoryContentsPane directoryContents;


	/*Logger instantiation*/
	static Logger log = Logger.getLogger(
			Irods_Plugin.class.getName());


	private static final long serialVersionUID = 3225639715931294038L;

	public Irods_Plugin() {
		super("iRODS");
		log =Logger.getLogger(Irods_Plugin.class);
	}

	public void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID()==WindowEvent.WINDOW_CLOSING) {
			instance = null;	
		}
	}


	public void run(String arg) {
		
		 MainWindow mw =new MainWindow();
         mw.setVisible(true);
/*
		panel = new Panel();
		Dimension d= new Dimension();
        d.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		panel.setSize(d);
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
		textFieldZone.setText("iplant");
		label_Host= new Label("Host :");
		textFieldHost= new TextField(30);
		textFieldHost.setText("data.iplantcollaborative.org");
		testFieldPassword.setEchoChar('*');

		Button button_Login=new Button();
		button_Login.setLabel("Login");

		button_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				Collect details from login page
				username=textFieldUsername.getText();
				password=testFieldPassword.getText();
				port=textFieldPort.getText();
				zone=textFieldZone.getText();
				host=textFieldHost.getText();

				Establish Connection
				try {
					irodsConnection();
				} catch (JargonException e) {
					e.printStackTrace();
				}


				setting values
				middleware.setUsername(username);
				middleware.setPassword(password);
				middleware.setPort(port);
				middleware.setZone(zone);
				middleware.setHost(host);

				middleware.connection();
			}
		});

		Button button_Cancel=new Button();
		button_Cancel.setLabel("Close Application");
		button_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		Adding contents to panel
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
		setVisible(true);*/
	}

	//Not using as of now
	public void irodsConnection() throws JargonException {
		try
		{
			irodsAccount =IrodsConnection.irodsConnection(username, password, zone, host, Integer.parseInt(port));
			List<String> dirList= FileOperations.getDirectoryContents(irodsAccount);
			
			IRODSFile irodsAccountFile =FileOperations.getiRodsFile();
			JOptionPane.showMessageDialog(null, irodsAccountFile.getParent());
			panel.removeAll();
            Dimension d= new Dimension();
            label_DirectoryList= new Label("Directory List:");
            //panel.add(label_DirectoryList);
            d.setSize(PANEL_WIDTH, PANEL_HEIGHT);
            panel.setSize(d);
            //createDirectoryTree(dirList,irodsAccountFile);
            
            MainWindow mw =new MainWindow();
            mw.setVisible(true);
            panel.repaint();
            pack();
            setVisible(true);
		}
		/*Exception when username/password is empty*/
		catch(CatalogSQLException catalogSQLException)
		{
			log.error(catalogSQLException.getMessage());
			JOptionPane.showMessageDialog(null, "Invalid Username or password!");
			catalogSQLException.printStackTrace();
			
		}
		/*Exception when username is invalid*/
		catch (InvalidUserException invalidUserException)
		{
			log.error(invalidUserException.getMessage());
			JOptionPane.showMessageDialog(null, "Invalid Username!");
			invalidUserException.printStackTrace();
		}

		/*Exception when password is invalid*/
		catch(AuthenticationException authenticationException)
		{
			log.error(authenticationException.getMessage());
			JOptionPane.showMessageDialog(null, "Invalid password!");
			authenticationException.printStackTrace();
		}
		catch(Exception e1)
		{
			log.error(e1.getMessage());
			JOptionPane.showMessageDialog(null, "Unknown Exception!");
			e1.printStackTrace();
		}
	}

	
	public void createDirectoryTree(List<String> ContentsInHome,IRODSFile irodsAccountFile ){
		
		try {
			directoryContents= new DirectoryContentsPane(ContentsInHome, irodsAccountFile, irodsAccount);
		} catch (JargonException jargonException) {
			log.error(jargonException.getMessage());
			JOptionPane.showMessageDialog(null, "Unknown Exception in createDirectoryTree!");
			jargonException.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = Irods_Plugin.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// open a sample bio5 image
		/*ImagePlus image = IJ.openImage("http://www.bio5.org/sites/default/files/homepage/slides/5_areas_circle_300pxWidth.png");
        image.show();*/

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}
