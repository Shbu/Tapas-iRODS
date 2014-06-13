package org.bio5.irods.iplugin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
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
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.bio5.irods.iplugin.fileoperations.GetFileFromIrodsSwingWorker;
import org.bio5.irods.iplugin.fileoperations.PutFileToIrodsSwingWorker;
import org.bio5.irods.iplugin.fileoperations.RetrieveInternalNodesSwingWorker;
import org.bio5.irods.iplugin.listeners.MyTreeModelListener;
import org.bio5.irods.iplugin.swingworkers.ObjectDetailsSwingWorker;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsPropertiesConstruction;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
//import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.domain.ObjStat;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class DirectoryContentsWindow extends JPanel implements
		TreeWillExpandListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 722996165620904921L;
	private IRODSFileSystem irodsFileSystem;
	@SuppressWarnings("unused")
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

	/* Logger instantiation */
	static Logger log = Logger.getLogger(DirectoryContentsWindow.class
			.getName());

	/**
	 * Create the panel.
	 * 
	 * @throws JargonException
	 * @throws MalformedURLException
	 */

	public DirectoryContentsWindow(final IPlugin iPlugin)
			throws JargonException, MalformedURLException {
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		this.iPlugin = iPlugin;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	/**
	 * @throws JargonException
	 */
	public void init() throws JargonException {
		irodsAccount = iPlugin.getIrodsAccount();
		iRODSFileFactory = iPlugin.getiRODSFileFactory();

		homeNode = new DefaultMutableTreeNode(Constants.HOME_STRING);
		if (!iPlugin.isHomeDirectoryTheRootNode()) {
			accountNode = new DefaultMutableTreeNode(iPlugin.getIrodsAccount()
					.getUserName());
			homeNode.add(accountNode); /* Adding accountNode to HomeNode */
			iPlugin.setRootTreeNodeForDirectoryContents(accountNode);
			treeModel = new DefaultTreeModel(accountNode, true);
		} else {
			iPlugin.setRootTreeNodeForDirectoryContents(homeNode);
			treeModel = new DefaultTreeModel(homeNode, true);
		}
		treeModel.addTreeModelListener(new MyTreeModelListener());
		iPlugin.setTreeModel(treeModel);

		/* Initiating Jprogressbar */
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setToolTipText("Progress of action");
		iPlugin.setJprogressbar(progressBar);

		/* Progress bar label to show bytesTrasferred out of TotalFileSize */
		label_ProgressBar_BytesTrasferredOutofTotalFileSize = new JLabel(
				" Progress:");
		label_ProgressBar_BytesTrasferredOutofTotalFileSize
				.setToolTipText(" Progress: bytesTransferred/Total File Size in Bytes");
		label_ProgressBar_BytesTrasferredOutofTotalFileSize
				.setBorder(new LineBorder(new Color(0, 0, 0)));

		/* Setting iRODS file system */
		irodsFileSystem = IRODSFileSystem.instance();
		iPlugin.setIrodsFileSystem(irodsFileSystem);

		dataTransferOperationsAO = irodsFileSystem
				.getIRODSAccessObjectFactory().getDataTransferOperations(
						irodsAccount);

		/* Setting scrollPane */
		scrollPane = new javax.swing.JScrollPane();
		viewport = scrollPane.getViewport();
		iPlugin.setScrollPane(scrollPane);
		iPlugin.setViewport(viewport);

		jTextField_sourceFile = new JLabel("Local file");
		jTextField_destinationPath = new JLabel("Destination");
		jButton_saveToIrodsServer = new JButton("Save to iRODS Server");

		/* Construct TransferControlBlock from default jargon properties */
		irodsPropertiesConstruction = new IrodsPropertiesConstruction();
		transferControlBlock = irodsPropertiesConstruction
				.constructHighPerformanceTransferControlBlockFromJargonProperties(iPlugin);
		iPlugin.setTransferControlBlock(transferControlBlock);

		/* Construct IrodsTransferStatusCallbackListener */
		irodsPropertiesConstruction
				.constructIrodsTransferStatusCallbackListener(iPlugin);

		imageJCacheFolder = iPlugin.getImageJCacheFolder();
		log.info("ImageJ cache folder: " + iPlugin.getImageJCacheFolder());
		if (null != imageJCacheFolder && "" != imageJCacheFolder) {
			File cacheFolder = new File(imageJCacheFolder);
			imageJCacheFolderSize = IrodsUtilities.getFolderSize(cacheFolder);
			log.info("Cache folder size:"
					+ FileUtils.byteCountToDisplaySize(imageJCacheFolderSize));
		}
		if (null != iPlugin.getMainWindow()) {
			mainWindowInstance = iPlugin.getMainWindow();
		}
		/* Creating model */
		setVisible(true);
	}

	/**
	 * @param iplugin
	 * @param jTextField_sourceFile
	 * @param jTextField_destinationPath
	 * @param jButton_saveToIrodsServer
	 */
	public void implementation() {

		if (null != iPlugin.getCollectionsUnderGivenAbsolutePath()) {
			List<CollectionAndDataObjectListingEntry> listOfCollectionsUnderGivenAbsolutePath = iPlugin
					.getCollectionsUnderGivenAbsolutePath();
			parseDirectoryContentsUsingList(
					listOfCollectionsUnderGivenAbsolutePath,
					iPlugin.getRootTreeNodeForDirectoryContents());
		} else {
			log.error("File directory is empty");
		}

		addNavigateOptionToMenuBar();

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		/* Added changeListener to get current selected tab in JtabbedPane */
		ChangeListener changeListener = new ChangeListener() {

			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent
						.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				log.info("CurrentActiveTabUnderJTabbedPane : "
						+ sourceTabbedPane.getTitleAt(index));
				String currentActiveTabUnderJTabbedPane = sourceTabbedPane
						.getTitleAt(index);
				iPlugin.setCurrentActiveTabUnderJTabbedPane(currentActiveTabUnderJTabbedPane);
			}
		};

		tabbedPane.addChangeListener(changeListener);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout
				.createParallelGroup(Alignment.TRAILING)
				.addGroup(
						groupLayout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(scrollPane,
										GroupLayout.PREFERRED_SIZE, 402,
										GroupLayout.PREFERRED_SIZE)
								.addGap(18)
								.addComponent(tabbedPane,
										GroupLayout.DEFAULT_SIZE, 480,
										Short.MAX_VALUE).addGap(18))
				.addGroup(groupLayout.createSequentialGroup().addGap(216)));
		groupLayout
				.setVerticalGroup(groupLayout
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(44)
																		.addComponent(
																				tabbedPane,
																				GroupLayout.DEFAULT_SIZE,
																				440,
																				Short.MAX_VALUE))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				scrollPane,
																				GroupLayout.DEFAULT_SIZE,
																				473,
																				Short.MAX_VALUE)))
										.addContainerGap()));

		JPanel panel = new JPanel();
		tabbedPane.addTab("File Operations", null, panel, null);

		JButton jButton_selectLocalFile = new JButton("Select local file");

		jButton_selectLocalFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int option = chooser
						.showOpenDialog(DirectoryContentsWindow.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					jTextField_sourceFile
							.setText(((chooser.getSelectedFile() != null) ? chooser
									.getSelectedFile().getAbsolutePath()
									: "nothing is selected"));
				} else {
					jTextField_sourceFile.setName("File selection canceled !");
				}
			}
		});

		jTextField_sourceFile.setEnabled(false);
		JButton jButton_selectDestination = new JButton("Select Destination");
		jButton_selectDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent destinationButtonActionEvent) {

				String destinationFolderPath = selectedNodeInTreeForSingleClick;
				jTextField_destinationPath.setText(destinationFolderPath);
				jButton_saveToIrodsServer.setEnabled(true);
			}
		});

		JButton btnDownload = new JButton("Download");
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		jTextField_destinationPath.setEnabled(false);

		jButton_saveToIrodsServer.setEnabled(false);
		jButton_saveToIrodsServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					implementSaveButton();

				} catch (JargonException jargonException) {
					log.error(jargonException.getMessage());
					jargonException.printStackTrace();
				}
			}

			/**
			 * @throws JargonException
			 */
			private void implementSaveButton() throws JargonException {
				log.info("Save to iRODS Server - Button Clicked");
				String sourceFilePath = null;
				String destinationFilePath = null;
				String targetResourceName = "";
				targetResourceName = iPlugin.getIrodsAccount()
						.getDefaultStorageResource();
				File sourceLocalfile = null;
				IRODSFile destinaitonIrodsFile = null;
				if (chooser.getSelectedFile().getAbsolutePath() != null
						&& chooser.getSelectedFile().getAbsolutePath() != "") {
					sourceFilePath = chooser.getSelectedFile()
							.getAbsolutePath();
					sourceLocalfile = new File(sourceFilePath);
					if (selectedNodeInTreeForSingleClick != null
							&& selectedNodeInTreeForSingleClick != "") {
						log.info("destination path || selectedNodeInTreeForSingleClick"
								+ selectedNodeInTreeForSingleClick);
						if (iPlugin.isHomeDirectoryTheRootNode()) {
							destinationFilePath = TapasCoreFunctions
									.getRootDirectoryPath(iPlugin)
									+ jTextField_destinationPath.getText();
							log.info("Destination Path if home directory is checked:"
									+ destinationFilePath);
						}
						if (!iPlugin.isHomeDirectoryTheRootNode()) {
							destinationFilePath = TapasCoreFunctions
									.getHomeDirectoryPath(iPlugin)
									+ jTextField_destinationPath.getText();
							log.info("Destination Path if home directory is not checked:"
									+ destinationFilePath);
						}
						destinaitonIrodsFile = iRODSFileFactory
								.instanceIRODSFile(destinationFilePath);
						log.info("sourceLocalfile absolute path: "
								+ sourceLocalfile.getAbsolutePath() + "\n"
								+ "destinaitonIrodsFile absolutepath: "
								+ destinaitonIrodsFile.getAbsoluteFile());
						try {
							// dataTransferOperationsAO.putOperation(sourceLocalfile.getAbsolutePath(),destinaitonIrodsFile.getAbsolutePath(),targetResourceName,irodsTransferStatusCallbackListener,transferControlBlock);
							if (null != iPlugin && null != sourceLocalfile
									&& null != destinaitonIrodsFile
									&& null != targetResourceName) {
								putFile = new PutFileToIrodsSwingWorker(
										iPlugin, sourceLocalfile,
										destinaitonIrodsFile,
										targetResourceName);
								putFile.execute();
								log.info("PutFile operation is executed!");

							}
						} catch (Exception exception) {
							log.error(exception.getMessage());
							JOptionPane.showMessageDialog(null,
									exception.getMessage());
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "Source is empty!");
					log.error("Source is empty!");
				}
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addComponent(
														iPlugin.getJprogressbar(),
														GroupLayout.DEFAULT_SIZE,
														217, Short.MAX_VALUE)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addComponent(
																						jButton_selectLocalFile)
																				.addComponent(
																						jButton_selectDestination))
																.addGap(27)
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addComponent(
																						jTextField_destinationPath)
																				.addComponent(
																						jTextField_sourceFile)))
												.addComponent(
														jButton_saveToIrodsServer))
								.addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(iPlugin.getJprogressbar(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGap(59)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														jButton_selectLocalFile)
												.addComponent(
														jTextField_sourceFile))
								.addGap(18)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														jButton_selectDestination)
												.addComponent(
														jTextField_destinationPath))
								.addGap(18)
								.addComponent(jButton_saveToIrodsServer)
								.addContainerGap(220, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("File Information", null, panel_1, null);

		table = new JTable();
		table.setIntercellSpacing(new Dimension(5, 5));
		table.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		table.setToolTipText("File Information");

		/* Add to Constants class - Pending */
		table.setModel(new DefaultTableModel(new Object[][] {
				{ " Absolute Path", null }, { " Object Size", null },
				{ " Created Date", null }, { " Modified Date", null },
				{ " Data Id", null }, { " Object Type", null },
				{ " File Checksum", null }, { " Owner Name", null },
				{ " Owner Zone", null }, { " Chache Dirty", null }, },
				new String[] { "Field", "Information" }));
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.setRowHeight(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(300);
		table.getColumnModel().getColumn(1).setMinWidth(200);
		panel_1.add(table);
		setLayout(groupLayout);

		constructUserDirectoryTree(iPlugin);

		userDirectoryTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {

				if (mouseEvent.getClickCount() == 2) {

					/* resetting progress bar to 0 if a double click is detected */
					iPlugin.getJprogressbar().setValue(0);
					log.info("tree path after double click"
							+ selectedNodeInTreeForDoubleClick);
					log.info("double click selection: "
							+ selectedNodeInTreeForDoubleClick);

					selectedNodeInTreeForDoubleClick = IrodsUtilities
							.getJtreeSelection(mouseEvent, userDirectoryTree);
					if (null != selectedNodeInTreeForDoubleClick
							&& selectedNodeInTreeForDoubleClick != "") {
						log.info("selectedNodeInTreeForDoubleClick string is "
								+ selectedNodeInTreeForDoubleClick);
						iPlugin.setSelectedNodeInTreeForDoubleClick(selectedNodeInTreeForDoubleClick);
						getFile = new GetFileFromIrodsSwingWorker(
								iRODSFileFactory,
								selectedNodeInTreeForDoubleClick, iPlugin,
								iPlugin.getJprogressbar());
						getFile.execute();
					}

				} else if (mouseEvent.getClickCount() == 1) {

					/* Multiple files selection is still pending */
					TreePath[] treePaths = userDirectoryTree
							.getSelectionPaths();
					if (null != treePaths) {
						for (int i = 0; i < treePaths.length; i++) {
							log.info("Single click Path" + i + ":"
									+ treePaths[i]);
						}
					}

					selectedNodeInTreeForSingleClick = IrodsUtilities
							.getJtreeSelectionForSingleClick(mouseEvent,
									userDirectoryTree);
					if (null != selectedNodeInTreeForSingleClick) {
						iPlugin.setSelectedNodeInTreeForSingleClick(selectedNodeInTreeForSingleClick);
					}

					if (iPlugin.getCurrentActiveTabUnderJTabbedPane() == Constants.JTABBEDPANE_SELECTED_TAB_FILE_INFORMATION) {
						String selectedNodeInTreeForSingleClickToGetObjStat = IrodsUtilities
								.getJtreeSelection(mouseEvent,
										userDirectoryTree);
						if (null != selectedNodeInTreeForSingleClickToGetObjStat) {
							iPlugin.setObjSelectedUsingSingleClick(selectedNodeInTreeForSingleClickToGetObjStat);
							log.info("ObjSelectedUsingSingleClick of irodsImageJ is set: "
									+ selectedNodeInTreeForSingleClickToGetObjStat);
							ObjectDetailsSwingWorker objectDetailsFromSwingWorker = new ObjectDetailsSwingWorker(
									iPlugin);
							objectDetailsFromSwingWorker.execute();
						}
					}
				}
			}
		});

		userDirectoryTree.setShowsRootHandles(true);
		userDirectoryTree.setEditable(false);
		userDirectoryTree.setVisible(true);
		viewport.add(userDirectoryTree);
	}

	/**
	 * 
	 */
	private void addNavigateOptionToMenuBar() {
		/* Adding additional menu items for Main Window */
		JMenuBar mainWindowMenubar = mainWindowInstance.getJMenuBar();
		JMenu mnNewMenu_File = new JMenu("Navigate");
		mnNewMenu_File.setMnemonic('N');
		mainWindowMenubar.add(mnNewMenu_File);

		JMenuItem mntmNewMenuItem_Home = new JMenuItem("Home");
		mntmNewMenuItem_Home.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_H, InputEvent.CTRL_MASK));
		mnNewMenu_File.add(mntmNewMenuItem_Home);

		JMenuItem mntmNewMenuItem_Root = new JMenuItem("Root Folder");
		mntmNewMenuItem_Root.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mnNewMenu_File.add(mntmNewMenuItem_Root);
	}

	public void setFileInformationFromObjStat(ObjStat objstatWithFileInformation) {

		TableModel tm = table.getModel();
		if (null != tm) {
			tm.setValueAt(objstatWithFileInformation.getAbsolutePath(), 0, 1);
			tm.setValueAt(FileUtils
					.byteCountToDisplaySize(objstatWithFileInformation
							.getObjSize()), 1, 1);
			tm.setValueAt(objstatWithFileInformation.getCreatedAt(), 2, 1);
			tm.setValueAt(objstatWithFileInformation.getModifiedAt(), 3, 1);
			tm.setValueAt(objstatWithFileInformation.getDataId(), 4, 1);
			tm.setValueAt(objstatWithFileInformation.getObjectType(), 5, 1);
			tm.setValueAt(objstatWithFileInformation.getChecksum(), 6, 1);
			tm.setValueAt(objstatWithFileInformation.getOwnerName(), 7, 1);
			tm.setValueAt(objstatWithFileInformation.getOwnerZone(), 8, 1);
			tm.setValueAt(objstatWithFileInformation.getCacheDir(), 9, 1);
		} else {
			log.error("Table Model object is null");
		}
	}

	/**
	 * @param irodsImagej
	 */
	private void constructUserDirectoryTree(final IPlugin irodsImagej) {
		userDirectoryTree = new JTree(treeModel);
		userDirectoryTree.setToolTipText("Directory list");
		userDirectoryTree.setVisibleRowCount(100);
		userDirectoryTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		irodsImagej.setUserDirectoryTree(userDirectoryTree);
		scrollPane.setViewportView(userDirectoryTree);
		userDirectoryTree.setModel(treeModel);
		irodsImagej.setUserDirectoryTree(userDirectoryTree);

		/* Adding Jtree Listeners */
		userDirectoryTree.addTreeWillExpandListener(this);
	}

	public void parseDirectoryContents(final IRODSFileFactory iRODSFileFactory,
			final File irodsAccountFile, DefaultMutableTreeNode node,
			final IRODSAccount irodsAccount) {

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
		viewport.setVisible(true);
		viewport.repaint();
		viewport.revalidate();
		add(scrollPane, BorderLayout.CENTER);
		setVisible(true);
		revalidate();
		repaint();
	}

	public void parseDirectoryContentsUsingList(
			List<CollectionAndDataObjectListingEntry> listOfCollectionsUnderGivenAbsolutePath,
			DefaultMutableTreeNode node) {
		CollectionAndDataObjectListingEntry fileUnderCollectionAndDataObjectListingEntry = null;
		for (int i = 0; i < listOfCollectionsUnderGivenAbsolutePath.size(); i++) {
			fileUnderCollectionAndDataObjectListingEntry = listOfCollectionsUnderGivenAbsolutePath
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

		viewport.removeAll();
		viewport.setVisible(true);
		viewport.repaint();
		viewport.revalidate();
		add(scrollPane, BorderLayout.CENTER);
		setVisible(true);
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
			Object child, boolean shouldBeVisible, TreePath path) {
		DefaultMutableTreeNode childNode = null;
		try {
			childNode = new DefaultMutableTreeNode(child, false);
			if (parent == null) {
				parent = iPlugin.getRootTreeNodeForDirectoryContents();
			}

			treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
			userDirectoryTree.makeVisible(path);

			/*
			 * if (shouldBeVisible) {
			 * userDirectoryTree.scrollPathToVisible(path); }
			 */
		} catch (IllegalStateException illegalStateException) {
			log.error(illegalStateException.getMessage());
			JOptionPane.showMessageDialog(null, "node does not allow children");
		}
		return childNode;
	}

	/**
	 * Default empty implementation, do nothing on collapse event.
	 */
	public void treeWillCollapse(TreeExpansionEvent arg0)
			throws ExpandVetoException {
	}

	/**
	 * Node will expand, it's time to retrieve nodes
	 */
	public void treeWillExpand(TreeExpansionEvent treeExpansionEvent)
			throws ExpandVetoException {
		log.info("Node expanded: " + treeExpansionEvent.getPath());
		TreePath tp = treeExpansionEvent.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
				.getLastPathComponent();
		log.info("last node: " + node.toString());
		/*
		 * Removing children of a node and re-inserting them back - This avoids
		 * the risk of re-adding the same children if handle is tree is expanded
		 * again
		 */
		node.removeAllChildren();
		treeModel.nodeStructureChanged(node);
		// DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)
		// tp.getLastPathComponent();
		Object[] elements = tp.getPath();/* edit path */
		// String pathOfInternalNode=builder.toString();

		RetrieveInternalNodesSwingWorker retrieveInternalNodesSwingWorker = new RetrieveInternalNodesSwingWorker(
				elements, iPlugin);
		try {
			retrieveInternalNodesSwingWorker.doInBackground();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Add nodes only if size of extracted list is more than Zero. This will
		 * prevent empty nodes from expanding.
		 */
		if (iPlugin.getChildNodesListAfterLazyLoading().size() > 0) {
			for (int i = 0; i < iPlugin.getChildNodesListAfterLazyLoading()
					.size(); i++) {
				treeModel.insertNodeInto(iPlugin
						.getChildNodesListAfterLazyLoading().get(i), node, node
						.getChildCount());
			}
		}
	}

}