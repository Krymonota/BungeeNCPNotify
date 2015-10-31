/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.manager
 * @file: ConfigurationManager.java
 * @author: Niklas (Krymonota)
 * @date: 31.10.2015
 */
package com.craftapi.bungeencpnotify.manager;

import java.util.HashMap;
import java.util.Map;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;

public class ConfigurationManager {
	/* NoCheatPlus hooks will be called off primary thread, for all checks that run off the primary thread (chat, certain packet level checks).
	 * The Bukkit API isn't thread-safe, so all keys with their corresponding values of the config are saved in the map below.
	 */

	private final Map<String, Object> cachedConfiguration = new HashMap<String, Object>(BungeeNCPNotify.getInstance().getConfig().getValues(true));
	
	/**
	 * @param String The key (path) of the value
	 * @return Object The value of the key
	 */
	public Object getObject(String key) {
		return this.cachedConfiguration.get(key);
	}
	
	/**
	 * {@link #getObject(String)}
	 */
	public String getString(String key) {
		return (String) this.getObject(key);
	}
	
	/**
	 * {@link #getObject(String)}
	 */
	public int getInt(String key) {
		return (int) this.getObject(key);
	}
	
	/**
	 * {@link #getObject(String)}
	 */
	public boolean getBoolean(String key) {
		return (boolean) this.getObject(key);
	}
	
}
