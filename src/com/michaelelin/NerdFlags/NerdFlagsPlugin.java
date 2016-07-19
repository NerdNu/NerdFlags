package com.michaelelin.NerdFlags;

import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class NerdFlagsPlugin extends JavaPlugin {

    private NerdFlagsListener listener;
    private NerdFlagsRegionListener regionListener;
    private Player nextTP;
    private long timestamp;

    public ProtocolManager protocolManager;

    public StateFlag ALLOW_DROPS;
    public StateFlag ALLOW_MOB_DROPS;

    public StateFlag PLAYER_MOB_DAMAGE;

    public StateFlag NETHER_PORTAL;
    public StateFlag END_PORTAL;

    public StateFlag SNOWBALL_FIREFIGHT;

    public StateFlag COMPASS;

    public StateFlag WEATHER;

    public StringFlag DATE;
    public StringFlag CREATED_BY;
    public StringFlag FIRST_OWNER;

    public StringFlag ENTRY_COMMANDS;

    //public CustomLocationFlag WARP;

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

        listener = new NerdFlagsListener(this, getPlugin("WorldGuard", WorldGuardPlugin.class), getPlugin("WorldEdit", WorldEditPlugin.class));
        getServer().getPluginManager().registerEvents(listener, this);

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

    public void expectTeleport(Player player) {
        this.nextTP = player;
        this.timestamp = player.getPlayerTime();
    }

    public boolean hasCompassed(Player player) {
        return this.nextTP == player && this.timestamp == player.getPlayerTime();
    }

    /**
     * Hack flags into world guard before any onLoad() of any plugin is called
     */
    @Override
    public void onLoad() {
    	
    	saveDefaultConfig();

    	WorldGuardPlugin wg = getPlugin("WorldGuard", WorldGuardPlugin.class);
    	FlagRegistry fr = wg.getFlagRegistry();

        fr.register(ALLOW_DROPS = new StateFlag("allow-drops", true));
        fr.register(ALLOW_MOB_DROPS = new StateFlag("allow-mob-drops", true));
        fr.register(PLAYER_MOB_DAMAGE = new StateFlag("player-mob-damage", true));
        fr.register(NETHER_PORTAL = new StateFlag("nether-portal", true));
        fr.register(END_PORTAL = new StateFlag("end-portal", true));
        fr.register(SNOWBALL_FIREFIGHT = new StateFlag("snowball-firefight", false));
        fr.register(COMPASS = new StateFlag("compass", true));
        fr.register(WEATHER = new StateFlag("weather", false));
        fr.register(DATE = new StringFlag("date"));
        fr.register(CREATED_BY = new StringFlag("created-by"));
        fr.register(FIRST_OWNER = new StringFlag("first-owner"));
        fr.register(ENTRY_COMMANDS = new StringFlag("entry-commands"));

        // TODO: Fix this
        //fr.register(WARP = new CustomLocationFlag("warp"));

        fr.register(USE_DISPENSER = new StateFlag("use-dispenser", getConfig().getBoolean("default-dispenser")));
        fr.register(USE_NOTE_BLOCK = new StateFlag("use-note-block", getConfig().getBoolean("default-note-block")));
        fr.register(USE_WORKBENCH = new StateFlag("use-workbench", getConfig().getBoolean("default-workbench")));
        fr.register(USE_DOOR = new StateFlag("use-door", getConfig().getBoolean("default-door")));
        fr.register(USE_LEVER = new StateFlag("use-lever", getConfig().getBoolean("default-lever")));
        fr.register(USE_PRESSURE_PLATE = new StateFlag("use-pressure-plate", getConfig().getBoolean("default-pressure-plate")));
        fr.register(USE_BUTTON = new StateFlag("use-button", getConfig().getBoolean("default-button")));
        fr.register(USE_JUKEBOX = new StateFlag("use-jukebox", getConfig().getBoolean("default-jukebox")));
        fr.register(USE_REPEATER = new StateFlag("use-repeater", getConfig().getBoolean("default-repeater")));
        fr.register(USE_TRAP_DOOR = new StateFlag("use-trap-door", getConfig().getBoolean("default-trap-door")));
        fr.register(USE_FENCE_GATE = new StateFlag("use-fence-gate", getConfig().getBoolean("default-fence-gate")));
        fr.register(USE_BREWING_STAND = new StateFlag("use-brewing-stand", getConfig().getBoolean("default-brewing-stand")));
        fr.register(USE_CAULDRON = new StateFlag("use-cauldron", getConfig().getBoolean("default-cauldron")));
        fr.register(USE_ENCHANTMENT_TABLE = new StateFlag("use-enchantment-table", getConfig().getBoolean("default-enchantment-table")));
        fr.register(USE_ENDER_CHEST = new StateFlag("use-ender-chest", getConfig().getBoolean("default-ender-chest")));
        fr.register(USE_TRIPWIRE = new StateFlag("use-tripwire", getConfig().getBoolean("default-tripwire")));
        fr.register(USE_BEACON = new StateFlag("use-beacon", getConfig().getBoolean("default-beacon")));
        fr.register(USE_ANVIL = new StateFlag("use-anvil", getConfig().getBoolean("default-anvil")));
        fr.register(USE_COMPARATOR = new StateFlag("use-comparator", getConfig().getBoolean("default-comparator")));
        fr.register(USE_HOPPER = new StateFlag("use-hopper", getConfig().getBoolean("default-hopper")));
        fr.register(USE_DROPPER = new StateFlag("use-dropper", getConfig().getBoolean("default-dropper")));
        fr.register(USE_DAYLIGHT_DETECTOR = new StateFlag("use-daylight-detector", getConfig().getBoolean("default-daylight-detector")));

        getLogger().log(Level.INFO, "Loaded all flags");
    }

}
