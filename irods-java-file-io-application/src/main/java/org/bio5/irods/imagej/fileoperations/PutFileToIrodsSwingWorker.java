package org.bio5.irods.imagej.fileoperations;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IPlugin;
import org.bio5.irods.imagej.views.DirectoryContentsWindow;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;

public class PutFileToIrodsSwingWorker extends SwingWorker<Void, Integer> {

	private IPlugin irodsImagej;
	private DataTransferOperations dataTransferOperationsAO;
	private File sourceLocalfile = null;
	private IRODSFile destinaitonIrodsFile = null;
	private String targetResourceName = "";

	/* Logger instantiation */
	static Logger log = Logger.getLogger(DirectoryContentsWindow.class.getName());

	public PutFileToIrodsSwingWorker(IPlugin irodsImagej, File sourceLocalfile,
			IRODSFile destinaitonIrodsFile, String targetResourceName) {
		super();
		this.irodsImagej = irodsImagej;
		this.sourceLocalfile = sourceLocalfile;
		this.destinaitonIrodsFile = destinaitonIrodsFile;
		this.targetResourceName = targetResourceName;
	}

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		if (null != irodsImagej.getIrodsAccount()
				&& null != irodsImagej.getIrodsTransferStatusCallbackListener()
				&& null != irodsImagej.getTransferControlBlock()) {
			dataTransferOperationsAO = irodsImagej.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getDataTransferOperations(irodsImagej.getIrodsAccount());
			if (null != dataTransferOperationsAO) {
				if (null != sourceLocalfile.getAbsolutePath()
						&& null != destinaitonIrodsFile.getAbsolutePath()) {
					/*Option -1 - Absolute path*/
					dataTransferOperationsAO.putOperation(sourceLocalfile
							.getAbsolutePath(), destinaitonIrodsFile
							.getAbsolutePath(), targetResourceName, irodsImagej
							.getIrodsTransferStatusCallbackListener(),
							irodsImagej.getTransferControlBlock());
					
					/*Option -2 - iRODS File */
				/*	dataTransferOperationsAO.putOperation(sourceLocalfile, destinaitonIrodsFile, irodsImagej
							.getIrodsTransferStatusCallbackListener(),
							irodsImagej.getTransferControlBlock());*/
					log.info("file Transfer successfull!!");
				}
			}
		}
		return null;
	}

	@Override
	public void done() {
		JOptionPane.showMessageDialog(null, "File Transfer done successfully");

		if (null != irodsImagej.getUserDirectoryTree()
				&& null != sourceLocalfile) {
			DefaultMutableTreeNode parentNode = null;

			/*
			 * Destination selection - Fetching parent path if leaf node is
			 * selected
			 */
			parentNode = (DefaultMutableTreeNode) irodsImagej
					.getUserDirectoryTree().getLastSelectedPathComponent();
			if (null!=parentNode) {
				if (parentNode.isLeaf()) {
					parentNode = (DefaultMutableTreeNode) parentNode
							.getParent();
				}
			}
			irodsImagej.getDirectoryContentsPane().addObject(parentNode, sourceLocalfile.getName(), true);
		} else {
			log.error("1. UserDirectoryTree value is null in irodsImageJ bean or 2.sourceLocalFile is empty");
		}

	}
}
