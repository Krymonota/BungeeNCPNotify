package com.craftapi.bungeencpnotify.listener;

import com.craftapi.bungeencpnotify.BungeeNCPNotify;
import com.craftapi.bungeencpnotify.hook.PlayerReport;
import fr.neatmonster.nocheatplus.NCPAPIProvider;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by tfrew on 5/26/16.
 */
public class IncomingRedisMessageListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, final String msg) {
        if (channel.equals("bncpnotify"))
        // Needs to be done in the server thread
        new BukkitRunnable() {
            @Override
            public void run() {

                PlayerReport report = null;
                try {
                    // Transform the JSON-String back to the PlayerReport class
                    report = (PlayerReport) BungeeNCPNotify.getGson().fromJson(msg, PlayerReport.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Just in case if something went wrong during the transformation
                if (report == null)
                    return;

                // Get the Report Message from the config and replace the variables
                String reportMessage = ChatColor.translateAlternateColorCodes('&', BungeeNCPNotify.getConfiguration().getString("general.report-message"));
                reportMessage = reportMessage.replaceAll("%player%", report.getPlayer()).replaceAll("%server%", report.getServer()).replaceAll("%check%", report.getCheckType().getName()).replaceAll("%violation%", String.valueOf(Math.round(report.getViolation())));

                // Send an admin notification to players with the corresponding permission (only if the player has turned notifications on)
                NCPAPIProvider.getNoCheatPlusAPI().sendAdminNotifyMessage(reportMessage);
            }
        }.runTask(BungeeNCPNotify.getInstance());
    }

    @Override
    public void onPMessage(String s, String s2, String s3) {
    }

    @Override
    public void onSubscribe(String s, int i) {
    }

    @Override
    public void onUnsubscribe(String s, int i) {
    }

    @Override
    public void onPUnsubscribe(String s, int i) {
    }

    @Override
    public void onPSubscribe(String s, int i) {
    }

}
