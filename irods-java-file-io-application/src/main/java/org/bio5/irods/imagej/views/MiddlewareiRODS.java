package org.bio5.irods.imagej.views;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.bio5.irods.imagej.connection.IrodsConnection;
import org.irods.jargon.core.connection.IRODSAccount;

public class MiddlewareiRODS extends JFrame{


	private String username;
	private String password;
	private String port;
	private String host;
	private String zone;

	private JPanel contentPane;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}

	
	/*Yet to update functionality once we resolve the ClassNotFound error*/
	public void connection(){
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		JOptionPane.showMessageDialog(null, username);
		IRODSAccount irodsAccount= IrodsConnection.irodsConnection(username, password, zone, host, Integer.parseInt(port));
		
	}

}
