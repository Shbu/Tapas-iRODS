package org.bio5.irods.iplugin.utilities;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class IrodsPropertiesConstruction {
	static Logger log = Logger.getLogger(IrodsPropertiesConstruction.class
			.getName());

	public TransferControlBlock constructTransferControlBlockFromJargonProperties(
			IPlugin iplugin) throws JargonException {
		TransferControlBlock defaultTransferControlBlock = null;
		defaultTransferControlBlock = iplugin.getIrodsFileSystem()
				.getIRODSAccessObjectFactory()
				.buildDefaultTransferControlBlockBasedOnJargonProperties();

		return defaultTransferControlBlock;
	}

	public TransferControlBlock constructHighPerformanceTransferControlBlockFromJargonProperties(
			IPlugin iplugin) {
		TransferControlBlock highPerformanceTransferControlBlock = null;
		if (null != iplugin) {
			Properties tapasProperties = iplugin.getTapasProperties();
			try {
				highPerformanceTransferControlBlock = iplugin
						.getIrodsFileSystem()
						.getIRODSAccessObjectFactory()
						.buildDefaultTransferControlBlockBasedOnJargonProperties();
			} catch (JargonException jargonException) {
				log.error("Error while constructing defaultTransferControlBlock"
						+ jargonException.getMessage());
			}
			try {
				highPerformanceTransferControlBlock.getTransferOptions()
						.setIntraFileStatusCallbacks(true);
				if (null != tapasProperties) {
					String maxThreadFromProperty = tapasProperties
							.getProperty("tapas.parallel.transfer.max.threads");
					if ((null != maxThreadFromProperty)
							&& ("" != maxThreadFromProperty)) {
						int maxThreads = Integer
								.parseInt(maxThreadFromProperty);
						if ((maxThreads > 0) || (maxThreads == 0)) {
							log.info("tapas.parallel.transfer.max.threads: "
									+ maxThreads);

							highPerformanceTransferControlBlock
									.getTransferOptions().setMaxThreads(
											maxThreads);
						}
					} else {
						highPerformanceTransferControlBlock
								.getTransferOptions().setMaxThreads(
										Constants.MAX_THREADS);

						log.error("tapasProperties is null while constructHighPerformanceTransferControlBlockFromJargonProperties");
						log.info("max threads set to: " + Constants.MAX_THREADS);
					}
				}
				highPerformanceTransferControlBlock.getTransferOptions()
						.setUseParallelTransfer(
								Constants.USE_PARALLEL_TRANSFERS_OPTION);

				highPerformanceTransferControlBlock
						.getTransferOptions()
						.setComputeAndVerifyChecksumAfterTransfer(
								Constants.COMPUTE_AND_VERIFY_CHECKSUM_AFTER_TRANSFER_OPTION);
			} catch (Exception exception) {
				log.error("Error while setting TransferOption in TransferControlBlock"
						+ exception.getMessage());
			}
		}
		return highPerformanceTransferControlBlock;
	}

	public IrodsTransferStatusCallbackListener constructIrodsTransferStatusCallbackListener(
			IPlugin irodsImagej) {
		IrodsTransferStatusCallbackListener irodsTransferStatusCallbackListener = null;
		if ((null != irodsImagej)
				&& (null != irodsImagej.getiRODSFileFactory())) {
			irodsTransferStatusCallbackListener = new IrodsTransferStatusCallbackListener(
					irodsImagej);

			irodsImagej
					.setIrodsTransferStatusCallbackListener(irodsTransferStatusCallbackListener);
		}
		return irodsTransferStatusCallbackListener;
	}
}
