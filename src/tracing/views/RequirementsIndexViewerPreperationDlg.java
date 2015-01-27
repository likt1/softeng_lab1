package tracing.views;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.awt.event.*;
import java.io.*;
import java.beans.*; 

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.SwingUtilities;


import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;


public class RequirementsIndexViewerPreperationDlg  extends JDialog
implements MouseListener  {

	JButton sourceBrowseButton, restoringBrosweButton, stopWordsBrowseButton, okButton;
	JLabel sourceFolderLabel, tokeninizingLabel, restoringLabel, stopWordsLabel, stemmingLabel;
	JTextField sourceFolderField, restoringField, stopWordsField;
	JPanel sourcePanel,tokenPanel,acroynmsPanel,stopWordsPanel,stemmingPanel;
	JCheckBox tokenizingBox, restoringBox, stopWordsBox, stemmingBox;
	JFileChooser fc;
	public RequirementsIndexViewerPreperationDlg()
	{
		super();
		Container pane = this.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        
		// Create the labels 
		sourceFolderLabel = new JLabel("Requiements source folder:");
		restoringLabel = new JLabel("Restoring acronyms");
		stopWordsLabel = new JLabel("Removing stop words");
		tokeninizingLabel = new JLabel("Tokenizing");
		stemmingLabel = new JLabel("stemming");
		//Create the Text Fields
		sourceFolderField = new JTextField(30);
		restoringField = new JTextField(30);
		stopWordsField = new JTextField(30);
		//Create the buttons and attach the listeners 
		sourceBrowseButton = new JButton("Browse");
		sourceBrowseButton.addMouseListener(this);
		restoringBrosweButton = new JButton("Browse");
		restoringBrosweButton.addMouseListener(this);
		stopWordsBrowseButton = new JButton("Browse");
		stopWordsBrowseButton.addMouseListener(this);
		okButton = new JButton("OK");
		okButton.addMouseListener(this);
		//Add the check Boxes
		tokenizingBox = new JCheckBox();
		restoringBox = new JCheckBox();
		stopWordsBox = new JCheckBox();
		stemmingBox = new JCheckBox();
		//Create the panels 
		sourcePanel = new JPanel();
		tokenPanel =  new JPanel();
		acroynmsPanel = new JPanel();
		stopWordsPanel = new JPanel();
		stemmingPanel = new JPanel();
		//Add the stuff to the panels
		sourcePanel.add(sourceFolderLabel);
		sourcePanel.add(sourceFolderField);
		sourcePanel.add(sourceBrowseButton);
		sourcePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		tokenPanel.add(tokenizingBox);
		tokenPanel.add(tokeninizingLabel);
		tokenPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		acroynmsPanel.add(restoringBox);
		acroynmsPanel.add(restoringLabel);
		acroynmsPanel.add(restoringBrosweButton);
		acroynmsPanel.add(restoringField);
		acroynmsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		stopWordsPanel.add(stopWordsBox);
		stopWordsPanel.add(stopWordsLabel);
		stopWordsPanel.add(stopWordsBrowseButton);
		stopWordsPanel.add(stopWordsField);
		stopWordsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		stemmingPanel.add(stemmingBox);
		stemmingPanel.add(stemmingLabel);
		stemmingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		//Add to this
		

		pane.add(sourcePanel);
		pane.add(tokenPanel);
		pane.add(acroynmsPanel);
		pane.add(stopWordsPanel);
		pane.add(stemmingPanel);
		pane.add(okButton);
		
		fc = new JFileChooser();
	}
	
	 public void mouseClicked (MouseEvent me) {
		 if(me.getSource() == restoringBrosweButton ||
			me.getSource() == stopWordsBrowseButton){
			 fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			 int retValue = fc.showDialog(this, "Choose");
			 if (retValue == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                //This is where a real application would open the file.
	                if(me.getSource()== restoringBrosweButton)
	                {
	                	restoringField.setText(file.getPath());
	                }
	                else
	                {
	                	stopWordsField.setText(file.getPath());
	                }
	            }
		 }
		 else if (me.getSource() == sourceBrowseButton){
			 fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			 int retValue = fc.showDialog(this, "Choose");
			 if (retValue == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                sourceFolderField.setText(file.getPath());
			 }
		 }
		 else if(me.getSource() == okButton)
		 {
			
			 //this.setVisable(false);
			 isNotDone= false; 
		 }
		 this.repaint();
	 }
	 
	 public void mouseEntered (MouseEvent me)  {this.repaint();} 
	 public void mousePressed (MouseEvent me) {this.repaint();} 
	 public void mouseReleased (MouseEvent me)  {this.repaint();} 
	 public void mouseExited (MouseEvent me)  {this.repaint();}  
	 
	 
	 public Boolean isNotDone = true;
	
	
}
