package org.bio5.irods.iplugin.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.bio5.irods.iplugin.connection.IrodsConnection;
import org.bio5.irods.iplugin.fileoperations.FileOperations;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.exception.AuthenticationException;
import org.irods.jargon.core.exception.CatalogSQLException;
import org.irods.jargon.core.exception.InvalidUserException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;

public class MainWindow extends JFrame {

	/**
	 * 
	 */

	private static final long serialVersionUID = 393011043100419159L;
	public IRODSFileSystem irodsFileSystem;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(MainWindow.class.getName());

	private JPanel contentPanePanel;
	private JTextField textbox_LoginId;
	private JPasswordField textField_passwordField;
	private JTextField textField_Port;
	private DirectoryContentsWindow directoryContentsPane;
	private IPlugin iplugin;
	private JCheckBox HomeDirectory_CheckBox;

	public JFileChooser localImageJFileChooser;
	private JTextField textField_ImageJCacheFolderPath;
	private IRODSFileSystemAOImpl iRODSFileSystemAOImpl;
	private JFileChooser fileChooserForImageJCacheFolder;
	private File selectedFileForImageJCacheFolder;
	private JComboBox<String> comboBox_Zone;
	private JComboBox<String> comboBox_Host;
	private IRODSFileFactory iRODSFileFactory;
	private String usernamePickedFromPropertyFiles = null;
	private String zonePickedFromPropertyFiles = null;
	private String hostPickedFromPropertyFiles = null;

	/**
	 * Launch the application.
	 */
	/**
	 * Create the frame.
	 */
	public MainWindow(IPlugin iPlugin) {
		// super();
		this.iplugin = iPlugin;

		mainWindowInit();

		setTitle("iRODS");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 682, 454);

		// setFocusable(true);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu_File = new JMenu("File");
		mnNewMenu_File.setMnemonic('F');
		menuBar.add(mnNewMenu_File);

		JMenuItem mntmNewMenuItem_Open = new JMenuItem("Open");
		mntmNewMenuItem_Open.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnNewMenu_File.add(mntmNewMenuItem_Open);

		JMenuItem mntmNewMenuItem_Exit = new JMenuItem("Exit");
		mntmNewMenuItem_Exit.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mntmNewMenuItem_Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				closeApplication(iplugin);
			}
		});
		mnNewMenu_File.add(mntmNewMenuItem_Exit);
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('H');
		menuBar.add(mnHelp);
		JMenuItem mntm_About = new JMenuItem("About iPlugin");
		mntm_About.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		mntm_About.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showMessageDialog(
								null,
								"iPlugin V1.2 - Plugin for imagej to handle file operations with iRODS Servers",
								"About iRODS ImageJ Plugin v1.2", 1);
			}
		});
		mnHelp.add(mntm_About);
		contentPanePanel = new JPanel();
		contentPanePanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		setContentPane(contentPanePanel);

		textbox_LoginId = new JTextField();
		textbox_LoginId.setHorizontalAlignment(SwingConstants.LEFT);
		textbox_LoginId.setToolTipText("User Id");
		textbox_LoginId.setColumns(13);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textbox_LoginId.requestFocus();
			}
		});

		final JButton button_Login = new JButton("Login");
		button_Login.setToolTipText("Click to Login");
		button_Login.setEnabled(true);

		/* Adding default button_Login as default button for ENTER_KEY */
		getRootPane().setDefaultButton(button_Login);
		/*
		 * Let cursor show into login text filed and user can input string to
		 * it.
		 */
		textbox_LoginId.requestFocusInWindow();

		button_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * When the root panel active, request the this panel focusable,
				 * and set key listener for ESC button.
				 */
				getRootPane().setFocusable(true);
				loginMethod();
				getRootPane().addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							closeApplication(iplugin);

						}
					}
				});
			}
		});

		JButton button_Cancel = new JButton("Cancel");
		button_Cancel.setToolTipText("Click to close application");
		button_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				closeApplication(iplugin);
			}
		});

		JLabel Label_Username = new JLabel("User Name:");
		Label_Username.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel Label_Password = new JLabel("Password:");
		Label_Password.setHorizontalAlignment(SwingConstants.CENTER);

		textField_passwordField = new JPasswordField();
		textField_passwordField.setHorizontalAlignment(SwingConstants.LEFT);
		textField_passwordField.setToolTipText("Password");

		textField_Port = new JTextField();
		textField_Port.setEditable(false);
		textField_Port.setText(Constants.PORT);
		textField_Port.setToolTipText("Port No.");
		textField_Port.setColumns(10);

		JLabel Label_Port = new JLabel("Port:");
		Label_Port.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel label_Zone = new JLabel("Zone:");
		label_Zone.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel label_Host = new JLabel("Host:");
		label_Host.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel homeDirectory_Label = new JLabel("File Selection:");

		HomeDirectory_CheckBox = new JCheckBox("Home Directory");

		textField_ImageJCacheFolderPath = new JTextField();
		textField_ImageJCacheFolderPath
				.setToolTipText("Enter ImageJ Cache folder path");
		textField_ImageJCacheFolderPath.setColumns(10);
		textField_ImageJCacheFolderPath.setText(iPlugin.getImageJCacheFolder());

		JLabel lblImagejCacheFolder = new JLabel("ImageJ Cache Folder:");
		JButton btnChooseFolder = new JButton("Choose folder");
		btnChooseFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooserForImageJCacheFolder = new JFileChooser(
						IrodsUtilities.getUserHomeFolderFromSystemProperty());
				fileChooserForImageJCacheFolder
						.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChooserForImageJCacheFolder
						.showOpenDialog(MainWindow.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					selectedFileForImageJCacheFolder = fileChooserForImageJCacheFolder
							.getSelectedFile();
					log.info("Opening: "
							+ selectedFileForImageJCacheFolder
									.getAbsolutePath() + "."
							+ Constants.NEW_LINE_STRING);
					iplugin.setImageJCacheFolder(selectedFileForImageJCacheFolder
							.getAbsolutePath());
					textField_ImageJCacheFolderPath.setText(iplugin
							.getImageJCacheFolder());
				} else {
					log.info("User cancelled file selection window: "
							+ Constants.NEW_LINE_STRING);
				}
			}
		});

		/* Initializing values */
		comboBox_Zone = new JComboBox<String>();
		comboBox_Host = new JComboBox<String>();

		/* Pulling tapas login configuration from properties */

		Properties tapasProperties = IrodsUtilities.getTapasLoginConfiguration(
				Constants.PROPERTY_FILE_NAME, Constants.IMAGEJ_CACHE_FOLDER);
		if (null != tapasProperties) {
			setPropertyFileDataToLoginPanel(tapasProperties);
		} else {
			log.error("tapas property file is null");
		}

		comboBox_Zone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				Object selected = comboBox.getSelectedItem();
				if (selected.toString().equals(Constants.ZONE_IPLANT)) {
					comboBox_Host.setSelectedItem(Constants.HOST_IPLANT);
				}
				if (selected.toString().equals(Constants.ZONE_SPX)) {
					comboBox_Host.setSelectedItem(Constants.HOST_SPX);
				}
			}
		});

		comboBox_Zone.setEditable(true);
		comboBox_Zone.setToolTipText("Select your zone");
		comboBox_Host.setToolTipText("Select your host");
		comboBox_Host.setEditable(true);

		GroupLayout gl_contentPane = new GroupLayout(contentPanePanel);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane.createSequentialGroup()
										.addGap(269).addComponent(button_Login)
										.addGap(18).addComponent(button_Cancel)
										.addContainerGap(257, Short.MAX_VALUE))
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(81)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																Label_Password)
														.addComponent(
																Label_Username)
														.addComponent(
																Label_Port)
														.addComponent(
																label_Host)
														.addComponent(
																label_Zone)
														.addComponent(
																homeDirectory_Label)
														.addComponent(
																lblImagejCacheFolder))
										.addGap(26)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addComponent(
																				comboBox_Zone,
																				GroupLayout.PREFERRED_SIZE,
																				142,
																				GroupLayout.PREFERRED_SIZE)
																		.addContainerGap())
														.addGroup(
																gl_contentPane
																		.createParallelGroup(
																				Alignment.LEADING)
																		.addGroup(
																				gl_contentPane
																						.createSequentialGroup()
																						.addComponent(
																								HomeDirectory_CheckBox)
																						.addContainerGap())
																		.addGroup(
																				gl_contentPane
																						.createSequentialGroup()
																						.addGroup(
																								gl_contentPane
																										.createParallelGroup(
																												Alignment.LEADING)
																										.addComponent(
																												textField_Port,
																												64,
																												64,
																												64)
																										.addGroup(
																												gl_contentPane
																														.createSequentialGroup()
																														.addGroup(
																																gl_contentPane
																																		.createParallelGroup(
																																				Alignment.LEADING)
																																		.addComponent(
																																				textField_passwordField,
																																				GroupLayout.DEFAULT_SIZE,
																																				368,
																																				Short.MAX_VALUE)
																																		.addComponent(
																																				textbox_LoginId,
																																				GroupLayout.DEFAULT_SIZE,
																																				368,
																																				Short.MAX_VALUE)
																																		.addGroup(
																																				gl_contentPane
																																						.createSequentialGroup()
																																						.addGroup(
																																								gl_contentPane
																																										.createParallelGroup(
																																												Alignment.TRAILING,
																																												false)
																																										.addComponent(
																																												comboBox_Host,
																																												Alignment.LEADING,
																																												0,
																																												GroupLayout.DEFAULT_SIZE,
																																												Short.MAX_VALUE)
																																										.addComponent(
																																												textField_ImageJCacheFolderPath,
																																												Alignment.LEADING,
																																												GroupLayout.DEFAULT_SIZE,
																																												251,
																																												Short.MAX_VALUE))
																																						.addGap(18)
																																						.addComponent(
																																								btnChooseFolder)))
																														.addGap(10)))
																						.addGap(85))))));
		gl_contentPane
				.setVerticalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(54)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE,
																false)
														.addComponent(
																Label_Username)
														.addComponent(
																textbox_LoginId,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(17)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																Label_Password)
														.addComponent(
																textField_passwordField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																Label_Port)
														.addComponent(
																textField_Port,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																label_Zone)
														.addComponent(
																comboBox_Zone,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(15)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																label_Host)
														.addComponent(
																comboBox_Host,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																textField_ImageJCacheFolderPath,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lblImagejCacheFolder)
														.addComponent(
																btnChooseFolder))
										.addGap(15)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																HomeDirectory_CheckBox)
														.addComponent(
																homeDirectory_Label))
										.addGap(31)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																button_Login)
														.addComponent(
																button_Cancel))
										.addGap(51)));
		contentPanePanel.setLayout(gl_contentPane);
	}

	public void setVisibilityOfForm() {
		setContentPane(directoryContentsPane);
		validate();
		repaint();
		pack();
		setVisible(true);
	}

	private void mainWindowInit() {
		// irodsImagejInstance = new IrodsImageJBean();

		/* Setting iRODS file system */
		try {
			irodsFileSystem = IRODSFileSystem.instance();
			iplugin.setIrodsFileSystem(irodsFileSystem);
		} catch (JargonException e) {
			log.error("Error while retrieving irodsFileSystem" + e.getMessage());
		}

	}

	@SuppressWarnings("deprecation")
	private void loginMethod() {

		String username = textbox_LoginId.getText();
		char[] password = textField_passwordField.getPassword();
		String password_full = "";
		cacheDirectoryCreation();

		for (char chars : password)
			password_full += chars;
		int port = Integer.parseInt(textField_Port.getText());
		String host = comboBox_Host.getSelectedItem().toString();
		String zone = comboBox_Zone.getSelectedItem().toString();
		{
			try {
				if (HomeDirectory_CheckBox.isSelected()) {
					iplugin.setHomeDirectoryTheRootNode(true);
				}

				IRODSAccount irodsAccount = IrodsConnection.irodsConnection(
						username, password_full, zone, host, port);
				iplugin.setIrodsAccount(irodsAccount);

				if (iplugin.getIrodsAccount() != null) {
					irodsFileFactoryCreation();
				}

				if (null != irodsFileSystem) {
					IRODSSession iRODSSession = irodsFileSystem
							.getIrodsSession();
					iplugin.setiRODSSession(iRODSSession);
				} else {
					log.error("iRODSSession is null");
				}

				if (iplugin.getIrodsAccount() != null
						&& iplugin.getiRODSSession() != null) {
					iRODSFileSystemAOImpl = new IRODSFileSystemAOImpl(
							iplugin.getiRODSSession(),
							iplugin.getIrodsAccount());
					if (null != iRODSFileSystemAOImpl)
						iplugin.setiRODSFileSystemAOImpl(iRODSFileSystemAOImpl);
					else
						log.error("iRODSFileSystemAOImpl is null");
				}

				/*
				 * IRODSFileSystem irodsFileSystem= IRODSFileSystem.instance();
				 * UserAO userAccount = irodsFileSystem
				 * .getIRODSAccessObjectFactory().getUserAO (irodsAccount);
				 */

				List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = FileOperations
						.setIrodsFile(null, iplugin,
								iplugin.isHomeDirectoryTheRootNode());
				iplugin.setCollectionsUnderGivenAbsolutePath(collectionsUnderGivenAbsolutePath);
				directoryContentsPane = new DirectoryContentsWindow(iplugin);
				iplugin.setDirectoryContentsPane(directoryContentsPane);
				directoryContentsPane.init();
				directoryContentsPane.implementation();
				setVisibilityOfForm();
				show();
			}
			/* Exception when Username/Password is empty */
			catch (CatalogSQLException catalogSQLException) {
				log.error(catalogSQLException.getMessage(), catalogSQLException);
				JOptionPane.showMessageDialog(null,
						"Invalid Username or password!", "Error",
						JOptionPane.ERROR_MESSAGE);
				catalogSQLException.printStackTrace();
			}
			/* Exception when Username is invalid */
			catch (InvalidUserException invalidUserException) {
				log.error(invalidUserException.getMessage(),
						invalidUserException);
				JOptionPane.showMessageDialog(null, "Invalid Username!",
						"Error", JOptionPane.ERROR_MESSAGE);
				/*
				 * If user input wrong username the cursor will let user
				 * re-input it.
				 */
				getRootPane().setFocusable(false);
				textbox_LoginId.requestFocusInWindow();
				invalidUserException.printStackTrace();
			}

			/* Exception when password is invalid */
			catch (AuthenticationException authenticationException) {
				log.error(authenticationException.getMessage(),
						authenticationException);
				JOptionPane.showMessageDialog(null, "Invalid password!",
						"Error", JOptionPane.ERROR_MESSAGE);
				/*
				 * If user input wrong password the cursor will let user
				 * re-input it.
				 */
				getRootPane().setFocusable(false);
				textField_passwordField.requestFocusInWindow();
				authenticationException.printStackTrace();
			}
			/* Unknown Exception */
			catch (Exception unknownException) {
				log.error(unknownException.getMessage(), unknownException);
				if (unknownException.getLocalizedMessage().toString()
						.contains(Constants.ERROR_STRING_CONNECTION_REFUSED)) {
					JOptionPane.showMessageDialog(null,
							"Connection Refused - Server Down!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				if (unknownException.getLocalizedMessage().toString()
						.contains(Constants.ERROR_STRING_UNKNOWN_HOST)) {
					JOptionPane.showMessageDialog(null, "Unknown Host",
							"Error", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Unknown Error!",
							"Error", JOptionPane.ERROR_MESSAGE);
					log.error("Unknown Error: "
							+ unknownException.getLocalizedMessage());
				}

			}

		}
	}

	/**
	 * 
	 */
	private void cacheDirectoryCreation() {
		if (null != textField_ImageJCacheFolderPath.getText()) {
			boolean isDirectoryCreated = IrodsUtilities
					.createDirectoryIfDoesntExist(textField_ImageJCacheFolderPath
							.getText());
			if (!isDirectoryCreated) {
				log.info("isDirectoryCreated: " + isDirectoryCreated);
				JOptionPane.showMessageDialog(null,
						"Error while creating path!");
			} else {
				log.info("ImageJ cache folder path specified by user:"
						+ textField_ImageJCacheFolderPath.getText());
				iplugin.setImageJCacheFolder(textField_ImageJCacheFolderPath
						.getText());
			}
		}
	}

	private void irodsFileFactoryCreation() {
		try {
			iRODSFileFactory = TapasCoreFunctions
					.getIrodsAccountFileFactory(iplugin);
			iplugin.setiRODSFileFactory(iRODSFileFactory);
		} catch (JargonException e) {
			log.error("Error while creating irodsFileFactory" + e.getMessage());
		}
	}

	private boolean setPropertyFileDataToLoginPanel(Properties tapasProperties) {
		Boolean isSet = false;

		if (null != tapasProperties) {
			log.info("Tapas Properties: "
					+ tapasProperties.getProperty("login.username"));
			usernamePickedFromPropertyFiles = tapasProperties
					.getProperty(Constants.PROPERTY_USER_NAME);
			if (null != usernamePickedFromPropertyFiles) {
				textbox_LoginId.setText(usernamePickedFromPropertyFiles);
			}
			zonePickedFromPropertyFiles = tapasProperties
					.getProperty(Constants.PROPERTY_ZONE_NAME);
			if (null != zonePickedFromPropertyFiles) {
				String[] zoneNames = zonePickedFromPropertyFiles.split(",");
				for (int j = 0; j < zoneNames.length; j++) {
					/*
					 * comboBox_Zone.setModel(new DefaultComboBoxModel<String>(
					 * new String[] { zoneNames}));
					 */
					comboBox_Zone.addItem(zoneNames[j]);
				}
			}
			hostPickedFromPropertyFiles = tapasProperties
					.getProperty(Constants.PROPERTY_HOST_NAME);
			if (null != hostPickedFromPropertyFiles) {
				/*
				 * comboBox_Host.setModel(new DefaultComboBoxModel<String>( new
				 * String[] { hostPickedFromPropertyFiles }));
				 */
				String[] hostNames = hostPickedFromPropertyFiles.split(",");
				for (int i = 0; i < hostNames.length; i++) {
					comboBox_Host.addItem(hostNames[i]);
				}

			}
		}
		return isSet;
	}

	private void closeApplication(IPlugin iplugin) {

		System.exit(0);
		TapasCoreFunctions.closeIRODSConnections(iplugin);

	}
}