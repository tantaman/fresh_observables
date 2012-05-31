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
