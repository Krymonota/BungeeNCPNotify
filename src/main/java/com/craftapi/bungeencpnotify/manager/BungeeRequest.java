/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.manager
 * @file: BungeeRequest.java
 * @author: Niklas (Krymonota)
 * @date: 27.10.2015
 */
package com.craftapi.bungeencpnotify.manager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeRequest {

	/**
	 * {@link #sendBungeeRequest(Player, String)}
	 */
	public static void sendBungeeRequest(String request) {
		sendBungeeRequest(null, request);
	}

	/**
	 * Sends a Bungee Request using the Plugin Messaging System
	 * 
	 * @param Player The player who will send the plugin message.
	 * @param String The request of the plugin message.
	 */
	public static void sendBungeeRequest(Player player, String request) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		out.writeUTF(request);

		if (player == null)
			player = getRandomPlayer();

		if (player != null)
			player.sendPluginMessage(BungeeNCPNotify.getInstance(), "BungeeCord", out.toByteArray());
	}

	/**
	 * {@link #sendBungeeRequest(String, String...)}
	 */
	public static void sendBungeeRequest(String message, String... request) throws IOException {
		sendBungeeRequest(null, message, request);
	}

	/**
	 * Sends a Bungee Request using the Plugin Messaging System
	 * 
	 * @param Player The player who will send the plugin message.
	 * @param String The message that will be sent.
	 * @param String... The request of the plugin message.
	 */
	public static void sendBungeeRequest(Player player, String message, String... request) throws IOException {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		Arrays.asList(request).forEach(req -> out.writeUTF(req));

		ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
		DataOutputStream msgOut = new DataOutputStream(msgBytes);

		msgOut.writeUTF(message);

		out.writeShort(msgBytes.toByteArray().length);
		out.write(msgBytes.toByteArray());

		if (player == null)
			player = getRandomPlayer();

		if (player != null)
			player.sendPluginMessage(BungeeNCPNotify.getInstance(), "BungeeCord", out.toByteArray());
	}

	/**
	 * @return A random Player (/ the first player in the Player Collection)
	 */
	private static Player getRandomPlayer() {
		if (Bukkit.getServer().getOnlinePlayers().size() > 0)
			return Bukkit.getServer().getOnlinePlayers().iterator().next();
		else
			return null;
	}

}
