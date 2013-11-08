package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.frame.PlugInFrame;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

public class Irods_Plugin extends PlugInFrame /*implements PlugIn*/ {


	private static final int PANEL_HEIGHT = 500;
	private static final int PANEL_WIDTH = 500;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3225639715931294038L;

	public Irods_Plugin() {
		super("iRODS");
		// TODO Auto-generated constructor stub
	}

	public void run(String arg) {
		// create a dialog with two numeric input fields
		GenericDialog gd = new GenericDialog("iRODS login");
		Panel panel=new Panel();
		panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		/*
		 * Set layout to panel -- pending*/
		
		gd.addPanel(panel);
		Button button_Login=new Button();
		TextField textFieldUsername,textFieldPort,textFieldZone,textFieldHost, textField_Password;
		Label label_username, label_password,label_port, label_Zone,label_Host;
		label_username = new Label("User Name :");
		label_password = new Label("Password :");
		label_port = new Label("Port :");
		label_Zone = new Label("Zone :");
		label_Host= new Label("Host :");
		textFieldUsername =new TextField(30);
		textField_Password = new TextField(30);
		char c=1;
		textField_Password.setEchoChar(c);
		textFieldPort= new TextField(30);
		textFieldZone= new TextField(30);
		textFieldHost= new TextField(30);
		Button button_Cancel=new Button();
		//button_Login.setLabel("Login");
		//button_Cancel.setLabel("Cancel");
		
		/*Adding contents to panel*/
		panel.add(label_username);
		panel.add(textFieldUsername);
		
		panel.add(label_password);
		panel.add(textField_Password);
		
		panel.add(label_port);
		textFieldPort.setText("1247");
		
		panel.add(label_Zone);
		textFieldZone.setText("iPlant");
		
		panel.add(label_Host);
		textFieldHost.setText("data.iplantcollaborative.org");
		
		panel.add(textFieldPort);
		panel.add(textFieldZone);
		panel.add(textFieldHost);
		panel.add(button_Login);
		panel.add(button_Cancel);

		gd.setOKLabel("Login");
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			IJ.error("Plugin canceled!");
			return;
		}

		// set the size of this frame to the values specified by the user,
		// add label and show the frame
		Panel UserDir=new Panel();
		this.add(UserDir);
		/*this.setSize((int) gd.getNextNumber(),(int) gd.getNextNumber());
		this.add(new Label("iRODS",Label.CENTER));*/
		this.show();
	}
}
