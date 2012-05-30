package com.tantaman.fresh_observables.agent;

import java.lang.instrument.Instrumentation;

import com.tantaman.fresh_observables.transformers.ObservablesTransformer;

/*
 * java -javaagent:fresh_observables-1.0-SNAPSHOT.jar -cp ..\..\..\workspace-ecl\EATs\bin com.tantaman.eats.demo.gui.DemoApp
 */
public class FreshObservablesAgent {
	public static void premain(String agentArguments, Instrumentation instrumentation) {	
		instrumentation.addTransformer(new ObservablesTransformer());
	}
}
