package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.plugin.PlugIn;
import ij.plugin.frame.PlugInFrame;

public class Irods_Plugin /*extends PlugInFrame*/ implements PlugIn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3378076092359211032L;

	/*
	 * Code specific to PlugInFrame subclass of AWT frame*/
	/*public Irods_Plugin(String title) {
		super("iRODS");
		IJ.showMessage("hai!");
		execute();

	}*/

	/* Code specific to PlugIn Interface*/
	public void run(String arg) {
		IJ.showMessage("iRODS Application", "Hello iRODS!");
		/*execute();*/
		MainWindow.main(null);
	}


	/*private void execute()
	{
		MainWindow.main(null);	
	}*/

}
