package tracing.views;


import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.awt.*;

public class RequirementsIndicesView extends ViewPart implements ISelectionProvider{

	
	private void showMessage() {
		frame = new RequirementsIndexViewerPreperationDlg();
		frame.pack();
		final int width = frame.getWidth();
		final int height = frame.getHeight();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width / 2) - (width / 2);
		int y = (screenSize.height / 2) - (height / 2);
		frame.setLocation(x, y);
		frame.setVisible(true);
	}

	
	// read in file from string
		private String FileReader(String fileName) {
			try {
				return new String(Files.readAllBytes(Paths.get(fileName)));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		// Convert all files in a given directory into dictionary
		// ["filename", "contents string"]
		private Map<String, String> getMapFromFolder(String folder) {
			Map<String, String> ret = new HashMap<String, String>();
			File dir = new File(folder);
			for (File file : dir.listFiles()) {
				// Don't search into any further directories

				if (file.isFile()) {
					if(frame.checkExtension(file.getPath(),".txt"))
					{
						ret.put(file.getName(), FileReader(file.getAbsolutePath()));
					}
				}
			}

			return ret;
		}

		// convert file to dictionary for restoring acronyms
		private Map<String, String> getMapForRestoringAcronyms() {
			Map<String, String> acronymMap  = new HashMap<String, String>();
			// once we get Feature 1 working we will have the file path to put in here
			String fileContents = FileReader(frame.getPath(false, false, true));
			// split file contents on ":", ".", and new lines to put abbreviation
			// and expanded word into dictionary
			String[] fileContentsArray = fileContents.split("(:\\s)|(\\.\r\n)");
			for (int i=0; i<fileContentsArray.length; i=i+2) {
				acronymMap.put(fileContentsArray[i].toString(), fileContentsArray[i+1].toString());
			}
			return acronymMap;
		}

		// restore acronyms
		// pass in string of file contents
		private String restoreAcronyms(String stringFromFile) {
			Map<String, String> acronymList = getMapForRestoringAcronyms();
			String newString = stringFromFile;
			for (String key : acronymList.keySet()) {
				newString = newString.replace(key, (String)acronymList.get(key));
			}
			return newString;
		}

		// gets array of stop words
		private String[] getStopWordArray() {
			// gets file path from dialog box
			String fileContents = FileReader(frame.getPath(false, true, false));
			String[] stopWordArray = fileContents.split("(,)");
			return stopWordArray;
		}

		private String Stem(String sentence )
		{
			String[] splitSentence = sentence.split("[^\\w\n]");
			List<String> temp = new ArrayList<String>(Arrays.asList(splitSentence));
			temp.removeAll(Arrays.asList(""));
			List<String> newWords = new ArrayList<String>();
			for(String word : temp )
			{
				Stemmer stemmer = new Stemmer();
				for (int i = 0; i < word.length(); i++) {
					stemmer.add(word.charAt(i));			
				}
				stemmer.stem();
				newWords.add(stemmer.toString());
			}
			String output = "";
			for (String s : newWords) {
				output += s;

				if (!s.equals("\n")) {
					// Add a space only after each word, not new lines
					output += " ";
				}
			}
			return output;

		}
		// remove stop words
		// pass in string returned after reading file contents
		private String removeStopWords(String stringFromFile) {
			String outPutString = "";
			String[] stopWordArray = getStopWordArray();
			Arrays.sort(stopWordArray);
			String[] stringFromFileArray = stringFromFile.split("((?<=\n)|(?=\n)|( ))");
			for (int i=0; i<stringFromFileArray.length; i++) {
				int index = Arrays.binarySearch(stopWordArray, stringFromFileArray[i].toLowerCase());
				// only add to output string if not a stop word
				if (index < 0) {
					String s = stringFromFileArray[i].toString();
					outPutString += s;

					if (!s.equals("\n")) {
						// Only add space after words, not new lines
						outPutString += " ";
					}
				}
			}

			// Removes trailing space
			outPutString = outPutString.substring(0, outPutString.length()-1);

			return outPutString;
		}

		private String tokenize(String str) {
			// Remove all tokens from the string and store result in an array
			// Make sure to only remove spaces and not-words
			String[] splitSentence = str.split("[^\\w\n]");
			List<String> temp = new ArrayList<String>(Arrays.asList(splitSentence));
			temp.removeAll(Arrays.asList(""));

			// Recreate the string without tokens
			String output = "";
			for (String s: temp) {
				//Do not add empty new lines
				if (!s.equals("\n")) {
					output += s;
					output += " ";
				}

			}

			// Removes trailing space
			output = output.substring(0, output.length()-1);

			return output;
		}

		private void save(String reqFolder, String contents, String fileName)
		{
			File newFolder = new File(reqFolder + "\\Req_Indices");
			String newFileName = "\\" + fileName.substring(0, fileName.lastIndexOf('.')) + "_Indices.txt";

			if(!newFolder.isDirectory())
			{	
				if(!newFolder.mkdir())
				{
					System.out.println("Failed to create Folder");
					return;
				}
			}

			File newFile = new File(newFolder + newFileName);
			try {
				FileOutputStream writeStream = new FileOutputStream(newFile, false);
				byte[] byteContent = contents.getBytes();

				try {
					writeStream.write(byteContent);
					writeStream.close();
				} catch (IOException e) {
					System.out.println("Failed to create File");
					return;
				}
			} catch (FileNotFoundException e) {	
				System.out.println("Failed to Find File");
				return;
			}
		}

		@Override
		public void createPartControl(Composite parent) {

			showMessage();

			//Set layout forum of parent composite
			parent.setLayout(new FormLayout());

			FormData formdata = new FormData();
			formdata.top=new FormAttachment(0,5);
			formdata.left = new FormAttachment(0,10);
			formdata.right = new FormAttachment(0,200);

			//Create title label
			Label titleLabel = new Label(parent,SWT.SINGLE);
			titleLabel.setText("Requirements Indices:");
			titleLabel.setLayoutData(formdata);

			//Create text area
			Text indicesText = new Text(parent,SWT.MULTI|SWT.V_SCROLL|SWT.READ_ONLY|SWT.WRAP);
			indicesText.setText("This is a sample result.");
			formdata = new FormData();
			formdata.top = new FormAttachment(titleLabel,10);
			formdata.bottom = new FormAttachment(titleLabel,230);
			formdata.left = new FormAttachment(0,10);
			formdata.right = new FormAttachment(0,800);
			indicesText.setLayoutData(formdata);
			Timer timer = new Timer();
			try {
				RequirementsView otherView = (RequirementsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("tracing.views.RequirementsView");
				timer.StartTimer();
				// Get source requirements from dialog box
				contentsFromFolder = getMapFromFolder(frame.getPath(true, false, false));

				// Copy contentsFromFolder map to outPut map
				modifiedMap = new HashMap<String, String>();
				modifiedMap.putAll(contentsFromFolder);

				// Start timing of file processing


				// Apply requested processing
				for (String key : contentsFromFolder.keySet()) {

					// check if acronyms box is checked
					if (frame.getAcronymsBox()) {
						modifiedMap.put(key, restoreAcronyms(modifiedMap.get(key)));
					}

					// check if get Stop Words box is checked
					if (frame.getStopBox()) {
						modifiedMap.put(key, removeStopWords(modifiedMap.get(key)));					
					}

					//check if the stemming box is checked.
					if(frame.getStemmingBox()){
						modifiedMap.put(key, Stem(modifiedMap.get(key)));
					}

					// check if tokenizing box is checked
					if (frame.getTokenizingBox()) {
						modifiedMap.put(key, tokenize(modifiedMap.get(key)));
					}

					this.save(frame.getPath(true, false, false), modifiedMap.get(key), key);
				}

				// End timing of file processing
				timer.EndTimer();


				// Default display for RequirementsView text
				String defaultDisplay = "Indexing time of " + contentsFromFolder.size() + " requirement(s) is: " + timer.CheckTimer() + " seconds.";

				ComboViewer comboViewer = otherView.getComboViewer();
				Combo combo = comboViewer.getCombo();
				Text text = otherView.getText();
				text.setText(defaultDisplay);

				// Add drop box options for each requirement stored in contentsFromFolder map
				for (Map.Entry<String, String> entry : contentsFromFolder.entrySet()) {
					combo.add(entry.getKey());
				}

				combo.addSelectionListener(new SelectionListener(){

					@Override
					public void widgetSelected(SelectionEvent e) {
						if(combo.getSelectionIndex()==0)
							text.setText(defaultDisplay);
						else
							// Set the text to the text of the selected file
							text.setText(contentsFromFolder.get(combo.getItem(combo.getSelectionIndex())));
						indicesText.setText(modifiedMap.get(combo.getItem(combo.getSelectionIndex())));
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub

					}

				});

				comboViewer.addSelectionChangedListener(new ISelectionChangedListener(){

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						ISelection comboSelection = event.getSelection();
						setSelection(comboSelection);
					}

				});

			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Button manageButton = new Button(parent,SWT.PUSH);
			manageButton.setText("Manage...");
			formdata = new FormData();
			formdata.top = new FormAttachment(indicesText,10);
			formdata.left = new FormAttachment(0,730);
			manageButton.setLayoutData(formdata);

			manageButton.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					// Prevent showing dialog box for Lab 2 showMessage();

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub

				}

			});
		}
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub

	}

	private RequirementsIndexViewerPreperationDlg frame;
	private Map<String, String> contentsFromFolder;
	private Map<String, String> modifiedMap;

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

}