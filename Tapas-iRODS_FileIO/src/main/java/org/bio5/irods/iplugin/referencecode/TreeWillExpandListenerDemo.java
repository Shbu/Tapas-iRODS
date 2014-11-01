package org.bio5.irods.iplugin.referencecode;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public class TreeWillExpandListenerDemo {
  public static void main(String args[]) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
    root.add(new DefaultMutableTreeNode("A"));
    root.add(new DefaultMutableTreeNode("B"));
    root.add(new DefaultMutableTreeNode("C"));
    JTree tree = new JTree(root);

    TreeWillExpandListener treeWillExpandListener = new TreeWillExpandListener() {
      public void treeWillCollapse(TreeExpansionEvent treeExpansionEvent)
          throws ExpandVetoException {
        TreePath path = treeExpansionEvent.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        String data = node.getUserObject().toString();
        System.out.println("WillCollapse: " + data);

      }

      public void treeWillExpand(TreeExpansionEvent treeExpansionEvent) throws ExpandVetoException {
        TreePath path = treeExpansionEvent.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        String data = node.getUserObject().toString();
        System.out.println("WillExpand: " + data);

      }
    };

    tree.addTreeWillExpandListener(treeWillExpandListener);

    JScrollPane scrollPane = new JScrollPane(tree);
    frame.add(scrollPane, BorderLayout.CENTER);
    frame.setSize(300, 150);
    frame.setVisible(true);

  }
}