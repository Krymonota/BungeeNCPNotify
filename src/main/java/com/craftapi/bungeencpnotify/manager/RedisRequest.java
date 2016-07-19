/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.manager
 * @file: BungeeRequest.java
 * @author: Niklas (Krymonota)
 * @date: 27.10.2015
 */
package com.craftapi.bungeencpnotify.manager;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;
import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RedisRequest {

	/**
	 * Sends a Bungee Request using the Plugin Messaging System
	 *
	 * @param String The message that will be sent.
	 * @param String... Unused. Can be null.
	 */
	public static void sendRedisRequest(final String message, String... request) throws IOException {
		new BukkitRunnable() {
			@Override
			public void run() {
				Jedis jedis = BungeeNCPNotify.getJedisPool().getResource();
				try {
					jedis.publish("bncpnotify", message);
				} catch (Exception e) {
					BungeeNCPNotify.getInstance().getLogger().severe("Unable to send message to Redis server.");
					throw e;
				} finally {
					if (jedis != null) { jedis.close(); }
				}
			}
		}.runTaskAsynchronously(BungeeNCPNotify.getInstance());
	}

	/**
	 * Sends a Bungee Request using the Plugin Messaging System
	 *
     * {@link #sendRedisRequest(String, String...)}
	 * @param Player Unused. Can be null.
	 */
	public static void sendRedisRequest(Player player, String message, String... request) throws IOException { sendRedisRequest(message, request); }

}
