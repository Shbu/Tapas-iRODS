package org.bio5.irods.iplugin.fileoperations;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.irods.jargon.core.exception.DataNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;

public class PutFileToIrodsSwingWorker extends SwingWorker<Void, Integer> {

	private IPlugin iPlugin;
	private DataTransferOperations dataTransferOperationsAO;
	private File sourceLocalfile = null;
	private IRODSFile destinaitonIrodsFile = null;
	private String targetResourceName = "";

	/* Logger instantiation */
	static Logger log = Logger.getLogger(PutFileToIrodsSwingWorker.class
			.getName());

	public PutFileToIrodsSwingWorker(IPlugin irodsImagej, File sourceLocalfile,
			IRODSFile destinaitonIrodsFile, String targetResourceName) {
		super();
		this.iPlugin = irodsImagej;
		this.sourceLocalfile = sourceLocalfile;
		this.destinaitonIrodsFile = destinaitonIrodsFile;
		this.targetResourceName = targetResourceName;
	}

	@Override
	protected Void doInBackground() {
		// TODO Auto-generated method stub
		if (null != iPlugin.getIrodsAccount()
				&& null != iPlugin.getIrodsTransferStatusCallbackListener()
				&& null != iPlugin.getTransferControlBlock()) {
			try {
				dataTransferOperationsAO = iPlugin.getIrodsFileSystem()
						.getIRODSAccessObjectFactory()
						.getDataTransferOperations(iPlugin.getIrodsAccount());
			} catch (JargonException jargonException) {
				log.error("Error while getting dataTransferOperationsAO object from FileSystem !"
						+ jargonException.getMessage());
			}
			if (null != dataTransferOperationsAO) {
				if (null != sourceLocalfile.getAbsolutePath()
						&& null != destinaitonIrodsFile.getAbsolutePath()) {
					/* Option -1 - Absolute path */
					try {
						log.info("Defaulting ErrorWhileUsingGetOperation value to :"
								+ "False");
						iPlugin.setErrorWhileUsingGetOperation(false);
						iPlugin.setDestinationPath(destinaitonIrodsFile
								.getAbsolutePath());

						dataTransferOperationsAO.putOperation(sourceLocalfile
								.getAbsolutePath(), destinaitonIrodsFile
								.getAbsolutePath(), targetResourceName, iPlugin
								.getIrodsTransferStatusCallbackListener(),
								iPlugin.getTransferControlBlock());

						if (!iPlugin.isErrorWhileUsingGetOperation()) {
							log.info("file Transfer successfull!!");
							JOptionPane.showMessageDialog(null,
									"File Transfer done successfully");
							reloadChildNodeAfterUploading();
						} else {
							log.error("Error while transfering files");
							JOptionPane.showMessageDialog(null,
									"Error while transferring files!",
									"Error in uploading file",
									JOptionPane.ERROR_MESSAGE);
						}

					} catch (DataNotFoundException dataNotFoundException) {
						log.error("DataNotFoundException while uploading file to irods"
								+ dataNotFoundException.getMessage());
						dataNotFoundException.printStackTrace();
						JOptionPane
								.showMessageDialog(
										null,
										"DataNotFoundException while uploading file to server!",
										"Error", JOptionPane.ERROR_MESSAGE);
					} catch (OverwriteException overWriteException) {
						log.error("overWriteException"
								+ overWriteException.getMessage());
						JOptionPane
								.showMessageDialog(
										null,
										"OverwriteException while uploading file to server!",
										"Error", JOptionPane.ERROR_MESSAGE);
					} catch (JargonException jargonException) {
						log.error("JargonException"
								+ jargonException.getMessage());
						JOptionPane
								.showMessageDialog(
										null,
										"JargonException while uploading file to server!",
										"Error", JOptionPane.ERROR_MESSAGE);
					}

					/* Option -2 - iRODS File */
					/*
					 * dataTransferOperationsAO.putOperation(sourceLocalfile,
					 * destinaitonIrodsFile, irodsImagej
					 * .getIrodsTransferStatusCallbackListener(),
					 * irodsImagej.getTransferControlBlock());
					 */
				}
			}
		} else {
			log.error("Required parameters are null in PutFileToIrodsSwingWorker-doInBackground  operation");
		}
		return null;
	}

	@Override
	public void done() {
	}

	private void reloadChildNodeAfterUploading() {
		if (null != iPlugin.getUserDirectoryTree() && null != sourceLocalfile) {

			DefaultMutableTreeNode node = null;
			log.info("inside reloadChildNodeAfterUploading");

			TreePath treePathToRetrieveInternalPaths = null;
			treePathToRetrieveInternalPaths = iPlugin.getUserDirectoryTree()
					.getSelectionPath();

			/*
			 * Implementation is still pending - if selected node is not leaf
			 * node, then parent node is taken while retrieveing files. FIx this
			 * immediately or test to check the funcitonality
			 */

			/*
			 * Destination selection - Fetching parent path if leaf node is
			 * selected
			 */
			node = (DefaultMutableTreeNode) iPlugin.getUserDirectoryTree()
					.getLastSelectedPathComponent();

			Object[] treePathForInternalNodes = null;
			treePathForInternalNodes = treePathToRetrieveInternalPaths
					.getPath();
			if (null != node) {

				/* Reset node to parent node if leaf node is selected */
				log.info("Empty folder check:"
						+ iPlugin.isEmptyFolder());
				if (node.isLeaf() && !iPlugin.isEmptyFolder()) {
					log.info("node is leaf and not empty folder");
					node = (DefaultMutableTreeNode) node.getParent();
					treePathForInternalNodes = treePathToRetrieveInternalPaths
							.getParentPath().getPath();
				}
				node.removeAllChildren();
				iPlugin.getTreeModel().nodeStructureChanged(node);
			}
			else{
				log.error("node is null");
			}

			RetrieveInternalNodesSwingWorker retrieve = null;
			String singleClickPathOnlyTillParentFolderWithSizeCheck = iPlugin
					.getSingleClickPathOnlyTillParentFolderWithSizeCheck();
			if (null != treePathForInternalNodes
					&& null != singleClickPathOnlyTillParentFolderWithSizeCheck) {
				log.info("TreePath: " + treePathForInternalNodes.toString());
				retrieve = new RetrieveInternalNodesSwingWorker(
						singleClickPathOnlyTillParentFolderWithSizeCheck, null,
						iPlugin);
				try {
					if (null != retrieve) {
						retrieve.doInBackground();
					}
				} catch (Exception exception) {
					log.error("Error while retrieving internal files: "
							+ exception.getMessage());
				}
			} else {
				log.error("treePathForInternalNodes is null");
			}

			if (null != iPlugin.getChildNodesListAfterLazyLoading()) {
				/*
				 * Add nodes only if size of extracted list is more than Zero.
				 * This will prevent empty nodes from expanding.
				 */
				if (iPlugin.getChildNodesListAfterLazyLoading().size() > 0) {
					for (int i = 0; i < iPlugin
							.getChildNodesListAfterLazyLoading().size(); i++) {
						log.info("node name before inserting:"
								+ iPlugin.getChildNodesListAfterLazyLoading()
										.get(i));
						log.info("child count: " + node.getChildCount());
						try {
							log.info("adding node to treemodel");
							iPlugin.getTreeModel()
									.insertNodeInto(
											iPlugin.getChildNodesListAfterLazyLoading()
													.get(i), node,
											node.getChildCount());
						} catch (Exception e) {
							log.error("Error while adding node elements to path: "
									+ e.getMessage());
						}
					}
				}
			} else {
				log.error("iPlugin.getChildNodesListAfterLazyLoading() is null");
			}

			/* Pending */
			// iPlugin.getDirectoryContentsPane().getTreeModel().reload(parentNode);

			/*
			 * RetrieveInternalNodesSwingWorker retrieve = new
			 * RetrieveInternalNodesSwingWorker(tp.getParentPath().getPath(),
			 * iPlugin);
			 * 
			 * try { retrieve.doInBackground(); } catch (Exception e) {
			 * e.printStackTrace(); }
			 */

			/*
			 * if (null != iPlugin.getDestinationPath()) { String fileName = new
			 * File(iPlugin.getDestinationPath()) .getName(); parentNode = new
			 * DefaultMutableTreeNode(fileName); } if (null != parentNode) {
			 * 
			 * iPlugin.getDirectoryContentsPane() .addObject(parentNode,
			 * sourceLocalfile.getName(), true, tp);
			 * 
			 * 
			 * if (parentNode.isLeaf()) {
			 * 
			 * if (!iPlugin.isFileExistFlag()) { try {
			 * log.info("last selected path component:" + parentNode);
			 * parentNode = (DefaultMutableTreeNode) parentNode .getParent();
			 * log.info("Parent node of last selected component: " +
			 * parentNode); iPlugin.getDirectoryContentsPane()
			 * .addObject(parentNode, sourceLocalfile.getName(), true); } catch
			 * (Exception exception) { log.error("Error:  " +
			 * exception.getMessage()); exception.printStackTrace(); } } else {
			 * log.info(
			 * "File already exists in that directory, so not showing in tree path!"
			 * ); } } else { log.info("parentNode is a directory");
			 * 
			 * if (!iPlugin.isFileExistFlag()) {
			 * iPlugin.getDirectoryContentsPane().addObject( parentNode,
			 * sourceLocalfile.getName(), true); } else { log.info(
			 * "File already exists in that directory, so not showing in tree path!"
			 * ); } } } else { log.error("Parent node is null!"); }
			 */

		} else {
			log.error("1. UserDirectoryTree value is null in irodsImageJ bean or 2.sourceLocalFile is empty");
		}

	}
}
