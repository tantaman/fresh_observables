package com.tantaman.fresh_observables.transformers;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ObservablesTransformer implements ClassFileTransformer {
	private ClassPool classPool = ClassPool.getDefault();
	
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
			for (CtClass iface : cl.getInterfaces()) {
				if (iface.getName().equals("com.tantaman.fresh_observables.Observable"))
					return true;
			}
		} catch (NotFoundException e) {
			return false;
		}
		return false;
	}
	
	private boolean instrument(CtClass cl) {
		boolean result = false;
		for (CtMethod method : cl.getMethods()) {
			result = result || (processMethod(method));
		}
		return result;
	}
	
	private boolean processMethod(CtMethod method) {
		try {
			for (Object annotation : method.getAnnotations()) {
				/*String annotationAsString =
						(String)(annotation.getClass().getMethod("toString").invoke(annotation));
					String annotationName = WrappedAnnotation.determineClassName(annotationAsString);*/
				/*
				 * Or . . . 
				 */
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
		
		return false;
	}
}
