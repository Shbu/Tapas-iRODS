package org.bio5.irods.iplugin.fileoperations;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.irods.jargon.core.exception.DataNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;

public class PutFileToIrodsSwingWorker extends SwingWorker<Void, Integer> {

	private IPlugin irodsImagej;
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
		this.irodsImagej = irodsImagej;
		this.sourceLocalfile = sourceLocalfile;
		this.destinaitonIrodsFile = destinaitonIrodsFile;
		this.targetResourceName = targetResourceName;
	}

	@Override
	protected Void doInBackground() {
		// TODO Auto-generated method stub
		if (null != irodsImagej.getIrodsAccount()
				&& null != irodsImagej.getIrodsTransferStatusCallbackListener()
				&& null != irodsImagej.getTransferControlBlock()) {
			try {
				dataTransferOperationsAO = irodsImagej
						.getIrodsFileSystem()
						.getIRODSAccessObjectFactory()
						.getDataTransferOperations(
								irodsImagej.getIrodsAccount());
			} catch (JargonException jargonException) {
				log.error("Error while getting dataTransferOperationsAO object from FileSystem !"
						+ jargonException.getMessage());
			}
			if (null != dataTransferOperationsAO) {
				if (null != sourceLocalfile.getAbsolutePath()
						&& null != destinaitonIrodsFile.getAbsolutePath()) {
					/* Option -1 - Absolute path */
					try {
						dataTransferOperationsAO
								.putOperation(
										sourceLocalfile.getAbsolutePath(),
										destinaitonIrodsFile.getAbsolutePath(),
										targetResourceName,
										irodsImagej
												.getIrodsTransferStatusCallbackListener(),
										irodsImagej.getTransferControlBlock());
						log.info("file Transfer successfull!!");
						JOptionPane.showMessageDialog(null,
								"File Transfer done successfully");
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
		if (null != irodsImagej.getUserDirectoryTree()
				&& null != sourceLocalfile) {
			DefaultMutableTreeNode parentNode = null;

			/*
			 * Destination selection - Fetching parent path if leaf node is
			 * selected
			 */
			parentNode = (DefaultMutableTreeNode) irodsImagej
					.getUserDirectoryTree().getLastSelectedPathComponent();
			if (null != parentNode) {
				if (parentNode.isLeaf()) {
					parentNode = (DefaultMutableTreeNode) parentNode
							.getParent();
				}
			} else {
				log.error("parentNode in PutFileToIrodsSwingWorker is null");
			}
			irodsImagej.getDirectoryContentsPane().addObject(parentNode,
					sourceLocalfile.getName(), true);
		} else {
			log.error("1. UserDirectoryTree value is null in irodsImageJ bean or 2.sourceLocalFile is empty");
		}

	}
}
