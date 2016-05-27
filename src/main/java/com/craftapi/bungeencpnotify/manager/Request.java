package com.craftapi.bungeencpnotify.manager;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;
import org.bukkit.entity.Player;

import java.io.IOException;


/**
 * Created by tfrew on 5/26/16.
 */
public class Request {

    /**
     * {@link #sendRequest(String, String...)}
     */
    public static void sendRequest(String message, String... request) throws IOException {
        sendRequest(null, message, request);
    }

    /**
     * Sends a Bungee Request using the Plugin Messaging System
     *
     * @param Player The player who will send the plugin message.
     * @param String The message that will be sent.
     * @param String... The request of the plugin message.
     */
    public static void sendRequest(Player player, String message, String... request) throws IOException {
        if (BungeeNCPNotify.getConfiguration().getBoolean("redis.enable"))
            RedisRequest.sendRedisRequest(player, message, request);
        else
            BungeeRequest.sendBungeeRequest(player, message, request);

    }

}
