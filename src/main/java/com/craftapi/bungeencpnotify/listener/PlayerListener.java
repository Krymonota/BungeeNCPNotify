/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.listener
 * @file: PlayerListener.java
 * @author: Niklas (Krymonota)
 * @date: 27.10.2015
 */
package com.craftapi.bungeencpnotify.listener;

import com.craftapi.bungeencpnotify.manager.Request;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;
import com.craftapi.bungeencpnotify.manager.BungeeRequest;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Fetch the server's name from BungeeCord because Plugin Messaging Channels require a player who sends the message.
		// It seems like it doesn't work without a runnable.
		new BukkitRunnable() {
			@Override
			public void run() {
				if (BungeeNCPNotify.SERVER_NAME.isEmpty() &&
						BungeeNCPNotify.getInstance().getServer().getPluginManager().getPlugin("LilyPad-Connect") == null)
					BungeeRequest.sendBungeeRequest(event.getPlayer(), "GetServer");
			}
		}.runTaskLater(BungeeNCPNotify.getInstance(), 5L);
		
		BungeeNCPNotify.getInstance().checkForStaffMembers();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (BungeeNCPNotify.getCooldownManager().hasCooldown(player.getUniqueId()))
			BungeeNCPNotify.getCooldownManager().removeCooldown(player.getUniqueId());
		
		BungeeNCPNotify.getInstance().checkForStaffMembers();
	}
	
}
