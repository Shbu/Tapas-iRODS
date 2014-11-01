package org.bio5.irods.iplugin.views;

import ij.IJ;
import ij.plugin.frame.PlugInFrame;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;

public class IPlugin_SaveImage extends PlugInFrame {
	private static final long serialVersionUID = -502073300497809137L;
	static Logger log = Logger.getLogger(IPlugin_SaveImage.class.getName());
	public IPlugin iplugin;
	private SaveImagePanelImplementation saveImagePanelImplementation;

	public IPlugin_SaveImage() {
		super("Saving Image");
		log.info("Executing Irods_Plugin_SaveImage option in ImageJ");
		init();
	}

	public void init() {
		if ((null != IPlugin_OpenImage.getIrodsImagej())
				&& (null != IJ.getImage())) {
			this.iplugin = IPlugin_OpenImage.getIrodsImagej();
			this.iplugin.setImageOpened(true);
			if (this.iplugin.isImageOpened()) {
				log.info("irodsImagej.isImageOpened() is true");

				new SaveImageImplementation(this.iplugin);
			} else {
				log.error("irodsImagej.isImageOpened() is false");
				IJ.error("Error while saving file",
						"No image available to save !");
			}
		} else {
			log.error("Irods_Plugin_OpenImage.getIrodsImagej() is null");
			IJ.error("Error while saving file",
					"Irods ImageJ instance is null !");
		}
	}

	private void setVisibility(boolean isVisibility) {
		if (isVisibility) {
			log.info("Inside setVisibility method");
			this.saveImagePanelImplementation = new SaveImagePanelImplementation(
					this.iplugin);
			if (null != this.iplugin.getSaveImagePanelImplementation()) {
				this.iplugin
						.setSaveImagePanelImplementation(this.saveImagePanelImplementation);
			} else {
				log.error("saveImagePanelImplementation instance is null");
			}
			this.saveImagePanelImplementation.setVisible(true);
		} else {
			log.error("Save Panel Visibility is set to false !");
		}
	}
}
