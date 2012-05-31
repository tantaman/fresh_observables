package com.tantaman.fresh_observables;

import java.lang.reflect.Field;

public class Observable {
	private final ObserverList globalObservers;
	private final int softMaxPerTopic;
	
	public Observable() {
		globalObservers = new ObserverList();
		softMaxPerTopic = 10;
	}
	
	public Observable(int softMaxObservers) {
		globalObservers = new ObserverList();
		softMaxPerTopic = softMaxObservers;
	}
	
	// TODO: this is the naive implementation.  Obviously we need to do some checking
	// on current and previous values to see if we should emit.
	protected void emit(ObserverList observers, Object [] args) {
		for (Callback cb : globalObservers.getObservers()) {
			cb.call(args);
		}
		
		for (Callback cb : observers.getObservers()) {
			cb.call(args);
		}
	}
	
	public Subscription on(String topic, Callback cb) {
		String [] parts = topic.split(":");
		switch (parts.length) {
		case 1:
			if (parts[0].equals("change"))
				subscribeGlobal(cb);
			break;
		case 2:
			if (parts[0].equals("change")) {
				subscribeLocal(parts[1], cb);
			}
			break;
		default:
			break;
		}
		
		return null;
	}
	
	private void subscribeGlobal(Callback cb) {
		globalObservers.add(cb);
	}
	
	private void subscribeLocal(String name, Callback cb) {
		try {
			Field f = this.getClass().getDeclaredField("__FO__" + name + "Observers");
			f.setAccessible(true);
			ObserverList obs = (ObserverList)f.get(this);
			
			obs.add(cb);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
