package com.michaelelin.NerdFlags;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.worldedit.WorldEdit;
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

    public WorldGuardPlugin worldguard;

    public Material _navigationWand;

    private Player nextTP;
    private long timestamp;

    ProtocolManager protocolManager;

    public StateFlag ALLOW_DROPS;
    public StateFlag ALLOW_MOB_DROPS;

    public StateFlag PLAYER_MOB_DAMAGE;

    public StateFlag NETHER_PORTAL;
    public StateFlag END_PORTAL;

    public StateFlag SNOWBALL_FIREFIGHT;

    public StateFlag COMPASS;
    public StateFlag TELEPORT_ENTRY;

    public EnumFlag<GameMode> FORCE_GAMEMODE;

    public StateFlag WEATHER;

    public StateFlag NERD_KEEP_INVENTORY;

    public StringFlag ENTRY_COMMANDS;

    public StateFlag ALLOW_RAIDS;

    public StateFlag ALLOW_COPPER_STATUES;

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
    public StateFlag USE_COMPARATOR;
    public StateFlag USE_HOPPER;
    public StateFlag USE_DROPPER;
    public StateFlag USE_DAYLIGHT_DETECTOR;
    public StateFlag TAKE_LECTERN_BOOK;

    @Override
    public void onEnable() {

        if (checkPlugin("ProtocolLib", false)) {
            protocolManager = ProtocolLibrary.getProtocolManager();
        }

        if (checkPlugin("WGRegionEvents", false)) {
            getServer().getPluginManager().registerEvents(new NerdFlagsRegionListener(this), this);
        }

        getServer().getPluginManager().registerEvents(new NerdFlagsListener(this), this);

        // pull WorldEdit navigation wand information now
        WorldEdit worldEdit = WorldEdit.getInstance();
        String navigationWandMaterialName = worldEdit.getConfiguration().navigationWand;
        _navigationWand = Material.getMaterial(navigationWandMaterialName);

        Plugin wgPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wgPlugin instanceof WorldGuardPlugin) {
            worldguard = (WorldGuardPlugin) wgPlugin;
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

    public void expectTeleport(Player player) {
        this.nextTP = player;
        this.timestamp = player.getPlayerTime();
    }

    public boolean hasCompassed(Player player) {
        return this.nextTP == player && this.timestamp == player.getPlayerTime();
    }

    /**
     * Register Flags.
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
        flagRegistry.register(NERD_KEEP_INVENTORY = new StateFlag("nerd-keep-inventory", false));
        flagRegistry.register(new StringFlag("date"));
        flagRegistry.register(new StringFlag("created-by"));
        flagRegistry.register(new StringFlag("first-owner"));
        flagRegistry.register(ENTRY_COMMANDS = new StringFlag("entry-commands"));
        flagRegistry.register(ALLOW_RAIDS = new StateFlag("allow-raids", true));
        flagRegistry.register(ALLOW_COPPER_STATUES = new StateFlag("allow-copper-statues", true));

        flagRegistry.register(TAKE_LECTERN_BOOK = new StateFlag("take-lectern-book", getConfig().getBoolean("default-lectern")));;
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
        flagRegistry.register(USE_COMPARATOR = new StateFlag("use-comparator", getConfig().getBoolean("default-comparator")));
        flagRegistry.register(USE_HOPPER = new StateFlag("use-hopper", getConfig().getBoolean("default-hopper")));
        flagRegistry.register(USE_DROPPER = new StateFlag("use-dropper", getConfig().getBoolean("default-dropper")));
        flagRegistry.register(USE_DAYLIGHT_DETECTOR = new StateFlag("use-daylight-detector", getConfig().getBoolean("default-daylight-detector")));

        getLogger().log(Level.INFO, "Loaded all flags");
    }

}
