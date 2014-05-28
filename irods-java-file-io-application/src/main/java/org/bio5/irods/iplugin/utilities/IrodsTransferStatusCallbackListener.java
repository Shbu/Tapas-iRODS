package org.bio5.irods.iplugin.utilities;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;

public class IrodsTransferStatusCallbackListener implements
		TransferStatusCallbackListener {

	private JProgressBar jprogressbar;
	@SuppressWarnings("unused")
	private IPlugin iPlugin;

	public IrodsTransferStatusCallbackListener(IPlugin iPlugin) {
		super();
		this.iPlugin = iPlugin;
		this.jprogressbar = iPlugin.getJprogressbar();
	}

	/* Logger instantiation */
	static Logger log = Logger
			.getLogger(IrodsTransferStatusCallbackListener.class.getName());

	public void overallStatusCallback(TransferStatus ts) throws JargonException {
	}

	public void statusCallback(TransferStatus transferStatus)
			throws JargonException {

		log.info("transfer status callback details: " + transferStatus);

		if (transferStatus.getTransferException() != null) {
			log.info("Exception in file transfer: "
					+ transferStatus.getTransferState());
			iPlugin.setErrorWhileUsingGetOperation(true);
			return;
		}

		if (transferStatus.getTransferState() == TransferStatus.TransferState.CANCELLED) {
			log.info("Transfer cancelled: " + transferStatus.getTransferState());
			iPlugin.setErrorWhileUsingGetOperation(true);
			return;
		}

		if (transferStatus.getTransferState() == TransferStatus.TransferState.FAILURE) {
			log.error("Error occurred in transfer :" + transferStatus);
			JOptionPane.showMessageDialog(null,
					"Error occured while transferring file!", "Error",
					JOptionPane.ERROR_MESSAGE);
			log.info("Setting setErrorWhileUsingGetOperation in iPlugin :"
					+ "True");
			iPlugin.setErrorWhileUsingGetOperation(true);
			return;

		} else if (transferStatus.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_START_FILE) {
			if (!iPlugin.isErrorWhileUsingGetOperation()) {
				log.info("Transfer state: " + transferStatus.getTransferState()
						+ " | Bytes Transferred so far:"
						+ transferStatus.getBytesTransfered()
						+ "| Total file size inf bytes:"
						+ transferStatus.getTotalSize()
						+ "| Transfer percentage out of 100: "
						+ transferStatus.getBytesTransfered() * 100
						/ transferStatus.getTotalSize());
				jprogressbar.setMinimum(0);
				jprogressbar.setMaximum(100);
				jprogressbar.setValue((int) (transferStatus
						.getBytesTransfered() * 100 / transferStatus
						.getTotalSize()));
				if (Constants.JPROGRESS_SET_STRING_PAINTED) {
					jprogressbar.setString("Progress: "
							+ FileUtils.byteCountToDisplaySize(transferStatus
									.getBytesTransfered())
							+ "/"
							+ FileUtils.byteCountToDisplaySize(transferStatus
									.getTotalSize()));
				}
			} else {
				log.info("Skipped displaying progress as file transfer is cancelled!");
			}
		} else if (transferStatus.isIntraFileStatusReport()) {
			log.info("Transfer state: " + transferStatus.getTransferState()
					+ " | Bytes Transferred so far:"
					+ transferStatus.getBytesTransfered()
					+ "| Total file size inf bytes:"
					+ transferStatus.getTotalSize()
					+ "| Transfer percentage out of 100: "
					+ transferStatus.getBytesTransfered() * 100
					/ transferStatus.getTotalSize());
			jprogressbar.setMinimum(0);
			jprogressbar.setMaximum(100);
			jprogressbar
					.setValue((int) (transferStatus.getBytesTransfered() * 100 / transferStatus
							.getTotalSize()));
			if (Constants.JPROGRESS_SET_STRING_PAINTED) {
				jprogressbar.setString("Progress: "
						+ FileUtils.byteCountToDisplaySize(transferStatus
								.getBytesTransfered())
						+ "/"
						+ FileUtils.byteCountToDisplaySize(transferStatus
								.getTotalSize()));
			}
		} else if (transferStatus.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_COMPLETE_FILE) {
			if (!iPlugin.isErrorWhileUsingGetOperation()) {
				log.info("Transfer state: " + transferStatus.getTransferState()
						+ " | Bytes Transferred so far:"
						+ transferStatus.getBytesTransfered()
						+ "| Total file size inf bytes:"
						+ transferStatus.getTotalSize()
						+ "| Transfer percentage out of 100: "
						+ transferStatus.getBytesTransfered() * 100
						/ transferStatus.getTotalSize());
				jprogressbar.setMinimum(0);
				jprogressbar.setMaximum(100);
				jprogressbar.setValue((int) (transferStatus
						.getBytesTransfered() * 100 / transferStatus
						.getTotalSize()));
				if (Constants.JPROGRESS_SET_STRING_PAINTED) {
					jprogressbar.setString("Progress: "
							+ FileUtils.byteCountToDisplaySize(transferStatus
									.getBytesTransfered())
							+ "/"
							+ FileUtils.byteCountToDisplaySize(transferStatus
									.getTotalSize()));
				}
			} else {
				log.info("Skipped displaying progress as file transfer is cancelled!");
			}
		}

		else {
			log.info("Something else is going on!"
					+ transferStatus.getTransferState());
		}
	}

	public CallbackResponse transferAsksWhetherToForceOperation(
			String irodsAbsolutePath, boolean isCollection) {

		// CallbackResponse response = CallbackResponse.YES_FOR_ALL;
		CallbackResponse response = null;
		StringBuilder stringBuilder = new StringBuilder(
				isCollection ? "Folder '" : "File'");
		stringBuilder.append(irodsAbsolutePath);
		stringBuilder.append("' already exists. Do you wish to overwrite?");

		Object[] options = { "No to All", "No", "Yes to All", "Yes" };
		int answer = JOptionPane.showOptionDialog(null, stringBuilder,
				"Confirm Transfer Overwrite", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				javax.swing.UIManager.getIcon("OptionPane.questionIcon"),
				options, options[2]);

		/*
		 * Fix issue - Imagej shouldn't open images when X button is clicked on
		 * OptionDialogue
		 */

		if (answer == JOptionPane.CLOSED_OPTION) {
			response = CallbackResponse.CANCEL;
			iPlugin.setErrorWhileUsingGetOperation(true);
		}

		switch (answer) {
		case 0:
			response = CallbackResponse.NO_FOR_ALL;
			break;
		case 1:
			response = CallbackResponse.NO_THIS_FILE;
			break;
		case 2:
			response = CallbackResponse.YES_FOR_ALL;
			break;
		case 3:
			response = CallbackResponse.YES_THIS_FILE;
			break;
		}
		return response;
	}
}
