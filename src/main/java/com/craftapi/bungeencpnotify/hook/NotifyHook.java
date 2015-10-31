/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.hook
 * @file: NotifyHook.java
 * @author: Niklas (Krymonota)
 * @date: 26.10.2015
 */
package com.craftapi.bungeencpnotify.hook;

import java.io.IOException;

import org.bukkit.entity.Player;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;
import com.craftapi.bungeencpnotify.manager.BungeeRequest;
import com.craftapi.bungeencpnotify.manager.CooldownManager;
import com.craftapi.bungeencpnotify.util.Duration;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.hooks.AbstractNCPHook;
import fr.neatmonster.nocheatplus.hooks.ILast;
import fr.neatmonster.nocheatplus.hooks.IStats;

public class NotifyHook extends AbstractNCPHook implements IStats, ILast {

	@Override
	public String getHookName() {
		return BungeeNCPNotify.getInstance().getDescription().getName();
	}

	@Override
	public String getHookVersion() {
		return BungeeNCPNotify.getInstance().getDescription().getVersion();
	}

	@Override
	public boolean onCheckFailure(CheckType checkType, Player player, IViolationInfo info) {
		int violation = BungeeNCPNotify.getConfiguration().getInt("checks." + checkType.getName());

		// Check if the check is disabled or the reported total violation is lower as defined in config
		if (violation <= 0 || info.getTotalVl() < violation)
			return false;

		// Send only a message to other servers if there are no staff members on this server online if enabled in config
		if (BungeeNCPNotify.getConfiguration().getBoolean("general.check-staff"))
			if (BungeeNCPNotify.getInstance().isStaffMemberOnline())
				return false;

		CooldownManager cooldown = BungeeNCPNotify.getCooldownManager();

		// Player is still on cooldown, return false
		if (cooldown.hasCooldown(player.getUniqueId()) && !cooldown.isExpired(player.getUniqueId()))
			return false;

		try {
			// Send a report notification to other servers
			BungeeRequest.sendBungeeRequest(player, BungeeNCPNotify.getGson().toJson(new PlayerReport(player.getName(), checkType, info.getTotalVl())), "Forward", "ALL", BungeeNCPNotify.getInstance().getName());
		} catch (IOException e) {
		}

		// Add a cooldown to the player
		cooldown.setExpiration(player.getUniqueId(), Duration.seconds(BungeeNCPNotify.getConfiguration().getInt("general.notify-cooldown")));

		return true;
	}

}
