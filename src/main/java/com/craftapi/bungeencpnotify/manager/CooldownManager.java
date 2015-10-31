/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.manager
 * @file: CooldownManager.java
 * @author: Niklas (Krymonota)
 * @date: 26.10.2015
 */
package com.craftapi.bungeencpnotify.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.craftapi.bungeencpnotify.util.Duration;
import com.craftapi.bungeencpnotify.util.Expiration;

public class CooldownManager {

	private final Map<UUID, Expiration> cooldown = new HashMap<UUID, Expiration>();

	/**
	 * @param UUID to check
	 * @return True if the Cooldown-Map contains the UUID
	 */
	public boolean hasCooldown(UUID uuid) {
		return this.cooldown.containsKey(uuid);
	}
	
	/**
	 * @param UUID to check
	 * @return True if the cooldown has expired
	 */
	public boolean isExpired(UUID uuid) {
		return this.cooldown.get(uuid).isExpired();
	}
	
	/**
	 * Sets the Expiration of a cooldown
	 * 
	 * @param UUID to insert
	 * @param Duration How long the cooldown should take effect
	 */
	public void setExpiration(UUID uuid, Duration duration) {
		this.cooldown.put(uuid, new Expiration().expireIn(duration));
	}
	
	/**
	 * Expire a cooldown now
	 * 
	 * @param UUID of which's cooldown should expire
	 */
	public void expireNow(UUID uuid) {
		this.cooldown.get(uuid).expireNow();
	}
	
	/**
	 * Remove a UUID from the Cooldown-Map
	 * 
	 * @param UUID to remove
	 */
	public void removeCooldown(UUID uuid) {
		this.cooldown.remove(uuid);
	}
	
}
