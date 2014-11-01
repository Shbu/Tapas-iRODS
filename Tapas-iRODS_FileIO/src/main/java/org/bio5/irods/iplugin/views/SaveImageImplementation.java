package org.bio5.irods.iplugin.views;

import ij.IJ;
import ij.ImagePlus;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.fileoperations.PutFileToIrodsSwingWorker;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;

public class SaveImageImplementation {
	private IPlugin iplugin;
	private PutFileToIrodsSwingWorker putFile;
	private String destinationFilePath = null;
	private String savePathWithFileName = null;
	static Logger log = Logger.getLogger(SaveImageImplementation.class
			.getName());

	public SaveImageImplementation(IPlugin ipluginInstance) {
		this.iplugin = ipluginInstance;

		assignVariablesToFields();
		saveCurrentEditedImageFileToLocal();
		saveCurrentEditedFileToIrodsByPluginOption();
	}

	private void assignVariablesToFields() {
		if (null != this.iplugin.getCustomPath()) {
			String customPath = this.iplugin.getCustomPath();
			String selectedNodeInTreeForSingleClick = this.iplugin
					.getSelectedNodeInTreeForSingleClick();
			if ((null != customPath)
					&& (null != selectedNodeInTreeForSingleClick)) {
				String[] customPathTokens = IrodsUtilities
						.getStringTokensForGivenURI(customPath);

				String newCustomPathAfterTokenizing = "";
				for (int i = 0; i < customPathTokens.length - 1; i++) {
					newCustomPathAfterTokenizing = newCustomPathAfterTokenizing
							+ IrodsUtilities.getPathSeperator()
							+ customPathTokens[i];
				}
				log.info("newCustomPathAfterTokenizing: "
						+ newCustomPathAfterTokenizing);

				newCustomPathAfterTokenizing = newCustomPathAfterTokenizing
						+ selectedNodeInTreeForSingleClick;
				this.destinationFilePath = newCustomPathAfterTokenizing;
			} else {
				log.error("CustomPath or selectedNodeInTreeForSingleClick object is null");
			}
		}
		if ((null != this.iplugin.getSelectedNodeInTreeForSingleClick())
				&& (this.iplugin.getSelectedNodeInTreeForSingleClick() != "")
				&& (null == this.iplugin.getCustomPath())) {
			this.destinationFilePath = setDestinationPathWithGivenString(this.iplugin
					.getSelectedNodeInTreeForSingleClick());

			log.info("destinationFilePath from singleClickSelection: "
					+ this.destinationFilePath);
		} else if ((null != this.iplugin.getSelectedNodeInTreeForDoubleClick())
				&& (this.iplugin.getSelectedNodeInTreeForDoubleClick() != "")
				&& (null == this.iplugin.getCustomPath())) {
			log.info("Inside assignVariablesToFields() and SelectedNodeInTreeForDoubleClick is not null"
					+ this.iplugin.getSelectedNodeInTreeForDoubleClick());

			this.destinationFilePath = setDestinationPathWithGivenString(this.iplugin
					.getSelectedNodeInTreeForDoubleClick());

			log.info("destinationFilePath from DoubleClickSelection: "
					+ this.destinationFilePath);
		}
	}

	private void saveCurrentEditedImageFileToLocal() {
		ImagePlus imp = IJ.getImage();
		if (null != imp) {
			if (null != this.iplugin.getImageJCacheFolder()) {
				String fileName = imp.getTitle();
				if (null != fileName) {
					this.savePathWithFileName = (this.iplugin
							.getImageJCacheFolder()
							+ IrodsUtilities.getPathSeperator() + fileName);

					log.info("savePathWithFileName : "
							+ this.savePathWithFileName);

					IJ.save(imp, this.savePathWithFileName);
					log.info("File saved to local machine : " + fileName);
					JOptionPane.showMessageDialog(null,
							"File saved to local machine with filename and extention : "
									+ fileName);
				}
			} else {
				log.error("ImageJCacheFolder or ImagePlus is null!");
				JOptionPane.showMessageDialog(null,
						"ImageJCacheFolder or ImagePlus is null!", "Error", 0);
			}
		} else {
			log.error("Error while getting iplugin-imagePlus instance");
			JOptionPane.showMessageDialog(null,
					"Error while getting iplugin-imagePlus instance!", "Error",
					0);

			return;
		}
	}

	private void saveCurrentEditedFileToIrodsByPluginOption() {
		try {
			String targetResourceName = "";

			File sourceLocalfile = null;
			IRODSFile destinationIrodsFile = null;
			if ((null != this.iplugin)
					&& (null != this.iplugin.getIrodsAccount())
					&& (null != this.iplugin.getiRODSFileFactory())) {
				targetResourceName = this.iplugin.getIrodsAccount()
						.getDefaultStorageResource();
				if (this.savePathWithFileName != null) {
					sourceLocalfile = new File(this.savePathWithFileName);
					log.info("savePathWithFileName is set into sourceLocalfile: "
							+ sourceLocalfile);
				} else {
					log.error("savePathWithFileName is null");
					JOptionPane.showMessageDialog(null,
							"savePathWithFileName is null!", "Error", 0);

					return;
				}
				if ((null != this.destinationFilePath)
						&& (this.destinationFilePath != "")
						&& (null != sourceLocalfile)
						&& (sourceLocalfile.getAbsolutePath() != "")) {
					destinationIrodsFile = this.iplugin.getiRODSFileFactory()
							.instanceIRODSFile(this.destinationFilePath);

					log.info("destinaitonIrodsFile absolutepath: "
							+ destinationIrodsFile.getAbsoluteFile());
					try {
						if ((null != sourceLocalfile)
								&& (null != destinationIrodsFile)
								&& (null != targetResourceName)) {
							log.info("Inside core save functionality - just before executing PutFileToIrodsSwingWorker method");
							this.putFile = new PutFileToIrodsSwingWorker(
									this.iplugin, sourceLocalfile,
									destinationIrodsFile, targetResourceName);

							this.putFile.execute();

							this.iplugin.getCancelTransaction_JButton()
									.setEnabled(true);

							log.info("Cancel Transaction button is enabled");

							this.iplugin.setCancelPutTransaction(true);
							this.iplugin.setCancelGetTransaction(false);

							log.info("Executed PutFile method!");
						} else {
							log.error("sourceLocalfile or destinaitonIrodsFile or targetResourceName is null");
							JOptionPane
									.showMessageDialog(
											null,
											"sourceLocalfile or destinaitonIrodsFile or targetResourceName is null!",
											"Error", 0);

							return;
						}
					} catch (Exception execeptionWhileExecutingPutFile) {
						log.error(execeptionWhileExecutingPutFile.getMessage());
						JOptionPane
								.showMessageDialog(null,
										execeptionWhileExecutingPutFile
												.getStackTrace(), "Error", 0);

						return;
					}
				} else {
					log.error("sourceLocalfile or destinationFilePath is null");
					JOptionPane.showMessageDialog(null,
							"sourceLocalfile or destinationFilePath is null!",
							"Error", 0);
				}
			} else {
				log.error("iplugin instance is null");
			}
		} catch (JargonException ExceptionInExecutingSaveButton) {
			log.error(ExceptionInExecutingSaveButton.getMessage());
			ExceptionInExecutingSaveButton.printStackTrace();
			JOptionPane.showMessageDialog(null,
					ExceptionInExecutingSaveButton.getMessage(), "Error", 0);
		}
	}

	private String setDestinationPathWithGivenString(String destinationPath) {
		String destinationFilePath = IrodsUtilities.getPathSeperator()
				+ this.iplugin.getIrodsAccount().getZone()
				+ IrodsUtilities.getPathSeperator() + Constants.HOME_STRING
				+ IrodsUtilities.getPathSeperator() + destinationPath;

		return destinationFilePath;
	}
}
