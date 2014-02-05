package org.bio5.irods.imagej.utilities;

import javax.swing.JOptionPane;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;

public class IrodsTransferStatusCallbackListener implements TransferStatusCallbackListener {

	public void overallStatusCallback(TransferStatus arg0)
			throws JargonException {
		JOptionPane.showMessageDialog(null, "overallStatusCallback");
	}

	public void statusCallback(TransferStatus arg0) throws JargonException {
		JOptionPane.showMessageDialog(null, "statusCallback");
	}

	public CallbackResponse transferAsksWhetherToForceOperation(String arg0,
			boolean arg1) {
		JOptionPane.showMessageDialog(null, "transferAsksWhetherToForceOperation");
		return null;
	}

}
