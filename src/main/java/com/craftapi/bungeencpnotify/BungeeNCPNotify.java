/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify
 * @file: BungeeNCPNotify.java
 * @author: Niklas (Krymonota)
 * @date: 26.10.2015
 */
package com.craftapi.bungeencpnotify;

import javax.annotation.Nonnull;

import org.bukkit.plugin.java.JavaPlugin;

import com.craftapi.bungeencpnotify.hook.NotifyHook;
import com.craftapi.bungeencpnotify.listener.IncomingMessageListener;
import com.craftapi.bungeencpnotify.listener.PlayerListener;
import com.craftapi.bungeencpnotify.manager.BungeeRequest;
import com.craftapi.bungeencpnotify.manager.CooldownManager;
import com.google.gson.Gson;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;

public class BungeeNCPNotify extends JavaPlugin {

	private static BungeeNCPNotify instance;
	private static Gson gson = new Gson();
	private static CooldownManager cooldownManager = new CooldownManager();
	public static String SERVER_NAME = "";
	public static String SUBCHANNEL = "BungeeNCPNotify";
	
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
	
	@Override
	public void onLoad() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		
		// Register Plugin Channels
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		if (this.getConfig().getBoolean("general.enable-receive"))
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new IncomingMessageListener());
	    
		// Register Listeners
	    this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	  
	    // Fetch the server's name (only for reloads)
	    BungeeRequest.sendBungeeRequest("GetServer");
	    
	    // Register NCP Hook for checks, but only if it's enabled in the config.
	    if (this.getConfig().getBoolean("general.enable-send"))
	    	NCPHookManager.addHook(CheckType.values(), new NotifyHook());
	}
	
	@Override
	public void onDisable() {
		// Unregister the NCP Hook if it's registered
		if (NCPHookManager.getHooksByName(this.getName()) != null)
			NCPHookManager.removeHooks(this.getName());
		
		// Unregister Plugin Channels
		if (this.getServer().getMessenger().isIncomingChannelRegistered(this, "BungeeCord"))
			this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");

		this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");

		SUBCHANNEL = null;
		SERVER_NAME = null;
		cooldownManager = null;
		gson = null;
		instance = null;
	}
	
}
