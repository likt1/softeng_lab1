package tracing.views;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tracing.views.RequirementsView";
	
	// for testing purposes only
	public String[] WORDARRAY = {"An", "admin", "creates", "a", "LHCP", "an", "ER", "a", "LT", "or", "a", "PHA"};
	Map<String, String> DICTIONARY = new HashMap<String, String>();
		
	/**
	 * The constructor.
	 */
	public RequirementsView() {
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
		combo.add("UC0");
		combo.add("UC1");
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
				else if(combo.getSelectionIndex()==1)
					text.setText("This is a sample.");
				else if(combo.getSelectionIndex()==2)
					restoreAcronyms(WORDARRAY);
				else
					text.setText("");
				
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
	
	// testing only
	public void populateDictionary() {
		DICTIONARY.put("LHCP", "Licensed Health Care Professional");
		DICTIONARY.put("ER", "Emergency Responder");
		DICTIONARY.put("LT", "Laboratory Technician");
		DICTIONARY.put("PHA", "Public Health Agent");
		DICTIONARY.put("OB/GYN", "Obsterics and Gynaecology");
	}
	
	// convert file to dictionary for restoring acronyms
	public Map<String, String> getMapForRestoringAcronyms() {
		Map<String, String> acronymMap  = new HashMap<String, String>();;
		String fileContents = FileReader("C:/Users/wisni_000/Documents/Spring 2015/Software Engineering/EECE3093SS15/src/Acronym_List");
		String[] fileContentsArray = fileContents.split("(:\\s)|(\r\n)");
		for (int i=0; i<fileContentsArray.length; i=i+2) {
			String abbreviation = fileContentsArray[i].toString();
			String expandedText = fileContentsArray[i+1].toString();
			expandedText = expandedText.substring(0, expandedText.length()-1);
			acronymMap.put(abbreviation, expandedText);
		}
		return acronymMap;
	}
	
	// restore acronyms
	public String restoreAcronyms(String[] wordArray) {
		String outPutString = "";
		populateDictionary();
		Map<String, String> acronymList = getMapForRestoringAcronyms();
		for (int i=0; i < wordArray.length; i++) {
			for (String key : acronymList.keySet()) {
				if (wordArray[i] == key) {
					wordArray[i] = acronymList.get(key);
				}
			}
			outPutString = outPutString + wordArray[i] + " ";
		}
		System.out.println(outPutString);
		return outPutString;
	}
	
	// read in file from string
	public String FileReader(String fileName) {
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}