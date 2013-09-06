/**
 * 
 */
package net.java.amateras.uml.dnd.cdt;

import net.java.amateras.uml.cdt.UMLCdtUtils;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.cdt.internal.core.model.Structure;
import org.eclipse.gef.requests.CreationFactory;


/**
 * 
 * @author Takahiro Shida
 * @author Naoki Takezoe
 * @author Evgeny Alexeyev
 */
@SuppressWarnings("restriction")
class ClassModelConverter implements CreationFactory {

	protected Object type;

	public ClassModelConverter(Object type) {
		this.type = type;
	}

	public Object getNewObject() {
			AbstractUMLEntityModel rv = null;
			if (type instanceof Structure) {
				rv = new InterfaceModel();
				((InterfaceModel) rv).setName(((Structure)type).getElementName());

				AttributeModel[] fields = UMLCdtUtils.getFields(type);
				for(int i=0;i<fields.length;i++){
					rv.addChild(fields[i]);
				}
				OperationModel[] methods = UMLCdtUtils.getMethods(type);
				for(int i=0;i<methods.length;i++){
					rv.addChild(methods[i]);
				}

			}
			
			
			return rv;

	}
	
	public Object getObjectType() {
		return InterfaceModel.class;
	}
/*		try {
			if (type.isInterface()) {

			} else {
				return ClassModel.class;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ClassModel.class;
	}
*/
}