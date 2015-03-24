package tracing.views;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;



public class MethodsIndicesView extends ViewPart implements ISelectionProvider {

	
	Map<String, ArrayList<String>> methodMap;
	
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
	
	@Override
	public void createPartControl(Composite parent) {
		File f = new File("C:\\iTrust\\.project");
		Timer timer = new Timer();
		
		if(!f.exists() || f.isDirectory()) { 
	        JOptionPane.showMessageDialog(null,"Could not find the iTrust project file. Please ensure that iTrsut located at C:\\iTrust" , "InfoBox: File Missing", JOptionPane.INFORMATION_MESSAGE); }
		try { // importiTrust
			IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path("C:\\iTrust\\.project"));
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
			project.create(description, null);
			project.open(null);
		} catch (Exception e)
		{
			
		}
		
		int totalMethod = 0;
		try{
			timer.StartTimer();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			
			methodMap = new HashMap<String, ArrayList<String>>();
			IProject[] projects = root.getProjects();

			// process each project
			for (IProject project : projects) {

				

				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
					IJavaProject javaProject = JavaCore.create(project);
					IPackageFragment[] packages = javaProject.getPackageFragments();

					// process each package
					for (IPackageFragment aPackage : packages) {

					
						if (aPackage.getKind() == IPackageFragmentRoot.K_SOURCE) {

							for (ICompilationUnit unit : aPackage
									.getCompilationUnits()) {

					

								IType[] allTypes = unit.getAllTypes();
								for (IType type : allTypes) {

									IMethod[] methods = type.getMethods();

									for (IMethod method : methods) {
										totalMethod++;
										ArrayList<String> list = new ArrayList<String>();
										list.add(method.getSource());
										breakFunction(list);
										list = tokenize(list);
										//should we have both the original and the tokenized string?
										methodMap.put(type.getFullyQualifiedName()+ '.' + method.getElementName(), list);
										
									}
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		timer.EndTimer();
		
		//Set layout forum of parent composite
		parent.setLayout(new FormLayout());
		
		FormData formdata = new FormData();
		formdata.top=new FormAttachment(0,5);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,200);
		
		//Create title label
		Label titleLabel = new Label(parent,SWT.SINGLE);
		titleLabel.setText("Method Indices:");
		titleLabel.setLayoutData(formdata);
		
		//Create text area
		Text indicesText = new Text(parent,SWT.MULTI|SWT.V_SCROLL|SWT.READ_ONLY|SWT.WRAP);
		indicesText.setText("It took "+ timer.CheckTimer() + " seconds to index "+ totalMethod  +"  methods.");
		formdata = new FormData();
		formdata.top = new FormAttachment(titleLabel,10);
		formdata.bottom = new FormAttachment(titleLabel,230);
		formdata.left = new FormAttachment(0,10);
		formdata.right = new FormAttachment(0,800);
		indicesText.setLayoutData(formdata);
		
		
		
		// Create selection listener for workbench selections (Feature 12)
		final ISelectionListener workbenchListener = new ISelectionListener() {
			
			@Override
			public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
				
				// Only proc on change events outside of this view
				if (sourcepart != MethodsIndicesView.this && selection instanceof IStructuredSelection) {
					
					
					
					//TODO: Set text of this view text area to contents in dictionary corresponding
					//      to selection method name.
					IStructuredSelection s = (IStructuredSelection)selection;
					if(s.getFirstElement() instanceof IMethod)
					{
						IMethod method = (IMethod)(s.getFirstElement());
						try{
						ArrayList<String> recovery = new ArrayList<String>();
						recovery.add(method.getSource());
						breakFunction(recovery);
						recovery = tokenize(recovery);
						indicesText.setText(printFunction(recovery));
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						
					}
				}
			}
		};
		
		// Add the workbench selection listener to the workbench
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(workbenchListener);
	}

	private String printFunction(ArrayList<String> list) {
		String s = "";
		for(String word : list)
		{
		 s += word + " ";
		}
		return s;
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	
	
	public static void breakFunction(ArrayList<String> functionList)
	{
		String text = functionList.get(0);
		functionList.clear();
		Boolean inQuotes = false;
		int lastIndex = 0;
		//Ok so here is what is going to happen
		//We are going to go though looking for as few special charaters
		//New line chars
		// comments lines
		// and more comments lines
		// and quotes
		for(int i = 0; i < text.length(); i++)
		{
			switch(text.charAt(i)){
			case '\n':
				if(!inQuotes)
				{
					String split = text.substring(lastIndex, i+1);
					functionList.add(split);
					lastIndex = i+1;
				}
				break;
			case '"':
				if((i-1) >= 0)
				{
					if(text.charAt(i-1) == '/')
					{
						inQuotes = !inQuotes;
					}
				}
				break;
			case '/':
				//We will catch coments on the second one of these not the first
				if(!inQuotes) //if we are in quotes we can not start a coment skip this
				{
					if((i+1) <text.length())
					{
						if(text.charAt(i+1) == '/')
						{
							functionList.add(text.substring(lastIndex, i));
							// we have a comment find the next newline
							int CommentEnd = text.indexOf('\n', i);
							if(CommentEnd > 0)
							{
							functionList.add(text.substring(i, CommentEnd+1));
							lastIndex = CommentEnd +1;
							i = CommentEnd;
							}
							else
							{
								functionList.add(text.substring(i));
								i= text.length();
							}
						}
						else if(text.charAt(i+1) == '*')
						{
							functionList.add(text.substring(lastIndex, i));
							// we have a long comment and need to find the end mark.
							int CommentEnd = text.indexOf("*/", i)+1;
							if(CommentEnd > 0)
							{
							functionList.add(text.substring(i, CommentEnd+1));
							lastIndex = CommentEnd +1;
							i = CommentEnd;
							}
							else
							{
								functionList.add(text.substring(i));
								i= text.length();
							}
							
						}
					}
				}
				break;
			default:
				break;
			}
		}	
	}
	
	private ArrayList<String> tokenize(ArrayList<String> list)
	{
		
		
		ArrayList<String> newList = new ArrayList<String>();
		for(String line : list)
		{
			if(line.startsWith("//") || line.startsWith("/*"))
			{
				//Its a comment make no changes
				newList.add(line);
			}
			else
			{
				Boolean endWithNewLine = false;
				if(line.endsWith("\n"))
				{
					endWithNewLine = true;
				}
				boolean inWord = false;
				int oldIndex = 0;
				for(int i = 0; i < line.length(); i++)
				{
					if(!inWord)
					{
						if(isLetter(line.charAt(i)))
						{
							inWord = true;
							oldIndex = i;
						}
					}
					else
					{
						if(!isLetter(line.charAt(i)))
						{
							inWord = false;
							newList.add(line.substring(oldIndex, i));
						}
						else if(isUpper(line.charAt(i)))
						{
							newList.add(line.substring(oldIndex, i));
							oldIndex = i;
						}
					}
					
				}
				if(endWithNewLine)
				{
					newList.add(newList.remove(newList.size()-1).concat("\n"));
				}
			}
		}
		return newList;
	}
	
	
	private boolean isLetter(char ch)
	{
		return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));
	}
	
	private boolean isUpper(char ch)
	{
		return (ch >= 'A' && ch <= 'Z');
	}
	
	private boolean isLower(char ch)
	{
		return (ch >= 'a' && ch <= 'z');
	}
	
	
}

