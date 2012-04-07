package org.darkscripts;

import java.awt.Graphics;

import org.darkscripts.events.*;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.bot.event.MessageEvent;
import org.powerbot.game.bot.event.listener.*;

public abstract class DarkScript extends ActiveScript implements EventListener,
		MessageListener, PaintListener {
	protected final EventManager eventManager;

	public DarkScript() {
		eventManager = new EventManager();
	}

	@Override
	protected abstract void setup();

	protected abstract void destroy();

	@Override
	public final void onStop() {
		eventManager.clearListeners();
		destroy();
	}

	@Override
	public void onRepaint(Graphics graphics) {
	}

	@Override
	public void messageReceived(MessageEvent messageEvent) {
	}
}
