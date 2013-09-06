package net.java.amateras.uml.cdt;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.CommonEntityModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.model.Structure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * The command to add Java types to the class diagram.
 * 
 * @author Naoki Takezoe
 */
public class ImportClassModelCommand extends Command {
	
	private ICElement[] types;
	private RootModel root;
	private List<AbstractUMLEntityModel> models;
	private Point location;
	
	/**
	 * Constructor for the one type adding.
	 * 
	 * @param root the root model
	 * @param type the type to add
	 */
	public ImportClassModelCommand(RootModel root,ICElement type){
		this.root = root;
		this.types = new ICElement[]{ type };
	}
	
	/**
	 * Constructor for the two or more types adding.
	 * 
	 * @param root the root model
	 * @param types types to add
	 */
	public ImportClassModelCommand(RootModel root,ICElement[] types){
		this.root = root;
		this.types = types;
	}
	
	public void setLocation(Point location){
		this.location = location;
	}
	
	public void execute(){
		models = new ArrayList<AbstractUMLEntityModel>();
		List<AbstractUMLEntityModel> addedModels = new ArrayList<AbstractUMLEntityModel>();
		for(int i=0;i<types.length;i++){
			AbstractUMLEntityModel entity = createModel(types[i]);
			addedModels.add(entity);
			if(entity != null){
				if(location!=null){
					entity.setConstraint(new Rectangle(
							location.x + (i * 10), 
							location.y + (i * 10), -1, -1));
				} else {
					entity.setConstraint(new Rectangle(10, 10, -1, -1));
				}
				root.copyPresentation(entity);
				root.addChild(entity);
				models.add(entity);
			}
		}
		
		//addConnections(addedModels);
	}
	
	private void addConnections(List<AbstractUMLEntityModel> addedModels){
		/*try {
			for(int i=0;i<addedModels.size();i++){
				AbstractUMLEntityModel model = addedModels.get(i);
				if(types[i].isInterface()){
					UMLJavaUtils.appendInterfacesConnection(this.root, types[i], model);
				} else {
					UMLJavaUtils.appendSuperClassConnection(this.root, types[i], model);
					UMLJavaUtils.appendInterfacesConnection(this.root, types[i], model);
					UMLJavaUtils.appendAggregationConnection(this.root, types[i], (ClassModel) model);
				}
				UMLJavaUtils.appendSubConnection(root, types[i].getJavaProject(), model);
			}
		} catch(JavaModelException ex){
			UMLPlugin.logException(ex);
		}*/
	}
	
	private AbstractUMLEntityModel createModel(ICElement type){
		if(type instanceof Structure){
			Structure s = (Structure)type;
			
			CommonEntityModel model;
			try {
				model = s.isAbstract() ? new InterfaceModel() : new ClassModel();
			} catch (CModelException e) {
				model = new ClassModel();
			}
			model.setName(s.getElementName());
			AttributeModel[] fields = UMLCdtUtils.getFields(type);
			for(int i=0;i<fields.length;i++){
				model.addChild(fields[i]);
			}
			OperationModel[] methods = UMLCdtUtils.getMethods(type);
			for(int i=0;i<methods.length;i++){
				model.addChild(methods[i]);
			}
			return model;
			
		}
		return null;
	}
	
	public void undo(){
		for(AbstractUMLEntityModel model: models){
			for(AbstractUMLConnectionModel conn: model.getModelSourceConnections()){
				conn.detachSource();
				conn.detachTarget();
			}
			for(AbstractUMLConnectionModel conn: model.getModelTargetConnections()){
				conn.detachSource();
				conn.detachTarget();
			}
			this.root.removeChild(model);
		}
	}
}
