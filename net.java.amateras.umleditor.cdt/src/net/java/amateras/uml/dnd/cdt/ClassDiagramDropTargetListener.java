/**
 * 
 */
package net.java.amateras.uml.dnd.cdt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.cdt.ImportClassModelCommand;
import net.java.amateras.uml.cdt.UMLCdtUtils;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.model.Structure;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
//import net.java.amateras.uml.cdt.ImportClassModelCommand;

class ClassDiagramDropTargetListener extends AbstractTransferDropTargetListener {
	
	public ClassDiagramDropTargetListener(EditPartViewer viewer, Transfer xfer) {
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

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#getCommand()
	 */
	protected Command getCommand() {
		RootModel root = (RootModel)getViewer().getContents().getModel();
		IStructuredSelection selection = (IStructuredSelection) getCurrentEvent().data;
		
		List<ICElement> types = new ArrayList<ICElement>();
		for(Iterator<?> ite = selection.iterator(); ite.hasNext();){
			Object element = ite.next();
			if(element instanceof ICElement){
				ICElement[] typeArray = UMLCdtUtils.getTypes((ICElement) element);
				for(int i=0;i<typeArray.length;i++){
					if(!types.contains(typeArray[i])){
						types.add(typeArray[i]);
					}
				}
			}
		}
		
		if(!types.isEmpty()){
			ImportClassModelCommand command = new ImportClassModelCommand(
					root, types.get(0));
			command.setLocation(getDropLocation());
			return command;
		}
		return null;
	}

	protected void handleDrop() {
		super.handleDrop();
		getCurrentEvent().detail = DND.DROP_COPY;
	}

	protected void updateTargetRequest() {
		// Nothing to do
	}

	public boolean isEnabled(DropTargetEvent event) {
		boolean result =super.isEnabled(event);
		
		if (event.data == null) {
			return result;
		}

		if (result) {
			IStructuredSelection selection = (IStructuredSelection) event.data;
			
			List<ICElement> types = new ArrayList<ICElement>();
			for(Iterator<?> ite = selection.iterator(); ite.hasNext();){
				Object element = ite.next();
				if(element instanceof ICElement){
					ICElement[] typeArray = UMLCdtUtils.getTypes((ICElement) element);
					for(int i=0;i<typeArray.length;i++){
						if(!types.contains(typeArray[i])){
							types.add(typeArray[i]);
						}
					}
				}
			}
			
			result = !types.isEmpty();
		}
		
		return result;
	}
}

/*@SuppressWarnings("restriction")
class ClassDiagramDropTargetListener extends AbstractTransferDropTargetListener {
	
	public ClassDiagramDropTargetListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOver();
	}
	
	protected void updateTargetRequest() {
		((CreateRequest) getTargetRequest()).setLocation(getDropLocation());
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
}*/