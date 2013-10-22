package org.bio5.irods.sampleapplications.irods_java_file_io_application;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.Opener;
import ij.plugin.PlugIn;
import ij.plugin.frame.PlugInFrame;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

//import org.bio5.irods.sampleapplications.irods_connection.IrodsConnection;
//import org.irods.jargon.core.connection.IRODSAccount;

public class Login_Window extends JFrame {

        public Login_Window(String title) {
                super("LoginWindow");
                // TODO Auto-generated constructor stub
        }

        private JPanel contentPane;
        private JTextField textbox_LoginId;
        private JPasswordField passwordField;

        /**
         * Launch the application.
         */
        public static void main(String[] args) {
                EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                try {
                                        Login_Window frame = new Login_Window();
                                        frame.setVisible(true);
                                        

                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                });
        }

        public void run(String arg0) {
                // TODO Auto-generated method stub
                
                GenericDialog gd = new GenericDialog("LoginWindow");
                gd.addNumericField("Frame width:",200.0,3);
                
                /*IJ.showMessage("iRODS Application", "Hello iRODS!");
                
                String URL  ="http://godatdesign.com/sites/default/files/styles/large/public/Bio5_1.jpg";
                
                System.out.println("Result of URL" +IJ.openUrlAsString(URL));
                
                IJ.showMessage(IJ.openUrlAsString(URL));
                
                PlugInFrame pif = new PlugInFrame(null);
                pif.getBackground();
                Opener opener= new Opener();
                ImagePlus imageplus =opener.openURL("http://godatdesign.com/sites/default/files/styles/large/public/Bio5_1.jpg");
                */
        }

        
        
        /**
         * Create the frame.
         */
        public Login_Window() {
                setTitle("iRODS");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setBounds(100, 100, 450, 300);
                contentPane = new JPanel();
                contentPane.setToolTipText("Password");
                contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
                setContentPane(contentPane);

                textbox_LoginId = new JTextField();
                textbox_LoginId.setToolTipText("User Id");
                textbox_LoginId.setColumns(13);

                JLabel label_2 = new JLabel("");

                final JButton button_Login = new JButton("Login");
                button_Login.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                String username= textbox_LoginId.getText();
                                String password= passwordField.getSelectedText();

                                if(username==null || password==null)
                                {        
                                 

                                        JOptionPane.showMessageDialog(null, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
                                }
                                else
                                {
                                       /* IRODSAccount irodsAccount = IrodsConnection.irodsConnection(textbox_LoginId.getText(), passwordField.getSelectedText());
                                        irodsAccount.getUserName();
                                        JOptionPane.showMessageDialog(null, "Connection Established! and your Home directory is" +irodsAccount.getUserName());*/
                                }

                        }
                });

                JButton button_Cancel = new JButton("Cancel");
                button_Cancel.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                                System.exit(0);
                        }
                });

                JLabel Label_Username = new JLabel("User Name:");

                JLabel Label_Password = new JLabel("Password:");

                passwordField = new JPasswordField();
                passwordField.setToolTipText("Password");

                JLabel label_MessageBox = new JLabel("");
                GroupLayout gl_contentPane = new GroupLayout(contentPane);
                gl_contentPane.setHorizontalGroup(
                                gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                                                .addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
                                                                                .addGap(164)
                                                                                .addComponent(button_Login)
                                                                                .addGap(18)
                                                                                .addComponent(button_Cancel)
                                                                                .addPreferredGap(ComponentPlacement.RELATED, 86, Short.MAX_VALUE))
                                                                                .addGroup(gl_contentPane.createSequentialGroup()
                                                                                                .addContainerGap(48, Short.MAX_VALUE)
                                                                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
                                                                                                                .addGroup(gl_contentPane.createSequentialGroup()
                                                                                                                                .addComponent(Label_Username)
                                                                                                                                .addGap(18)
                                                                                                                                .addComponent(textbox_LoginId, GroupLayout.PREFERRED_SIZE, 268, GroupLayout.PREFERRED_SIZE))
                                                                                                                                .addGroup(gl_contentPane.createSequentialGroup()
                                                                                                                                                .addComponent(Label_Password)
                                                                                                                                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
                                                                                                                                                                .addComponent(label_MessageBox, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                                                                                .addComponent(passwordField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))))))
                                                                                                                                                                .addGap(34))
                                );
                gl_contentPane.setVerticalGroup(
                                gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(54)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                                                .addComponent(textbox_LoginId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(Label_Username))
                                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                                                .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(Label_Password))
                                                                                .addGap(18)
                                                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                                                                .addComponent(button_Cancel)
                                                                                                .addComponent(button_Login))
                                                                                                .addGap(36)
                                                                                                .addComponent(label_MessageBox, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                                                                                .addContainerGap(25, Short.MAX_VALUE))
                                );
                contentPane.setLayout(gl_contentPane);
        }
}