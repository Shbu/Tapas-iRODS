package org.bio5.irods.imagej.views;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.irods.jargon.core.pub.io.IRODSFile;

public class DirectoryContents extends JPanel {

	private JTree userDirectoryTree;
	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode childNode;
	private DefaultTreeModel treeModel;
	/**
	 * Create the panel.
	 */
	public DirectoryContents(List<String> DirList, IRODSFile irodsAccountFile) {

		//createRootNodeWithContents(DirList);


		
		File rootFile = irodsAccountFile.getParentFile();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootFile);
		rootNode.removeAllChildren();
		
		treeModel=  new DefaultTreeModel(rootNode);
		treeModel.reload();
		

		addFiles(rootFile, treeModel, rootNode);

		userDirectoryTree= new JTree();
		userDirectoryTree.expandPath(new TreePath(rootNode));
		userDirectoryTree.setModel(treeModel);
		userDirectoryTree.setShowsRootHandles(true);
		userDirectoryTree.setEditable(true);
		add(userDirectoryTree);
		add(new JScrollPane(userDirectoryTree));
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
	/**
	 * @param DirList
	 */
	private void createRootNodeWithContents(List<String> DirList) {
		rootNode = new DefaultMutableTreeNode("Root");
		Iterator<String> itr=DirList.iterator();
		while(itr.hasNext())
		{
			childNode = new DefaultMutableTreeNode(itr.next());
			rootNode.add(childNode);
		}
	}

	private void addFiles(File rootFile, DefaultTreeModel model, DefaultMutableTreeNode root){

		for (File file : rootFile.listFiles()) {
			System.out.println("list-main" +file.toString());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(file);
			model.insertNodeInto(child, root, root.getChildCount());
			if (file.isDirectory()) {
				addFiles(file, model, child);
				System.out.println("list" +file.toString());
			}
		}
	}
}
