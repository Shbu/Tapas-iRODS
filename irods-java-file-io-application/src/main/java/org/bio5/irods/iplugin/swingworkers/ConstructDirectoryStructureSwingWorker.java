package org.bio5.irods.iplugin.swingworkers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IPlugin;
import org.bio5.irods.imagej.views.DirectoryContentsWindow;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public class ConstructDirectoryStructureSwingWorker extends SwingWorker<Void, Integer> {
	
	private IPlugin irodsImageJ;
	private DirectoryContentsWindow direcetoryContentspane;
	private IRODSFileFactory iRODSFileFactory;
	private File irodsAccountFile;
	private DefaultMutableTreeNode node;
	private IRODSAccount irodsAccount;
	private JDialog dialog;
	private JProgressBar progress;
	private DirectoryContentsWindow directoryContentsPane;
	private IPlugin irodsImagej;
	


	public ConstructDirectoryStructureSwingWorker(IPlugin irodsImageJ,
			IRODSFileFactory iRODSFileFactory, File irodsAccountFile,
			DefaultMutableTreeNode node, IRODSAccount irodsAccount) {
		super();
		this.irodsImageJ = irodsImageJ;
		this.iRODSFileFactory = iRODSFileFactory;
		this.irodsAccountFile = irodsAccountFile;
		this.node = node;
		this.irodsAccount = irodsAccount;
	}

	/* Logger instantiation */
	static Logger log = Logger.getLogger(DirectoryContentsWindow.class.getName());
	
	
	@Override
	protected Void doInBackground() throws Exception {
		parseDirectoryContents(iRODSFileFactory, irodsAccountFile, node, irodsAccount);
		return null;
	}
	
	public void parseDirectoryContents(final IRODSFileFactory iRODSFileFactory,final File irodsAccountFile, DefaultMutableTreeNode node, final IRODSAccount irodsAccount)
	{

		if(!irodsAccountFile.isDirectory()){
			//System.out.println("File name" +irodsAccountFile.getName() +":" +irodsAccountFile.getAbsolutePath());
			log.info("File name:" +irodsAccountFile.getName() +":" +irodsAccountFile.getAbsolutePath());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(irodsAccountFile.getName(),false);
			node.add(child);
			//addObject(node,child,true);
		}

		if(irodsAccountFile.isDirectory()){
			//System.out.println("Direc name" + irodsAccountFile.getName());
			log.info("Direc name:" + irodsAccountFile.getName());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(irodsAccountFile.getName(),true);
			node.add(child);
			//addObject(node,child,true);
			File[] direcFiles=irodsAccountFile.listFiles();
			for(int i=0;i<direcFiles.length;i++){
				//System.out.println("File number" +i +"\n depth:" +direcFiles.length);
				log.info("File number:" +i +"\t depth:" +direcFiles.length);
				parseDirectoryContents(iRODSFileFactory, direcFiles[i], child, irodsAccount);
			}
		}
		
	}
	
	/*public void addObject(DefaultMutableTreeNode parent,
			Object child, 
			boolean shouldBeVisible) {
		DefaultMutableTreeNode childNode =null;
		try{
			childNode = 
					new DefaultMutableTreeNode(child, false);

			if (parent == null && null !=irodsImageJ.getRootTreeNodeForDirectoryContents()) {
				parent = irodsImageJ.getRootTreeNodeForDirectoryContents();
			}

			if (null !=irodsImageJ.getTreeModel() && null !=irodsImageJ.getUserDirectoryTree()) {
				irodsImageJ.getTreeModel().insertNodeInto(childNode, parent,
						parent.getChildCount());
				irodsImageJ.getTreeModel().nodeChanged(parent);
				if (shouldBeVisible) {
					irodsImageJ.getUserDirectoryTree().scrollPathToVisible(new TreePath(
							childNode.getPath()));
				}
			}
			else{
				log.error("1. Tree Model in irodsImageJ bean is null or 2. User Directory Tree in irodsImageJ bean is null");
			}
		}
		catch(IllegalStateException illegalStateException){
			log.error(illegalStateException.getMessage());
			JOptionPane.showMessageDialog(null, "node does not allow children");
		}
		//return childNode;
	}*/
	
	@Override
	public void done() {
		if(null!= irodsImageJ.getDirectoryContentsPane() &&  null!=irodsImageJ.getScrollPane()){
			direcetoryContentspane =irodsImageJ.getDirectoryContentsPane();
			direcetoryContentspane.add(irodsImageJ.getScrollPane(),BorderLayout.CENTER);
			direcetoryContentspane.setVisible(true);
			direcetoryContentspane.revalidate();
			direcetoryContentspane.repaint();
			direcetoryContentspane.repaintPanel();
		}
		else{
			log.error("1. Directory Conents in irodsImageJ bean is empty or 2. Scroll pane instance in irodsImageJ bean is empty");
		}
		
	}

}
