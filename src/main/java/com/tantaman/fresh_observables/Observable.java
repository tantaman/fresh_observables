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
			cb.call(this, args);
		}
		
		for (Callback cb : observers.getObservers()) {
			cb.call(this, args);
		}
	}
	
	public Subscription on(String topic, Callback cb) {
		String [] parts = topic.split(":");
		ObserverList list = null;
		switch (parts.length) {
		case 1:
			if (parts[0].equals("change"))
				list = subscribeGlobal(cb);
			break;
		case 2:
			if (parts[0].equals("change")) {
				list = subscribeLocal(parts[1], cb);
			}
			break;
		default:
			break;
		}
		
		return new SubscriptionImpl(list, cb);
	}
	
	public void off(String topic) {
		ObserverList list = getObserverListByTopic(topic);
		
		if (list != null) {
			list.clear();
		}
	}
	
	private ObserverList getObserverListByTopic(String topic) {
		String [] parts = topic.split(":");
		switch (parts.length) {
		case 1:
			if (parts[0].equals("change"))
				return globalObservers;
		case 2:
			if (parts[0].equals("change")) {
				ObserverList list = getObserverList(parts[1]);
				return list;
			}
		}
		
		return null;
	}
	
	public void off(String topic, Callback cb) {
		ObserverList list = getObserverListByTopic(topic);
		
		if (list != null)
			list.remove(cb);
	}
	
	/*public void off(Object registrant) {
		
	}*/
	
	private ObserverList subscribeGlobal(Callback cb) {
		globalObservers.add(cb);
		return globalObservers;
	}
	
	private ObserverList subscribeLocal(String name, Callback cb) {
		ObserverList obs = getObserverList(name);
		if (obs != null)
			obs.add(cb);

		return obs;
	}
	
	private ObserverList getObserverList(String name) {
		Field f;
		try {
			f = this.getClass().getDeclaredField("__FO__" + name + "Observers");
			f.setAccessible(true);
			return (ObserverList)f.get(this);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private class SubscriptionImpl implements Subscription {
		private final ObserverList list;
		private final Callback cb;
		public SubscriptionImpl(ObserverList list, Callback cb) {
			this.list = list;
			this.cb = cb;
		}
		
		public void dispose() {
			if (this.list != null)
				this.list.remove(cb);
		}
	}
}
