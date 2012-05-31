package com.tantaman.fresh_observables;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ObserverList {
	private final Set<Callback> observers;
	
	public ObserverList() {
		observers = new CopyOnWriteArraySet<Callback>();
	}
	
	public void add(Callback o) {
		observers.add(o);
	}
	
	public void remove(Callback o) {
		observers.remove(o);
	}
	
	public void clear() {
		observers.clear();
	}
	
	public Collection<Callback> getObservers() {
		return observers;
	}
}
