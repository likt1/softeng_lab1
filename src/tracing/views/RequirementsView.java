package tracing.views;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.SWT;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class RequirementsView extends ViewPart implements ISelectionProvider{
	
	private ISelection selection;
	private ComboViewer comboViewer;
	
	//private Map<String, String> reqs;
	
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tracing.views.RequirementsView";
			
	/**
	 * The constructor.
	 */
	public RequirementsView() {
		// Hard coding folder name for testing purposes
		//reqs = getMapFromFolder("C:\\Users\\Nathan\\Documents\\School\\Spring Semester 2015\\Software Engineering\\Labs\\Lab1_test_files");
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	
	public ComboViewer getComboViewer()
	{
		return comboViewer;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		//Set layout forum of parent composite
		parent.setLayout(new FormLayout());
		
		//Create a drop box
		comboViewer = new ComboViewer(parent,SWT.NONE|SWT.DROP_DOWN);
		Combo combo = comboViewer.getCombo();
		combo.add("Choose Use Case");
		
		// Add drop box options for each requirement stored in reqs map
		/*for (Map.Entry<String, String> entry : reqs.entrySet()) {
			combo.add(entry.getKey());
		}*/
		
		combo.select(0);
		
		//Set combo position
		FormData formdata = new FormData();
		formdata.top=new FormAttachment(0,5);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,290);
		combo.setLayoutData(formdata);
		
		//Set text position
		Text text = new Text(parent,SWT.MULTI|SWT.V_SCROLL|SWT.READ_ONLY);
		formdata = new FormData();
		formdata.top=new FormAttachment(combo,10);
		formdata.bottom = new FormAttachment(combo,600);
		formdata.left = new FormAttachment(0,5);
		formdata.right = new FormAttachment(0,355);
		text.setLayoutData(formdata);
		//set text content
		text.setText("Indexing time of X requirement(s) is: Y seconds.");
		
		combo.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(combo.getSelectionIndex()==0)
					text.setText("Indexing time of X requirement(s) is: Y seconds.");
				//else
					// Set the text to the text of the selected file
					//text.setText(reqs.get(combo.getItem(combo.getSelectionIndex())));
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
		
	}
	
	@Override
	public void setSelection(ISelection selection) {
		this.selection = selection;
		SelectionChangedEvent event = new SelectionChangedEvent(comboViewer,selection);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
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
	
	// convert file to dictionary for restoring acronyms
	public Map<String, String> getMapForRestoringAcronyms() {
		Map<String, String> acronymMap  = new HashMap<String, String>();
		// once we get Feature 1 working we will have the file path to put in here
		String fileContents = FileReader("");
		// split file contents on ":", ".", and new lines to put abbreviation
		// and expanded word into dictionary
		String[] fileContentsArray = fileContents.split("(:\\s)|(\\.\r\n)");
		for (int i=0; i<fileContentsArray.length; i=i+2) {
			acronymMap.put(fileContentsArray[i].toString(), fileContentsArray[i+1].toString());
		}
		return acronymMap;
	}
	
	// restore acronyms
	// pass in word array after tokenizing
	public String restoreAcronyms(String stringFromFile) {
		Map<String, String> acronymList = getMapForRestoringAcronyms();
		for (String key : acronymList.keySet()) {
			stringFromFile = stringFromFile.replace(key, (String)acronymList.get(key));
		}
		return stringFromFile;
	}
	
	// gets array of stop words
	public String[] getStopWordArray() {
		// once we get Feature 1 working we will have the file path to put in here
		String fileContents = FileReader("");
		String[] stopWordArray = fileContents.split("(,)");
		return stopWordArray;
	}
	
	// remove stop words
	// pass in word array after tokenizing
	public String removeStopWords(String[] wordArray) {
		String outPutString = "";
		String[] stopWordArray = getStopWordArray();
		Arrays.sort(stopWordArray);
		for (int i=0; i<wordArray.length; i++) {
			int index = Arrays.binarySearch(stopWordArray, wordArray[i].toLowerCase());
	        if (index >= 0) {} else {
	        	outPutString = outPutString + wordArray[i].toString() + " ";
	        }
		}
		outPutString = outPutString.substring(0, outPutString.length()-1);
		return outPutString;
	}
	
	// read in file from string
	public String FileReader(String fileName) {
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Convert all files in a given directory into dictionary
	// ["filename", "contents string"]
	public Map<String, String> getMapFromFolder(String folder) {
		Map<String, String> ret = new HashMap<String, String>();
		File dir = new File(folder);
		for (File file : dir.listFiles()) {
			// Don't search into any further directories
			if (file.isFile()) {
				ret.put(file.getName(), FileReader(file.getAbsolutePath()));
			}
		}
		
		return ret;
	}
}