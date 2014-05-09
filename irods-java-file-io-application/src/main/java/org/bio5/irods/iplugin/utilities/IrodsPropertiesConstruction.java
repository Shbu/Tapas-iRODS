package org.bio5.irods.iplugin.utilities;

import javax.swing.JProgressBar;

import org.bio5.irods.iplugin.bean.IPlugin;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class IrodsPropertiesConstruction {

	/* Default jargon Properties */
	public TransferControlBlock constructTransferControlBlockFromJargonProperties(
			IPlugin irodsImagej) throws JargonException {
		TransferControlBlock defaultTransferControlBlock = null;
		defaultTransferControlBlock = irodsImagej.getIrodsFileSystem()
				.getIRODSAccessObjectFactory()
				.buildDefaultTransferControlBlockBasedOnJargonProperties();
		return defaultTransferControlBlock;
	}

	/* Enhanced jargon Properties */
	public TransferControlBlock constructHighPerformanceTransferControlBlockFromJargonProperties(
			IPlugin irodsImagej) throws JargonException {
		TransferControlBlock defaultTransferControlBlock = null;
		defaultTransferControlBlock = irodsImagej.getIrodsFileSystem()
				.getIRODSAccessObjectFactory()
				.buildDefaultTransferControlBlockBasedOnJargonProperties();

		defaultTransferControlBlock.getTransferOptions()
				.setIntraFileStatusCallbacks(true);
		defaultTransferControlBlock.getTransferOptions().setMaxThreads(
				Constants.MAX_THREADS);
		defaultTransferControlBlock.getTransferOptions()
				.setUseParallelTransfer(true);
		return defaultTransferControlBlock;
	}

	public IrodsTransferStatusCallbackListener constructIrodsTransferStatusCallbackListener(
			IPlugin irodsImagej) {
		IrodsTransferStatusCallbackListener irodsTransferStatusCallbackListener = null;
		if (null != irodsImagej && null != irodsImagej.getiRODSFileFactory()) {
			irodsTransferStatusCallbackListener = new IrodsTransferStatusCallbackListener(
					irodsImagej);
			irodsImagej
					.setIrodsTransferStatusCallbackListener(irodsTransferStatusCallbackListener);
		}
		return irodsTransferStatusCallbackListener;

	}

}
