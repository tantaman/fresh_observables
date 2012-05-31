package com.tantaman.fresh_observables.transformers;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import com.tantaman.fresh_observables.ObserverList;
import com.tantaman.fresh_observables.annotations.Observable;

public class ObservablesTransformer implements ClassFileTransformer {
	private ClassPool classPool = ClassPool.getDefault();
	
	public static String determineClassName(String pAnnotationAsString) {
		int endIndex = pAnnotationAsString.indexOf("(");
		if (endIndex < 0) {
			endIndex = pAnnotationAsString.length();
		}
		
		return pAnnotationAsString.substring(1, endIndex);
	}
	
	public byte[] transform(ClassLoader loader, String className, Class<?> clazz,
			ProtectionDomain protectionDomain, byte[] classfileBuffer)
			throws IllegalClassFormatException {
		CtClass cl = null;
		byte [] result = null;
		
		try {
			// TODO: is this quick enough or is it quicker to do "new ClassFile" and check its inheritance tree there?
			cl = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
			
			if (shouldInstrument(cl)) {
				boolean instrumented = instrument(cl);
				if (instrumented)
					result = cl.toBytecode();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if (result != null) {
			return result;
		} else {
			return classfileBuffer;
		}
	}
	
	private boolean shouldInstrument(CtClass cl) {
		try {
			if (cl.getSuperclass().getName().equals(com.tantaman.fresh_observables.Observable.class.getName()))
				return true;
		} catch (NotFoundException e) {
			return false;
		}
		return false;
	}
	
	// TODO: clean up our exception handling
	private boolean instrument(CtClass cl) throws Exception {
		boolean result = false;
		for (CtMethod method : cl.getMethods()) {
			result = (processMethod(method, cl, result)) || result;
		}
		return result;
	}
	
	private boolean processMethod(CtMethod method, CtClass cl, boolean previouslyInstrumented) throws Exception {
		boolean instrumented = false;
		try {
			for (Object annotation : method.getAnnotations()) {
				// TODO: quicker way of doing this?
				// The problem is that annotations turn up as proxy classes...
				String annotationAsString =
						(String)(annotation.getClass().getMethod("toString").invoke(annotation));
				String annotationName = determineClassName(annotationAsString);
				
				if (annotationName.equals(Observable.class.getName())) {
					makeObservable(method, cl, previouslyInstrumented);
					instrumented = true;
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			return instrumented;
		}
		
		return instrumented;
	}
	
	private void makeObservable(CtMethod method, CtClass cl, boolean previouslyInstrumented) throws Exception {
		int previousModifiers = method.getModifiers();
		String previousName = method.getName();
		method.setModifiers(Modifier.PRIVATE);
		String newName = "__FO__" + method.getName();
		method.setName(newName);
		
		String observersMember = newName + "Observers";
		
		/*if (!previouslyInstrumented) {
			prepareClassForObservation(cl);
		}*/
		
		try {
			// TODO: how do we want to handle overloaded observable methods...?
			cl.getDeclaredField(observersMember);
		} catch (NotFoundException e) {
			CtField field = CtField.make("private final " + ObserverList.class.getName() + " " + observersMember + " = new " + ObserverList.class.getName() + "();", cl);
			cl.addField(field);
		}
		
		StringBuilder newMethodBodySrc = new StringBuilder();
		newMethodBodySrc.append("{Object result = ($r)" + newName + "($$);").append("super.emit(" + observersMember + ", $args);").append("return ($r)result;}");
		
		CtMethod wrapperMethod = CtNewMethod.make(
				previousModifiers,
				method.getReturnType(),
				previousName,
				method.getParameterTypes(),
				method.getExceptionTypes(),
				newMethodBodySrc.toString(),
				cl
				);
		
		cl.addMethod(wrapperMethod);
	}
	
	private void prepareClassForObservation(CtClass cl) throws CannotCompileException {
		//CtField observableSupport = CtField.make("private final " + ObservableSupport.class.getName() + " " + "__FOObservableSupport = new " + ObservableSupport.class.getName() + "();", cl);
		//cl.addField(observableSupport);
	}
}
