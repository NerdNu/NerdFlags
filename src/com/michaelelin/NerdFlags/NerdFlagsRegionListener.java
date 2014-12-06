package com.michaelelin.NerdFlags;

import java.lang.reflect.InvocationTargetException;

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

}
