package org.bio5.irods.iplugin.referencecode;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import java.awt.Component;

public class samplePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5527815157916844473L;
	private JTable table;

	/**
	 * Create the panel.
	 */
	public samplePanel() {

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
					.addGap(19))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(23)
					.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 407, Short.MAX_VALUE)
					.addGap(9))
		);
		
		Object[][] data =
			{
			    {"Homer", "Simpson", "delete Homer"},
			    {"Madge", "Simpson", "delete Madge"},
			    {"Bart",  "Simpson", "delete Bart"},
			    {"Lisa",  "Simpson", "delete Lisa"},
			};
		
		String[] columnNames = {"First Name", "Last Name", "avf"};

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("File Information", null, panel_1, null);

		table = new JTable();
		table.setRowHeight(20);
		table.setIntercellSpacing(new Dimension(2, 2));
		table.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		table.setToolTipText("File Information");
		table.setModel(new DefaultTableModel(new Object[][] {
				{ "abcd", "abcd" }, { null, null }, { null, null },
				{ null, null }, { null, null }, { null, null }, { null, null },
				{ null, null }, { null, null }, { null, null }, },
				new String[] { "Field", "Information" }));
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(300);
		table.getColumnModel().getColumn(1).setMinWidth(200);

		JLabel label_ProgressBar_BytesTrasferredOutofTotalFileSize = new JLabel(
				" Progress:");
		label_ProgressBar_BytesTrasferredOutofTotalFileSize
				.setToolTipText(" Progress: bytesTransferred/Total File Size in Bytes");
		label_ProgressBar_BytesTrasferredOutofTotalFileSize
				.setBorder(new LineBorder(new Color(0, 0, 0)));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1
				.setHorizontalGroup(gl_panel_1
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panel_1
										.createSequentialGroup()
										.addGroup(
												gl_panel_1
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_panel_1
																		.createSequentialGroup()
																		.addGap(43)
																		.addComponent(
																				table,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
														.addGroup(
																gl_panel_1
																		.createSequentialGroup()
																		.addGap(146)
																		.addComponent(
																				label_ProgressBar_BytesTrasferredOutofTotalFileSize,
																				GroupLayout.PREFERRED_SIZE,
																				179,
																				GroupLayout.PREFERRED_SIZE)))
										.addGap(72)));
		gl_panel_1
				.setVerticalGroup(gl_panel_1
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panel_1
										.createSequentialGroup()
										.addGap(76)
										.addComponent(
												label_ProgressBar_BytesTrasferredOutofTotalFileSize,
												GroupLayout.PREFERRED_SIZE, 41,
												GroupLayout.PREFERRED_SIZE)
										.addGap(41)
										.addComponent(table,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE).addGap(57)));
		panel_1.setLayout(gl_panel_1);
		
				JPanel panel = new JPanel();
				
				tabbedPane.addTab("New tab", null, panel, null);
						
						JSplitPane splitPane = new JSplitPane();
						GroupLayout gl_panel = new GroupLayout(panel);
						gl_panel.setHorizontalGroup(
							gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addContainerGap()
									.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
									.addContainerGap())
						);
						gl_panel.setVerticalGroup(
							gl_panel.createParallelGroup(Alignment.TRAILING)
								.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
									.addContainerGap()
									.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
									.addContainerGap())
						);
						
						JTree tree = new JTree();
						tree.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
						tree.setAlignmentX(Component.RIGHT_ALIGNMENT);
						tree.setAutoscrolls(true);
						splitPane.setLeftComponent(tree);
						
						JPanel panel_2 = new JPanel();
						splitPane.setRightComponent(panel_2);
						panel.setLayout(gl_panel);
		setLayout(groupLayout);

	}
}
