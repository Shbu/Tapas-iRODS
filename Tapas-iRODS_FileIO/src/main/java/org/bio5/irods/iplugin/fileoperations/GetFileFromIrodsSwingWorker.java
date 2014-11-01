package org.bio5.irods.iplugin.fileoperations;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;
import java.net.SocketTimeoutException;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.exception.DataNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class GetFileFromIrodsSwingWorker extends SwingWorker<Void, Integer>
		implements Runnable {
	private IRODSFileFactory iRODSFileFactory;
	private String treePath;
	private DataTransferOperations dataTransferOperationsAO;
	private IPlugin iPlugin;
	private DataObjectAO dataObjectAO;
	private TransferControlBlock transferControlBlock;
	IRODSFile sourceIrodsFilePath = null;
	static Logger log = Logger.getLogger(GetFileFromIrodsSwingWorker.class
			.getName());

	public GetFileFromIrodsSwingWorker(IRODSFileFactory iRODSFileFactory,
			String treePath, IPlugin irodsImagej, JProgressBar progressbar) {
		this.iRODSFileFactory = iRODSFileFactory;
		this.treePath = treePath;
		this.iPlugin = irodsImagej;
	}

	public Void doInBackground() throws JargonException, SocketTimeoutException {
		log.info("finalTreePath:" + this.treePath);
		if ((null != this.iPlugin.getCustomPath()) && (null != this.treePath)) {
			String[] customPathTokens = IrodsUtilities
					.getStringTokensForGivenURI(this.treePath);

			String newtreePath = "";
			log.info("length of tokens: " + customPathTokens.length);
			for (int i = 2; i < customPathTokens.length; i++) {
				newtreePath = newtreePath + IrodsUtilities.getPathSeperator()
						+ customPathTokens[i].toString();
			}
			log.info("Final internal loop path for custom path: " + newtreePath);

			newtreePath = IrodsUtilities
					.replaceBackSlashWithForwardSlash_ViceVersa(newtreePath);
			this.treePath = (this.iPlugin.getCustomPath() + newtreePath);
			log.info("final tree path: " + this.treePath);
		} else {
			log.error("either customPath or treePath is null");
		}
		if (null != this.iPlugin) {
			this.transferControlBlock = this.iPlugin.getTransferControlBlock();
			if (null != this.transferControlBlock) {
				this.iPlugin.setTransferOptions(this.transferControlBlock
						.getTransferOptions());
			} else {
				log.error("transferControlBlock is null");
			}
			this.dataTransferOperationsAO = this.iPlugin.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getDataTransferOperations(this.iPlugin.getIrodsAccount());
			if (null != this.iPlugin.getCustomPath()) {
				this.sourceIrodsFilePath = this.iRODSFileFactory
						.instanceIRODSFile(this.treePath);

				log.info("sourceIrodsFilePath" + this.sourceIrodsFilePath);
			} else if ((this.iPlugin.isHomeDirectoryTheRootNode())
					&& (null == this.iPlugin.getCustomPath())) {
				this.sourceIrodsFilePath = this.iRODSFileFactory
						.instanceIRODSFile(TapasCoreFunctions
								.getRootDirectoryPath(this.iPlugin)
								+ this.treePath);

				log.info("sourceIrodsFilePath" + this.sourceIrodsFilePath);
			} else if ((!this.iPlugin.isHomeDirectoryTheRootNode())
					&& (null == this.iPlugin.getCustomPath())) {
				this.sourceIrodsFilePath = this.iRODSFileFactory
						.instanceIRODSFile(TapasCoreFunctions
								.getHomeDirectoryPath(this.iPlugin)
								+ this.treePath);

				log.info("sourceIrodsFilePath" + this.sourceIrodsFilePath);
			}
			this.dataObjectAO = this.iPlugin.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getDataObjectAO(this.iPlugin.getIrodsAccount());

			String md5ChecksumLocalFile = null;
			String md5ChecksumServerFile = null;
			try {
				md5ChecksumServerFile = this.dataObjectAO
						.computeMD5ChecksumOnDataObject(this.sourceIrodsFilePath);
			} catch (Exception e) {
				log.info("Error while reading MD5 checksum of md5ChecksumServerFile"
						+ e.getMessage());

				JOptionPane
						.showMessageDialog(
								null,
								"Error while reading MD5 checksum of md5ChecksumServerFile!",
								"Error", 0);
			}
			File destinationLocalFilePath = new File(
					this.iPlugin.getImageJCacheFolder());

			log.info("sourceIrodsFilePath before inserting file"
					+ this.sourceIrodsFilePath);

			log.info("destinationLocalFilePath before inserting file"
					+ destinationLocalFilePath);

			File localFile = new File(
					destinationLocalFilePath.getAbsolutePath()
							+ IrodsUtilities.getPathSeperator()
							+ this.sourceIrodsFilePath.getName());

			md5ChecksumLocalFile = IrodsUtilities
					.calculateMD5CheckSum(localFile);

			log.info("MD5checksum of iRODS server file: "
					+ md5ChecksumServerFile);

			log.info("MD5checksum of local file: " + md5ChecksumLocalFile);
			if ((null != md5ChecksumLocalFile)
					&& (null != md5ChecksumServerFile)
					&& ("" != md5ChecksumLocalFile)
					&& ("" != md5ChecksumServerFile)) {
				log.info("MD5 checksum compared - are they Similar files ?"
						+ md5ChecksumLocalFile.equals(md5ChecksumServerFile));
				if (!md5ChecksumLocalFile.equals(md5ChecksumServerFile)) {
					JOptionPane
							.showMessageDialog(
									null,
									"Local cache directory have files with same name but MD5 checksum is different!",
									"Information", 1);
				}
				if (md5ChecksumLocalFile.equals(md5ChecksumServerFile)) {
					JOptionPane
							.showMessageDialog(
									null,
									"File already exists in local. MD5 checksum of local file and remote file is same!",
									"Information", 1);
				}
			}
			try {
				if ((null != this.sourceIrodsFilePath)
						&& (null != destinationLocalFilePath)) {
					if (null != this.iPlugin) {
						log.info("Defaulting ErrorWhileUsingGetOperation value to :False");

						this.iPlugin.setErrorWhileUsingGetOperation(false);

						log.info("Transfer Options in IntraFileStatusCallBack status: "
								+ this.transferControlBlock
										.getTransferOptions());

						this.dataTransferOperationsAO
								.getOperation(
										this.sourceIrodsFilePath,
										destinationLocalFilePath,
										this.iPlugin
												.getIrodsTransferStatusCallbackListener(),
										this.transferControlBlock);
						if (!this.iPlugin.isErrorWhileUsingGetOperation()) {
							log.info("Executing openImageUsingImageJ method");
							openImageUsingImageJ();
						} else {
							log.error("Error while transferring files");
							JOptionPane.showMessageDialog(null,
									"Error while transfering files!", "Error",
									0);
						}
					}
				}
			} catch (OverwriteException overwriteException) {
				log.error("File with same name already exist in local directory! "
						+ overwriteException.getMessage());

				JOptionPane
						.showMessageDialog(
								null,
								"File with same name already exist in local directory!",
								"Information", 1);

				File fileInLocal = new File(
						destinationLocalFilePath.getAbsolutePath()
								+ IrodsUtilities.getPathSeperator()
								+ this.sourceIrodsFilePath.getName());

				md5ChecksumLocalFile = IrodsUtilities
						.calculateMD5CheckSum(fileInLocal);

				log.info("MD5checksum of local file: " + md5ChecksumLocalFile);

				log.info("MD5 checksum compared - Similar files:"
						+ md5ChecksumLocalFile.equals(md5ChecksumServerFile));
				if (!md5ChecksumLocalFile.equals(md5ChecksumServerFile)) {
					JOptionPane
							.showMessageDialog(null,
									"File names are same but MD5 checksum is different!");
				}
				overwriteException.printStackTrace();
			} catch (DataNotFoundException dataNotFoundException) {
				JOptionPane.showMessageDialog(null, "dataNotFoundException!",
						"Error", 0);

				log.info("Error while pulling files!"
						+ dataNotFoundException.getMessage());
			} catch (JargonException jargonException) {
				JOptionPane.showMessageDialog(null,
						"Error while pulling files!", "Error", 0);

				log.info("Error while pulling files!"
						+ jargonException.getMessage());
			}
		}
		return null;
	}

	public void done() {
	}

	private void openImageUsingImageJ() {
		Opener imagejOpener = new Opener();
		ImagePlus imagePlusInstanceOfCurrentActiveImage = null;
		if (null != this.iPlugin.getImageJCacheFolder()) {
			String imageFilePath = this.iPlugin.getImageJCacheFolder()
					+ IrodsUtilities.getPathSeperator()
					+ this.sourceIrodsFilePath.getName();

			log.info("Current file opened by user: " + imageFilePath);
			ImagePlus imagePlus = imagejOpener.openImage(imageFilePath);
			if (null != imagePlus) {
				this.iPlugin.setImagePlus(imagePlus);
				log.info("ImagePlus instance is not null and before calling show() function of ImagePlus class");
				imagePlus.show();
				imagePlusInstanceOfCurrentActiveImage = IJ.getImage();

				log.info("ImagePlus instance of current image from IJ"
						+ imagePlusInstanceOfCurrentActiveImage);
				if (null != imagePlusInstanceOfCurrentActiveImage) {
					log.info("Current Image from IJ"
							+ imagePlusInstanceOfCurrentActiveImage.getImage());

					log.error("image windows are open");
					this.iPlugin.setImageOpened(true);
				} else {
					log.error("No image windows are open");
					this.iPlugin.setImageOpened(false);
				}
				log.info("irodsImagej.isImageOpened is set to true");
			} else {
				log.error("ImagePlus instance in GetFileFromIrodsSwingWorker is null and irodsImagej.isImageOpened is false");
				JOptionPane.showMessageDialog(null,
						"File format is not supported by ImageJ!", "Error", 0);
			}
		} else {
			IJ.showMessage("ImageJ is not able to open requested file!");
			IJ.showStatus("ImageJ is not able to open requested file!");
			log.error("ImagePlus instance is null and opening file Failed.");
			JOptionPane.showMessageDialog(null,
					"ImagePlus instance is null and opening file Failed!",
					"Error", 0);
		}
	}
}
