import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

public class MyMenuHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
		messageBox.setText("Warning");
//		ISelection sel = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
//			      .getSelectionService().getSelection();
//			  if (sel instanceof ITreeSelection) {
//			    ITreeSelection treeSel = (ITreeSelection) sel;
//			    if (treeSel.getFirstElement() instanceof IFile) {
//			      IFile file = (IFile) treeSel.getFirstElement();
//			      List<IMarker> markers = MarkerFactory.findMarkers(file);
//			      MessageDialog dialog = new MessageDialog(MarkerActivator.getShell(), "Marker Count", null,
//			          markers.size() + " marker(s)", MessageDialog.INFORMATION, new String[] {"OK"}, 0);
//			      dialog.open();
//			    }
//			  }
		return null;
	}

}
