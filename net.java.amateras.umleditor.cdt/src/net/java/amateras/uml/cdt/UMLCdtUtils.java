package net.java.amateras.uml.cdt;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.classdiagram.model.Argument;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;

import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IMethodDeclaration;
import org.eclipse.cdt.core.parser.ast.ASTAccessVisibility;
import org.eclipse.cdt.internal.core.model.Structure;

/**
 * This class provides utility methods for the AmaterasUML-Java Plug-In.
 * 
 * @author Naoki Takezoe
 */
@SuppressWarnings("restriction")
public class UMLCdtUtils {
	
	/**
	 * Get classname from {@link ClassModel} or {@link InterfaceModel}.
	 * 
	 * @param model ClassModel or InterfaceModel
	 * @return classname
	 */
	public static String getClassName(Object model){
		if(model instanceof ClassModel){
			return ((ClassModel)model).getName();
		} else if(model instanceof InterfaceModel){
			return ((InterfaceModel)model).getName();
		}
		
		return null;
	}
	
	/**
	 * This method judges whether the type is a primitive type. 
	 * 
	 * @param type type (classname or primitive type)
	 * @return 
	 * <ul>
	 *   <li>true - primitive type</li>
	 *   <li>false - not primitive type</li>
	 * </ul>
	 */
	public static boolean isPrimitive(String type){
		if(type.equals("int") || type.equals("long") || type.equals("double") || type.equals("float") || 
				type.equals("char") || type.equals("boolean") || type.equals("byte")){
			return true;
		}
		return false;
	}

	/**
	 * Creates a qualified class name from a class name which doesn't contain package name.
	 * 
	 * @param parent a full qualified class name of the class which uses this variable
	 * @param type a class name which doesn't contain package name
	 * @return full a created qualified class name
	 */
	public static String getFullQName(Object parent,String type){
		
		type = stripGenerics(type);
		
		if(type.indexOf('.') >= 0){
			return type;
		}
		if(isPrimitive(type)){
			return type;
		}
/*		
		IJavaProject project = parent.getJavaProject();
		try {
			IType javaType = project.findType("java.lang." + type);
			if(javaType!=null && javaType.exists()){
				return javaType.getFullyQualifiedName();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			IType javaType = project.findType(parent.getPackageFragment().getElementName() + "." + type);
			if(javaType!=null && javaType.exists()){
				return javaType.getFullyQualifiedName();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			IImportDeclaration[] imports = parent.getCompilationUnit().getImports();
			for(int i=0;i<imports.length;i++){
				String importName = imports[i].getElementName();
				if(importName.endsWith("." + type)){
					return importName;
				}
				if(importName.endsWith(".*")){
					try {
						IType javaType = project.findType(importName.replaceFirst("\\*$",type));
						if(javaType!=null && javaType.exists()){
							return javaType.getFullyQualifiedName();
						}
					} catch(Exception ex){
					}
				}
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}*/
		return type;
	}
	
	public static AttributeModel[] getFields(Object type){
		
		if (type instanceof Structure) {
			try {
				Structure s = (Structure)type;
				org.eclipse.cdt.core.model.IField[] fields = s.getFields();
				AttributeModel[] result = new AttributeModel[fields.length];
				for(int i=0;i<fields.length;i++){
					AttributeModel attr = new AttributeModel();
					attr.setName(fields[i].getElementName());
					attr.setType(fields[i].getTypeName());
					attr.setStatic(fields[i].isStatic());
					
					if(fields[i].getVisibility() == ASTAccessVisibility.PUBLIC){
						attr.setVisibility(Visibility.PUBLIC);
					} else if(fields[i].getVisibility() == ASTAccessVisibility.PRIVATE){
						attr.setVisibility(Visibility.PRIVATE);
					} else if(fields[i].getVisibility() == ASTAccessVisibility.PROTECTED){
						attr.setVisibility(Visibility.PROTECTED);
					} else {
						attr.setVisibility(Visibility.PACKAGE);
					}
					result[i] = attr;
				}
				return result;
			} catch (CModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new AttributeModel[0];
	}

	public static OperationModel[] getMethods(Object type){
		if (type instanceof Structure) {
			try {
				Structure s = (Structure)type;
				IMethodDeclaration[] methods = s.getMethods();
				OperationModel[] result = new OperationModel[methods.length];
				for(int i=0;i<methods.length;i++){
					OperationModel ope = new OperationModel();
					ope.setName(methods[i].getElementName());
					ope.setType(methods[i].getReturnType());
					if(methods[i].getVisibility() == ASTAccessVisibility.PUBLIC){
						ope.setVisibility(Visibility.PUBLIC);
					} else if(methods[i].getVisibility() == ASTAccessVisibility.PRIVATE){
						ope.setVisibility(Visibility.PRIVATE);
					} else if(methods[i].getVisibility() == ASTAccessVisibility.PROTECTED){
						ope.setVisibility(Visibility.PROTECTED);
					}
					
					ope.setAbstract(methods[i].isPureVirtual());
					ope.setStatic(methods[i].isStatic());
					List<Argument> params = new ArrayList<Argument>();
					String[] types = methods[i].getParameterTypes();
					for(int j=0;j<types.length;j++){
						Argument arg = new Argument();
						arg.setName(methods[i].getParameterInitializer(j));
						arg.setType(types[j]);
						params.add(arg);
					}
					ope.setParams(params);
					result[i] = ope;
				}
				return result;
				} catch (CModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new OperationModel[0];
	}
/*	
	public static void appendSuperClassConnection(RootModel root, IType type, 
			AbstractUMLEntityModel model) throws JavaModelException {
		
		if(type.getSuperclassName()==null){
			return;
		}
		
		String superClass = UMLJavaUtils.getFullQName(type, type.getSuperclassName());
		
		List<AbstractUMLModel> children = root.getChildren();
		for(int i=0;i<children.size();i++){
			Object obj = children.get(i);
			String className = stripGenerics(UMLJavaUtils.getClassName(obj));
			if(className!=null && className.equals(superClass)){
				GeneralizationModel conn = new GeneralizationModel();
				conn.setSource(model);
				conn.setTarget((AbstractUMLEntityModel)obj);
				conn.attachSource();
				conn.attachTarget();
				break;
			}
		}
	}
	
	public static void appendSubConnection(RootModel root, IJavaProject project, AbstractUMLEntityModel model){
		List<AbstractUMLModel>  children = root.getChildren();
		for(int i=0; i< children.size(); i++){
			AbstractUMLEntityModel child = (AbstractUMLEntityModel) children.get(i);
			if(child != model){
				if(child instanceof InterfaceModel){
					String name = ((InterfaceModel) child).getName();
					try {
						IType type = project.findType(name);
						if(type != null){
							appendInterfacesConnection(root, type, child);
						}
					} catch(JavaModelException ex){
					}
				}
				if(child instanceof ClassModel){
					String name = ((ClassModel) child).getName();
					try {
						IType type = project.findType(name);
						if(type != null){
							appendSuperClassConnection(root, type, child);
							appendInterfacesConnection(root, type, child);
							appendAggregationConnection(root, type, (ClassModel) child);
						}
					} catch(JavaModelException ex){
					}
				}
			}
		}
	}
	
	public static void appendInterfacesConnection(RootModel root, IType type, 
			AbstractUMLEntityModel model) throws JavaModelException {
		
		String[] interfaces = type.getSuperInterfaceNames();
		
		for(int i=0;i<interfaces.length;i++){
			String interfaceName = UMLJavaUtils.getFullQName(type, interfaces[i]);
			List<AbstractUMLModel>  children = root.getChildren();
			for(int j=0;j<children.size();j++){
				Object obj = children.get(j);
				if(obj instanceof InterfaceModel){
					String className = stripGenerics(((InterfaceModel)obj).getName());
					if(className != null && className.equals(interfaceName)){
						AbstractUMLConnectionModel conn = null;
						if(model instanceof ClassModel){
							conn = new RealizationModel();
						} else if(model instanceof InterfaceModel){
							conn = new GeneralizationModel();
						}
						conn.setSource(model);
						conn.setTarget((AbstractUMLEntityModel)obj);
						conn.attachSource();
						conn.attachTarget();
						break;
					}
				}
			}
		}
	}
	
	public static void appendAggregationConnection(RootModel root, IType type, 
			ClassModel model) throws JavaModelException {
		List<AbstractUMLModel>  children = model.getChildren();
		for(AbstractUMLModel obj: children){
			if(obj instanceof AttributeModel){
				AttributeModel attr = (AttributeModel) obj;
				String attrType = attr.getType();
				if(attrType.startsWith("List") || attrType.startsWith("java.util.List")){
					int fromIndex = attrType.indexOf('<');
					int endIndex = attrType.indexOf('>');
					if(fromIndex >= 0 && endIndex >= 0){
						attrType = attrType.substring(fromIndex + 1, endIndex);
						//System.out.println(attrType);
					}
				}
				attrType = attrType.replaceAll("<.*>", "");
				attrType = attrType.replaceAll("\\[\\]", "");
				attrType = getFullQName(type, attrType);
				
				List<AbstractUMLModel> entities = root.getChildren();
				for(AbstractUMLModel entity: entities){
					if(entity instanceof ClassModel){
						if(stripGenerics(((ClassModel) entity).getName()).equals(attrType)){
							AbstractUMLConnectionModel conn = new AggregationModel();
							conn.setSource((ClassModel) entity);
							conn.setTarget(model);
							conn.attachSource();
							conn.attachTarget();
							break;
						}
					} else if(entity instanceof InterfaceModel){
						if(stripGenerics(((InterfaceModel) entity).getName()).equals(attrType)){
							AbstractUMLConnectionModel conn = new AggregationModel();
							conn.setSource((InterfaceModel) entity);
							conn.setTarget(model);
							conn.attachSource();
							conn.attachTarget();
							break;
						}
					}
				}
			}
		}
	}
*/
	public static String stripGenerics(String className){
		if(className != null){
			className = className.replaceAll("<.+?>", "");
		}
		return className;
	}

	/**
	 * This method extract all acceptable instances from selected element.
	 * which could be class, insterface, enumeration, struct etc 
	 * 
	 * @param ICElement CDT primitive
	 * @return 
	 * <ul>
	 *   <li>arrary - the list of extracted primitives</li>
	 *   <li>emty array - no elements found</li>
	 * </ul>
	 */
	public static ICElement[] getTypes(ICElement element){
			List<ICElement> list = new ArrayList<ICElement>();
			
/*			if(element instanceof ICompilationUnit){
				IType[] types = ((ICompilationUnit)element).getTypes();
				for(int i=0; i< types.length; i++){
					list.add(types[i]);
					extractTypes(list, types[i]);
				}
				
			} else if(element instanceof IClassFile){
				IType type = ((IClassFile) element).getType();
				list.add(type);
				extractTypes(list, type);
				
			} else */
			if(element instanceof Structure){
				ICElement type = (ICElement) element;
				list.add(type);
			}
			
			return list.toArray(new ICElement[list.size()]);
	}
	/*
	private static void extractTypes(List<IType> list, IType type){
		try {
			IType[] types = type.getTypes();
			for(int i=0;i<types.length;i++){
				if(!list.contains(types[i])){
					list.add(types[i]);
				}
			}
		} catch(JavaModelException ex){
		}
	}
	*/
}
