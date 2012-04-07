package org.darkscripts.events;

import java.lang.reflect.Method;
import java.util.*;

final class EventSender {
	private final Map<EventListener, List<Method>> handlers;
	private final Class<? extends Event> listenerEventClass;

	public EventSender(Class<? extends Event> listenerEventClass) {
		handlers = new HashMap<EventListener, List<Method>>();
		this.listenerEventClass = listenerEventClass;
	}

	public synchronized void addHandler(EventListener listener, Method method) {
		List<Method> methods = handlers.get(listener);
		if(methods == null) {
			methods = new ArrayList<Method>();
			handlers.put(listener, methods);
		}
		methods.add(method);
	}

	public synchronized void unregisterListener(EventListener listener) {
		handlers.remove(listener);
	}

	public synchronized List<EventListener> getListeners() {
		return new ArrayList<EventListener>(handlers.keySet());
	}

	public synchronized void sendEvent(Event event) {
		Class<?> eventClass = event.getClass();
		if(!eventClass.isAssignableFrom(listenerEventClass))
			return;
		for(EventListener listener : handlers.keySet()) {
			for(Method method : handlers.get(listener)) {
				try {
					method.invoke(listener, event);
				} catch(Throwable exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	public synchronized Class<? extends Event> getListenerEventClass() {
		return listenerEventClass;
	}
}
