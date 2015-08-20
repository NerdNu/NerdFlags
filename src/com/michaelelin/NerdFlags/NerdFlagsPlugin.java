package com.michaelelin.NerdFlags;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;

public class NerdFlagsPlugin extends JavaPlugin {

    private NerdFlagsListener listener;
    private NerdFlagsRegionListener regionListener;
    private Player nextTP;
    private long timestamp;

    public ProtocolManager protocolManager;

    public StateFlag ALLOW_DROPS;
    public StateFlag ALLOW_MOB_DROPS;

    public StateFlag NETHER_PORTAL;
    public StateFlag END_PORTAL;

    public StateFlag SNOWBALL_FIREFIGHT;

    public StateFlag COMPASS;

    public StateFlag WEATHER;

    public StringFlag DATE;
    public StringFlag CREATED_BY;

    public StringFlag ENTRY_COMMANDS;

    public StateFlag USE_DISPENSER;
    public StateFlag USE_NOTE_BLOCK;
    public StateFlag USE_WORKBENCH;
    public StateFlag USE_DOOR;
    public StateFlag USE_LEVER;
    public StateFlag USE_PRESSURE_PLATE;
    public StateFlag USE_BUTTON;
    public StateFlag USE_JUKEBOX;
    public StateFlag USE_REPEATER;
    public StateFlag USE_TRAP_DOOR;
    public StateFlag USE_FENCE_GATE;
    public StateFlag USE_BREWING_STAND;
    public StateFlag USE_CAULDRON;
    public StateFlag USE_ENCHANTMENT_TABLE;
    public StateFlag USE_ENDER_CHEST;
    public StateFlag USE_TRIPWIRE;
    public StateFlag USE_BEACON;
    public StateFlag USE_ANVIL;
    public StateFlag USE_COMPARATOR;
    public StateFlag USE_HOPPER;
    public StateFlag USE_DROPPER;
    public StateFlag USE_DAYLIGHT_DETECTOR;

    @Override
    public void onEnable() {

        if (checkPlugin("ProtocolLib", false)) {
            protocolManager = ProtocolLibrary.getProtocolManager();
        }

        if (checkPlugin("WGRegionEvents", false)) {
            regionListener = new NerdFlagsRegionListener(this);
            getServer().getPluginManager().registerEvents(regionListener, this);
        }

        if (checkPlugin("WGCustomFlags", true) && checkPlugin("WorldGuard", true) && checkPlugin("WorldEdit", true)) {
            WGCustomFlagsPlugin wgCustomFlagsPlugin = getPlugin("WGCustomFlags", WGCustomFlagsPlugin.class);
            listener = new NerdFlagsListener(this, getPlugin("WorldGuard", WorldGuardPlugin.class), getPlugin("WorldEdit", WorldEditPlugin.class));
            getServer().getPluginManager().registerEvents(listener, this);

            ALLOW_DROPS = new StateFlag("allow-drops", true);
            ALLOW_MOB_DROPS = new StateFlag("allow-mob-drops", true);
            NETHER_PORTAL = new StateFlag("nether-portal", true);
            END_PORTAL = new StateFlag("end-portal", true);
            SNOWBALL_FIREFIGHT = new StateFlag("snowball-firefight", false);
            COMPASS = new StateFlag("compass", true);
            WEATHER = new StateFlag("weather", false);
            DATE = new StringFlag("date");
            CREATED_BY = new StringFlag("created-by");
            ENTRY_COMMANDS = new StringFlag("entry-commands");

            wgCustomFlagsPlugin.addCustomFlag(ALLOW_DROPS);
            wgCustomFlagsPlugin.addCustomFlag(ALLOW_MOB_DROPS);
            wgCustomFlagsPlugin.addCustomFlag(NETHER_PORTAL);
            wgCustomFlagsPlugin.addCustomFlag(END_PORTAL);
            wgCustomFlagsPlugin.addCustomFlag(SNOWBALL_FIREFIGHT);
            wgCustomFlagsPlugin.addCustomFlag(WEATHER);
            wgCustomFlagsPlugin.addCustomFlag(COMPASS);
            wgCustomFlagsPlugin.addCustomFlag(DATE);
            wgCustomFlagsPlugin.addCustomFlag(CREATED_BY);
            wgCustomFlagsPlugin.addCustomFlag(ENTRY_COMMANDS);

            saveDefaultConfig();
            loadConfig();

            wgCustomFlagsPlugin.addCustomFlag(USE_DISPENSER);
            wgCustomFlagsPlugin.addCustomFlag(USE_NOTE_BLOCK);
            wgCustomFlagsPlugin.addCustomFlag(USE_WORKBENCH);
            wgCustomFlagsPlugin.addCustomFlag(USE_DOOR);
            wgCustomFlagsPlugin.addCustomFlag(USE_LEVER);
            wgCustomFlagsPlugin.addCustomFlag(USE_PRESSURE_PLATE);
            wgCustomFlagsPlugin.addCustomFlag(USE_BUTTON);
            wgCustomFlagsPlugin.addCustomFlag(USE_JUKEBOX);
            wgCustomFlagsPlugin.addCustomFlag(USE_REPEATER);
            wgCustomFlagsPlugin.addCustomFlag(USE_TRAP_DOOR);
            wgCustomFlagsPlugin.addCustomFlag(USE_FENCE_GATE);
            wgCustomFlagsPlugin.addCustomFlag(USE_BREWING_STAND);
            wgCustomFlagsPlugin.addCustomFlag(USE_CAULDRON);
            wgCustomFlagsPlugin.addCustomFlag(USE_ENCHANTMENT_TABLE);
            wgCustomFlagsPlugin.addCustomFlag(USE_ENDER_CHEST);
            wgCustomFlagsPlugin.addCustomFlag(USE_TRIPWIRE);
            wgCustomFlagsPlugin.addCustomFlag(USE_BEACON);
            wgCustomFlagsPlugin.addCustomFlag(USE_ANVIL);
            wgCustomFlagsPlugin.addCustomFlag(USE_COMPARATOR);
            wgCustomFlagsPlugin.addCustomFlag(USE_HOPPER);
            wgCustomFlagsPlugin.addCustomFlag(USE_DROPPER);
            wgCustomFlagsPlugin.addCustomFlag(USE_DAYLIGHT_DETECTOR);
        }
    }

    private <T extends Plugin> boolean checkPlugin(String name, boolean required) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        if (plugin == null) {
            if (required) {
                getLogger().warning("[" + getName() + "] " + name + " is required for this plugin to work; disabling.");
                getServer().getPluginManager().disablePlugin(this);
            }
            return false;
        }
        return true;
    }

    private <T extends Plugin> T getPlugin(String name, Class<T> mainClass) {
        return mainClass.cast(getServer().getPluginManager().getPlugin(name));
    }

    private void loadConfig() {
        USE_DISPENSER = new StateFlag("use-dispenser", getConfig().getBoolean("default-dispenser"));
        USE_NOTE_BLOCK = new StateFlag("use-note-block", getConfig().getBoolean("default-note-block"));
        USE_WORKBENCH = new StateFlag("use-workbench", getConfig().getBoolean("default-workbench"));
        USE_DOOR = new StateFlag("use-door", getConfig().getBoolean("default-door"));
        USE_LEVER = new StateFlag("use-lever", getConfig().getBoolean("default-lever"));
        USE_PRESSURE_PLATE = new StateFlag("use-pressure-plate", getConfig().getBoolean("default-pressure-plate"));
        USE_BUTTON = new StateFlag("use-button", getConfig().getBoolean("default-button"));
        USE_JUKEBOX = new StateFlag("use-jukebox", getConfig().getBoolean("default-jukebox"));
        USE_REPEATER = new StateFlag("use-repeater", getConfig().getBoolean("default-repeater"));
        USE_TRAP_DOOR = new StateFlag("use-trap-door", getConfig().getBoolean("default-trap-door"));
        USE_FENCE_GATE = new StateFlag("use-fence-gate", getConfig().getBoolean("default-fence-gate"));
        USE_BREWING_STAND = new StateFlag("use-brewing-stand", getConfig().getBoolean("default-brewing-stand"));
        USE_CAULDRON = new StateFlag("use-cauldron", getConfig().getBoolean("default-cauldron"));
        USE_ENCHANTMENT_TABLE = new StateFlag("use-enchantment-table", getConfig().getBoolean("default-enchantment-table"));
        USE_ENDER_CHEST = new StateFlag("use-ender-chest", getConfig().getBoolean("default-ender-chest"));
        USE_TRIPWIRE = new StateFlag("use-tripwire", getConfig().getBoolean("default-tripwire"));
        USE_BEACON = new StateFlag("use-beacon", getConfig().getBoolean("default-beacon"));
        USE_ANVIL = new StateFlag("use-anvil", getConfig().getBoolean("default-anvil"));
        USE_COMPARATOR = new StateFlag("use-comparator", getConfig().getBoolean("default-comparator"));
        USE_HOPPER = new StateFlag("use-hopper", getConfig().getBoolean("default-hopper"));
        USE_DROPPER = new StateFlag("use-dropper", getConfig().getBoolean("default-dropper"));
        USE_DAYLIGHT_DETECTOR = new StateFlag("use-daylight-detector", getConfig().getBoolean("default-daylight-detector"));
    }

    public void expectTeleport(Player player) {
        this.nextTP = player;
        this.timestamp = player.getPlayerTime();
    }

    public boolean hasCompassed(Player player) {
        return this.nextTP == player && this.timestamp == player.getPlayerTime();
    }

}
