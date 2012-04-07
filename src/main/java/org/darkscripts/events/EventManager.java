package org.darkscripts.events;

import java.lang.reflect.Method;
import java.util.*;

public final class EventManager {
	private final List<EventSender> eventSenders;

	public EventManager() {
		eventSenders = new ArrayList<EventSender>();
	}

	public synchronized void registerListener(EventListener listener) {
		Class<? extends EventListener> listenerClass = listener.getClass();
		for(Method method : listenerClass.getMethods()) {
			if(method.getAnnotation(EventHandler.class) == null)
				continue;
			if(!method.isAccessible())
				throw new IllegalArgumentException("Method "
						+ method.toString() + " in class "
						+ listenerClass.getName() + " is not accessible.");
			if(method.getParameterTypes().length != 1)
				throw new IllegalArgumentException("Method "
						+ method.toString() + " in class "
						+ listenerClass.getName()
						+ " has incorrect amount of parameters");
			Class<? extends Event> eventClass = method.getParameterTypes()[0]
					.asSubclass(Event.class);
			boolean senderExists = false;
			for(EventSender sender : eventSenders) {
				if(eventClass.isAssignableFrom(sender.getListenerEventClass())) {
					sender.addHandler(listener, method);
					senderExists = true;
				}
			}
			if(!senderExists) {
				EventSender sender = new EventSender(eventClass);
				eventSenders.add(sender);
				sender.addHandler(listener, method);
			}
		}
	}

	public synchronized void unregisterListeners(
			Class<? extends Event> eventClass, EventListener listener) {
		for(EventSender sender : eventSenders) {
			if(eventClass.isAssignableFrom(sender.getListenerEventClass())) {
				sender.unregisterListener(listener);
				if(sender.getListeners().size() == 0)
					eventSenders.remove(sender);
				break;
			}
		}
	}

	public synchronized void clearListeners() {
		eventSenders.clear();
	}

	public synchronized void sendEvent(Event event) {
		for(EventSender sender : eventSenders) {
			Class<? extends Event> eventClass = sender.getListenerEventClass();
			if(eventClass.isInstance(event))
				sender.sendEvent(event);
		}
	}

	public synchronized List<EventListener> getListeners(
			Class<? extends Event> eventClass) {
		for(EventSender sender : eventSenders)
			if(eventClass.isAssignableFrom(sender.getListenerEventClass()))
				return sender.getListeners();
		return new ArrayList<EventListener>();
	}
}
