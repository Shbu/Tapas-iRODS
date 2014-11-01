package org.bio5.irods.iplugin.swingworkers;

import java.io.File;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.views.DirectoryContentsWindow;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public class ConstructDirectoryStructureSwingWorker extends
		SwingWorker<Void, Integer> {
	private IPlugin irodsImageJ;
	private DirectoryContentsWindow direcetoryContentspane;
	private IRODSFileFactory iRODSFileFactory;
	private File irodsAccountFile;
	private DefaultMutableTreeNode node;
	private IRODSAccount irodsAccount;

	public ConstructDirectoryStructureSwingWorker(IPlugin irodsImageJ,
			IRODSFileFactory iRODSFileFactory, File irodsAccountFile,
			DefaultMutableTreeNode node, IRODSAccount irodsAccount) {
		this.irodsImageJ = irodsImageJ;
		this.iRODSFileFactory = iRODSFileFactory;
		this.irodsAccountFile = irodsAccountFile;
		this.node = node;
		this.irodsAccount = irodsAccount;
	}

	static Logger log = Logger
			.getLogger(ConstructDirectoryStructureSwingWorker.class.getName());

	protected Void doInBackground() throws Exception {
		parseDirectoryContents(this.iRODSFileFactory, this.irodsAccountFile,
				this.node, this.irodsAccount);

		return null;
	}

	public void parseDirectoryContents(IRODSFileFactory iRODSFileFactory,
			File irodsAccountFile, DefaultMutableTreeNode node,
			IRODSAccount irodsAccount) {
		if (!irodsAccountFile.isDirectory()) {
			log.info("File name:" + irodsAccountFile.getName() + ":"
					+ irodsAccountFile.getAbsolutePath());

			DefaultMutableTreeNode child = new DefaultMutableTreeNode(
					irodsAccountFile.getName(), false);

			node.add(child);
		}
		if (irodsAccountFile.isDirectory()) {
			log.info("Direc name:" + irodsAccountFile.getName());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(
					irodsAccountFile.getName(), true);

			node.add(child);

			File[] direcFiles = irodsAccountFile.listFiles();
			for (int i = 0; i < direcFiles.length; i++) {
				log.info("File number:" + i + "\t depth:" + direcFiles.length);
				parseDirectoryContents(iRODSFileFactory, direcFiles[i], child,
						irodsAccount);
			}
		}
	}

	public void done() {
		if ((null != this.irodsImageJ.getDirectoryContentsPane())
				&& (null != this.irodsImageJ.getScrollPane())) {
			this.direcetoryContentspane = this.irodsImageJ
					.getDirectoryContentsPane();
			this.direcetoryContentspane.add(this.irodsImageJ.getScrollPane(),
					"Center");

			this.direcetoryContentspane.setVisible(true);
			this.direcetoryContentspane.revalidate();
			this.direcetoryContentspane.repaint();
			this.direcetoryContentspane.repaintPanel();
		} else {
			log.error("1. Directory Conents in irodsImageJ bean is empty or 2. Scroll pane instance in irodsImageJ bean is empty");
		}
	}
}
