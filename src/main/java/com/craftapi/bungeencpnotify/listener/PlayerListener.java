/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.listener
 * @file: PlayerListener.java
 * @author: Niklas (Krymonota)
 * @date: 27.10.2015
 */
package com.craftapi.bungeencpnotify.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;
import com.craftapi.bungeencpnotify.manager.BungeeRequest;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Fetch the server's name from BungeeCord because Plugin Messaging Channels require a player who sends the message.
		if (BungeeNCPNotify.SERVER_NAME.isEmpty())
			BungeeRequest.sendBungeeRequest(event.getPlayer(), "GetServer");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (BungeeNCPNotify.getCooldownManager().hasCooldown(player.getUniqueId()))
			BungeeNCPNotify.getCooldownManager().removeCooldown(player.getUniqueId());
	}
	
}
