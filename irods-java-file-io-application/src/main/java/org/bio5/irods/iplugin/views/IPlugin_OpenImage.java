package org.bio5.irods.iplugin.views;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.frame.PlugInFrame;

import java.awt.Frame;
import java.awt.event.WindowEvent;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;

public class IPlugin_OpenImage extends PlugInFrame {
	private static final long serialVersionUID = 3225639715931294038L;
	private static IPlugin_OpenImage irods_Plugin_instance;
	private static Frame instance;
	private static IPlugin iplugin;
	private static MainWindow mainWindowInstance;
	static Logger log = Logger.getLogger(IPlugin_OpenImage.class.getName());

	public static IPlugin getIrodsImagej() {
		return iplugin;
	}

	public static IPlugin_OpenImage getIrods_Plugin_instance() {
		return irods_Plugin_instance;
	}

	public IPlugin_OpenImage() {
		super("Imagej v1.0.1");
		log = Logger.getLogger(IPlugin_OpenImage.class);
	}

	public void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == 201) {
			instance = null;
		}
	}

	public void run(String arg) {
		iplugin = new IPlugin();

		mainWindowInstance = new MainWindow(iplugin);
		if (null != mainWindowInstance) {
			iplugin.setMainWindow(mainWindowInstance);
		}
		mainWindowInstance.setVisible(true);
	}

	public static void main(String[] args) {
		Class<?> clazz = IPlugin_OpenImage.class;
		String url = clazz.getResource(
				"/" + clazz.getName().replace('.', '/') + ".class").toString();

		String pluginsDir = url.substring(5, url.length()
				- clazz.getName().length() - 6);

		System.setProperty("plugins.dir", pluginsDir);

		new ImageJ();

		IJ.runPlugIn(clazz.getName(), "");
	}
}
