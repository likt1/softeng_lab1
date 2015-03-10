
package tracing.views;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.*;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFrame;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

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
		super(new JFrame(), true);
		Container pane = this.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowListener (){
			public void windowOpened(WindowEvent arg){}
			public void windowClosing(WindowEvent arg){
				if(checkValues())
				{
					setVisible(false);
				}
			}
			public void windowDeactivated(WindowEvent arg){}
			public void windowClosed(WindowEvent arg){}
			public void windowDeiconified(WindowEvent arg){}
			public void windowActivated(WindowEvent arg){}
			public void windowIconified(WindowEvent arg){}
		});
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
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Text Files", "txt");
			fc.addChoosableFileFilter(filter);
			fc.setFileFilter(filter);
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
			if(checkValues())
			{
				this.setVisible(false);

//			

			}
		}
		this.repaint();
	}
	
	// takes in array list of lines from code
	// puts tokenized string into a dictionary


	private boolean checkValues()
	{
		boolean retValue = true;
		try{
			File f = new File(restoringField.getText());
			File g = new File(stopWordsField.getText());
			File h = new File(sourceFolderField.getText());
			if( !h.isDirectory())
			{
				JOptionPane.showMessageDialog(this, "Please provide valid directory.");
				retValue = false;
			}		
			else if(restoringBox.isSelected() && (!f.exists() || f.isDirectory() || !f.canRead() || !checkExtension(f.getPath(), ".txt")))
			{
				JOptionPane.showMessageDialog(this, "Please provide a valid filePath for the restoring acroyms.");
				retValue = false;
			}
			else if(stopWordsBox.isSelected() && (!g.exists() || g.isDirectory() || !g.canRead() || !checkExtension(g.getPath(), ".txt")))
			{
				JOptionPane.showMessageDialog(this, "Please provide a valid file path for stop words.");
				retValue = false;
			}				 
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(this, e.toString());
			retValue = false; 
		}
		return retValue;
	}

	public void mouseEntered (MouseEvent me)  {this.repaint();} 
	public void mousePressed (MouseEvent me) {this.repaint();} 
	public void mouseReleased (MouseEvent me)  {this.repaint();} 
	public void mouseExited (MouseEvent me)  {this.repaint();}  
	private boolean checkExtension(String filePath, String extension)
	{
		boolean retValue = false;
		int index= filePath.lastIndexOf('.');
		if(index >0){
			String fileExt = filePath.substring(index);
			if(fileExt.equals(extension))
			{	 
				retValue = true;
			}
		}
		return retValue;
	}

	public String  getSourcePath()
	{
		return sourceFolderField.getText();
	}

	public String getStopWordsPath()
	{
		String  retValue = null;
		if(stopWordsBox.isSelected()){ retValue =  stopWordsField.getText();}
		return retValue;
	}

	public String getAcroymsPath()
	{	
		String retValue = null;
		if(restoringBox.isSelected()) {retValue = restoringField.getText(); }
		return retValue;
	}

	public boolean getTokenizingBox()
	{
		return tokenizingBox.isSelected();
	}

	public boolean getStopBox()
	{
		return stopWordsBox.isSelected();
	}

	public boolean getStemmingBox()
	{
		return stemmingBox.isSelected();
	}

	public boolean getAcronymsBox()
	{
		return restoringBox.isSelected();
	}

}