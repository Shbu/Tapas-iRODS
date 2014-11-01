package org.bio5.irods.iplugin.fileoperations;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
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
	static Logger log = Logger.getLogger(PutFileToIrodsSwingWorker.class
			.getName());

	public PutFileToIrodsSwingWorker(IPlugin irodsImagej, File sourceLocalfile,
			IRODSFile destinaitonIrodsFile, String targetResourceName) {
		this.iPlugin = irodsImagej;
		this.sourceLocalfile = sourceLocalfile;
		this.destinaitonIrodsFile = destinaitonIrodsFile;
		this.targetResourceName = targetResourceName;
	}

	protected Void doInBackground() {
		if ((null != this.iPlugin.getIrodsAccount())
				&& (null != this.iPlugin
						.getIrodsTransferStatusCallbackListener())
				&& (null != this.iPlugin.getTransferControlBlock())) {
			try {
				this.dataTransferOperationsAO = this.iPlugin
						.getIrodsFileSystem()
						.getIRODSAccessObjectFactory()
						.getDataTransferOperations(
								this.iPlugin.getIrodsAccount());
			} catch (JargonException jargonException) {
				log.error("Error while getting dataTransferOperationsAO object from FileSystem !"
						+ jargonException.getMessage());
			}
			if ((null != this.dataTransferOperationsAO)
					&& (null != this.sourceLocalfile.getAbsolutePath())
					&& (null != this.destinaitonIrodsFile.getAbsolutePath())) {
				try {
					log.info("Defaulting ErrorWhileUsingGetOperation value to :False");

					log.info("Source file before uploading:"
							+ this.sourceLocalfile.getAbsolutePath());
					log.info("Destination file before uploading:"
							+ this.destinaitonIrodsFile.getAbsolutePath());
					this.iPlugin.setErrorWhileUsingGetOperation(false);
					this.iPlugin.setDestinationPath(this.destinaitonIrodsFile
							.getAbsolutePath());

					this.dataTransferOperationsAO.putOperation(
							this.sourceLocalfile.getAbsolutePath(),
							this.destinaitonIrodsFile.getAbsolutePath(),
							this.targetResourceName, this.iPlugin
									.getIrodsTransferStatusCallbackListener(),
							this.iPlugin.getTransferControlBlock());
					if (!this.iPlugin.isErrorWhileUsingGetOperation()) {
						log.info("file Transfer successfull!!");
						JOptionPane.showMessageDialog(null,
								"File Transfer done successfully");

						reloadChildNodeAfterUploading();
					} else {
						log.error("Error while transfering files");
						JOptionPane.showMessageDialog(null,
								"Error while transferring files!",
								"Error in uploading file", 0);
					}
				} catch (DataNotFoundException dataNotFoundException) {
					log.error("DataNotFoundException while uploading file to irods"
							+ dataNotFoundException.getMessage());

					dataNotFoundException.printStackTrace();
					JOptionPane
							.showMessageDialog(
									null,
									"DataNotFoundException while uploading file to server!",
									"Error", 0);
				} catch (OverwriteException overWriteException) {
					log.error("overWriteException"
							+ overWriteException.getMessage());

					JOptionPane
							.showMessageDialog(
									null,
									"OverwriteException while uploading file to server!",
									"Error", 0);
				} catch (JargonException jargonException) {
					log.error("JargonException" + jargonException.getMessage());

					JOptionPane.showMessageDialog(null,
							"JargonException while uploading file to server!",
							"Error", 0);
				}
			}
		} else {
			log.error("Required parameters are null in PutFileToIrodsSwingWorker-doInBackground  operation");
		}
		return null;
	}

	public void done() {
	}

	private void reloadChildNodeAfterUploading() {
		if ((null != this.iPlugin.getUserDirectoryTree())
				&& (null != this.sourceLocalfile)) {
			DefaultMutableTreeNode node = null;
			log.info("inside reloadChildNodeAfterUploading");

			TreePath treePathToRetrieveInternalPaths = null;
			treePathToRetrieveInternalPaths = this.iPlugin
					.getUserDirectoryTree().getSelectionPath();

			node = (DefaultMutableTreeNode) this.iPlugin.getUserDirectoryTree()
					.getLastSelectedPathComponent();

			Object[] treePathForInternalNodes = null;
			treePathForInternalNodes = treePathToRetrieveInternalPaths
					.getPath();
			if (null != node) {
				log.info("Empty folder check:" + this.iPlugin.isEmptyFolder());
				if ((node.isLeaf()) && (!this.iPlugin.isEmptyFolder())) {
					log.info("node is leaf and not empty folder");
					node = (DefaultMutableTreeNode) node.getParent();
					treePathForInternalNodes = treePathToRetrieveInternalPaths
							.getParentPath().getPath();
				}
				node.removeAllChildren();
				this.iPlugin.getTreeModel().nodeStructureChanged(node);
			} else {
				log.error("node is null");
			}
			RetrieveInternalNodesSwingWorker retrieve = null;
			String singleClickPathOnlyTillParentFolderWithSizeCheck = this.iPlugin
					.getSingleClickPathOnlyTillParentFolderWithSizeCheck();

			log.info("singleClickPathOnlyTillParentFolderWithSizeCheck"
					+ singleClickPathOnlyTillParentFolderWithSizeCheck);
			if ((null != treePathForInternalNodes)
					&& (null != singleClickPathOnlyTillParentFolderWithSizeCheck)) {
				log.info("TreePath: " + treePathForInternalNodes.toString());
				retrieve = new RetrieveInternalNodesSwingWorker(
						singleClickPathOnlyTillParentFolderWithSizeCheck, null,
						this.iPlugin);
				try {
					if (null != retrieve) {
						retrieve.doInBackground();
					}
				} catch (Exception exception) {
					log.error("Exception while retrieving internal files: "
							+ exception.getMessage());
				}
			} else {
				log.error("treePathForInternalNodes is null");
			}
			if (null != this.iPlugin.getChildNodesListAfterLazyLoading()) {
				if (this.iPlugin.getChildNodesListAfterLazyLoading().size() > 0) {
					for (int i = 0; i < this.iPlugin
							.getChildNodesListAfterLazyLoading().size(); i++) {
						log.info("node name before inserting:"
								+ this.iPlugin
										.getChildNodesListAfterLazyLoading()
										.get(i));

						log.info("child count: " + node.getChildCount());
						try {
							log.info("adding node to treemodel");
							this.iPlugin
									.getTreeModel()
									.insertNodeInto(
											(MutableTreeNode) this.iPlugin
													.getChildNodesListAfterLazyLoading()
													.get(i), node,
											node.getChildCount());
						} catch (Exception e) {
							log.error("Error while adding node elements to path: "
									+ e.getMessage());
						}
					}
				}
				log.error("iPlugin.getChildNodesListAfterLazyLoading().size() is 0");
			} else {
				log.error("iPlugin.getChildNodesListAfterLazyLoading() is null");
			}
		} else {
			log.error("1. UserDirectoryTree value is null in irodsImageJ bean or 2.sourceLocalFile is empty");
		}
	}
}
