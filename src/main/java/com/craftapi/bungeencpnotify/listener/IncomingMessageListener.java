/**
 * @project: bungeencpnotify
 * @package: com.craftapi.bungeencpnotify.listener
 * @file: IncomingMessageListener.java
 * @author: Niklas (Krymonota)
 * @date: 26.10.2015
 */
package com.craftapi.bungeencpnotify.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;
import com.craftapi.bungeencpnotify.hook.PlayerReport;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonSyntaxException;

import fr.neatmonster.nocheatplus.NCPAPIProvider;

public class IncomingMessageListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord"))
			return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subChannel = in.readUTF();
		
		if (subChannel.equals("GetServer"))
			BungeeNCPNotify.SERVER_NAME = in.readUTF();
		else if (subChannel.equals(BungeeNCPNotify.SUBCHANNEL)) {
			byte[] msgBytes = new byte[in.readShort()];
			in.readFully(msgBytes);
	
			DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
			PlayerReport report = null;
			
			try {
				// Transform the JSON-String back to the PlayerReport class
				report = (PlayerReport) BungeeNCPNotify.getGson().fromJson(msgIn.readUTF(), PlayerReport.class);
			} catch (JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}

			// Just in case if something went wrong during the transformation
			if (report == null)
				return;

			// Get the Report Message from the config and replace the variables
			String reportMessage = ChatColor.translateAlternateColorCodes('&', BungeeNCPNotify.getInstance().getConfig().getString("general.report-message"));
			reportMessage = reportMessage.replaceAll("%player%", report.getPlayer()).replaceAll("%server%", report.getServer()).replaceAll("%check%", report.getCheckType().getName()).replaceAll("%violation%", String.valueOf(Math.round(report.getViolation())));			
			
			// Send an admin notification to players with the corresponding permission (only if the player has turned notifications on)
			NCPAPIProvider.getNoCheatPlusAPI().sendAdminNotifyMessage(reportMessage);
		}
	}

}
