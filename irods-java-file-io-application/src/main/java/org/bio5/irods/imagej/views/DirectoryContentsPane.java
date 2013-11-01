package org.bio5.irods.imagej.views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.irods.jargon.core.pub.io.IRODSFile;

public class DirectoryContentsPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 722996165620904921L;
	private JTree userDirectoryTree;
	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode childNode;
	/**
	 * Create the panel.
	 */
	public DirectoryContentsPane(List<String> ContentsInHome,IRODSFile irodsAccountFile) {

		rootNode = new DefaultMutableTreeNode("Home");
		/*Iterator<String> itr=ContentsInHome.iterator();
		while(itr.hasNext())
		{
			childNode = new DefaultMutableTreeNode(itr.next());
			rootNode.add(childNode);
		}*/

		File localFiles = (File) irodsAccountFile;
		parseDirectoryContents(localFiles, rootNode);
		
		userDirectoryTree.setToolTipText("Directory list");
		userDirectoryTree.setVisibleRowCount(50);
		userDirectoryTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JButton btnNewButton_AddToImageJ = new JButton("Add to ImageJ");
		
		JButton btnNewButton_OpenImage = new JButton("Open Image");
		btnNewButton_OpenImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * Show directory contents if user is authenticated already*/
			}
		});
		
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(userDirectoryTree, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addComponent(btnNewButton_OpenImage, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton_AddToImageJ)
							.addContainerGap(163, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 284, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(22)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 395, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnNewButton_OpenImage)
								.addComponent(btnNewButton_AddToImageJ)))
						.addComponent(userDirectoryTree, GroupLayout.PREFERRED_SIZE, 429, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		setLayout(groupLayout);

	}
	
	void parseDirectoryContents(File irodsAccountFile, DefaultMutableTreeNode node)
	{
		
		/*File[] f =irodsAccountFile.listFiles();
		for(int i=0; i<f.length;i++)
		{
			if(f[i].isDirectory()){
				node.add(new DefaultMutableTreeNode(f[i].getName()));
				System.out.println(f[i].getName() +node.getDepth());
				parseDirectoryContents(f[i],new DefaultMutableTreeNode(f[i].getName()));
			}
			else{
				Need to update code to display leaf nodes in a dir -- currently check console o/p 
				System.out.println(f[i].getName());
				DefaultMutableTreeNode parentNode= new DefaultMutableTreeNode(f[i].getParent());
				parentNode.add(new DefaultMutableTreeNode(f[i].getName()));
			}
		}*/
		
		
		if(!irodsAccountFile.isDirectory()){

			System.out.println("File name" +irodsAccountFile.getName());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(irodsAccountFile);
			node.add(child);
		}
		else{
			System.out.println("Direc name" + irodsAccountFile.getName());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(irodsAccountFile);
			node.add(child);
			File[] direcFiles=irodsAccountFile.listFiles();
			for(int i=0;i<direcFiles.length;i++){
				parseDirectoryContents(direcFiles[i], child);
			}
		}
		
		userDirectoryTree= new JTree(rootNode);
		
		userDirectoryTree.setShowsRootHandles(true);
		userDirectoryTree.setEditable(true);
		add(userDirectoryTree);
	}
	
}
