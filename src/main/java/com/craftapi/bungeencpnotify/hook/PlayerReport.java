/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.manager
 * @file: PlayerReport.java
 * @author: Niklas (Krymonota)
 * @date: 27.10.2015
 */
package com.craftapi.bungeencpnotify.hook;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;

import fr.neatmonster.nocheatplus.checks.CheckType;

public class PlayerReport {
	
	private String player;
	private String server;
	private CheckType check;
	private double violation;
	
	public PlayerReport(String player, CheckType check, double violation) {
		this.player = player;
		this.server = BungeeNCPNotify.SERVER_NAME;
		this.check = check;
		this.violation = violation;
	}
	
	/**
	 * @return String The name of the reported player.
	 */
	public String getPlayer() {
		return this.player;
	}
	
	/**
	 * @return String The server name of the reported player.
	 */
	public String getServer() {
		return this.server;
	}
	
	/**
	 * @return CheckType The check/cheat that was detected.
	 */
	public CheckType getCheckType() {
		return this.check;
	}
	
	/**
	 * @return Double The violation of the check/cheat
	 */
	public double getViolation() {
		return this.violation;
	}

}
