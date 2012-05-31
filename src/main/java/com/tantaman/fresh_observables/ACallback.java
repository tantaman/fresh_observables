package com.tantaman.fresh_observables;

public abstract class ACallback implements Callback {
	public void call(Object model, Object[] args) {
		this.call(args);
	}
	
	public void call(Object [] args) {
		
	}
}
