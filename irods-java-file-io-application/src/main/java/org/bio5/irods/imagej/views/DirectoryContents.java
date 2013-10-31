package org.bio5.irods.imagej.views;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.irods.jargon.core.pub.io.IRODSFile;

public class DirectoryContents extends JPanel {

	private JTree userDirectoryTree;
	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode childNode;
	/**
	 * Create the panel.
	 */
	public DirectoryContents(List<String> DirList,IRODSFile irodsAccountFile) {

		rootNode = new DefaultMutableTreeNode("Root");
		Iterator<String> itr=DirList.iterator();
		while(itr.hasNext())
		{
			childNode = new DefaultMutableTreeNode(itr.next());
			rootNode.add(childNode);
		}

		userDirectoryTree= new JTree(rootNode);
		userDirectoryTree.setShowsRootHandles(true);
		userDirectoryTree.setEditable(true);
		add(userDirectoryTree);
		userDirectoryTree.setToolTipText("Directory list");
		userDirectoryTree.setVisibleRowCount(50);
		userDirectoryTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(userDirectoryTree, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(403, Short.MAX_VALUE))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(userDirectoryTree, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
						.addContainerGap())
				);
		setLayout(groupLayout);

	}
}
