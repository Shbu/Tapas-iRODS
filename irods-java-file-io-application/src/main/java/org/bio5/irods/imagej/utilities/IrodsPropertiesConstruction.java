package org.bio5.irods.imagej.utilities;

import org.bio5.irods.imagej.bean.IrodsImageJBean;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class IrodsPropertiesConstruction {

	/* Default jargon Properties */
	public TransferControlBlock constructTransferControlBlockFromJargonProperties(
			IrodsImageJBean irodsImagej) throws JargonException {
		TransferControlBlock defaultTransferControlBlock = null;
		defaultTransferControlBlock = irodsImagej.getIrodsFileSystem()
				.getIRODSAccessObjectFactory()
				.buildDefaultTransferControlBlockBasedOnJargonProperties();
		return defaultTransferControlBlock;
	}

	/* Enhanced jargon Properties */
	public TransferControlBlock constructHighPerformanceTransferControlBlockFromJargonProperties(
			IrodsImageJBean irodsImagej) throws JargonException {
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

}
