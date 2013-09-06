/**
 * 
 */
package net.java.amateras.uml.dnd.cdt;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.cdt.internal.core.model.Enumeration;
import org.eclipse.cdt.internal.core.model.Namespace;
import org.eclipse.cdt.internal.core.model.Parent;
import org.eclipse.cdt.internal.core.model.Structure;

/**
 * 
 * @author shida
 * @author Naoki Takezoe
 */
@SuppressWarnings({ "unused", "restriction" })
public class SequenceDiagramDropTargetListener extends
		AbstractTransferDropTargetListener {

	public SequenceDiagramDropTargetListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOver();
	}
	
	protected Request createTargetRequest() {
		CreateRequest request = new CreateRequest();
		return request;
	}

	private InstanceModelConverter getConverter(IStructuredSelection selection){
		if (selection == null)
			return null;
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof Structure) {
			return new InstanceModelConverter(firstElement);
		}
		return null;
/*
		if(firstElement instanceof IJavaElement){
			IType[] types = UMLJavaUtils.getTypes((IJavaElement) firstElement);
			if(types != null && types.length > 0){
				return new InstanceModelConverter(types[0]);
			}
		}
		*/

	}
	
	protected void updateTargetRequest() {
		((CreateRequest) getTargetRequest()).setLocation(getDropLocation());
	}
	
	protected Command getCommand() {
		InstanceModelConverter converter = getConverter((IStructuredSelection) getCurrentEvent().data);
		if (converter == null) {
			return null;
		}
		return super.getCommand();
	}
	
	protected void handleDrop() {
		InstanceModelConverter converter = getConverter((IStructuredSelection) getCurrentEvent().data);
		if(converter!=null){
			((CreateRequest) getTargetRequest()).setFactory(converter);
		}
		super.handleDrop();
		getCurrentEvent().detail = DND.DROP_COPY;
	}
	
	public boolean isEnabled(DropTargetEvent event) {
		boolean result =super.isEnabled(event);
		
		if (event.data == null) {
			return result;
		}

		if (result) {
		  result = (getConverter((IStructuredSelection) event.data) != null);
		}
		
		return result;
	}
}
