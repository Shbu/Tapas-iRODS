package org.bio5.irods.sampleapplications.irods_java_file_io_application;

import ij.IJ;
import ij.plugin.PlugIn;

public class Irods_Plugin /*extends PlugInFrame*/ implements PlugIn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3378076092359211032L;

	/*
	 * Code specific to PlugInFrame subclass of AWT frame*/
	/*public Irods_Plugin(String title) {
		super("iRODS!");
		IJ.showMessage("hai!");

	}*/



		
	/* Code specific to PlugIn Interface*/
	public void run(String arg) {
		IJ.showMessage("iRODS Application", "Hello iRODS!");
		execute();
	}


	public void execute()
	{
		Login_Window.main(null);	
	}

}
