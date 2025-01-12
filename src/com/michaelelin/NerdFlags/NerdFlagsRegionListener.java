package com.michaelelin.NerdFlags;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.sk89q.worldguard.protection.flags.StateFlag;

import de.netzkronehd.wgregionevents.events.RegionEnterEvent;
import de.netzkronehd.wgregionevents.events.RegionLeaveEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class NerdFlagsRegionListener implements Listener {

    private NerdFlagsPlugin plugin;

    NerdFlagsRegionListener(NerdFlagsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerEnteredRegion(RegionEnterEvent event) {
        Player player = event.getPlayer();
        StateFlag.State weatherState = event.getRegion().getFlag(plugin.WEATHER);
        if (weatherState == StateFlag.State.ALLOW) {
            setWeather(player, true);
        }

        String entryCommands = event.getRegion().getFlag(plugin.ENTRY_COMMANDS);
        if (entryCommands != null) {
            for (String command : parseCommands(entryCommands)) {
                plugin.getServer().dispatchCommand(player, command);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLeftRegion(RegionLeaveEvent event) {
        Player player = event.getPlayer();
        StateFlag.State weatherState = event.getRegion().getFlag(plugin.WEATHER);
        boolean storming = player.getWorld().hasStorm();
        if (weatherState == StateFlag.State.ALLOW && !storming) {
            setWeather(player, false);
        }
    }

    private void setWeather(Player player, boolean weather) {
        PacketContainer weatherPacket = plugin.protocolManager.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        weatherPacket.getIntegers().write(0, weather ? 2 : 1);
        weatherPacket.getFloat().write(0, 0F);
        plugin.protocolManager.sendServerPacket(player, weatherPacket);
    }

    private static List<String> parseCommands(String commands) {
        List<String> commandList = new LinkedList<>();
        StringBuilder curr = new StringBuilder();
        boolean escape = false;
        for (char c : commands.toCharArray()) {
            if (c == '|' && !escape) {
                commandList.add(curr.toString());
                curr = new StringBuilder();
                escape = false;
            } else if (c == '\\') {
                if (escape) {
                    curr.append('\\');
                }
                escape = !escape;
            } else {
                curr.append(c);
                escape = false;
            }
        }
        commandList.add(curr.toString());
        return commandList;
    }

}
