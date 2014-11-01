package org.bio5.irods.iplugin.views;

import ij.IJ;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.fileoperations.PutFileToIrodsSwingWorker;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;

public class SaveImagePanelImplementation extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField_LocalFileName;
	private IPlugin iplugin;
	private JTextField textField_irodsDestinationPath;
	private JFileChooser fileChooser;
	private PutFileToIrodsSwingWorker putFile;
	private JLabel lbl_localFileName;
	private JLabel lbl_irodsDestinationPath;
	private String destinationFilePath = null;
	private String savePathWithFileName = null;
	static Logger log = Logger.getLogger(SaveImagePanelImplementation.class
			.getName());

	public SaveImagePanelImplementation(IPlugin ipluginInstance) {
		setTitle("iRODS ImageJ - Save Image");
		this.iplugin = ipluginInstance;

		init();

		assignVariablesToFields();
		saveCurrentEditedImageFileToLocal();

		JButton btn_saveButton = new JButton("Save");
		btn_saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SaveImagePanelImplementation.log
						.info("Save to iRODS Server - Button Clicked");
			}
		});
		JButton btn_Cancel = new JButton("Cancel");
		btn_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (null != SaveImagePanelImplementation.this.iplugin
						.getSaveImagePanelImplementation()) {
					SaveImagePanelImplementation.this.iplugin
							.getSaveImagePanelImplementation().dispose();
				}
			}
		});
		JButton btn_select_local_file = new JButton("Browse File");
		btn_select_local_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SaveImagePanelImplementation.this.browseFileFromLocalMachine();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(this.contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(38)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																this.lbl_irodsDestinationPath)
														.addComponent(
																this.lbl_localFileName))
										.addGap(18)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addComponent(
																				btn_saveButton)
																		.addGap(37)
																		.addComponent(
																				btn_Cancel))
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addComponent(
																				this.textField_LocalFileName,
																				-2,
																				181,
																				-2)
																		.addGap(18)
																		.addComponent(
																				btn_select_local_file))
														.addComponent(
																this.textField_irodsDestinationPath))
										.addGap(67)));

		gl_contentPane
				.setVerticalGroup(gl_contentPane
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(73)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																this.textField_LocalFileName,
																-2, -1, -2)
														.addComponent(
																this.lbl_localFileName)
														.addComponent(
																btn_select_local_file))
										.addGap(27)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																this.textField_irodsDestinationPath,
																-2, -1, -2)
														.addComponent(
																this.lbl_irodsDestinationPath))
										.addGap(39)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																btn_saveButton)
														.addComponent(
																btn_Cancel))
										.addContainerGap(81, 32767)));

		this.contentPane.setLayout(gl_contentPane);
		setFocusTraversalPolicy(new FocusTraversalOnArray(
				new Component[] { this.contentPane, this.lbl_localFileName,
						this.textField_LocalFileName, btn_select_local_file,
						this.lbl_irodsDestinationPath,
						this.textField_irodsDestinationPath, btn_saveButton,
						btn_Cancel }));
	}

	private void init() {
		setDefaultCloseOperation(3);
		setBounds(100, 100, 526, 332);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);

		this.textField_LocalFileName = new JTextField();
		this.textField_LocalFileName.setColumns(10);

		this.lbl_localFileName = new JLabel("Local File Name : ");

		this.lbl_irodsDestinationPath = new JLabel("iRODS Destination Path :");

		this.textField_irodsDestinationPath = new JTextField();
		this.textField_irodsDestinationPath.setColumns(10);

		this.fileChooser = new JFileChooser();
	}

	private void assignVariablesToFields() {
		if ((null != this.iplugin.getSelectedNodeInTreeForSingleClick())
				&& (this.iplugin.getSelectedNodeInTreeForSingleClick() != "")) {
			this.destinationFilePath = setDestinationPathWithGivenString(this.iplugin
					.getSelectedNodeInTreeForSingleClick());
		} else if ((null != this.iplugin.getSelectedNodeInTreeForDoubleClick())
				&& (this.iplugin.getSelectedNodeInTreeForDoubleClick() != "")) {
			log.info("Inside assignVariablesToFields() and SelectedNodeInTreeForDoubleClick is not null"
					+ this.iplugin.getSelectedNodeInTreeForDoubleClick());

			this.destinationFilePath = setDestinationPathWithGivenString(this.iplugin
					.getSelectedNodeInTreeForDoubleClick());
		}
	}

	private void saveCurrentEditedImageFileToLocal() {
		try {
			if (null != this.iplugin.getImagePlus()) {
				if ((null != this.iplugin.getImageJCacheFolder())
						&& (null != this.iplugin
								.getSelectedNodeInTreeForDoubleClick())) {
					String fileName = IrodsUtilities
							.getFileNameFromDirectoryPath(this.iplugin
									.getSelectedNodeInTreeForDoubleClick());
					if (null != fileName) {
						this.savePathWithFileName = (this.iplugin
								.getImageJCacheFolder()
								+ IrodsUtilities.getPathSeperator() + fileName);

						log.info("savePathWithFileName"
								+ this.savePathWithFileName);
						IJ.save(this.iplugin.getImagePlus(),
								this.savePathWithFileName);

						log.info("File saved to local machine with same name and extention"
								+ fileName);

						JOptionPane.showMessageDialog(null,
								"File saved to local machine with filename and extention: "
										+ fileName);

						this.textField_LocalFileName
								.setText(this.savePathWithFileName);
						log.info("textField_LocalFileName set to savePathWithFileName"
								+ this.textField_LocalFileName.getText());

						saveCurrentEditedFileToIrodsByPluginOption();
					} else {
						log.error("File name is null in saveCurrentEditedImageFileToLocal method!");
					}
				} else {
					log.error("ImageJCacheFolder or ImagePlus is null");
				}
			} else {
				log.error("iplugin imageJ imagePlus instance is null!");
				JOptionPane.showMessageDialog(null,
						"iplugin imageJ imagePlus instance is null!", "Error",
						0);
			}
		} catch (Exception exception) {
			log.error("Error while saving file to local!"
					+ exception.getMessage());
		}
	}

	private void saveCurrentEditedFileToIrodsByPluginOption() {
		try {
			String targetResourceName = "";
			targetResourceName = this.iplugin.getIrodsAccount()
					.getDefaultStorageResource();

			File sourceLocalfile = null;
			IRODSFile destinationIrodsFile = null;
			if (this.savePathWithFileName != null) {
				sourceLocalfile = new File(this.savePathWithFileName);
				log.info("savePathWithFileName is set into sourceLocalfile: "
						+ sourceLocalfile);
			} else {
				log.error("savePathWithFileName is null");
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
					if ((null != this.iplugin) && (null != sourceLocalfile)
							&& (null != destinationIrodsFile)
							&& (null != targetResourceName)) {
						log.info("Inside core save functionality - just before executing PutFileToIrodsSwingWorker method");
						this.putFile = new PutFileToIrodsSwingWorker(
								this.iplugin, sourceLocalfile,
								destinationIrodsFile, targetResourceName);

						this.putFile.execute();

						this.iplugin.getCancelTransaction_JButton().setEnabled(
								true);
						log.info("Cancel Transaction button is enabled");

						this.iplugin.setCancelPutTransaction(true);
						this.iplugin.setCancelGetTransaction(false);

						log.info("Executed PutFile method!");
						JOptionPane.showMessageDialog(null,
								"Uploading file completed!");
					} else {
						log.error("sourceLocalfile or destinaitonIrodsFile or targetResourceName is null");
					}
				} catch (Exception execeptionWhileExecutingPutFile) {
					log.error(execeptionWhileExecutingPutFile.getMessage());
					JOptionPane.showMessageDialog(null,
							execeptionWhileExecutingPutFile.getMessage());
				}
			} else {
				log.error("sourceLocalfile or textField_irodsDestinationPath is null");
			}
		} catch (JargonException ExceptionInExecutingSaveButton) {
			log.error(ExceptionInExecutingSaveButton.getMessage());
			ExceptionInExecutingSaveButton.printStackTrace();
		}
	}

	private String setDestinationPathWithGivenString(String destinationPath) {
		String destinationFilePath = IrodsUtilities.getPathSeperator()
				+ this.iplugin.getIrodsAccount().getZone()
				+ IrodsUtilities.getPathSeperator() + Constants.HOME_STRING
				+ IrodsUtilities.getPathSeperator() + destinationPath;

		return destinationFilePath;
	}

	private void saveCurrentEditedFileToIrods() {
		try {
			String sourceFilePath = null;
			String destinationFilePath = null;
			String targetResourceName = "";
			targetResourceName = this.iplugin.getIrodsAccount()
					.getDefaultStorageResource();

			File sourceLocalfile = null;
			IRODSFile destinationIrodsFile = null;
			sourceLocalfile = new File(sourceFilePath);
			if (this.textField_LocalFileName != null) {
				sourceFilePath = this.textField_LocalFileName.getText();

				log.info("sourceLocalfile is not null and setting to textField_LocalFileName: "
						+ sourceLocalfile);
			} else {
				log.error("textField_LocalFileName is null");
			}
			if ((this.fileChooser.getSelectedFile().getAbsolutePath() != null)
					&& (this.fileChooser.getSelectedFile().getAbsolutePath() != "")) {
				sourceFilePath = this.fileChooser.getSelectedFile()
						.getAbsolutePath();

				log.info("sourceFilePath of fileChoose: " + sourceFilePath);
				log.info("sourceLocalfile after choosing file: "
						+ sourceLocalfile);
			} else {
				log.error("fileChooser.getSelectedFile().getAbsolutePath() is null or empty");
			}
			log.info("Just beffore checking if SelectedNodeInTreeForSingleClick is null");
			if ((null != this.textField_irodsDestinationPath)
					&& (this.textField_irodsDestinationPath.getText() != "")
					&& (null != sourceLocalfile)
					&& (sourceLocalfile.getAbsolutePath() != "")) {
				destinationFilePath = IrodsUtilities.getPathSeperator()
						+ this.iplugin.getIrodsAccount().getZone()
						+ IrodsUtilities.getPathSeperator()
						+ Constants.HOME_STRING
						+ IrodsUtilities.getPathSeperator()
						+ this.textField_irodsDestinationPath.getText();

				log.info("Final destinationFilePath: " + destinationFilePath);
				destinationIrodsFile = this.iplugin.getiRODSFileFactory()
						.instanceIRODSFile(destinationFilePath);

				log.info("destinaitonIrodsFile absolutepath: "
						+ destinationIrodsFile.getAbsoluteFile());
				try {
					if ((null != this.iplugin) && (null != sourceLocalfile)
							&& (null != destinationIrodsFile)
							&& (null != targetResourceName)) {
						log.info("Inside core save functionality - just before executing PutFileToIrodsSwingWorker method");
						this.putFile = new PutFileToIrodsSwingWorker(
								this.iplugin, sourceLocalfile,
								destinationIrodsFile, targetResourceName);

						this.putFile.execute();
						log.info("Executed PutFile method!");
					} else {
						log.error("sourceLocalfile or destinaitonIrodsFile or targetResourceName are null");
					}
				} catch (Exception excetionPutFile) {
					log.error(excetionPutFile.getMessage());
					JOptionPane.showMessageDialog(null,
							excetionPutFile.getMessage());
				}
			} else {
				log.error("sourceLocalfile or textField_irodsDestinationPath is null");
			}
		} catch (JargonException ExceptionInSaveButtonClicked) {
			log.error(ExceptionInSaveButtonClicked.getMessage());
			ExceptionInSaveButtonClicked.printStackTrace();
		}
	}

	private void browseFileFromLocalMachine() {
		this.fileChooser.setFileSelectionMode(2);
		int option = this.fileChooser.showOpenDialog(this);
		if (option == 0) {
			this.textField_LocalFileName.setText(this.fileChooser
					.getSelectedFile() != null ? this.fileChooser
					.getSelectedFile().getAbsolutePath()
					: "nothing is selected");
		} else {
			this.textField_LocalFileName.setName("File selection canceled !");
		}
	}
}
