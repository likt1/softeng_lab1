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
	private Text text;
		
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tracing.views.RequirementsView";
			
	/**
	 * The constructor.
	 */
	public RequirementsView() {
		
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	
	// Get drop-down menu
	public ComboViewer getComboViewer()
	{
		return comboViewer;
	}
	
	// Get text area
	public Text getText() {
		return text;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		//Set layout forum of parent composite
		parent.setLayout(new FormLayout());
		
		//Create a drop box
		comboViewer = new ComboViewer(parent,SWT.NONE|SWT.DROP_DOWN);
		Combo combo = comboViewer.getCombo();
		combo.add("Choose Use Case");
		
		combo.select(0);
		
		//Set combo position
		FormData formdata = new FormData();
		formdata.top=new FormAttachment(0,5);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,290);
		combo.setLayoutData(formdata);
		
		//Set text position
		text = new Text(parent,SWT.MULTI|SWT.V_SCROLL|SWT.READ_ONLY|SWT.WRAP);
		formdata = new FormData();
		formdata.top=new FormAttachment(combo,10);
		formdata.bottom = new FormAttachment(combo,600);
		formdata.left = new FormAttachment(0,5);
		formdata.right = new FormAttachment(0,355);
		text.setLayoutData(formdata);
		//set text content
		text.setText("Indexing time of X requirement(s) is: Y seconds.");
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
		String newString = stringFromFile;
		for (String key : acronymList.keySet()) {
			newString = newString.replace(key, (String)acronymList.get(key));
		}
		return newString;
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
	public String removeStopWords(String stringFromFile) {
		String outPutString = "";
		String[] stopWordArray = getStopWordArray();
		Arrays.sort(stopWordArray);
		String[] stringFromFileArray = stringFromFile.split("\\s+");
		for (int i=0; i<stringFromFileArray.length; i++) {
			int index = Arrays.binarySearch(stopWordArray, stringFromFileArray[i].toLowerCase());
	        if (index >= 0) {} else {
	        	outPutString = outPutString + stringFromFileArray[i].toString() + " ";
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
	
	// Convert all files in a given directory into dictionary
	// ["filename", "contents string"]
	public Map<String, String> getMapFromFolder(String folder) {
		Map<String, String> ret = new HashMap<String, String>();
		File dir = new File(folder);
		for (File file : dir.listFiles()) {
			// Don't search into any further directories
			
			if (file.isFile()) {
				if(checkExtension(file.getPath(),".txt"))
				{
					ret.put(file.getName(), FileReader(file.getAbsolutePath()));
				}
			}
		}
		
		return ret;
	}
}