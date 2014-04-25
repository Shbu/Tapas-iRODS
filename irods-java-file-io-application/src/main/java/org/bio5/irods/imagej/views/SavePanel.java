package org.bio5.irods.imagej.views;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SavePanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private HashMap<String, Object> saveDetails;
	private JTextField textField_FileName;
	private JTextField textField_irodsDestinationPath;

	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { SavePanel frame = new
	 * SavePanel(null); frame.setVisible(true); } catch (Exception e) {
	 * e.printStackTrace(); } } }); }
	 */

	/* Logger instantiation */
	static Logger log = Logger.getLogger(SavePanel.class.getName());

	/**
	 * Create the frame.
	 * 
	 * @param saveDetails
	 */
	public SavePanel() {
		setTitle("iRODS ImageJ - Save Image");

		/* Functionality - pending */

		/* Screen Design */

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 526, 332);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		textField_FileName = new JTextField();
		textField_FileName.setColumns(10);

		JLabel lbl_localFileName = new JLabel("Local File Name : ");

		JLabel lbl_irodsDestinationPath = new JLabel("iRODS Destination Path :");

		textField_irodsDestinationPath = new JTextField();
		textField_irodsDestinationPath.setColumns(10);

		JButton btn_saveButton = new JButton("Save");

		JButton btn_Cancel = new JButton("Cancel");
		btn_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JButton btn_select_local_file = new JButton("Browse File");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(38)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																lbl_irodsDestinationPath)
														.addComponent(
																lbl_localFileName))
										.addGap(18)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.LEADING,
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
																				textField_FileName,
																				GroupLayout.PREFERRED_SIZE,
																				181,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(18)
																		.addComponent(
																				btn_select_local_file))
														.addComponent(
																textField_irodsDestinationPath))
										.addGap(67)));
		gl_contentPane
				.setVerticalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(73)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																textField_FileName,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lbl_localFileName)
														.addComponent(
																btn_select_local_file))
										.addGap(27)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																textField_irodsDestinationPath,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lbl_irodsDestinationPath))
										.addGap(39)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																btn_saveButton)
														.addComponent(
																btn_Cancel))
										.addContainerGap(81, Short.MAX_VALUE)));
		contentPane.setLayout(gl_contentPane);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] {
				contentPane, lbl_localFileName, textField_FileName,
				btn_select_local_file, lbl_irodsDestinationPath,
				textField_irodsDestinationPath, btn_saveButton, btn_Cancel }));

		/* Setting visibility of form */
		// setVisibility(true);
	}

	public boolean saveFileToIrods() {
		boolean isFileUploaded = false;

		return isFileUploaded;
	}
}
