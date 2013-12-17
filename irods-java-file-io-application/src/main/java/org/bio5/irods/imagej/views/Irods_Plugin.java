package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.gui.GUI;
import ij.plugin.frame.PlugInFrame;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.bio5.irods.imagej.connection.IrodsConnection;
import org.bio5.irods.imagej.utilities.Constants;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public class Irods_Plugin extends PlugInFrame {


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
	private List<String> listInDir;
	private JTree userDirectoryTree;
	private DefaultMutableTreeNode rootNode;
	private IRODSFile iRodsFile;
	//private IRODSFile iRodsFile;

	private static final long serialVersionUID = 3225639715931294038L;

	public Irods_Plugin() {
		super("iRODS");
		// TODO Auto-generated constructor stub
	}

	public void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID()==WindowEvent.WINDOW_CLOSING) {
			instance = null;	
		}
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
		testFieldPassword.setEchoChar('*');

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
			}
		});

		Button button_Cancel=new Button();
		button_Cancel.setLabel("Close Application");
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

	void irodsConnection(){

		JOptionPane.showMessageDialog(this,"outside try block");
		JOptionPane.showMessageDialog(this,host +Integer.parseInt(port) +username +password +Constants.HOME_DIR +zone +Constants.DEFAULT_STORAGE_RESOURCE);
		iRODSAccount = new IRODSAccount (host, Integer.parseInt(port), username, password, Constants.HOME_DIR, zone, Constants.DEFAULT_STORAGE_RESOURCE);
		try {
			
			irodsFileSystem= IRODSFileSystem.instance();
			/*userAccount not required*/
			userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(iRODSAccount);
			iRODSFileFactory=irodsFileSystem.getIRODSFileFactory(iRODSAccount);
			iRODSSession=IrodsConnection.createDefaultiRodsSession();
			String parentFileName= iRODSAccount.getUserName();

			/*iRODSFileFactory */
			/*Change path to HOME_DIR in future to display other folders.
			 *Pull zone from iRODSAccount*/
			iRodsFile=iRODSFileFactory.instanceIRODSFile(Constants.HOME_DIR_IPLANT_HOME +parentFileName);

			/*
			 * Directory List*/
			IRODSFileSystemAOImpl IRODSFileSystemAOImpl  =new IRODSFileSystemAOImpl(iRODSSession, iRODSAccount);
			listInDir  = IRODSFileSystemAOImpl.getListInDir(iRodsFile);

			Iterator<String> listInDirectory =listInDir.iterator();
			int count = 1;
			while(listInDirectory.hasNext())
			{
				System.out.println("Files in Dir:" +count +" " +listInDirectory.next());
				count++;
			}

			final File localFiles = (File) iRodsFile;
			rootNode = new DefaultMutableTreeNode("home");
			//parseDirectoryContents(iRODSFileFactory, localFiles, rootNode, iRODSAccount);
			
		}

		catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}

	
	/*Un-commenting this block of code is giving ClassNotFound error*/

/*	void parseDirectoryContents(final IRODSFileFactory iRODSFileFactory,final File irodsAccountFile, DefaultMutableTreeNode node, final IRODSAccount irodsAccount)
	{
		if(!irodsAccountFile.isDirectory()){
			System.out.println("File name" +irodsAccountFile.getName() +":" +irodsAccountFile.getAbsolutePath());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(irodsAccountFile.getName());
			node.add(child);
		}
		else{
			System.out.println("Direc name" + irodsAccountFile.getName());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(irodsAccountFile.getName());
			node.add(child);
			File[] direcFiles=irodsAccountFile.listFiles();
			for(int i=0;i<direcFiles.length;i++){
				System.out.println("File number" +i);
				parseDirectoryContents(iRODSFileFactory, direcFiles[i], child, irodsAccount);
			}
		}

		userDirectoryTree= new JTree(rootNode);
		userDirectoryTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if(e.getClickCount()==2){
					String treePath=getJtreeSelection(e);
					System.out.println("tree path after double click" +treePath);
					try {
						getImageFile(iRODSFileFactory, treePath,irodsAccount);
					} catch (JargonException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		userDirectoryTree.setShowsRootHandles(true);
		userDirectoryTree.setEditable(true);
		add(userDirectoryTree);
	}

	String getJtreeSelection(MouseEvent me)
	{
		String fullTreePath="";
		TreePath tp =userDirectoryTree.getPathForLocation(me.getX(), me.getY());
		if(tp!=null)
		{
			Object treepath[] =tp.getPath();
			for(int i=0;i<treepath.length;i++)
			{
				fullTreePath  += "/" +treepath[i].toString();
			}
		}
		return fullTreePath;
	}

	void getImageFile(IRODSFileFactory iRODSFileFactory,String treePath,IRODSAccount irodsAccount) throws JargonException, IOException
	{
		System.out.println("finalTreePath:" +treePath);

		//Re-check irodsAccounZone for all accounts
		IRODSFileInputStream irodsfileistream = iRODSFileFactory.instanceIRODSFileInputStream("/" +irodsAccount.getZone() +treePath);

		BufferedImage bufferImageIrodsFile = ImageIO.read(irodsfileistream);
		irodsfileistream.close();
		JFrame frame = new JFrame();
		JLabel label = new JLabel(new ImageIcon(bufferImageIrodsFile));
		JScrollPane scrollPane = new JScrollPane(label);

		//Get file to local directory using getDataTransferOperations --- Need to check benchmarks
		DataTransferOperations dataTransferOperationsAO =  irodsFileSystem
				.getIRODSAccessObjectFactory().
				getDataTransferOperations(
						irodsAccount);
		IRODSFile irodsfile = iRODSFileFactory.instanceIRODSFile("/" +irodsAccount.getZone() +treePath);

		//Change directory address
		File localfile =new File("D:\\iRODS");
		dataTransferOperationsAO.getOperation(irodsfile, localfile, null, null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		frame.getContentPane().add(scrollPane,BorderLayout.CENTER);
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}*/
}
