package org.bio5.irods.iplugin.referencecode;

//SimpleFileChooser.java
//A simple file chooser to see what it takes to make one of these work.
//
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class SimpleFileChooser extends JFrame {

public SimpleFileChooser() {
 super("File Chooser Test Frame");
 setSize(350, 200);
 setDefaultCloseOperation(EXIT_ON_CLOSE);

 Container c = getContentPane();
 c.setLayout(new FlowLayout());
 
 JButton openButton = new JButton("Open");
 JButton saveButton = new JButton("Save");
 JButton dirButton = new JButton("Choose local file");
 final JLabel statusbar = 
              new JLabel("Output of your selection will go here");

 // Create a file chooser that opens up as an Open dialog
 openButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent ae) {
     JFileChooser chooser = new JFileChooser();
     chooser.setMultiSelectionEnabled(true);
     int option = chooser.showOpenDialog(SimpleFileChooser.this);
     if (option == JFileChooser.APPROVE_OPTION) {
       File[] sf = chooser.getSelectedFiles();
       String filelist = "nothing";
       if (sf.length > 0) filelist = sf[0].getName();
       for (int i = 1; i < sf.length; i++) {
         filelist += ", " + sf[i].getName();
       }
       statusbar.setText("You chose " + filelist);
     }
     else {
       statusbar.setText("You canceled.");
     }
   }
 });

 // Create a file chooser that opens up as a Save dialog
 saveButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent ae) {
     JFileChooser chooser = new JFileChooser();
     int option = chooser.showSaveDialog(SimpleFileChooser.this);
     if (option == JFileChooser.APPROVE_OPTION) {
       statusbar.setText("You saved " + ((chooser.getSelectedFile()!=null)?
                         chooser.getSelectedFile().getName():"nothing"));
     }
     else {
       statusbar.setText("You canceled.");
     }
   }
 });

 // Create a file chooser that allows you to pick a directory
 // rather than a file
 dirButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent ae) {
     JFileChooser chooser = new JFileChooser();
     chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
     int option = chooser.showOpenDialog(SimpleFileChooser.this);
     if (option == JFileChooser.APPROVE_OPTION) {
       statusbar.setText("You opened " + ((chooser.getSelectedFile()!=null)?
                         chooser.getSelectedFile().getAbsolutePath():"nothing"));
     }
     else {
       statusbar.setText("You canceled.");
     }
   }
 });

 c.add(openButton);
 c.add(saveButton);
 c.add(dirButton);
 c.add(statusbar);
}

public static void main(String args[]) {
 SimpleFileChooser sfc = new SimpleFileChooser();
 sfc.setVisible(true);
}
}
