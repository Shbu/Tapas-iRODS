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
	private IPlugin irodsImagej;

	public IrodsTransferStatusCallbackListener(IPlugin irodsImagej) {
		super();
		this.irodsImagej = irodsImagej;
		this.jprogressbar = irodsImagej.getJprogressbar();
	}

	/* Logger instantiation */
	static Logger log = Logger
			.getLogger(IrodsTransferStatusCallbackListener.class.getName());

	public void overallStatusCallback(TransferStatus ts) throws JargonException {
	}

	public void statusCallback(TransferStatus ts) throws JargonException {

		log.info("transfer status callback details: " + ts);

		if (ts.getTransferState() == TransferStatus.TransferState.FAILURE) {
			log.error("error occurred in transfer:" + ts);
			JOptionPane.showMessageDialog(null, ts.getTransferState());
			return;

		} else if (ts.isIntraFileStatusReport()) {
			log.info("Transfer state: " + ts.getTransferState()
					+ " | Bytes Transferred so far:" + ts.getBytesTransfered()
					+ "| Total file size inf bytes:" + ts.getTotalSize()
					+ "| Transfer percentage out of 100: "
					+ ts.getBytesTransfered() * 100 / ts.getTotalSize());
			jprogressbar.setMinimum(0);
			jprogressbar.setMaximum(100);
			jprogressbar.setValue((int) (ts.getBytesTransfered() * 100 / ts
					.getTotalSize()));
			if (Constants.JPROGRESS_SET_STRING_PAINTED) {
				jprogressbar.setString("Progress: "
						+ FileUtils.byteCountToDisplaySize(ts
								.getBytesTransfered()) + "/"
						+ FileUtils.byteCountToDisplaySize(ts.getTotalSize()));
			}
		} else if (ts.getTransferState() == TransferStatus.TransferState.IN_PROGRESS_COMPLETE_FILE) {
			log.info("Transfer state: " + ts.getTransferState()
					+ " | Bytes Transferred so far:" + ts.getBytesTransfered()
					+ "| Total file size inf bytes:" + ts.getTotalSize()
					+ "| Transfer percentage out of 100: "
					+ ts.getBytesTransfered() * 100 / ts.getTotalSize());
			jprogressbar.setMinimum(0);
			jprogressbar.setMaximum(100);
			jprogressbar.setValue((int) (ts.getBytesTransfered() * 100 / ts
					.getTotalSize()));
			if (Constants.JPROGRESS_SET_STRING_PAINTED) {
				jprogressbar.setString("Progress: "
						+ FileUtils.byteCountToDisplaySize(ts
								.getBytesTransfered()) + "/"
						+ FileUtils.byteCountToDisplaySize(ts.getTotalSize()));
			}
		} else if (ts.getTransferException() != null) {
			log.info("Exception in file transfer: " + ts.getTransferState());
		} else {
			log.info("Something else is going on!" + ts.getTransferState());
		}
	}

	public CallbackResponse transferAsksWhetherToForceOperation(
			String irodsAbsolutePath, boolean isCollection) {

		CallbackResponse response = CallbackResponse.YES_FOR_ALL;
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
