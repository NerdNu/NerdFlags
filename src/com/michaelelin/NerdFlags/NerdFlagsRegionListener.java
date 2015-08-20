package com.michaelelin.NerdFlags;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class NerdFlagsRegionListener implements Listener {

    private NerdFlagsPlugin plugin;

    public NerdFlagsRegionListener(NerdFlagsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerEnteredRegion(RegionEnteredEvent event) {
        if (event.getRegion().getFlag(plugin.WEATHER) == StateFlag.State.ALLOW) {
            PacketContainer weatherPacket = plugin.protocolManager.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
            weatherPacket.getIntegers().write(0, 2);
            weatherPacket.getFloat().write(0, 0F);
            try {
                plugin.protocolManager.sendServerPacket(event.getPlayer(), weatherPacket);
            } catch (InvocationTargetException e) {
            }
        }
        String entryCommands = event.getRegion().getFlag(plugin.ENTRY_COMMANDS);
        if (entryCommands != null) {
            for (String command : parseCommands(entryCommands)) {
                plugin.getServer().dispatchCommand(event.getPlayer(), command);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLeftRegion(RegionLeftEvent event) {
        if (event.getRegion().getFlag(plugin.WEATHER) == StateFlag.State.ALLOW && !event.getPlayer().getWorld().hasStorm()) {
            PacketContainer weatherPacket = plugin.protocolManager.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
            weatherPacket.getIntegers().write(0, 1);
            weatherPacket.getFloat().write(0, 0F);
            try {
                plugin.protocolManager.sendServerPacket(event.getPlayer(), weatherPacket);
            } catch (InvocationTargetException e) {
            }
        }
    }

    private static List<String> parseCommands(String commands) {
        List<String> commandList = new LinkedList<String>();
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
