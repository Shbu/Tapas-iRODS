package org.bio5.irods.iplugin.views;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.frame.PlugInFrame;

import java.awt.Frame;
import java.awt.event.WindowEvent;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.utilities.Constants;

/**
 * <u>ImageJ-Plugin: </u> IPlugin (ImageJ Plugin) <br>
 * The main programm which contains the workflow, implemented as Plugin for
 * ImageJ.
 * 
 * @author Sharan
 * 
 */
public class IPlugin_OpenImage extends PlugInFrame {

	private static final long serialVersionUID = 3225639715931294038L;

	/* Declare static variables */
	private static IPlugin_OpenImage irods_Plugin_instance;
	@SuppressWarnings("unused")
	private static Frame instance;
	private static IPlugin iplugin;
	private static MainWindow mainWindowInstance;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(IPlugin_OpenImage.class.getName());

	/**
	 * Returns iplugin instance
	 * 
	 * @return
	 */
	public static IPlugin getIrodsImagej() {
		return iplugin;
	}

	public static IPlugin_OpenImage getIrods_Plugin_instance() {
		return irods_Plugin_instance;
	}

	public IPlugin_OpenImage() {
		super(Constants.PLUGIN_TITLE);
		log = Logger.getLogger(IPlugin_OpenImage.class);
	}

	public void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			instance = null;
		}
	}

	public void run(String arg) {
		iplugin = new IPlugin();
		// irodsImagej.setMainWindow(mainWindow);
		mainWindowInstance = new MainWindow(iplugin);
		if (null != mainWindowInstance) {
			iplugin.setMainWindow(mainWindowInstance);
		}
		mainWindowInstance.setVisible(true);
	}

	/* Main method to Debug the code - Remove it once app is done! */
	public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins
		// menu
		Class<?> clazz = IPlugin_OpenImage.class;
		String url = clazz.getResource(
				"/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length()
				- clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		/* start Imagej */
		new ImageJ();

		// Open a sample bio5 image
		/*
		 * ImagePlus image = IJ.openImage(
		 * "http://www.bio5.org/sites/default/files/homepage/slides/5_areas_circle_300pxWidth.png"
		 * ); image.show();
		 */

		// Run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}
