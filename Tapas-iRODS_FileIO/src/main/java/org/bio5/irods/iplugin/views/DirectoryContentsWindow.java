package org.bio5.irods.iplugin.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.bio5.irods.iplugin.exception.IpluginException;
import org.bio5.irods.iplugin.fileoperations.FileOperations;
import org.bio5.irods.iplugin.fileoperations.GetFileFromIrodsSwingWorker;
import org.bio5.irods.iplugin.fileoperations.PutFileToIrodsSwingWorker;
import org.bio5.irods.iplugin.fileoperations.RetrieveInternalNodesSwingWorker;
import org.bio5.irods.iplugin.listeners.MyTreeModelListener;
import org.bio5.irods.iplugin.services.IPluginConfigurationServiceImpl;
import org.bio5.irods.iplugin.swingworkers.ObjectDetailsSwingWorker;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsPropertiesConstruction;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.domain.ObjStat;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class DirectoryContentsWindow extends JSplitPane implements
		TreeWillExpandListener {
	private static final long serialVersionUID = 722996165620904921L;
	private IRODSFileSystem irodsFileSystem;
	private DataTransferOperations dataTransferOperationsAO;
	private IRODSFileFactory iRODSFileFactory;
	private String selectedNodeInTreeForDoubleClick;
	private String selectedNodeInTreeForSingleClick;
	private GetFileFromIrodsSwingWorker getFile;
	private PutFileToIrodsSwingWorker putFile;
	private JProgressBar progressBar;
	private JFileChooser chooser;
	private DefaultTreeModel treeModel;
	private JTree userDirectoryTree;
	private DefaultMutableTreeNode homeNode;
	private DefaultMutableTreeNode accountNode;
	private JViewport viewport;
	private JScrollPane scrollPane;
	private IRODSAccount irodsAccount;
	private TransferControlBlock transferControlBlock;
	private IPlugin iPlugin;
	private JLabel jTextField_sourceFile;
	private JLabel jTextField_destinationPath;
	private JButton jButton_saveToIrodsServer;
	private IrodsPropertiesConstruction irodsPropertiesConstruction;
	private JTable table;
	private JLabel label_ProgressBar_BytesTrasferredOutofTotalFileSize;
	private String imageJCacheFolder;
	private Long imageJCacheFolderSize;
	private MainWindow mainWindowInstance;
	private TreePath[] treePaths;
	private JButton jButton_download;
	private JButton jButton_cancelTransaction;
	private String multiSelected;
	private JTabbedPane tabbedPane;
	static Logger log = Logger.getLogger(DirectoryContentsWindow.class
			.getName());

	public DirectoryContentsWindow(IPlugin iPlugin) throws JargonException,
			MalformedURLException {
		this.iPlugin = iPlugin;

		setOrientation(1);
	}

	public DefaultTreeModel getTreeModel() {
		return this.treeModel;
	}

	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public void init() throws JargonException {
		this.irodsAccount = this.iPlugin.getIrodsAccount();
		this.iRODSFileFactory = this.iPlugin.getiRODSFileFactory();

		this.homeNode = new DefaultMutableTreeNode(Constants.HOME_STRING);
		if (!this.iPlugin.isHomeDirectoryTheRootNode()) {
			this.accountNode = new DefaultMutableTreeNode(this.iPlugin
					.getIrodsAccount().getUserName());

			this.homeNode.add(this.accountNode);
			this.iPlugin.setRootTreeNodeForDirectoryContents(this.accountNode);
			this.treeModel = new DefaultTreeModel(this.accountNode, true);
		} else {
			this.iPlugin.setRootTreeNodeForDirectoryContents(this.homeNode);
			this.treeModel = new DefaultTreeModel(this.homeNode, true);
		}
		this.treeModel.addTreeModelListener(new MyTreeModelListener());
		this.iPlugin.setTreeModel(this.treeModel);

		this.progressBar = new JProgressBar();
		this.progressBar.setStringPainted(true);
		this.progressBar.setToolTipText("Progress of action");
		this.iPlugin.setJprogressbar(this.progressBar);

		this.label_ProgressBar_BytesTrasferredOutofTotalFileSize = new JLabel(
				" Progress:");

		this.label_ProgressBar_BytesTrasferredOutofTotalFileSize
				.setToolTipText(" Progress: bytesTransferred/Total File Size in Bytes");

		this.label_ProgressBar_BytesTrasferredOutofTotalFileSize
				.setBorder(new LineBorder(new Color(0, 0, 0)));

		this.irodsFileSystem = IRODSFileSystem.instance();
		this.iPlugin.setIrodsFileSystem(this.irodsFileSystem);

		this.dataTransferOperationsAO = this.irodsFileSystem
				.getIRODSAccessObjectFactory().getDataTransferOperations(
						this.irodsAccount);

		this.scrollPane = new JScrollPane();
		this.viewport = this.scrollPane.getViewport();
		this.iPlugin.setScrollPane(this.scrollPane);
		this.iPlugin.setViewport(this.viewport);

		this.jTextField_sourceFile = new JLabel("Local file");
		this.jTextField_destinationPath = new JLabel("Destination");
		this.jButton_saveToIrodsServer = new JButton("Save to iRODS Server");

		this.irodsPropertiesConstruction = new IrodsPropertiesConstruction();
		this.transferControlBlock = this.irodsPropertiesConstruction
				.constructHighPerformanceTransferControlBlockFromJargonProperties(this.iPlugin);

		this.iPlugin.setTransferControlBlock(this.transferControlBlock);

		this.irodsPropertiesConstruction
				.constructIrodsTransferStatusCallbackListener(this.iPlugin);
		try {
			IPluginConfigurationServiceImpl iPluginConfigurationService = TapasCoreFunctions
					.createConfigurationServiceForTapasTransactions(this.iPlugin);
			if (null != iPluginConfigurationService) {
				this.iPlugin
						.setiPluginConfigurationService(iPluginConfigurationService);
			} else {
				log.error("iPluginConfigurationService is null");
			}
		} catch (IpluginException e) {
			log.error("Error while setting ConfigurationServices for Tapas Transactions:"
					+ e.getMessage());
		}
		this.imageJCacheFolder = this.iPlugin.getImageJCacheFolder();
		log.info("ImageJ cache folder: " + this.iPlugin.getImageJCacheFolder());
		if ((null != this.imageJCacheFolder) && ("" != this.imageJCacheFolder)) {
			File cacheFolder = new File(this.imageJCacheFolder);
			this.imageJCacheFolderSize = Long.valueOf(IrodsUtilities
					.getFolderSize(cacheFolder));
			log.info("Cache folder size:"
					+ FileUtils
							.byteCountToDisplaySize(this.imageJCacheFolderSize
									.longValue()));
		}
		if (null != this.iPlugin.getMainWindow()) {
			this.mainWindowInstance = this.iPlugin.getMainWindow();
		}
		setVisible(true);
	}

	public void implementation() {
		if (null != this.iPlugin.getCollectionsUnderGivenAbsolutePath()) {
			List<CollectionAndDataObjectListingEntry> listOfCollectionsUnderGivenAbsolutePath = this.iPlugin
					.getCollectionsUnderGivenAbsolutePath();

			parseDirectoryContentsUsingList(
					listOfCollectionsUnderGivenAbsolutePath,
					this.iPlugin.getRootTreeNodeForDirectoryContents());
		} else {
			log.error("File directory is empty");
		}
		addNavigateOptionToMenuBar();

		this.tabbedPane = new JTabbedPane(1);

		this.tabbedPane.setPreferredSize(new Dimension(200, 200));

		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent
						.getSource();

				int index = sourceTabbedPane.getSelectedIndex();
				DirectoryContentsWindow.log
						.info("CurrentActiveTabUnderJTabbedPane : "
								+ sourceTabbedPane.getTitleAt(index));

				String currentActiveTabUnderJTabbedPane = sourceTabbedPane
						.getTitleAt(index);

				DirectoryContentsWindow.this.iPlugin
						.setCurrentActiveTabUnderJTabbedPane(currentActiveTabUnderJTabbedPane);
			}
		};
		this.tabbedPane.addChangeListener(changeListener);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(150, 200));
		this.tabbedPane.addTab("File Operations", null, panel, null);

		JButton jButton_selectLocalFile = new JButton("Select local file");

		jButton_selectLocalFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DirectoryContentsWindow.this.chooser = new JFileChooser();
				DirectoryContentsWindow.this.chooser.setFileSelectionMode(2);
				int option = DirectoryContentsWindow.this.chooser
						.showOpenDialog(DirectoryContentsWindow.this);
				if (option == 0) {
					DirectoryContentsWindow.this.jTextField_sourceFile.setText(DirectoryContentsWindow.this.chooser
							.getSelectedFile() != null ? DirectoryContentsWindow.this.chooser
							.getSelectedFile().getAbsolutePath()
							: "nothing is selected");
				} else {
					DirectoryContentsWindow.this.jTextField_sourceFile
							.setName("File selection canceled !");
				}
			}
		});
		this.jButton_download = new JButton("Download files");
		this.jButton_download.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < DirectoryContentsWindow.this.treePaths.length; i++) {
					DirectoryContentsWindow.this.iPlugin.getJprogressbar()
							.setValue(0);
					DirectoryContentsWindow.this.multiSelected = IrodsUtilities
							.getJtreeSelection(DirectoryContentsWindow.this.treePaths[i]);

					DirectoryContentsWindow.this.getFile = new GetFileFromIrodsSwingWorker(
							DirectoryContentsWindow.this.iRODSFileFactory,
							DirectoryContentsWindow.this.multiSelected,
							DirectoryContentsWindow.this.iPlugin,
							DirectoryContentsWindow.this.iPlugin
									.getJprogressbar());

					DirectoryContentsWindow.this.getFile.execute();
				}
				DirectoryContentsWindow.this.jButton_download.setEnabled(false);

				DirectoryContentsWindow.this.iPlugin
						.getCancelTransaction_JButton().setEnabled(true);
				DirectoryContentsWindow.log
						.info("Cancel Transaction button is enabled");

				DirectoryContentsWindow.this.iPlugin
						.setCancelGetTransaction(true);
				DirectoryContentsWindow.this.iPlugin
						.setCancelPutTransaction(false);
			}
		});
		this.jButton_cancelTransaction = new JButton("Cancel Transactions");
		this.jButton_cancelTransaction.setEnabled(false);
		this.iPlugin
				.setCancelTransaction_JButton(this.jButton_cancelTransaction);
		this.jButton_cancelTransaction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DirectoryContentsWindow.log
						.info("Cancel Transaction button is clicked!");
				if (DirectoryContentsWindow.this.iPlugin
						.isCancelGetTransaction()) {
					DirectoryContentsWindow.log
							.info("Get Transaction is cancelled: "
									+ DirectoryContentsWindow.this.getFile
											.cancel(true));
				}
				if (DirectoryContentsWindow.this.iPlugin
						.isCancelPutTransaction()) {
					DirectoryContentsWindow.log
							.info("Put Transaction is cancelled: "
									+ DirectoryContentsWindow.this.putFile
											.cancel(true));
				}
				DirectoryContentsWindow.this.jButton_cancelTransaction
						.setEnabled(false);
				DirectoryContentsWindow.log
						.info("Cancel Transaction button is disabled");
			}
		});
		this.jTextField_sourceFile.setEnabled(false);
		JButton jButton_selectDestination = new JButton("Select Destination");
		jButton_selectDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent destinationButtonActionEvent) {
				String destinationFolderPath = DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick;
				DirectoryContentsWindow.this.jTextField_destinationPath
						.setText(destinationFolderPath);
				DirectoryContentsWindow.this.jButton_saveToIrodsServer
						.setEnabled(true);
			}
		});
		JButton btnDownload = new JButton("Download");
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		this.jTextField_destinationPath.setEnabled(false);

		this.jButton_download.setEnabled(false);

		this.jButton_saveToIrodsServer.setEnabled(false);
		this.jButton_saveToIrodsServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					implementSaveButton();
				} catch (JargonException jargonException) {
					DirectoryContentsWindow.log.error(jargonException
							.getMessage());
					jargonException.printStackTrace();
				}
			}

			private void implementSaveButton() throws JargonException {
				DirectoryContentsWindow.log
						.info("Save to iRODS Server - Button Clicked");
				String sourceFilePath = null;
				String destinationFilePath = null;
				String targetResourceName = "";
				targetResourceName = DirectoryContentsWindow.this.iPlugin
						.getIrodsAccount().getDefaultStorageResource();

				File sourceLocalfile = null;
				IRODSFile destinaitonIrodsFile = null;
				if ((DirectoryContentsWindow.this.chooser.getSelectedFile()
						.getAbsolutePath() != null)
						&& (DirectoryContentsWindow.this.chooser
								.getSelectedFile().getAbsolutePath() != "")) {
					sourceFilePath = DirectoryContentsWindow.this.chooser
							.getSelectedFile().getAbsolutePath();

					sourceLocalfile = new File(sourceFilePath);
					if ((DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick != null)
							&& (DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick != "")) {
						DirectoryContentsWindow.log
								.info("destination path || selectedNodeInTreeForSingleClick"
										+ DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick);
						if (null != DirectoryContentsWindow.this.iPlugin
								.getCustomPath()) {
							String customPath = DirectoryContentsWindow.this.iPlugin
									.getCustomPath();
							if (null != customPath) {
								String[] customPathTokens = IrodsUtilities
										.getStringTokensForGivenURI(customPath);

								String newCustomPathAfterTokenizing = "";
								for (int i = 0; i < customPathTokens.length - 1; i++) {
									newCustomPathAfterTokenizing = newCustomPathAfterTokenizing
											+ IrodsUtilities.getPathSeperator()
											+ customPathTokens[i];
								}
								DirectoryContentsWindow.log
										.info("newCustomPathAfterTokenizing: "
												+ newCustomPathAfterTokenizing);

								newCustomPathAfterTokenizing = newCustomPathAfterTokenizing
										+ DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick;
								destinationFilePath = newCustomPathAfterTokenizing;
							}
						}
						if ((DirectoryContentsWindow.this.iPlugin
								.isHomeDirectoryTheRootNode())
								&& (null == DirectoryContentsWindow.this.iPlugin
										.getCustomPath())) {
							destinationFilePath = TapasCoreFunctions
									.getRootDirectoryPath(DirectoryContentsWindow.this.iPlugin)
									+ DirectoryContentsWindow.this.jTextField_destinationPath
											.getText();

							DirectoryContentsWindow.log
									.info("Destination Path if home directory is checked:"
											+ destinationFilePath);
						}
						if ((!DirectoryContentsWindow.this.iPlugin
								.isHomeDirectoryTheRootNode())
								&& (null == DirectoryContentsWindow.this.iPlugin
										.getCustomPath())) {
							destinationFilePath = TapasCoreFunctions
									.getHomeDirectoryPath(DirectoryContentsWindow.this.iPlugin)
									+ DirectoryContentsWindow.this.jTextField_destinationPath
											.getText();

							DirectoryContentsWindow.log
									.info("Destination Path if home directory is not checked:"
											+ destinationFilePath);
						}
						destinaitonIrodsFile = DirectoryContentsWindow.this.iRODSFileFactory
								.instanceIRODSFile(destinationFilePath);

						DirectoryContentsWindow.log
								.info("sourceLocalfile absolute path: "
										+ sourceLocalfile.getAbsolutePath()
										+ "\n"
										+ "destinaitonIrodsFile absolutepath: "
										+ destinaitonIrodsFile
												.getAbsoluteFile());
						try {
							if ((null != DirectoryContentsWindow.this.iPlugin)
									&& (null != sourceLocalfile)
									&& (null != destinaitonIrodsFile)
									&& (null != targetResourceName)) {
								DirectoryContentsWindow.this.putFile = new PutFileToIrodsSwingWorker(
										DirectoryContentsWindow.this.iPlugin,
										sourceLocalfile, destinaitonIrodsFile,
										targetResourceName);

								DirectoryContentsWindow.this.putFile.execute();
								DirectoryContentsWindow.log
										.info("PutFile operation is executed!");

								DirectoryContentsWindow.this.iPlugin
										.getCancelTransaction_JButton()
										.setEnabled(true);

								DirectoryContentsWindow.log
										.info("Cancel Transaction button is enabled");

								DirectoryContentsWindow.this.iPlugin
										.setCancelPutTransaction(true);
								DirectoryContentsWindow.this.iPlugin
										.setCancelGetTransaction(false);
							}
						} catch (Exception exception) {
							DirectoryContentsWindow.log.error(exception
									.getMessage());
							JOptionPane.showMessageDialog(null,
									exception.getMessage());
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "Source is empty!");
					DirectoryContentsWindow.log.error("Source is empty!");
				}
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_panel.createParallelGroup(
												GroupLayout.Alignment.LEADING)
												.addComponent(
														this.iPlugin
																.getJprogressbar(),
														-1, 217, 32767)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGroup(
																		gl_panel.createParallelGroup(
																				GroupLayout.Alignment.LEADING)
																				.addComponent(
																						jButton_selectLocalFile)
																				.addComponent(
																						jButton_selectDestination))
																.addGap(27)
																.addGroup(
																		gl_panel.createParallelGroup(
																				GroupLayout.Alignment.LEADING)
																				.addComponent(
																						this.jTextField_destinationPath)
																				.addComponent(
																						this.jTextField_sourceFile)))
												.addComponent(
														this.jButton_saveToIrodsServer)
												.addComponent(
														this.jButton_download)
												.addComponent(
														this.jButton_cancelTransaction))
								.addContainerGap()));

		gl_panel.setVerticalGroup(gl_panel
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(this.iPlugin.getJprogressbar(),
										-2, -1, -2)
								.addGap(59)
								.addGroup(
										gl_panel.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
												.addComponent(
														jButton_selectLocalFile)
												.addComponent(
														this.jTextField_sourceFile))
								.addGap(18)
								.addGroup(
										gl_panel.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
												.addComponent(
														jButton_selectDestination)
												.addComponent(
														this.jTextField_destinationPath))
								.addGap(18)
								.addComponent(this.jButton_saveToIrodsServer)
								.addGap(18).addComponent(this.jButton_download)
								.addGap(18)
								.addComponent(this.jButton_cancelTransaction)
								.addContainerGap(220, 32767)));

		panel.setLayout(gl_panel);

		JPanel panel_1 = new JPanel();
		this.tabbedPane.addTab("File Information", null, panel_1, null);

		this.table = new JTable();
		this.table.setEnabled(true);
		this.table.setIntercellSpacing(new Dimension(5, 5));
		this.table.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		this.table.setToolTipText("File Information");

		this.table.setModel(new DefaultTableModel(new Object[][] {
				{ " Absolute Path", null }, { " Object Size", null },
				{ " Created Date", null }, { " Modified Date", null },
				{ " Data Id", null }, { " Object Type", null },
				{ " File Checksum", null }, { " Owner Name", null },
				{ " Owner Zone", null }, { " Chache Dirty", null } },
				new String[] { "Field", "Information" }));

		this.table.getColumnModel().getColumn(0).setPreferredWidth(150);
		this.table.getColumnModel().getColumn(0).setMinWidth(100);
		this.table.setRowHeight(20);
		this.table.getColumnModel().getColumn(1).setPreferredWidth(300);
		this.table.getColumnModel().getColumn(1).setMinWidth(200);
		panel_1.add(this.table);

		this.iPlugin.getDirectoryContentsPane().setRightComponent(
				this.tabbedPane);
		this.iPlugin.getDirectoryContentsPane().setLeftComponent(
				this.scrollPane);

		HierarchyListener hierarchyListener = new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent event) {
				long flags = event.getChangeFlags();
				if ((flags & 0x4) == 4L) {
					DirectoryContentsWindow.this.iPlugin
							.getDirectoryContentsPane()
							.setDividerLocation(0.5D);
				}
			}
		};
		constructUserDirectoryTree(this.iPlugin);
		addMouseListenerForUserDirectoryTree();

		this.userDirectoryTree.setShowsRootHandles(true);
		this.userDirectoryTree.setEditable(false);
		this.userDirectoryTree.setVisible(true);
		this.viewport.add(this.userDirectoryTree);
	}

	private void addNavigateOptionToMenuBar() {
		JMenuBar mainWindowMenubar = this.mainWindowInstance.getJMenuBar();
		JMenu mnNewMenu_File = new JMenu("Navigate");
		mnNewMenu_File.setMnemonic('N');
		mainWindowMenubar.add(mnNewMenu_File, 1);

		JMenuItem mntmNewMenuItem_Home = new JMenuItem("Home");
		mntmNewMenuItem_Home.setAccelerator(KeyStroke.getKeyStroke(72, 2));

		mnNewMenu_File.add(mntmNewMenuItem_Home);

		JMenuItem mntmNewMenuItem_Root = new JMenuItem("Root Folder");
		mntmNewMenuItem_Root.setAccelerator(KeyStroke.getKeyStroke(82, 2));

		mnNewMenu_File.add(mntmNewMenuItem_Root);

		JMenuItem mntmNewMenuItem_Custom = new JMenuItem("Custom Folder");
		mntmNewMenuItem_Custom.setAccelerator(KeyStroke.getKeyStroke(67, 2));

		mnNewMenu_File.add(mntmNewMenuItem_Custom);

		mntmNewMenuItem_Custom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String customPath = (String) JOptionPane
						.showInputDialog(
								null,
								"Please input the folder you want to show in file tree:",
								"Custom Folder", -1, null, null, null);

				DirectoryContentsWindow.log
						.info("custom Path before calling setIrodsFile in FileOperations: "
								+ customPath);

				List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;
				try {
					collectionsUnderGivenAbsolutePath = FileOperations
							.setIrodsFile(customPath,
									DirectoryContentsWindow.this.iPlugin, false);
				} catch (SocketTimeoutException socketTimeoutException) {
					DirectoryContentsWindow.log
							.error("SocketTimeoutException: "
									+ socketTimeoutException.getMessage());

					JOptionPane
							.showMessageDialog(
									null,
									"Data server timeout exception. Please restart application!",
									"Error", 0);

					return;
				} catch (JargonException jargonException) {
					DirectoryContentsWindow.log
							.error("Error while calling setIrodsFile in FileOperations"
									+ jargonException.getMessage());
				}
				if (null != collectionsUnderGivenAbsolutePath) {
					DirectoryContentsWindow.this.iPlugin
							.setCollectionsUnderGivenAbsolutePath(collectionsUnderGivenAbsolutePath);
					DirectoryContentsWindow.this.iPlugin
							.setCustomPath(customPath);
				} else {
					JOptionPane
							.showMessageDialog(
									null,
									"Invalid Path! Please give complete path (Ex: /<zone>/home/<username>/<folders>)",
									"Error", 0);

					return;
				}
				DefaultMutableTreeNode customNode = null;
				if ((customPath.contains("\\")) || (customPath.contains("/"))) {
					String[] subPath = null;
					if (customPath.contains("\\")) {
						subPath = customPath.split("\\");
					}
					if (customPath.contains("/")) {
						subPath = customPath.split("/");
					}
					String customPathName = subPath[(subPath.length - 1)];
					DirectoryContentsWindow.log
							.info("customPathName before creating node: "
									+ customPathName);

					customNode = new DefaultMutableTreeNode(customPathName);
				} else {
					DirectoryContentsWindow.log
							.info("customPathName before creating node: "
									+ customPath);

					customNode = new DefaultMutableTreeNode(customPath);
				}
				DirectoryContentsWindow.this.iPlugin
						.setRootTreeNodeForDirectoryContents(customNode);
				DirectoryContentsWindow.this.iPlugin.getTreeModel().setRoot(
						customNode);

				List<CollectionAndDataObjectListingEntry> listOfCollectionsUnderGivenAbsolutePath = DirectoryContentsWindow.this.iPlugin
						.getCollectionsUnderGivenAbsolutePath();

				DirectoryContentsWindow.this.parseDirectoryContentsUsingList(
						listOfCollectionsUnderGivenAbsolutePath,
						DirectoryContentsWindow.this.iPlugin
								.getRootTreeNodeForDirectoryContents());

				DirectoryContentsWindow.this
						.constructUserDirectoryTree(DirectoryContentsWindow.this.iPlugin);
				DirectoryContentsWindow.this
						.addMouseListenerForUserDirectoryTree();
				DirectoryContentsWindow.this.userDirectoryTree
						.setShowsRootHandles(true);
				DirectoryContentsWindow.this.userDirectoryTree
						.setEditable(false);
				DirectoryContentsWindow.this.userDirectoryTree.setVisible(true);
				DirectoryContentsWindow.this.viewport
						.add(DirectoryContentsWindow.this.userDirectoryTree);
			}
		});
	}

	private void addMouseListenerForUserDirectoryTree() {
		this.userDirectoryTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					int n = JOptionPane.showConfirmDialog(null,
							"Would you want to download it?", "Make sure", 0);
					if (n == 0) {
						DirectoryContentsWindow.this.iPlugin.getJprogressbar()
								.setValue(0);
						DirectoryContentsWindow.log
								.info("tree path after double click"
										+ DirectoryContentsWindow.this.selectedNodeInTreeForDoubleClick);

						DirectoryContentsWindow.log
								.info("double click selection: "
										+ DirectoryContentsWindow.this.selectedNodeInTreeForDoubleClick);

						DirectoryContentsWindow.this.selectedNodeInTreeForDoubleClick = IrodsUtilities
								.getJtreeSelection(
										mouseEvent,
										DirectoryContentsWindow.this.userDirectoryTree);
						if ((null != DirectoryContentsWindow.this.selectedNodeInTreeForDoubleClick)
								&& (DirectoryContentsWindow.this.selectedNodeInTreeForDoubleClick != "")) {
							DirectoryContentsWindow.log
									.info("selectedNodeInTreeForDoubleClick string is "
											+ DirectoryContentsWindow.this.selectedNodeInTreeForDoubleClick);

							DirectoryContentsWindow.this.iPlugin
									.setSelectedNodeInTreeForDoubleClick(DirectoryContentsWindow.this.selectedNodeInTreeForDoubleClick);
							DirectoryContentsWindow.this.getFile = new GetFileFromIrodsSwingWorker(
									DirectoryContentsWindow.this.iRODSFileFactory,
									DirectoryContentsWindow.this.selectedNodeInTreeForDoubleClick,
									DirectoryContentsWindow.this.iPlugin,
									DirectoryContentsWindow.this.iPlugin
											.getJprogressbar());

							DirectoryContentsWindow.this.getFile.execute();

							DirectoryContentsWindow.this.iPlugin
									.getCancelTransaction_JButton().setEnabled(
											true);
							DirectoryContentsWindow.log
									.info("Cancel Transaction button is enabled");

							DirectoryContentsWindow.this.iPlugin
									.setCancelGetTransaction(true);
							DirectoryContentsWindow.this.iPlugin
									.setCancelPutTransaction(false);
						}
					}
					if (n == 1) {
						return;
					}
				} else if (mouseEvent.getClickCount() == 1) {
					DirectoryContentsWindow.this.treePaths = DirectoryContentsWindow.this.userDirectoryTree
							.getSelectionPaths();
					if (null != DirectoryContentsWindow.this.treePaths) {
						if (DirectoryContentsWindow.this.treePaths.length > 1) {
							for (int i = 0; i < DirectoryContentsWindow.this.treePaths.length; i++) {
								DirectoryContentsWindow.log
										.info("Single click Path"
												+ i
												+ ":"
												+ DirectoryContentsWindow.this.treePaths[i]);
							}
							DirectoryContentsWindow.this.jButton_download
									.setEnabled(true);
						}
					}
					DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick = IrodsUtilities
							.getJtreeSelection(
									mouseEvent,
									DirectoryContentsWindow.this.userDirectoryTree);
					if (null != DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick) {
						DirectoryContentsWindow.log
								.info("Single click path is not null: "
										+ DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick);

						DirectoryContentsWindow.this.iPlugin
								.setSelectedNodeInTreeForSingleClick(DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick);
					}
					DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick = IrodsUtilities
							.getJtreeSelectionForSingleClick(
									DirectoryContentsWindow.this.iPlugin,
									mouseEvent,
									DirectoryContentsWindow.this.userDirectoryTree);
					if (null != DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick) {
						DirectoryContentsWindow.log
								.info("Single click path is not null: "
										+ DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick);

						DirectoryContentsWindow.this.iPlugin
								.setSelectedNodeInTreeForSingleClick(DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick);
						DirectoryContentsWindow.this.iPlugin
								.setSingleClickPathOnlyTillParentFolderWithSizeCheck(DirectoryContentsWindow.this.selectedNodeInTreeForSingleClick);
					}
					if (DirectoryContentsWindow.this.iPlugin
							.getCurrentActiveTabUnderJTabbedPane() == Constants.JTABBEDPANE_SELECTED_TAB_FILE_INFORMATION) {
						String selectedNodeInTreeForSingleClickToGetObjStat = IrodsUtilities
								.getJtreeSelection(
										mouseEvent,
										DirectoryContentsWindow.this.userDirectoryTree);
						if (null != selectedNodeInTreeForSingleClickToGetObjStat) {
							DirectoryContentsWindow.this.iPlugin
									.setObjSelectedUsingSingleClick(selectedNodeInTreeForSingleClickToGetObjStat);
							DirectoryContentsWindow.log
									.info("ObjSelectedUsingSingleClick of irodsImageJ is set: "
											+ selectedNodeInTreeForSingleClickToGetObjStat);

							ObjectDetailsSwingWorker objectDetailsFromSwingWorker = new ObjectDetailsSwingWorker(
									DirectoryContentsWindow.this.iPlugin);

							objectDetailsFromSwingWorker.execute();
						}
					}
				}
			}
		});
	}

	public void setFileInformationFromObjStat(ObjStat objstatWithFileInformation) {
		TableModel tm = this.table.getModel();
		if (null != tm) {
			tm.setValueAt(objstatWithFileInformation.getAbsolutePath(), 0, 1);
			tm.setValueAt(FileUtils
					.byteCountToDisplaySize(objstatWithFileInformation
							.getObjSize()), 1, 1);

			tm.setValueAt(objstatWithFileInformation.getCreatedAt(), 2, 1);
			tm.setValueAt(objstatWithFileInformation.getModifiedAt(), 3, 1);
			tm.setValueAt(
					Integer.valueOf(objstatWithFileInformation.getDataId()), 4,
					1);
			tm.setValueAt(objstatWithFileInformation.getObjectType(), 5, 1);
			tm.setValueAt(objstatWithFileInformation.getChecksum(), 6, 1);
			tm.setValueAt(objstatWithFileInformation.getOwnerName(), 7, 1);
			tm.setValueAt(objstatWithFileInformation.getOwnerZone(), 8, 1);
			tm.setValueAt(objstatWithFileInformation.getCacheDir(), 9, 1);
		} else {
			log.error("Table Model object is null");
		}
	}

	private void constructUserDirectoryTree(IPlugin irodsImagej) {
		this.userDirectoryTree = new JTree(this.treeModel);
		this.userDirectoryTree.setToolTipText("Directory list");
		this.userDirectoryTree.setVisibleRowCount(100);
		this.userDirectoryTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		irodsImagej.setUserDirectoryTree(this.userDirectoryTree);
		this.scrollPane.setViewportView(this.userDirectoryTree);
		this.userDirectoryTree.setModel(this.treeModel);
		irodsImagej.setUserDirectoryTree(this.userDirectoryTree);

		this.userDirectoryTree.addTreeWillExpandListener(this);
	}

	public void parseDirectoryContents(IRODSFileFactory iRODSFileFactory,
			File irodsAccountFile, DefaultMutableTreeNode node,
			IRODSAccount irodsAccount) {
		if (!irodsAccountFile.isDirectory()) {
			log.info("File name:" + irodsAccountFile.getName() + ":"
					+ irodsAccountFile.getAbsolutePath());

			DefaultMutableTreeNode child = new DefaultMutableTreeNode(
					irodsAccountFile.getName(), false);

			node.add(child);
		}
		if (irodsAccountFile.isDirectory()) {
			log.info("Direc name:" + irodsAccountFile.getName());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(
					irodsAccountFile.getName(), true);

			node.add(child);
			File[] direcFiles = irodsAccountFile.listFiles();
			for (int i = 0; i < direcFiles.length; i++) {
				log.info("File number:" + i + "\t depth:" + direcFiles.length);
				parseDirectoryContents(iRODSFileFactory, direcFiles[i], child,
						irodsAccount);
			}
		}
		repaintPanel();
	}

	public void repaintPanel() {
		this.viewport.setVisible(true);
		this.viewport.repaint();
		this.viewport.revalidate();
		add(this.scrollPane, "Center");
		setVisible(true);
		revalidate();
		repaint();
	}

	public void parseDirectoryContentsUsingList(
			List<CollectionAndDataObjectListingEntry> listOfCollectionsUnderGivenAbsolutePath,
			DefaultMutableTreeNode node) {
		CollectionAndDataObjectListingEntry fileUnderCollectionAndDataObjectListingEntry = null;
		for (int i = 0; i < listOfCollectionsUnderGivenAbsolutePath.size(); i++) {
			fileUnderCollectionAndDataObjectListingEntry = (CollectionAndDataObjectListingEntry) listOfCollectionsUnderGivenAbsolutePath
					.get(i);
			if (!fileUnderCollectionAndDataObjectListingEntry.isCollection()) {
				log.info("File name:"
						+ fileUnderCollectionAndDataObjectListingEntry
								.getNodeLabelDisplayValue()
						+ ":"
						+ fileUnderCollectionAndDataObjectListingEntry
								.getFormattedAbsolutePath());

				DefaultMutableTreeNode child = new DefaultMutableTreeNode(
						fileUnderCollectionAndDataObjectListingEntry
								.getNodeLabelDisplayValue(),
						false);

				node.add(child);
			}
			if (fileUnderCollectionAndDataObjectListingEntry.isCollection()) {
				log.info("Direc name:"
						+ fileUnderCollectionAndDataObjectListingEntry
								.getNodeLabelDisplayValue());

				DefaultMutableTreeNode child = new DefaultMutableTreeNode(
						fileUnderCollectionAndDataObjectListingEntry
								.getNodeLabelDisplayValue(),
						true);

				node.add(child);
			}
		}
		this.viewport.removeAll();
		this.viewport.setVisible(true);
		this.viewport.repaint();
		this.viewport.revalidate();

		setVisible(true);
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
			Object child, boolean shouldBeVisible, TreePath path) {
		DefaultMutableTreeNode childNode = null;
		try {
			childNode = new DefaultMutableTreeNode(child, false);
			if (parent == null) {
				parent = this.iPlugin.getRootTreeNodeForDirectoryContents();
			}
			this.treeModel.insertNodeInto(childNode, parent,
					parent.getChildCount());
			this.userDirectoryTree.makeVisible(path);
		} catch (IllegalStateException illegalStateException) {
			log.error(illegalStateException.getMessage());
			JOptionPane.showMessageDialog(null, "node does not allow children");
		}
		return childNode;
	}

	public void treeWillCollapse(TreeExpansionEvent arg0)
			throws ExpandVetoException {
	}

	public void treeWillExpand(TreeExpansionEvent treeExpansionEvent)
			throws ExpandVetoException {
		log.info("Node expanded: " + treeExpansionEvent.getPath());
		TreePath tp = treeExpansionEvent.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
				.getLastPathComponent();

		log.info("last node: " + node.toString());

		node.removeAllChildren();
		this.treeModel.nodeStructureChanged(node);

		Object[] elements = tp.getPath();

		RetrieveInternalNodesSwingWorker retrieveInternalNodesSwingWorker = new RetrieveInternalNodesSwingWorker(
				"", elements, this.iPlugin);
		try {
			if (null != retrieveInternalNodesSwingWorker) {
				retrieveInternalNodesSwingWorker.doInBackground();
			}
		} catch (Exception exception) {
			log.error("Exception while retrieving internal nodes: "
					+ exception.getMessage());
		}
		if (null != this.iPlugin.getChildNodesListAfterLazyLoading()) {
			if (this.iPlugin.getChildNodesListAfterLazyLoading().size() > 0) {
				for (int i = 0; i < this.iPlugin
						.getChildNodesListAfterLazyLoading().size(); i++) {
					this.treeModel
							.insertNodeInto(
									(MutableTreeNode) this.iPlugin
											.getChildNodesListAfterLazyLoading()
											.get(i), node, node.getChildCount());
				}
			}
		}
	}
}
