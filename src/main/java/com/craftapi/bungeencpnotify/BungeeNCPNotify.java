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

public class BungeeNCPNotify extends JavaPlugin {

	private static BungeeNCPNotify instance;
	
	@Nonnull
	public static BungeeNCPNotify getInstance() {
		return instance;
	}
	
	@Override
	public void onLoad() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}
	
}
