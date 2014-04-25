package org.bio5.irods.imagej.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IPlugin;
import org.bio5.irods.imagej.connection.IrodsConnection;
import org.bio5.irods.imagej.fileoperations.FileOperations;
import org.bio5.irods.imagej.utilities.Constants;
import org.bio5.irods.imagej.utilities.IrodsUtilities;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.exception.AuthenticationException;
import org.irods.jargon.core.exception.CatalogSQLException;
import org.irods.jargon.core.exception.InvalidUserException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 393011043100419159L;
	public IRODSFileSystem irodsFileSystem;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(IPlugin_OpenImage.class.getName());

	private JPanel contentPanePanel;
	private JTextField textbox_LoginId;
	private JPasswordField textField_passwordField;
	private JTextField textField_Port;
	private JTextField textField_Zone;
	private JTextField textField_Host;
	private DirectoryContentsWindow directoryContentsPane;
	private IPlugin irodsImagejInstance;
	private JCheckBox HomeDirectory_CheckBox;

	public JFileChooser localImageJFileChooser;
	private JTextField textField_ImageJCacheFolderPath;
	private IRODSFileSystemAOImpl iRODSFileSystemAOImpl;

	/**
	 * Launch the application.
	 */
	/**
	 * Create the frame.
	 */
	public MainWindow(IPlugin irodsImagej) {
		// super();
		this.irodsImagejInstance = irodsImagej;

		mainWindowInit();

		setTitle("iRODS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 682, 454);

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
				JOptionPane.showMessageDialog(null, "iRODS File Operations",
						"About iRODS ImageJ Plugin", 1);
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

		final JButton button_Login = new JButton("Login");
		button_Login.setToolTipText("Click to Login");
		button_Login.setEnabled(true);
		/*
		 * contentPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER
		 * ,0), button_Login);
		 * contentPane.getRootPane().getActionMap().put(button_Login, new
		 * AbstractAction(){ public void actionPerformed(ActionEvent ae) {
		 * button_Login.doClick(); System.out.println("Login button-clicked"); }
		 * });
		 */

		button_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = textbox_LoginId.getText();
				char[] password = textField_passwordField.getPassword();
				String password_full = "";
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
						irodsImagejInstance
								.setImageJCacheFolder(textField_ImageJCacheFolderPath
										.getText());
					}
				}

				for (char chars : password)
					password_full += chars;
				int port = Integer.parseInt(textField_Port.getText());
				String host = textField_Host.getText();
				String zone = textField_Zone.getText();
				{
					try {
						if (HomeDirectory_CheckBox.isSelected()) {
							irodsImagejInstance
									.setHomeDirectoryTheRootNode(true);
						}

						IRODSAccount irodsAccount = IrodsConnection
								.irodsConnection(username, password_full, zone,
										host, port);
						irodsImagejInstance.setIrodsAccount(irodsAccount);

						if (null != irodsFileSystem) {
							IRODSSession iRODSSession = irodsFileSystem
									.getIrodsSession();
							irodsImagejInstance.setiRODSSession(iRODSSession);
						} else {
							log.error("iRODSSession is null");
						}

						if (irodsImagejInstance.getIrodsAccount() != null
								&& irodsImagejInstance.getiRODSSession() != null) {
							iRODSFileSystemAOImpl = new IRODSFileSystemAOImpl(
									irodsImagejInstance.getiRODSSession(),
									irodsImagejInstance.getIrodsAccount());
							irodsImagejInstance
									.setiRODSFileSystemAOImpl(iRODSFileSystemAOImpl);
						}

						/*
						 * IRODSFileSystem irodsFileSystem=
						 * IRODSFileSystem.instance(); UserAO userAccount =
						 * irodsFileSystem
						 * .getIRODSAccessObjectFactory().getUserAO
						 * (irodsAccount);
						 */

						List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = FileOperations
								.setIrodsFile(null, irodsImagejInstance,
										irodsImagejInstance
												.isHomeDirectoryTheRootNode());
						irodsImagejInstance
								.setCollectionsUnderGivenAbsolutePath(collectionsUnderGivenAbsolutePath);
						directoryContentsPane = new DirectoryContentsWindow(
								irodsImagejInstance);
						irodsImagejInstance
								.setDirectoryContentsPane(directoryContentsPane);
						directoryContentsPane.init();
						directoryContentsPane.implementation();
						setVisibilityOfForm();
						show();
					}
					/* Exception when username/password is empty */
					catch (CatalogSQLException catalogSQLException) {
						log.error(catalogSQLException.getMessage(),
								catalogSQLException);
						JOptionPane.showMessageDialog(null,
								"Invalid Username or password!");
						catalogSQLException.printStackTrace();
					}
					/* Exception when username is invalid */
					catch (InvalidUserException invalidUserException) {
						log.error(invalidUserException.getMessage(),
								invalidUserException);
						JOptionPane
								.showMessageDialog(null, "Invalid Username!");
						invalidUserException.printStackTrace();
					}

					/* Exception when password is invalid */
					catch (AuthenticationException authenticationException) {
						log.error(authenticationException.getMessage(),
								authenticationException);
						JOptionPane
								.showMessageDialog(null, "Invalid password!");
						authenticationException.printStackTrace();
					} catch (Exception e1) {
						log.error(e1.getMessage(), e1);
						JOptionPane.showMessageDialog(null, "Unknown Error!");
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
		Label_Username.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel Label_Password = new JLabel("Password:");
		Label_Password.setHorizontalAlignment(SwingConstants.CENTER);

		textField_passwordField = new JPasswordField();
		textField_passwordField.setHorizontalAlignment(SwingConstants.LEFT);
		textField_passwordField.setToolTipText("Password");

		textField_Port = new JTextField();
		textField_Port.setText(Constants.PORT);
		textField_Port.setToolTipText("Port No.");
		textField_Port.setColumns(10);

		JLabel Label_Port = new JLabel("Port:");
		Label_Port.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel label_Zone = new JLabel("Zone:");
		label_Zone.setHorizontalAlignment(SwingConstants.CENTER);

		textField_Zone = new JTextField();
		textField_Zone.setText(Constants.ZONE);
		textField_Zone.setToolTipText("Zone");
		textField_Zone.setColumns(10);

		textField_Host = new JTextField();
		textField_Host.setHorizontalAlignment(SwingConstants.LEFT);
		textField_Host.setToolTipText("Host Address");
		textField_Host.setText(Constants.HOST);
		textField_Host.setColumns(10);

		JLabel label_Host = new JLabel("Host:");
		label_Host.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel homeDirectory_Label = new JLabel("File Selection:");

		HomeDirectory_CheckBox = new JCheckBox("Home Directory");

		textField_ImageJCacheFolderPath = new JTextField();
		textField_ImageJCacheFolderPath.setColumns(10);
		textField_ImageJCacheFolderPath.setText(irodsImagej
				.getImageJCacheFolder());

		JLabel lblImagejCacheFolder = new JLabel("ImageJ Cache Folder:");
		GroupLayout gl_contentPane = new GroupLayout(contentPanePanel);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane.createSequentialGroup()
										.addGap(269).addComponent(button_Login)
										.addGap(18).addComponent(button_Cancel)
										.addContainerGap(237, Short.MAX_VALUE))
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
																								textField_Zone,
																								64,
																								64,
																								64)
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
																																				Alignment.TRAILING)
																																		.addComponent(
																																				textField_Host,
																																				GroupLayout.DEFAULT_SIZE,
																																				378,
																																				Short.MAX_VALUE)
																																		.addComponent(
																																				textField_passwordField,
																																				GroupLayout.DEFAULT_SIZE,
																																				378,
																																				Short.MAX_VALUE)
																																		.addComponent(
																																				textbox_LoginId,
																																				GroupLayout.DEFAULT_SIZE,
																																				378,
																																				Short.MAX_VALUE)
																																		.addComponent(
																																				textField_ImageJCacheFolderPath,
																																				Alignment.LEADING,
																																				GroupLayout.DEFAULT_SIZE,
																																				378,
																																				Short.MAX_VALUE))
																														.addGap(10))))
																		.addGap(85)))));
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
																textField_Zone,
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
																textField_Host,
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
																lblImagejCacheFolder))
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
		} catch (JargonException e) {
			log.error("Error while retrieving irodsFileSystem" + e.getMessage());
		}
		irodsImagejInstance.setIrodsFileSystem(irodsFileSystem);

	}

	private void isEnterkeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			JOptionPane.showMessageDialog(null, "Enter key pressed!");
		}
	}
}