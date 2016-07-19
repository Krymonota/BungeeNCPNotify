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

import com.craftapi.bungeencpnotify.listener.IncomingRedisMessageListener;
import com.craftapi.bungeencpnotify.util.BungeeGetServer;
import com.craftapi.bungeencpnotify.util.Metrics;
import lilypad.client.connect.api.Connect;
import net.gravitydevelopment.updater.Updater;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.craftapi.bungeencpnotify.hook.NotifyHook;
import com.craftapi.bungeencpnotify.listener.IncomingMessageListener;
import com.craftapi.bungeencpnotify.listener.PlayerListener;
import com.craftapi.bungeencpnotify.manager.BungeeRequest;
import com.craftapi.bungeencpnotify.manager.ConfigurationManager;
import com.craftapi.bungeencpnotify.manager.CooldownManager;
import com.google.gson.Gson;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import fr.neatmonster.nocheatplus.permissions.Permissions;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class BungeeNCPNotify extends JavaPlugin {

	private static BungeeNCPNotify instance;
	private static Gson gson;
	private static CooldownManager cooldownManager;
	private static ConfigurationManager configurationManager;
	public static String SERVER_NAME;
	private boolean isStaffMemberOnline = false;
	private final int BUKKITDEV_PROJECT_ID = 95815;
	private static JedisPool jedisPool;
	private static IncomingRedisMessageListener jedisListener;

	@Nonnull
	public static JedisPool getJedisPool() { return jedisPool; }

	@Nonnull
	public static void setJedisPool(JedisPool pool) { jedisPool = pool; }

	@Nonnull
	public static IncomingRedisMessageListener getJedisListener() { return jedisListener; }

	@Nonnull
	public static void setJedisListener(IncomingRedisMessageListener listener) { jedisListener = listener; }

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
	
	@Nonnull
	public boolean isStaffMemberOnline() {
		return this.isStaffMemberOnline;
	}

	@Override
	public void onEnable() {

		instance = this;

		// Config stuff
		this.saveDefaultConfig();
		configurationManager = new ConfigurationManager();

		// Other stuff
		cooldownManager = new CooldownManager();
		gson = new Gson();
		SERVER_NAME = "";


		// Fetch the server's name (only for reloads)
		if (getServer().getPluginManager().getPlugin("LilyPad-Connect") != null) {
			Connect connect = this.getServer().getServicesManager().getRegistration(Connect.class).getProvider();
			SERVER_NAME = connect.getSettings().getUsername();
		} else {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeGetServer());
			BungeeRequest.sendBungeeRequest("GetServer");
		}

		// If we are using redis then create the connection
		if (getConfiguration().getBoolean("redis.enable")) {
			if (getConfiguration().getString("redis.password") == null || getConfiguration().getString("redis.password").equals(""))
				setJedisPool(new JedisPool(new JedisPoolConfig(), getConfiguration().getString("redis.ip"), getConfiguration().getInt("redis.port"), 0));
			else
				setJedisPool(new JedisPool(new JedisPoolConfig(), getConfiguration().getString("redis.ip"), getConfiguration().getInt("redis.port"), 0, getConfiguration().getString("redis.password")));
		}

		// Register NCP Hook outgoing messages if it's enabled in config
		if (getConfiguration().getBoolean("general.enable-send")) {
			NCPHookManager.addHook(CheckType.values(), new NotifyHook());
		}

		// Register incoming message channel if it's enabled in config
		if (getConfiguration().getBoolean("general.enable-receive")) {
			if (getConfiguration().getBoolean("redis.enable")) {
				new BukkitRunnable() {
					@Override
					public void run() {
						setJedisListener(new IncomingRedisMessageListener());
						Jedis jedis = getJedisPool().getResource();
						try {
							jedis.subscribe(getJedisListener(), "bncpnotify");
						} catch (Exception e) {
							getLogger().severe("Unable to connect to Redis server.");
							throw e;
						} finally {
						    if (jedis != null) { jedis.close(); }
						}
					}
				}.runTaskAsynchronously(this);
			} else this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new IncomingMessageListener());
		}
		
		// Check if an online player is a staff member
		this.checkForStaffMembers();
		
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

		// Kill any async tasks that may be left over
		getServer().getScheduler().cancelTasks(instance);

		// Destroy jedis listener if redis was enabled
		if (getConfiguration().getBoolean("redis.enable")) {
			getJedisListener().unsubscribe();
			jedisListener = null;
			getJedisPool().destroy();
			jedisPool = null;
		}

		// Terminate variables
		SERVER_NAME = null;
		configurationManager = null;
		cooldownManager = null;
		gson = null;
		instance = null;
	}
	
	public void checkForStaffMembers() {
		for (Player onlineplayer : this.getServer().getOnlinePlayers()) {
			if (!onlineplayer.hasPermission(Permissions.NOTIFY))
				continue;
			
			isStaffMemberOnline = true;
			break;
		}
	}

}
