Fresh Observables
====

Its a breath of fresh air, its Fresh Observables!

Fresh Observables brings the best in Javascript event handling (think: node.js EventEmitter and Backbone.js models) to Java.

Want to observe calls to a setter?  Check this out:

```java
@Observable
public void setSize(Dimension size) {
	this.size = size;
}
```

Thats right.  Just tag whatever method you want to observe with @Observable.

Want to register for notifications when someone calls that setter?

```java
someObject.on("change:setSize", new Callback() {
	public void call(Object[] args) {
		System.out.println("Size was set to: ");
		System.out.println(args[0]);
	}
});
```

It couldn't get any easier.  

###TODO###
 * Templated callbacks and registration.
 * Don't emit change events if new value == old value
 * Allow registration & de-registration via a registration context

###Usage###

Fresh Observables modifies the bytecode of your classes at run time.  In order to do this, you'll have to tell the JVM to load in Fresh Observables when it starts up.  
The easiest way to do that is through the `-javaagent` command line parameter.

`java -javaagent:fresh_observables-1.0-SNAPSHOT-jar-with-dependencies.jar rest_of_your_startup_parameters`

###Building###

Fresh Observables is built using maven.  To create a jar with all required dependencies:

`mvn assembly:single`


###Full Example###

```java
package com.tantaman.fresh_observables.examples;

import java.awt.Dimension;

import com.tantaman.fresh_observables.Callback;
import com.tantaman.fresh_observables.annotations.Observable;

public class BasicExample {
	public static void main(String[] args) {
		SomeClass c = new SomeClass();
		
		c.on("change:setSize", new Callback() {
			public void call(Object[] args) {
				System.out.println("Size was set to: ");
				System.out.println(args[0]);
			}
		});
		
		c.on("change:setWeight", new Callback() {
			public void call(Object[] args) {
				System.out.println("Weight was set to: ");
				System.out.println(args[0]);
			}
		});
		
		c.on("change", new Callback() {
			public void call(Object[] args) {
				System.out.println("Observing all changes");
				System.out.println(args[0]);
			}
		});
		
		c.setSize(new Dimension(100,100));
		c.setWeight(245);
	}
	
	private static class SomeClass extends com.tantaman.fresh_observables.Observable {
		private Dimension size;
		private int weight;
		
		@Observable
		public void setSize(Dimension size) {
			this.size = size;
		}
		
		@Observable
		public void setWeight(int weight) {
			this.weight = weight;
		}
	}
}

```
