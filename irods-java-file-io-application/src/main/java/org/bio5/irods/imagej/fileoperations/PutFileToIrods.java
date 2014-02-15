package org.bio5.irods.imagej.fileoperations;

import java.io.File;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IrodsImageJBean;
import org.bio5.irods.imagej.views.DirectoryContentsPane;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;

public class PutFileToIrods extends SwingWorker<Void, Integer> {

	private IrodsImageJBean irodsImagej;
	private DataTransferOperations dataTransferOperationsAO;
	private File sourceLocalfile = null;
	private IRODSFile destinaitonIrodsFile = null;
	private String targetResourceName = "";
	private JProgressBar progressBar = null;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(DirectoryContentsPane.class.getName());

	public PutFileToIrods(IrodsImageJBean irodsImagej, File sourceLocalfile,
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
					dataTransferOperationsAO.putOperation(sourceLocalfile
							.getAbsolutePath(), destinaitonIrodsFile
							.getAbsolutePath(), targetResourceName, irodsImagej
							.getIrodsTransferStatusCallbackListener(),
							irodsImagej.getTransferControlBlock());
				}
			}
		}
		return null;
	}
}
