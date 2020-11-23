package com.bingoloves.plugin_core.skin.listener;

public interface ISkinLoader {
	void attach(ISkinUpdate observer);
	void detach(ISkinUpdate observer);
	void notifySkinUpdate();
//	void notifySkinDefault();
}
