/**
 * 
 */
package net.java.amateras.uml.dnd.cdt;

import org.eclipse.cdt.internal.core.model.Structure;

import net.java.amateras.uml.model.TypeEntityModel;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;

/**
 * 
 * @author shida
 * @author Naoki Takezoe
 */
@SuppressWarnings("restriction")
public class InstanceModelConverter extends ClassModelConverter {

	private String name;
	
	public InstanceModelConverter(Object type) {
		super(type);
		if (type instanceof Structure) {
			Structure s = (Structure)type;
			this.name = s.getElementName();
		}
		
	}

	public Object getNewObject() {
		TypeEntityModel model = (TypeEntityModel) super.getNewObject();
		model.setName(this.name);
		InstanceModel instance = new InstanceModel();
		instance.setType(model);
		return instance;
	}
	
	public Object getObjectType() {
		return InstanceModel.class;
	}
}
