package com.michaelelin.NerdFlags;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class NerdFlagsPlugin extends JavaPlugin {

    WorldGuardPlugin worldguard;

    WorldEditPlugin worldedit;

    Material _navigationWand;

    private NerdFlagsListener listener;
    private NerdFlagsRegionListener regionListener;
    private Player nextTP;
    private long timestamp;

    ProtocolManager protocolManager;

    StateFlag ALLOW_DROPS;
    StateFlag ALLOW_MOB_DROPS;

    StateFlag PLAYER_MOB_DAMAGE;

    StateFlag NETHER_PORTAL;
    StateFlag END_PORTAL;

    StateFlag SNOWBALL_FIREFIGHT;

    StateFlag COMPASS;
    StateFlag TELEPORT_ENTRY;

    EnumFlag<GameMode> FORCE_GAMEMODE;

    StateFlag WEATHER;

    StateFlag KEEP_INVENTORY;

    private StringFlag DATE;
    private StringFlag CREATED_BY;
    private StringFlag FIRST_OWNER;

    StringFlag ENTRY_COMMANDS;

    //public CustomLocationFlag WARP;

    StateFlag USE_DISPENSER;
    StateFlag USE_NOTE_BLOCK;
    StateFlag USE_WORKBENCH;
    StateFlag USE_DOOR;
    StateFlag USE_LEVER;
    StateFlag USE_PRESSURE_PLATE;
    StateFlag USE_BUTTON;
    StateFlag USE_JUKEBOX;
    StateFlag USE_REPEATER;
    StateFlag USE_TRAP_DOOR;
    StateFlag USE_FENCE_GATE;
    StateFlag USE_BREWING_STAND;
    StateFlag USE_CAULDRON;
    StateFlag USE_ENCHANTMENT_TABLE;
    StateFlag USE_ENDER_CHEST;
    StateFlag USE_TRIPWIRE;
    StateFlag USE_BEACON;
    StateFlag USE_ANVIL;
    StateFlag USE_COMPARATOR;
    StateFlag USE_HOPPER;
    StateFlag USE_DROPPER;
    StateFlag USE_DAYLIGHT_DETECTOR;

    @Override
    public void onEnable() {

        if (checkPlugin("ProtocolLib", false)) {
            protocolManager = ProtocolLibrary.getProtocolManager();
        }

        if (checkPlugin("WGRegionEvents", false)) {
            regionListener = new NerdFlagsRegionListener(this);
            getServer().getPluginManager().registerEvents(regionListener, this);
        }

        listener = new NerdFlagsListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

        // pull WorldEdit navigation wand information now
        WorldEdit worldEdit = WorldEdit.getInstance();
        String navigationWandMaterialName = worldEdit.getConfiguration().navigationWand;
        _navigationWand = Material.getMaterial(navigationWandMaterialName);
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

    void expectTeleport(Player player) {
        this.nextTP = player;
        this.timestamp = player.getPlayerTime();
    }

    boolean hasCompassed(Player player) {
        return this.nextTP == player && this.timestamp == player.getPlayerTime();
    }

    /**
     * Hack flags into world guard before any onLoad() of any plugin is called
     */
    @Override
    public void onLoad() {
    	
    	saveDefaultConfig();

        WorldGuard worldGuard = WorldGuard.getInstance();
        FlagRegistry flagRegistry = worldGuard.getFlagRegistry();

        flagRegistry.register(ALLOW_DROPS = new StateFlag("allow-drops", true));
        flagRegistry.register(ALLOW_MOB_DROPS = new StateFlag("allow-mob-drops", true));
        flagRegistry.register(PLAYER_MOB_DAMAGE = new StateFlag("player-mob-damage", true));
        flagRegistry.register(NETHER_PORTAL = new StateFlag("nether-portal", true));
        flagRegistry.register(END_PORTAL = new StateFlag("end-portal", true));
        flagRegistry.register(SNOWBALL_FIREFIGHT = new StateFlag("snowball-firefight", false));
        flagRegistry.register(COMPASS = new StateFlag("compass", true));
        flagRegistry.register(TELEPORT_ENTRY = new StateFlag("teleport-entry", true));
        flagRegistry.register(FORCE_GAMEMODE = new EnumFlag<>("force-gamemode", GameMode.class));
        flagRegistry.register(WEATHER = new StateFlag("weather", false));
        flagRegistry.register(KEEP_INVENTORY = new StateFlag("keep-inventory", false));
        flagRegistry.register(DATE = new StringFlag("date"));
        flagRegistry.register(CREATED_BY = new StringFlag("created-by"));
        flagRegistry.register(FIRST_OWNER = new StringFlag("first-owner"));
        flagRegistry.register(ENTRY_COMMANDS = new StringFlag("entry-commands"));

        flagRegistry.register(USE_DISPENSER = new StateFlag("use-dispenser", getConfig().getBoolean("default-dispenser")));
        flagRegistry.register(USE_NOTE_BLOCK = new StateFlag("use-note-block", getConfig().getBoolean("default-note-block")));
        flagRegistry.register(USE_WORKBENCH = new StateFlag("use-workbench", getConfig().getBoolean("default-workbench")));
        flagRegistry.register(USE_DOOR = new StateFlag("use-door", getConfig().getBoolean("default-door")));
        flagRegistry.register(USE_LEVER = new StateFlag("use-lever", getConfig().getBoolean("default-lever")));
        flagRegistry.register(USE_PRESSURE_PLATE = new StateFlag("use-pressure-plate", getConfig().getBoolean("default-pressure-plate")));
        flagRegistry.register(USE_BUTTON = new StateFlag("use-button", getConfig().getBoolean("default-button")));
        flagRegistry.register(USE_JUKEBOX = new StateFlag("use-jukebox", getConfig().getBoolean("default-jukebox")));
        flagRegistry.register(USE_REPEATER = new StateFlag("use-repeater", getConfig().getBoolean("default-repeater")));
        flagRegistry.register(USE_TRAP_DOOR = new StateFlag("use-trap-door", getConfig().getBoolean("default-trap-door")));
        flagRegistry.register(USE_FENCE_GATE = new StateFlag("use-fence-gate", getConfig().getBoolean("default-fence-gate")));
        flagRegistry.register(USE_BREWING_STAND = new StateFlag("use-brewing-stand", getConfig().getBoolean("default-brewing-stand")));
        flagRegistry.register(USE_CAULDRON = new StateFlag("use-cauldron", getConfig().getBoolean("default-cauldron")));
        flagRegistry.register(USE_ENCHANTMENT_TABLE = new StateFlag("use-enchantment-table", getConfig().getBoolean("default-enchantment-table")));
        flagRegistry.register(USE_ENDER_CHEST = new StateFlag("use-ender-chest", getConfig().getBoolean("default-ender-chest")));
        flagRegistry.register(USE_TRIPWIRE = new StateFlag("use-tripwire", getConfig().getBoolean("default-tripwire")));
        flagRegistry.register(USE_BEACON = new StateFlag("use-beacon", getConfig().getBoolean("default-beacon")));
        flagRegistry.register(USE_ANVIL = new StateFlag("use-anvil", getConfig().getBoolean("default-anvil")));
        flagRegistry.register(USE_COMPARATOR = new StateFlag("use-comparator", getConfig().getBoolean("default-comparator")));
        flagRegistry.register(USE_HOPPER = new StateFlag("use-hopper", getConfig().getBoolean("default-hopper")));
        flagRegistry.register(USE_DROPPER = new StateFlag("use-dropper", getConfig().getBoolean("default-dropper")));
        flagRegistry.register(USE_DAYLIGHT_DETECTOR = new StateFlag("use-daylight-detector", getConfig().getBoolean("default-daylight-detector")));

        getLogger().log(Level.INFO, "Loaded all flags");
    }

}
