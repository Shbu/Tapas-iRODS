package org.bio5.irods.imagej.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.bio5.irods.imagej.utilities.Constants;
import org.bio5.irods.imagej.utilities.IrodsTransferStatusCallbackListener;
import org.bio5.irods.imagej.views.DirectoryContentsPane;
import org.bio5.irods.imagej.views.MainWindow;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.domain.ObjStat;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class IrodsImageJBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public IrodsImageJBean() {
		super();
	}

	private MainWindow mainWindow = null;

	private IRODSAccount irodsAccount = null;

	private IRODSFileSystem irodsFileSystem = null;

	private static IRODSFile iRodsFile = null;

	private TransferControlBlock transferControlBlock = null;

	private TransferOptions transferOptions = null;

	private IRODSSession iRODSSession = null;

	private IRODSFileFactory iRODSFileFactory = null;

	private JProgressBar jprogressbar = null;

	private IrodsTransferStatusCallbackListener irodsTransferStatusCallbackListener = null;

	private JTree userDirectoryTree = null;

	private DirectoryContentsPane directoryContentsPane = null;

	private JFrame frame = null;

	private JScrollPane scrollPane;

	private DefaultMutableTreeNode rootNode;

	private DefaultTreeModel treeModel;

	private JViewport viewport;

	private String pathTillHome;

	private List<DefaultMutableTreeNode> childNodesListAfterLazyLoading;

	private List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;

	private DefaultMutableTreeNode rootTreeNodeForDirectoryContents = null;

	/*
	 * true - if you want to pull everything from home directory (This includes
	 * shared files too). False- if you want to pull collections from only your
	 * account
	 */
	private boolean isHomeDirectoryTheRootNode = false;

	private String imageJCacheFolder = Constants.IMAGEJ_CACHE_FOLDER;

	private String objSelectedUsingSingleClick;

	private IRODSFileSystemAOImpl iRODSFileSystemAOImpl;

	private String currentActiveTabUnderJTabbedPane;

	private ObjStat objStatForGivenAbsolutePath;

	private HashMap<String, Object> saveDetails;

	private boolean isImageOpened = false;

	public boolean isImageOpened() {
		return isImageOpened;
	}

	public void setImageOpened(boolean isImageOpened) {
		this.isImageOpened = isImageOpened;
	}

	public HashMap<String, Object> getSaveDetails() {
		return saveDetails;
	}

	public void setSaveDetails(HashMap<String, Object> saveDetails) {
		this.saveDetails = saveDetails;
	}

	public ObjStat getObjStatForGivenAbsolutePath() {
		return objStatForGivenAbsolutePath;
	}

	public void setObjStatForGivenAbsolutePath(
			ObjStat objStatForGivenAbsolutePath) {
		this.objStatForGivenAbsolutePath = objStatForGivenAbsolutePath;
	}

	public String getCurrentActiveTabUnderJTabbedPane() {
		return currentActiveTabUnderJTabbedPane;
	}

	public void setCurrentActiveTabUnderJTabbedPane(
			String currentActiveTabUnderJTabbedPane) {
		this.currentActiveTabUnderJTabbedPane = currentActiveTabUnderJTabbedPane;
	}

	public IRODSFileSystemAOImpl getiRODSFileSystemAOImpl() {
		return iRODSFileSystemAOImpl;
	}

	public void setiRODSFileSystemAOImpl(
			IRODSFileSystemAOImpl iRODSFileSystemAOImpl) {
		this.iRODSFileSystemAOImpl = iRODSFileSystemAOImpl;
	}

	public String getObjSelectedUsingSingleClick() {
		return objSelectedUsingSingleClick;
	}

	public void setObjSelectedUsingSingleClick(
			String objSelectedUsingSingleClick) {
		this.objSelectedUsingSingleClick = objSelectedUsingSingleClick;
	}

	public String getImageJCacheFolder() {
		return imageJCacheFolder;
	}

	public void setImageJCacheFolder(String imageJCacheFolder) {
		this.imageJCacheFolder = imageJCacheFolder;
	}

	public boolean isHomeDirectoryTheRootNode() {
		return isHomeDirectoryTheRootNode;
	}

	public void setHomeDirectoryTheRootNode(boolean isHomeDirectoryTheRootNode) {
		this.isHomeDirectoryTheRootNode = isHomeDirectoryTheRootNode;
	}

	public DefaultMutableTreeNode getRootTreeNodeForDirectoryContents() {
		return rootTreeNodeForDirectoryContents;
	}

	public void setRootTreeNodeForDirectoryContents(
			DefaultMutableTreeNode rootTreeNode) {
		this.rootTreeNodeForDirectoryContents = rootTreeNode;
	}

	public String getPathTillHome() {
		return pathTillHome;
	}

	public void setPathTillHome(String pathTillHome) {
		this.pathTillHome = pathTillHome;
	}

	public List<DefaultMutableTreeNode> getChildNodesListAfterLazyLoading() {
		return childNodesListAfterLazyLoading;
	}

	public void setChildNodesListAfterLazyLoading(
			List<DefaultMutableTreeNode> childNodesListAfterLazyLoading) {
		this.childNodesListAfterLazyLoading = childNodesListAfterLazyLoading;
	}

	public List<CollectionAndDataObjectListingEntry> getCollectionsUnderGivenAbsolutePath() {
		return collectionsUnderGivenAbsolutePath;
	}

	public void setCollectionsUnderGivenAbsolutePath(
			List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath) {
		this.collectionsUnderGivenAbsolutePath = collectionsUnderGivenAbsolutePath;
	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	public JViewport getViewport() {
		return viewport;
	}

	public void setViewport(JViewport viewport) {
		this.viewport = viewport;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(DefaultMutableTreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JProgressBar getJprogressbar() {
		return jprogressbar;
	}

	public DirectoryContentsPane getDirectoryContentsPane() {
		return directoryContentsPane;
	}

	public void setDirectoryContentsPane(
			DirectoryContentsPane directoryContentsPane) {
		this.directoryContentsPane = directoryContentsPane;
	}

	public JTree getUserDirectoryTree() {
		return userDirectoryTree;
	}

	public void setUserDirectoryTree(JTree userDirectoryTree) {
		this.userDirectoryTree = userDirectoryTree;
	}

	public void setJprogressbar(JProgressBar jprogressbar) {
		this.jprogressbar = jprogressbar;
	}

	public IRODSFileFactory getiRODSFileFactory() {
		return iRODSFileFactory;
	}

	public void setiRODSFileFactory(IRODSFileFactory iRODSFileFactory) {
		this.iRODSFileFactory = iRODSFileFactory;
	}

	public IRODSSession getiRODSSession() {
		return iRODSSession;
	}

	public void setiRODSSession(IRODSSession iRODSSession) {
		this.iRODSSession = iRODSSession;
	}

	public TransferOptions getTransferOptions() {
		return transferOptions;
	}

	public void setTransferOptions(TransferOptions transferOptions) {
		this.transferOptions = transferOptions;
	}

	public TransferControlBlock getTransferControlBlock() {
		return transferControlBlock;
	}

	public void setTransferControlBlock(
			TransferControlBlock transferControlBlock) {
		this.transferControlBlock = transferControlBlock;
	}

	public IrodsTransferStatusCallbackListener getIrodsTransferStatusCallbackListener() {
		return irodsTransferStatusCallbackListener;
	}

	public void setIrodsTransferStatusCallbackListener(
			IrodsTransferStatusCallbackListener irodsTransferStatusCallbackListener) {
		this.irodsTransferStatusCallbackListener = irodsTransferStatusCallbackListener;
	}

	public IRODSFile getiRodsFile() {
		return iRodsFile;
	}

	public void setiRodsFile(IRODSFile iRodsFile) {
		IrodsImageJBean.iRodsFile = iRodsFile;
	}

	public IRODSAccount getIrodsAccount() {
		return irodsAccount;
	}

	public void setIrodsAccount(IRODSAccount irodsAccount) {
		this.irodsAccount = irodsAccount;
	}

	public IRODSFileSystem getIrodsFileSystem() {
		return irodsFileSystem;
	}

	public void setIrodsFileSystem(IRODSFileSystem irodsFileSystem) {
		this.irodsFileSystem = irodsFileSystem;
	}

}
