package com.tantaman.fresh_observables;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tantaman.fresh_observables.annotations.Observable;

public class ObservableTest {

	private class Container<T> {
		public T item;
		
		public Container(T val) {
			item = val;
		}
	}
	
	@Test
	public void testOnChange() {
		Instrumented i = new Instrumented();
		
		final Container<Integer> count = new Container<Integer>(0);
		i.on("change", new Callback() {
			public void call(Object model, Object[] args) {
				++count.item;
			}
		});
		
		i.setLength(2);
		assertEquals(1, count.item);
		
		i.setLength(2);
		assertEquals(1, count.item);
		
		i.setStrength(2.2f);
		assertEquals(2, count.item);
		
		i.setLength(22);
		assertEquals(3, count.item);
		
		i.setTaters("wer");
		assertEquals(4, count.item);
	}
	
	@Test
	public void testOnChangeSpecific() {
		Instrumented i = new Instrumented();
		
		final Container<Integer> count = new Container<Integer>(0);
		i.on("change:setLength", new Callback() {
			public void call(Object model, Object[] args) {
				++count.item;
			}
		});
		
		i.setLength(0);
		assertEquals(1, count.item);
		
		i.setLength(0);
		assertEquals(1, count.item);
		
		i.setLength(1);
		assertEquals(2, count.item);
		
		i.setLength(2);
		assertEquals(3, count.item);
		
		final Container<Integer> strCount = new Container<Integer>(0);
		i.on("change:setStrength", new Callback() {
			public void call(Object model, Object[] args) {
				++strCount.item;
			}
		});
		
		i.setStrength(2);
		assertEquals(1, strCount.item);
		assertEquals(3, count.item);
		
		i.setLength(123);
		assertEquals(1, strCount.item);
		assertEquals(4, count.item);
	}
	
	@Test
	public void testOnCustom() {
		
	}

	@Test
	public void testOffString() {
		Instrumented i = new Instrumented();
		
		final Container<Integer> c = new Container<Integer>(0);
		i.on("change:setTaters", new Callback() {
			public void call(Object model, Object[] args) {
				c.item +=1 ;
			}
		});
		
		i.setTaters("t");
		assertEquals(1, c.item);
		
		i.off("change:setTaters");
		i.setTaters("b");
		assertEquals(1, c.item);
		
		i.on("change", new Callback() {
			public void call(Object model, Object[] args) {
				c.item += 1;
			}
		});
		
		i.off("change");
		i.setTaters("sdc");
		assertEquals(1, c.item);
	}

	@Test
	public void testOffStringCallback() {
		Instrumented i = new Instrumented();
		
		final Container<Integer> container = new Container<Integer>(0);
		Callback globCB = new Callback() {
			public void call(Object model, Object[] args) {
				container.item += 1;
			}
		};
		
		Callback specCB = new Callback() {
			public void call(Object model, Object[] args) {
				container.item += 1;
			}
		};
		
		i.on("change", globCB);
		i.on("change:setLength", specCB);
		
		i.off("change", globCB);
		i.off("change:setLength", specCB);
		
		i.setLength(12344);
		assertEquals(0, container.item);
	}

	private static class Instrumented extends com.tantaman.fresh_observables.Observable {
		public void emitSomething() {
			
		}
		
		@Observable
		public void setLength(int length) {
			
		}
		
		@Observable
		public void setStrength(float strength) {
			
		}
		
		@Observable
		public void setTaters(String taters) {
			
		}
	}
}
