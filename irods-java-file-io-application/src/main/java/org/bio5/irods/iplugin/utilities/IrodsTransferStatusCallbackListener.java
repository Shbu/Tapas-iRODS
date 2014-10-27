package org.bio5.irods.iplugin.utilities;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;

public class IrodsTransferStatusCallbackListener implements
		TransferStatusCallbackListener {
	private JProgressBar jprogressbar;
	private IPlugin iPlugin;

	public IrodsTransferStatusCallbackListener(IPlugin iPlugin) {
		this.iPlugin = iPlugin;
		this.jprogressbar = iPlugin.getJprogressbar();
	}

	static Logger log = Logger
			.getLogger(IrodsTransferStatusCallbackListener.class.getName());

	public void overallStatusCallback(TransferStatus ts) throws JargonException {
		log.info("inside overallStatusCallback block");
	}

	public void statusCallback(TransferStatus transferStatus)
			throws JargonException {
		log.info("transfer status callback details: "
				+ transferStatus.getTransferState());
		if (transferStatus.getTransferException() != null) {
			log.info("Exception in file transfer: "
					+ transferStatus.getTransferException());

			log.error("Exception occured:"
					+ transferStatus.getTransferException().getMessage());
			if (transferStatus.getTransferException().getLocalizedMessage()
					.contains("No access to item in catalog")) {
				JOptionPane
						.showMessageDialog(
								null,
								"Transaction Failed:  CatNoAccessException - No access to item in catalog",
								"Error", 0);

				this.iPlugin.setErrorWhileUsingGetOperation(true);
				return;
			}
			if (transferStatus.getTransferException().getLocalizedMessage()
					.contains("InterruptedException")) {
				JOptionPane
						.showMessageDialog(
								null,
								"Cannot download file:  User interrupted file transaction",
								"Error", 0);

				this.iPlugin.setErrorWhileUsingGetOperation(true);
				return;
			}
			if (transferStatus.getTransferException().getLocalizedMessage()
					.contains("error in parallel transfer")) {
				JOptionPane
						.showMessageDialog(
								null,
								"Cannot download file:  User cancelled file transaction",
								"Error", 0);

				this.iPlugin.setErrorWhileUsingGetOperation(true);
				return;
			}
			if (transferStatus.getTransferException().getLocalizedMessage()
					.contains("ArithmeticException")) {
				JOptionPane.showMessageDialog(null,
						"Cannot download file:  File size is 0 bytes", "Error",
						0);

				this.iPlugin.setErrorWhileUsingGetOperation(true);
				return;
			}
			JOptionPane.showMessageDialog(null, "Exception occured : "
					+ transferStatus.getTransferException(), "Error", 0);

			this.iPlugin.setErrorWhileUsingGetOperation(true);
			return;
		}
		if (transferStatus.getTransferState() == TransferStatus.TransferState.FAILURE) {
			log.error("Error occurred in transfer :" + transferStatus);
			JOptionPane.showMessageDialog(null,
					"Error occured while transferring file!", "Error", 0);

			log.info("Setting setErrorWhileUsingGetOperation in iPlugin :True");

			this.iPlugin.setErrorWhileUsingGetOperation(true);
			return;
		}
		if (transferStatus.getTransferState() == TransferStatus.TransferState.CANCELLED) {
			log.info("Transfer cancelled: " + transferStatus.getTransferState());
			this.iPlugin.setErrorWhileUsingGetOperation(true);
			return;
		}
		if (transferStatus.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_START_FILE) {
			if (!this.iPlugin.isErrorWhileUsingGetOperation()) {
				log.info("Inside IN_PROGRESS_START_FILE");
			} else {
				log.info("Skipped displaying progress as file transfer is cancelled!");
			}
		}
		if (transferStatus.isIntraFileStatusReport()) {
			log.info("Transfer state: " + transferStatus.getTransferState()
					+ " | Bytes Transferred so far:"
					+ transferStatus.getBytesTransfered()
					+ "| Total file size inf bytes:"
					+ transferStatus.getTotalSize()
					+ "| Transfer percentage out of 100: "
					+ transferStatus.getBytesTransfered() * 100L
					/ transferStatus.getTotalSize());

			this.jprogressbar.setMinimum(0);
			this.jprogressbar.setMaximum(100);
			this.jprogressbar.setValue((int) (transferStatus
					.getBytesTransfered() * 100L / transferStatus
					.getTotalSize()));
			if (Constants.JPROGRESS_SET_STRING_PAINTED) {
				this.jprogressbar.setString("Progress: "
						+ FileUtils.byteCountToDisplaySize(transferStatus
								.getBytesTransfered())
						+ "/"
						+ FileUtils.byteCountToDisplaySize(transferStatus
								.getTotalSize()));
			}
		}
		if (transferStatus.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_COMPLETE_FILE) {
			if (!this.iPlugin.isErrorWhileUsingGetOperation()) {
				log.info("Transfer state: " + transferStatus.getTransferState()
						+ " | Bytes Transferred so far:"
						+ transferStatus.getBytesTransfered()
						+ "| Total file size inf bytes:"
						+ transferStatus.getTotalSize()
						+ "| Transfer percentage out of 100: "
						+ transferStatus.getBytesTransfered() * 100L
						/ transferStatus.getTotalSize());

				this.jprogressbar.setMinimum(0);
				this.jprogressbar.setMaximum(100);
				this.jprogressbar.setValue((int) (transferStatus
						.getBytesTransfered() * 100L / transferStatus
						.getTotalSize()));
				if (Constants.JPROGRESS_SET_STRING_PAINTED) {
					this.jprogressbar.setString("Progress: "
							+ FileUtils.byteCountToDisplaySize(transferStatus
									.getBytesTransfered())
							+ "/"
							+ FileUtils.byteCountToDisplaySize(transferStatus
									.getTotalSize()));
				}
			} else {
				log.info("Skipped displaying progress as file transfer is cancelled!");
			}
		} else {
			log.info("Something else is going on!"
					+ transferStatus.getTransferState());
		}
	}

	public TransferStatusCallbackListener.CallbackResponse transferAsksWhetherToForceOperation(
			String irodsAbsolutePath, boolean isCollection) {
		this.iPlugin.setFileExistFlag(true);

		TransferStatusCallbackListener.CallbackResponse response = null;
		StringBuilder stringBuilder = new StringBuilder(
				isCollection ? "Folder '" : "File'");

		stringBuilder.append(irodsAbsolutePath);
		stringBuilder
				.append("' already exists. Do you wish to overwrite? (Note: Yes/No to All - Includes all future transactions)");

		Object[] options = { "No to All", "No", "Yes to All", "Yes" };
		int answer = JOptionPane.showOptionDialog(null, stringBuilder,
				"Confirm Transfer Overwrite", -1, 3,
				UIManager.getIcon("OptionPane.questionIcon"), options,
				options[2]);
		if (answer == -1) {
			response = TransferStatusCallbackListener.CallbackResponse.CANCEL;
			this.iPlugin.setErrorWhileUsingGetOperation(true);
		}
		switch (answer) {
		case 0:
			response = TransferStatusCallbackListener.CallbackResponse.NO_FOR_ALL;
			break;
		case 1:
			response = TransferStatusCallbackListener.CallbackResponse.NO_THIS_FILE;
			break;
		case 2:
			response = TransferStatusCallbackListener.CallbackResponse.YES_FOR_ALL;
			break;
		case 3:
			response = TransferStatusCallbackListener.CallbackResponse.YES_THIS_FILE;
		}
		return response;
	}

	public synchronized void cancelTransferUsingTransferControlBlock() {
		log.info("Inside cancelTransferUsingTransferControlBlock");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				IrodsTransferStatusCallbackListener.this.iPlugin
						.getTransferControlBlock().setCancelled(true);
			}
		});
	}

	public void signalUnhandledConveyorException(Exception paramException) {
		log.error("exception is occurring in conveyor framework",
				paramException);
	}
}
