package com.tantaman.fresh_observables.agent;

import java.lang.instrument.Instrumentation;

import com.tantaman.fresh_observables.transformers.ObservablesTransformer;

/*
 * mvn assembly:single
java -cp .\fresh_observables-1.0-SNAPSHOT-jar-with-dependencies.jar -javaagent:fresh_observables-1.0-SNAPSHOT-jar-with-dependencies.jar com.tantaman.fresh_observables.examples.BasicExample
 */
public class FreshObservablesAgent {
	public static void premain(String agentArguments, Instrumentation instrumentation) {	
		instrumentation.addTransformer(new ObservablesTransformer());
	}
}
