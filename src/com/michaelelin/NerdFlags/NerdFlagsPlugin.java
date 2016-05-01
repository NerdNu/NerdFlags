package com.michaelelin.NerdFlags;

import com.sk89q.worldguard.protection.flags.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.CustomFlagBroker;
import com.sk89q.worldguard.protection.flags.CustomFlagProvider;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class NerdFlagsPlugin extends JavaPlugin implements CustomFlagProvider {

    private NerdFlagsListener listener;
    private NerdFlagsRegionListener regionListener;
    private Player nextTP;
    private long timestamp;

    public ProtocolManager protocolManager;

    public StateFlag ALLOW_DROPS;
    public StateFlag ALLOW_MOB_DROPS;

    public SetFlag<EntityType> PLAYER_MOB_DAMAGE;

    public StateFlag NETHER_PORTAL;
    public StateFlag END_PORTAL;

    public StateFlag SNOWBALL_FIREFIGHT;

    public StateFlag COMPASS;

    public StateFlag WEATHER;

    public StringFlag DATE;
    public StringFlag CREATED_BY;

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
     * This is a call back from WorldGuard. It could be called at any time after onLaod(), even
     * before onEnable()!!!
     */
    @Override
    public void addCustomFlags(CustomFlagBroker broker) {
        ALLOW_DROPS = new StateFlag("allow-drops", true);
        broker.addFlag(ALLOW_DROPS);
        
        ALLOW_MOB_DROPS = new StateFlag("allow-mob-drops", true);
        broker.addFlag(ALLOW_MOB_DROPS);
        
        // Using EntityTypeFlag here instead of EnumFlag causes WGCustomFlags to run into some reflection issues
        PLAYER_MOB_DAMAGE = new SetFlag<EntityType>("player-mob-damage", new EnumFlag<EntityType>("", EntityType.class));
        broker.addFlag(PLAYER_MOB_DAMAGE);
        
        NETHER_PORTAL = new StateFlag("nether-portal", true);
        broker.addFlag(NETHER_PORTAL);
        
        END_PORTAL = new StateFlag("end-portal", true);
        broker.addFlag(END_PORTAL);
        
        SNOWBALL_FIREFIGHT = new StateFlag("snowball-firefight", false);
        broker.addFlag(SNOWBALL_FIREFIGHT);
        
        COMPASS = new StateFlag("compass", true);
        broker.addFlag(COMPASS);
        
        WEATHER = new StateFlag("weather", false);
        broker.addFlag(WEATHER);
        
        DATE = new StringFlag("date");
        broker.addFlag(DATE);
        
        CREATED_BY = new StringFlag("created-by");
        broker.addFlag(CREATED_BY);
        
        ENTRY_COMMANDS = new StringFlag("entry-commands");
        broker.addFlag(ENTRY_COMMANDS);

        // TODO: Fix this
        //WARP = new CustomLocationFlag("warp");
        //broker.addFlag(WARP);
        
        saveDefaultConfig();
        
        USE_DISPENSER = new StateFlag("use-dispenser", getConfig().getBoolean("default-dispenser"));
        broker.addFlag(USE_DISPENSER);
        
        USE_NOTE_BLOCK = new StateFlag("use-note-block", getConfig().getBoolean("default-note-block"));
        broker.addFlag(USE_NOTE_BLOCK);
        
        USE_WORKBENCH = new StateFlag("use-workbench", getConfig().getBoolean("default-workbench"));
        broker.addFlag(USE_WORKBENCH);
        
        USE_DOOR = new StateFlag("use-door", getConfig().getBoolean("default-door"));
        broker.addFlag(USE_DOOR);
        
        USE_LEVER = new StateFlag("use-lever", getConfig().getBoolean("default-lever"));
        broker.addFlag(USE_LEVER);
        
        USE_PRESSURE_PLATE = new StateFlag("use-pressure-plate", getConfig().getBoolean("default-pressure-plate"));
        broker.addFlag(USE_PRESSURE_PLATE);
        
        USE_BUTTON = new StateFlag("use-button", getConfig().getBoolean("default-button"));
        broker.addFlag(USE_BUTTON);
        
        USE_JUKEBOX = new StateFlag("use-jukebox", getConfig().getBoolean("default-jukebox"));
        broker.addFlag(USE_JUKEBOX);
        
        USE_REPEATER = new StateFlag("use-repeater", getConfig().getBoolean("default-repeater"));
        broker.addFlag(USE_REPEATER);
        
        USE_TRAP_DOOR = new StateFlag("use-trap-door", getConfig().getBoolean("default-trap-door"));
        broker.addFlag(USE_TRAP_DOOR);
        
        USE_FENCE_GATE = new StateFlag("use-fence-gate", getConfig().getBoolean("default-fence-gate"));
        broker.addFlag(USE_FENCE_GATE);
        
        USE_BREWING_STAND = new StateFlag("use-brewing-stand", getConfig().getBoolean("default-brewing-stand"));
        broker.addFlag(USE_BREWING_STAND);
        
        USE_CAULDRON = new StateFlag("use-cauldron", getConfig().getBoolean("default-cauldron"));
        broker.addFlag(USE_CAULDRON);
        
        USE_ENCHANTMENT_TABLE = new StateFlag("use-enchantment-table", getConfig().getBoolean("default-enchantment-table"));
        broker.addFlag(USE_ENCHANTMENT_TABLE);
        
        USE_ENDER_CHEST = new StateFlag("use-ender-chest", getConfig().getBoolean("default-ender-chest"));
        broker.addFlag(USE_ENDER_CHEST);
        
        USE_TRIPWIRE = new StateFlag("use-tripwire", getConfig().getBoolean("default-tripwire"));
        broker.addFlag(USE_TRIPWIRE);
        
        USE_BEACON = new StateFlag("use-beacon", getConfig().getBoolean("default-beacon"));
        broker.addFlag(USE_BEACON);
        
        USE_ANVIL = new StateFlag("use-anvil", getConfig().getBoolean("default-anvil"));
        broker.addFlag(USE_ANVIL);
        
        USE_COMPARATOR = new StateFlag("use-comparator", getConfig().getBoolean("default-comparator"));
        broker.addFlag(USE_COMPARATOR);
        
        USE_HOPPER = new StateFlag("use-hopper", getConfig().getBoolean("default-hopper"));
        broker.addFlag(USE_HOPPER);
        
        USE_DROPPER = new StateFlag("use-dropper", getConfig().getBoolean("default-dropper"));
        broker.addFlag(USE_DROPPER);
        
        USE_DAYLIGHT_DETECTOR = new StateFlag("use-daylight-detector", getConfig().getBoolean("default-daylight-detector"));
        broker.addFlag(USE_DAYLIGHT_DETECTOR);
        
    }

}
