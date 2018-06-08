package com.michaelelin.NerdFlags;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.projectiles.ProjectileSource;

public class NerdFlagsListener implements Listener {

    private NerdFlagsPlugin plugin;

    private final Pattern compassPattern = Pattern.compile("(?i)^/(worldedit:)?/?("
            + StringUtils.join(Arrays.asList("unstuck", "!", "ascend", "asc", "descend", "desc",
                    "ceil", "thru", "jumpto", "j", "up"), "|") + ")");

    public NerdFlagsListener(NerdFlagsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        boolean canDrop = testState(event.getLocation(), plugin.ALLOW_DROPS);
        if (!canDrop) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getDamager().getType() == EntityType.PLAYER) {
            player = (Player) event.getDamager();
        } else if (event.getDamager().getType() == EntityType.ARROW) {
            ProjectileSource source = ((Arrow) event.getDamager()).getShooter();
            if (source instanceof Player) {
                player = (Player) source;
            }
        }
        if (player != null) {
            boolean canDamage = testState(event.getEntity(), plugin.PLAYER_MOB_DAMAGE);
            if (!canDamage) {
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        boolean canDrop = testState(event.getEntity(), plugin.ALLOW_MOB_DROPS);
        if (!canDrop) {
            event.getDrops().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        Environment fromDimension = event.getFrom().getWorld().getEnvironment();
        Environment toDimension = event.getTo().getWorld().getEnvironment();
        if (fromDimension == Environment.THE_END && toDimension == Environment.NORMAL || toDimension == Environment.THE_END) {
            boolean canUsePortal = testState(event.getFrom(), plugin.END_PORTAL);
            if (!canUsePortal) {
                event.setCancelled(true);
            }
        } else {
            boolean canUsePortal = testState(event.getFrom(), plugin.NETHER_PORTAL);
            if (!canUsePortal) {
                event.setCancelled(true);
            }
        }
    }

    // WE checks this at a NORMAL priority, so we'll intercept it beforehand.
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        int navigationWandId = plugin.worldedit.getWorldEdit().getConfiguration().navigationWand;
        if (event.getItem() != null && event.getItem().getTypeId() == navigationWandId) {
            plugin.expectTeleport(event.getPlayer());
        }
        if (!event.isCancelled() && event.getClickedBlock() != null) {
            Location location = event.getClickedBlock().getLocation();
            Player player = event.getPlayer();

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                switch (event.getClickedBlock().getType()) {
                case DISPENSER:
                    setCancelled(event, !testBuild(player, location, plugin.USE_DISPENSER), true);
                    break;
                case NOTE_BLOCK:
                    setCancelled(event, !testBuild(player, location, plugin.USE_NOTE_BLOCK), true);
                    break;
                case WORKBENCH:
                    setCancelled(event, !testBuild(player, location, plugin.USE_WORKBENCH), true);
                    break;
                case WOODEN_DOOR:
                    setCancelled(event, !testBuild(player, location, plugin.USE_DOOR), true);
                    break;
                case LEVER:
                    setCancelled(event, !testBuild(player, location, plugin.USE_LEVER), true);
                    break;
                case STONE_BUTTON:
                case WOOD_BUTTON:
                    setCancelled(event, !testBuild(player, location, plugin.USE_BUTTON), true);
                    break;
                case JUKEBOX:
                    setCancelled(event, !testBuild(player, location, plugin.USE_JUKEBOX), true);
                    break;
                case DIODE_BLOCK_OFF:
                case DIODE_BLOCK_ON:
                    setCancelled(event, !testBuild(player, location, plugin.USE_REPEATER), true);
                    break;
                case TRAP_DOOR:
                    setCancelled(event, !testBuild(player, location, plugin.USE_TRAP_DOOR), true);
                    break;
                case FENCE_GATE:
                    setCancelled(event, !testBuild(player, location, plugin.USE_FENCE_GATE), true);
                    break;
                case BREWING_STAND:
                    setCancelled(event, !testBuild(player, location, plugin.USE_BREWING_STAND), true);
                    break;
                case CAULDRON:
                    setCancelled(event, !testBuild(player, location, plugin.USE_CAULDRON), true);
                    break;
                case ENCHANTMENT_TABLE:
                    setCancelled(event, !testBuild(player, location, plugin.USE_ENCHANTMENT_TABLE), true);
                    break;
                case ENDER_CHEST:
                    setCancelled(event, !testBuild(player, location, plugin.USE_ENDER_CHEST), true);
                    break;
                case BEACON:
                    setCancelled(event, !testBuild(player, location, plugin.USE_BEACON), true);
                    break;
                case ANVIL:
                    setCancelled(event, !testBuild(player, location, plugin.USE_ANVIL), true);
                    break;
                case REDSTONE_COMPARATOR_OFF:
                case REDSTONE_COMPARATOR_ON:
                    setCancelled(event, !testBuild(player, location, plugin.USE_COMPARATOR), true);
                    break;
                case HOPPER:
                    setCancelled(event, !testBuild(player, location, plugin.USE_HOPPER), true);
                    break;
                case DROPPER:
                    setCancelled(event, !testBuild(player, location, plugin.USE_DROPPER), true);
                    break;
                case DAYLIGHT_DETECTOR:
                case DAYLIGHT_DETECTOR_INVERTED:
                    setCancelled(event, !testBuild(player, location, plugin.USE_DAYLIGHT_DETECTOR), true);
                    break;
                default:
                }
            }
            if (event.getAction() == Action.PHYSICAL) {
                Material mat = event.getClickedBlock().getType();
                if (mat == Material.STONE_PLATE || mat == Material.WOOD_PLATE || mat == Material.GOLD_PLATE || mat == Material.IRON_PLATE) {
                    setCancelled(event, !testBuild(player, location, plugin.USE_PRESSURE_PLATE), false);
                }
                else if (mat == Material.TRIPWIRE) {
                    setCancelled(event, !testBuild(player, location, plugin.USE_TRIPWIRE), false);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (compassPattern.matcher(event.getMessage()).matches()) {
            plugin.expectTeleport(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location locationFrom = event.getFrom();
        World worldFrom = locationFrom.getWorld();
        Location locationTo = event.getTo();
        World worldTo = locationTo.getWorld();

        boolean canTeleport = hasBypass(event.getPlayer(), worldTo) || testState(locationTo, plugin.TELEPORT_ENTRY);
        if (!canTeleport) {
            cancelEvent(event, true);
        }

        if (plugin.hasCompassed(event.getPlayer())) {
            Player player = event.getPlayer();
            boolean canBypass = hasBypass(player, worldFrom) && hasBypass(player, worldTo);
            boolean canCompass = testState(event.getFrom(), plugin.COMPASS) && testState(event.getTo(), plugin.COMPASS);
            if (!canBypass && !canCompass) {
                cancelEvent(event, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        RegionQuery query = plugin.worldguard.getRegionContainer().createQuery();
        Player player = event.getPlayer();
        Location location = player.getLocation();
        GameMode forcedMode = query.queryValue(location, (RegionAssociable) null, plugin.FORCE_GAMEMODE);
        if (forcedMode != null && !forcedMode.equals(event.getNewGameMode())) {
            if (!hasBypass(player, location.getWorld())) {
                cancelEvent(event, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == TeleportCause.END_PORTAL) {
            boolean canUsePortal = testState(event.getFrom(), plugin.END_PORTAL);
            if (!canUsePortal) {
                event.setCancelled(true);
            }
        } else if (event.getCause() == TeleportCause.NETHER_PORTAL) {
            boolean canUsePortal = testState(event.getFrom(), plugin.NETHER_PORTAL);
            if (!canUsePortal) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntityType() == EntityType.SNOWBALL) {
            Block b = event.getEntity().getLocation().getBlock();
            if (b.getType() == Material.FIRE) {
                boolean canExtinguish = testState(b.getLocation(), plugin.SNOWBALL_FIREFIGHT);
                if (canExtinguish) {
                    b.setType(Material.AIR);
                    b.getWorld().playEffect(b.getLocation(), Effect.EXTINGUISH, 0);
                }
            }
        }
    }

    /**
     * Listens for player deaths in order to properly handle the keep-inventory region flag.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        
        if (testState(player, plugin.KEEP_INVENTORY)) {
            e.setKeepLevel(false);
            e.setKeepInventory(true);
        }
    }

    private boolean testBuild(Player player, Location location, StateFlag flag) {
        World world = location.getWorld();
        RegionQuery query = plugin.worldguard.getRegionContainer().createQuery();
        return hasBypass(player, world) || query.testBuild(location, player, flag);
    }

    private boolean hasBypass(Player player, World world) {
        GlobalRegionManager regionManager = plugin.worldguard.getGlobalRegionManager();
        return regionManager.hasBypass(player, world);
    }

    private void cancelEvent(Cancellable e, boolean notifyPlayer) {
        setCancelled(e, true, notifyPlayer);
    }

    private void setCancelled(Cancellable e, boolean cancel, boolean notifyPlayer) {
        e.setCancelled(cancel);
        if (e.isCancelled() && notifyPlayer && e instanceof PlayerEvent) {
            PlayerEvent playerEvent = (PlayerEvent) e;
            Player player = playerEvent.getPlayer();
            player.sendMessage(ChatColor.DARK_RED + "You don't have permission to use that in this area.");
        }
    }

    private boolean testState(Entity entity, StateFlag flag) {
        return testState(entity.getLocation(), flag);
    }

    private boolean testState(Location location, StateFlag flag) {
        RegionQuery query = plugin.worldguard.getRegionContainer().createQuery();
        return query.testState(location, (RegionAssociable) null, flag);
    }
}
