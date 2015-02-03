package tracing.views;


import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Arrays;

public class RequirementsIndicesView extends ViewPart implements ISelectionProvider{
	
	private RequirementsIndexViewerPreperationDlg frame;
	private Map<String, String> reqs;
	
	private void showMessage() {
		frame = new RequirementsIndexViewerPreperationDlg();
		frame.pack();
		frame.setVisible(true);

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
	
	public String[] tokenize(String str) {
//		String fileContents = new String();
//		String[] fail = {"Failed"};
//		
//		try {
//			FileReader file = new FileReader(filePath);
//			BufferedReader reader = new BufferedReader(file);
//			
//			String line;
//			while ((line = reader.readLine()) != null) {
//				fileContents = fileContents.concat(line);
//			}
//			reader.close();
//		}
//		catch (Exception e)
//		{
//			return fail;
//		}
		
		List<String> temp = new ArrayList<String>(Arrays.asList(str.split("[\\W]")));
		temp.removeAll(Arrays.asList(""));
		
		return temp.toArray(new String[temp.size()]);
	}

	@Override
	public void createPartControl(Composite parent) {
		
		showMessage();
		
		try {
			RequirementsView otherView = (RequirementsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("tracing.views.RequirementsView");

			Combo combo = otherView.getComboViewer().getCombo();
			combo.add("I was successfully ACTIVATED");

			// Get source requirements from dialog box
			reqs = otherView.getMapFromFolder(frame.getSourcePath());
			
			ComboViewer comboViewer = otherView.getComboViewer();
			//Combo combo = comboViewer.getCombo();
			Text text = otherView.getText();
			
			// Add drop box options for each requirement stored in reqs map
			for (Map.Entry<String, String> entry : reqs.entrySet()) {
				combo.add(entry.getKey());
			}
			
			combo.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					if(combo.getSelectionIndex()==0)
						text.setText("Indexing time of X requirement(s) is: Y seconds.");
					else
						// Set the text to the text of the selected file
						text.setText(reqs.get(combo.getItem(combo.getSelectionIndex())));
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
		
		
		Button manageButton = new Button(parent,SWT.PUSH);
		manageButton.setText("Manage...");
		formdata = new FormData();
		formdata.top = new FormAttachment(indicesText,10);
		formdata.left = new FormAttachment(0,730);
		manageButton.setLayoutData(formdata);
		
		manageButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				showMessage();
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
			
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}