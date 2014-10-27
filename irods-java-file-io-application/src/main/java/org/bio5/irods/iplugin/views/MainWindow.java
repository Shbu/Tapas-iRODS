package org.bio5.irods.iplugin.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.ConnectException;
import java.util.List;
import java.util.Properties;

import javax.swing.GroupLayout;
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
	private static final long serialVersionUID = 393011043100419159L;
	public IRODSFileSystem irodsFileSystem;
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
	private IRODSFileFactory iRODSFileFactory = null;
	private String usernamePickedFromPropertyFiles = null;
	private String zonePickedFromPropertyFiles = null;
	private String hostPickedFromPropertyFiles = null;

	public MainWindow(IPlugin iPlugin) {
		this.iplugin = iPlugin;

		mainWindowInit();

		setTitle("Tapas");
		setDefaultCloseOperation(2);
		setBounds(100, 100, 682, 454);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu_File = new JMenu("File");
		mnNewMenu_File.setMnemonic('F');
		menuBar.add(mnNewMenu_File);

		JMenuItem mntmNewMenuItem_Open = new JMenuItem("Open");
		mntmNewMenuItem_Open.setAccelerator(KeyStroke.getKeyStroke(79, 2));

		mnNewMenu_File.add(mntmNewMenuItem_Open);

		JMenuItem mntmNewMenuItem_Exit = new JMenuItem("Exit");
		mntmNewMenuItem_Exit.setAccelerator(KeyStroke.getKeyStroke(115, 8));

		mntmNewMenuItem_Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainWindow.this.dispose();
			}
		});
		mnNewMenu_File.add(mntmNewMenuItem_Exit);
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('H');
		menuBar.add(mnHelp);
		JMenuItem mntm_About = new JMenuItem("About iPlugin");
		mntm_About.setAccelerator(KeyStroke.getKeyStroke(122, 0));
		mntm_About.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showMessageDialog(
								null,
								"Tapas V1.0 - Plugin for imagej to handle file operations with iRODS Data Servers",
								"About Tapas - ImageJ Plugin v1.0", 1);
			}
		});
		mnHelp.add(mntm_About);
		this.contentPanePanel = new JPanel();
		this.contentPanePanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		setContentPane(this.contentPanePanel);

		this.textbox_LoginId = new JTextField();
		this.textbox_LoginId.setHorizontalAlignment(2);
		this.textbox_LoginId.setToolTipText("User Id");
		this.textbox_LoginId.setColumns(13);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow.this.textbox_LoginId.requestFocus();
			}
		});
		JButton button_Login = new JButton("Login");
		button_Login.setToolTipText("Click to Login");
		button_Login.setEnabled(true);

		getRootPane().setDefaultButton(button_Login);

		this.textbox_LoginId.requestFocusInWindow();

		button_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.getRootPane().setFocusable(true);
				try {
					MainWindow.this.loginMethod();
				} catch (ConnectException connectException) {
					MainWindow.log.error(connectException.getMessage(),
							connectException);
					JOptionPane
							.showMessageDialog(
									null,
									"ConnectionException - Connection timed out: connect",
									"Error", 0);

					connectException.printStackTrace();
				}
				MainWindow.this.getRootPane().addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == 27) {
							try {
								MainWindow.this
										.closeApplication(MainWindow.this.iplugin);
							} catch (JargonException jargonException) {
								MainWindow.log
										.error("Error while closing application!"
												+ jargonException);

								JOptionPane.showMessageDialog(null,
										"Error while closing application!",
										"Error", 0);

								return;
							}
						}
					}
				});
			}
		});
		JButton button_Cancel = new JButton("Cancel");
		button_Cancel.setToolTipText("Click to close application");
		button_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					MainWindow.this.closeApplication(MainWindow.this.iplugin);
				} catch (JargonException jargonException) {
					MainWindow.log.error("Error while closing application!"
							+ jargonException);

					JOptionPane.showMessageDialog(null,
							"Error while closing application!", "Error", 0);

					return;
				}
			}
		});
		JLabel Label_Username = new JLabel("User Name:");
		Label_Username.setHorizontalAlignment(0);
		JLabel Label_Password = new JLabel("Password:");
		Label_Password.setHorizontalAlignment(0);

		this.textField_passwordField = new JPasswordField();
		this.textField_passwordField.setHorizontalAlignment(2);
		this.textField_passwordField.setToolTipText("Password:");

		this.textField_Port = new JTextField();
		this.textField_Port.setEditable(false);
		this.textField_Port.setText(Constants.PORT);
		this.textField_Port.setToolTipText("Port No.");
		this.textField_Port.setColumns(10);

		JLabel Label_Port = new JLabel("Port:");
		Label_Port.setHorizontalAlignment(0);

		JLabel label_Zone = new JLabel("Zone:");
		label_Zone.setHorizontalAlignment(0);

		JLabel label_Host = new JLabel("Host:");
		label_Host.setHorizontalAlignment(0);

		JLabel homeDirectory_Label = new JLabel("File Selection:");

		this.HomeDirectory_CheckBox = new JCheckBox("Home Directory");

		this.textField_ImageJCacheFolderPath = new JTextField();
		this.textField_ImageJCacheFolderPath
				.setToolTipText("Enter ImageJ Cache folder path");

		this.textField_ImageJCacheFolderPath.setColumns(10);
		this.textField_ImageJCacheFolderPath.setText(iPlugin
				.getImageJCacheFolder());

		JLabel lblImagejCacheFolder = new JLabel("ImageJ Cache Folder:");
		JButton btnChooseFolder = new JButton("Choose folder");
		btnChooseFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.fileChooserForImageJCacheFolder = new JFileChooser(
						IrodsUtilities.getUserHomeFolderFromSystemProperty());

				MainWindow.this.fileChooserForImageJCacheFolder
						.setFileSelectionMode(1);

				int returnVal = MainWindow.this.fileChooserForImageJCacheFolder
						.showOpenDialog(MainWindow.this);
				if (returnVal == 0) {
					MainWindow.this.selectedFileForImageJCacheFolder = MainWindow.this.fileChooserForImageJCacheFolder
							.getSelectedFile();

					MainWindow.log.info("Opening: "
							+ MainWindow.this.selectedFileForImageJCacheFolder
									.getAbsolutePath() + "." + "\n");

					MainWindow.this.iplugin
							.setImageJCacheFolder(MainWindow.this.selectedFileForImageJCacheFolder
									.getAbsolutePath());

					MainWindow.this.textField_ImageJCacheFolderPath
							.setText(MainWindow.this.iplugin
									.getImageJCacheFolder());
				} else {
					MainWindow.log
							.error("User cancelled file selection window: \n");
				}
			}
		});
		this.comboBox_Zone = new JComboBox();
		this.comboBox_Host = new JComboBox();

		Properties tapasProperties = IrodsUtilities.getTapasLoginConfiguration(
				"tapas.properties", Constants.IMAGEJ_CACHE_FOLDER);
		if (null != tapasProperties) {
			setPropertyFileDataToLoginPanel(tapasProperties);
			this.iplugin.setTapasProperties(tapasProperties);
		} else {
			log.error("tapas property file is null");
		}
		this.comboBox_Zone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				Object selected = comboBox.getSelectedItem();
				if (selected.toString().equals(Constants.ZONE_IPLANT)) {
					MainWindow.this.comboBox_Host
							.setSelectedItem("data.iplantcollaborative.org");
				}
				if (selected.toString().equals(Constants.ZONE_SPX)) {
					MainWindow.this.comboBox_Host
							.setSelectedItem("spxirods.dyndns.org");
				}
			}
		});
		this.comboBox_Zone.setEditable(true);
		this.comboBox_Zone.setToolTipText("Select your zone");
		this.comboBox_Host.setToolTipText("Select your host");
		this.comboBox_Host.setEditable(true);

		GroupLayout gl_contentPane = new GroupLayout(this.contentPanePanel);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_contentPane.createSequentialGroup()
										.addGap(269).addComponent(button_Login)
										.addGap(18).addComponent(button_Cancel)
										.addContainerGap(257, 32767))
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(81)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
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
																GroupLayout.Alignment.LEADING)
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addComponent(
																				this.comboBox_Zone,
																				-2,
																				142,
																				-2)
																		.addContainerGap())
														.addGroup(
																gl_contentPane
																		.createParallelGroup(
																				GroupLayout.Alignment.LEADING)
																		.addGroup(
																				gl_contentPane
																						.createSequentialGroup()
																						.addComponent(
																								this.HomeDirectory_CheckBox)
																						.addContainerGap())
																		.addGroup(
																				gl_contentPane
																						.createSequentialGroup()
																						.addGroup(
																								gl_contentPane
																										.createParallelGroup(
																												GroupLayout.Alignment.LEADING)
																										.addComponent(
																												this.textField_Port,
																												64,
																												64,
																												64)
																										.addGroup(
																												gl_contentPane
																														.createSequentialGroup()
																														.addGroup(
																																gl_contentPane
																																		.createParallelGroup(
																																				GroupLayout.Alignment.LEADING)
																																		.addComponent(
																																				this.textField_passwordField,
																																				-1,
																																				368,
																																				32767)
																																		.addComponent(
																																				this.textbox_LoginId,
																																				-1,
																																				368,
																																				32767)
																																		.addGroup(
																																				gl_contentPane
																																						.createSequentialGroup()
																																						.addGroup(
																																								gl_contentPane
																																										.createParallelGroup(
																																												GroupLayout.Alignment.TRAILING,
																																												false)
																																										.addComponent(
																																												this.comboBox_Host,
																																												GroupLayout.Alignment.LEADING,
																																												0,
																																												-1,
																																												32767)
																																										.addComponent(
																																												this.textField_ImageJCacheFolderPath,
																																												GroupLayout.Alignment.LEADING,
																																												-1,
																																												251,
																																												32767))
																																						.addGap(18)
																																						.addComponent(
																																								btnChooseFolder)))
																														.addGap(10)))
																						.addGap(85))))));

		gl_contentPane
				.setVerticalGroup(gl_contentPane
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(54)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE,
																false)
														.addComponent(
																Label_Username)
														.addComponent(
																this.textbox_LoginId,
																-2, -1, -2))
										.addGap(17)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																Label_Password)
														.addComponent(
																this.textField_passwordField,
																-2, -1, -2))
										.addGap(18)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																Label_Port)
														.addComponent(
																this.textField_Port,
																-2, -1, -2))
										.addGap(18)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																label_Zone)
														.addComponent(
																this.comboBox_Zone,
																-2, -1, -2))
										.addGap(15)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																label_Host)
														.addComponent(
																this.comboBox_Host,
																-2, -1, -2))
										.addGap(18)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																this.textField_ImageJCacheFolderPath,
																-2, -1, -2)
														.addComponent(
																lblImagejCacheFolder)
														.addComponent(
																btnChooseFolder))
										.addGap(15)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																this.HomeDirectory_CheckBox)
														.addComponent(
																homeDirectory_Label))
										.addGap(31)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																button_Login)
														.addComponent(
																button_Cancel))
										.addGap(51)));

		this.contentPanePanel.setLayout(gl_contentPane);
	}

	private void setVisibilityOfForm() {
		setContentPane(this.directoryContentsPane);
		this.directoryContentsPane.setPreferredSize(getPreferredSize());
		this.directoryContentsPane.setMinimumSize(getMinimumSize());
		validate();
		repaint();
		pack();
		setVisible(true);
	}

	public Dimension getMinimumSize() {
		return new Dimension(200, 100);
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	private void mainWindowInit() {
		try {
			this.irodsFileSystem = IRODSFileSystem.instance();
			this.iplugin.setIrodsFileSystem(this.irodsFileSystem);
		} catch (JargonException e) {
			log.error("Error while retrieving irodsFileSystem" + e.getMessage());
		}
	}

	private void loginMethod() throws ConnectException {
		String username = this.textbox_LoginId.getText();
		char[] password = this.textField_passwordField.getPassword();
		String password_full = "";
		cacheDirectoryCreation();
		for (char chars : password) {
			password_full = password_full + chars;
		}
		int port = Integer.parseInt(this.textField_Port.getText());
		String host = this.comboBox_Host.getSelectedItem().toString();
		String zone = this.comboBox_Zone.getSelectedItem().toString();
		try {
			if (this.HomeDirectory_CheckBox.isSelected()) {
				this.iplugin.setHomeDirectoryTheRootNode(true);
			}
			IRODSAccount irodsAccount = IrodsConnection.irodsConnection(
					username, password_full, zone, host, port);

			this.iplugin.setIrodsAccount(irodsAccount);
			if (this.iplugin.getIrodsAccount() != null) {
				irodsFileFactoryCreation();
			}
			if (null != this.irodsFileSystem) {
				IRODSSession iRODSSession = this.irodsFileSystem
						.getIrodsSession();

				this.iplugin.setiRODSSession(iRODSSession);
			} else {
				log.error("iRODSSession is null");
			}
			if ((this.iplugin.getIrodsAccount() != null)
					&& (this.iplugin.getiRODSSession() != null)) {
				this.iRODSFileSystemAOImpl = new IRODSFileSystemAOImpl(
						this.iplugin.getiRODSSession(),
						this.iplugin.getIrodsAccount());
				if (null != this.iRODSFileSystemAOImpl) {
					this.iplugin
							.setiRODSFileSystemAOImpl(this.iRODSFileSystemAOImpl);
				} else {
					log.error("iRODSFileSystemAOImpl is null");
				}
			}
			List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = FileOperations
					.setIrodsFile(null, this.iplugin,
							this.iplugin.isHomeDirectoryTheRootNode());

			this.iplugin
					.setCollectionsUnderGivenAbsolutePath(collectionsUnderGivenAbsolutePath);
			this.directoryContentsPane = new DirectoryContentsWindow(
					this.iplugin);
			this.iplugin.setDirectoryContentsPane(this.directoryContentsPane);
			this.directoryContentsPane.init();
			this.directoryContentsPane.implementation();
			setVisibilityOfForm();
			show();
		} catch (CatalogSQLException catalogSQLException) {
			log.error(catalogSQLException.getMessage(), catalogSQLException);
			JOptionPane.showMessageDialog(null,
					"Invalid Username or password!", "Error", 0);

			catalogSQLException.printStackTrace();
		} catch (InvalidUserException invalidUserException) {
			log.error(invalidUserException.getMessage(), invalidUserException);

			JOptionPane
					.showMessageDialog(null, "Invalid Username!", "Error", 0);

			getRootPane().setFocusable(false);
			this.textbox_LoginId.requestFocusInWindow();
			invalidUserException.printStackTrace();
		} catch (AuthenticationException authenticationException) {
			log.error(authenticationException.getMessage(),
					authenticationException);

			JOptionPane
					.showMessageDialog(null, "Invalid password!", "Error", 0);

			getRootPane().setFocusable(false);
			this.textField_passwordField.requestFocusInWindow();
			authenticationException.printStackTrace();
		} catch (Exception unknownException) {
			log.error(unknownException.getMessage(), unknownException);
			if (unknownException.getLocalizedMessage().toString()
					.contains(Constants.ERROR_STRING_CONNECTION_REFUSED)) {
				JOptionPane.showMessageDialog(null,
						"Connection Refused - Server Down!", "Error", 0);
			}
			if (unknownException.getLocalizedMessage().toString()
					.contains(Constants.ERROR_STRING_UNKNOWN_HOST)) {
				JOptionPane.showMessageDialog(null, "Unknown Host", "Error", 0);
			} else {
				JOptionPane.showMessageDialog(null, "Unknown Error!", "Error",
						0);

				log.error("Unknown Error: "
						+ unknownException.getLocalizedMessage());
			}
		}
	}

	private void cacheDirectoryCreation() {
		if (null != this.textField_ImageJCacheFolderPath.getText()) {
			boolean isDirectoryCreated = IrodsUtilities
					.createDirectoryIfDoesntExist(this.textField_ImageJCacheFolderPath
							.getText());
			if (!isDirectoryCreated) {
				log.info("isDirectoryCreated: " + isDirectoryCreated);
				JOptionPane.showMessageDialog(null,
						"Error while creating path!");
			} else {
				log.info("ImageJ cache folder path specified by user:"
						+ this.textField_ImageJCacheFolderPath.getText());

				this.iplugin
						.setImageJCacheFolder(this.textField_ImageJCacheFolderPath
								.getText());
			}
		} else {
			log.error("textField_ImageJCacheFolderPath.getText() is null");
		}
	}

	private void irodsFileFactoryCreation() {
		try {
			this.iRODSFileFactory = TapasCoreFunctions
					.getIrodsAccountFileFactory(this.iplugin);

			this.iplugin.setiRODSFileFactory(this.iRODSFileFactory);
		} catch (JargonException jargonException) {
			log.error("Error while creating irodsFileFactory"
					+ jargonException.getMessage());
		}
	}

	private boolean setPropertyFileDataToLoginPanel(Properties tapasProperties) {
		Boolean isSet = Boolean.valueOf(false);
		if (null != tapasProperties) {
			log.info("Tapas Properties: "
					+ tapasProperties.getProperty("login.username"));

			this.usernamePickedFromPropertyFiles = tapasProperties
					.getProperty("login.username");
			if (null != this.usernamePickedFromPropertyFiles) {
				this.textbox_LoginId
						.setText(this.usernamePickedFromPropertyFiles);
			}
			this.zonePickedFromPropertyFiles = tapasProperties
					.getProperty("login.zone");
			if (null != this.zonePickedFromPropertyFiles) {
				String[] zoneNames = this.zonePickedFromPropertyFiles
						.split(",");
				for (int j = 0; j < zoneNames.length; j++) {
					this.comboBox_Zone.addItem(zoneNames[j]);
				}
			}
			this.hostPickedFromPropertyFiles = tapasProperties
					.getProperty("login.host");
			if (null != this.hostPickedFromPropertyFiles) {
				String[] hostNames = this.hostPickedFromPropertyFiles
						.split(",");
				for (int i = 0; i < hostNames.length; i++) {
					this.comboBox_Host.addItem(hostNames[i]);
				}
			} else {
				log.error("hostPickedFromPropertyFiles is null");
			}
		}
		return isSet.booleanValue();
	}

	private void closeApplication(IPlugin iplugin) throws JargonException {
		dispose();
		if (null != iplugin) {
			TapasCoreFunctions.closeIRODSConnections(iplugin);
		}
	}
}
