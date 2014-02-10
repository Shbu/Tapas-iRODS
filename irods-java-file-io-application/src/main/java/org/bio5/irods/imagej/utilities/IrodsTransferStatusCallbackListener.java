package org.bio5.irods.imagej.utilities;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IrodsImageJ;
import org.bio5.irods.imagej.fileoperations.GetFileFromIrods;
import org.bio5.irods.imagej.views.DirectoryContentsPane;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;

public class IrodsTransferStatusCallbackListener extends GetFileFromIrods implements TransferStatusCallbackListener{
	
	/*Logger instantiation*/
	static Logger log = Logger.getLogger(
			IrodsTransferStatusCallbackListener.class.getName());
	
	private static JProgressBar jprogressbar;
	
	public IrodsTransferStatusCallbackListener(
			IRODSFileFactory iRODSFileFactory, String treePath,
			IrodsImageJ irodsImagej, JProgressBar progressbar) {
		super(iRODSFileFactory, treePath, irodsImagej, jprogressbar =progressbar);
	}

	public void overallStatusCallback(TransferStatus ts)
			throws JargonException {
	}

	public void statusCallback( TransferStatus ts) throws JargonException {
		log.info("TransferStatus" +ts.getBytesTransfered());
		System.out.println("Bytes Transferred" +ts.getBytesTransfered() +"Total Bytes" +ts.getTotalSize() +"value out of 100" +ts.getBytesTransfered()*100/ts.getTotalSize());
		jprogressbar.setMinimum(0);
		jprogressbar.setMaximum((int) ts.getTotalSize()/1024);
		jprogressbar.setValue((int)(ts.getBytesTransfered()/1024));
	}

	public CallbackResponse transferAsksWhetherToForceOperation(String arg0,
			boolean arg1) {
		return null;
	}

}
