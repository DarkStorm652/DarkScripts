package org.darkscripts.events;

public abstract class Event {
	public String getName() {
		return getClass().getSimpleName();
	}
}
