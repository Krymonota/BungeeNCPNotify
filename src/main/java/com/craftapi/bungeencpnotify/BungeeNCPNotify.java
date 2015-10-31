/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify
 * @file: BungeeNCPNotify.java
 * @author: Niklas (Krymonota)
 * @date: 26.10.2015
 */
package com.craftapi.bungeencpnotify;

import java.io.IOException;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.craftapi.bungeencpnotify.hook.NotifyHook;
import com.craftapi.bungeencpnotify.listener.IncomingMessageListener;
import com.craftapi.bungeencpnotify.listener.PlayerListener;
import com.craftapi.bungeencpnotify.manager.BungeeRequest;
import com.craftapi.bungeencpnotify.manager.ConfigurationManager;
import com.craftapi.bungeencpnotify.manager.CooldownManager;
import com.google.gson.Gson;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;

public class BungeeNCPNotify extends JavaPlugin {

	private static BungeeNCPNotify instance;
	private static Gson gson = new Gson();
	private static CooldownManager cooldownManager = new CooldownManager();
	private static ConfigurationManager configurationManager;
	public static String SERVER_NAME = "";
	private final int BUKKITDEV_PROJECT_ID = 95815;

	@Nonnull
	public static BungeeNCPNotify getInstance() {
		return instance;
	}

	@Nonnull
	public static Gson getGson() {
		return gson;
	}

	@Nonnull
	public static CooldownManager getCooldownManager() {
		return cooldownManager;
	}

	@Nonnull
	public static ConfigurationManager getConfiguration() {
		return configurationManager;
	}

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override
	public void onEnable() {
		// Config stuff
		this.saveDefaultConfig();
		configurationManager = new ConfigurationManager();

		// Register NCP Hook and outgoing plugin channel if it's enabled in
		// config
		if (getConfiguration().getBoolean("general.enable-send")) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			NCPHookManager.addHook(CheckType.values(), new NotifyHook());
		}

		// Register incoming plugin channel if it's enabled in config
		if (getConfiguration().getBoolean("general.enable-receive"))
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new IncomingMessageListener());

		// Fetch the server's name (only for reloads)
		BungeeRequest.sendBungeeRequest("GetServer");
		
		// Register listeners
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

		// Start Plugin Metrics (mcstats.org) if it's enabled in config
		if (getConfiguration().getBoolean("general.enable-metrics")) {
			try {
				new Metrics(this).start();
			} catch (IOException e) {
				this.getServer().getLogger().log(Level.SEVERE, "Couldn't submit stats to MCStats.org!", e);
			}
		}

		// Start Updater if it's enabled in config
		if (getConfiguration().getBoolean("general.enable-updater"))
			new Updater(this, BUKKITDEV_PROJECT_ID, this.getFile(), Updater.UpdateType.DEFAULT, true);
	}

	@Override
	public void onDisable() {
		// Unregister the NCP Hook if it's registered
		if (NCPHookManager.getHooksByName(this.getName()) != null)
			NCPHookManager.removeHooks(this.getName());

		// Unregister outgoing plugin channel if it's registered
		if (this.getServer().getMessenger().isOutgoingChannelRegistered(this, "BungeeCord"))
			this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");

		// Unregister incoming plugin channel if it's registered
		if (this.getServer().getMessenger().isIncomingChannelRegistered(this, "BungeeCord"))
			this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");

		SERVER_NAME = null;
		configurationManager = null;
		cooldownManager = null;
		gson = null;
		instance = null;
	}

}
