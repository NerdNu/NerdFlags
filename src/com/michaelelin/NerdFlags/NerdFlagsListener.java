package com.michaelelin.NerdFlags;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

public class NerdFlagsListener implements Listener {

    private NerdFlagsPlugin plugin;
    private WorldGuardPlugin worldguard;
    private WorldEditPlugin worldedit;

    public NerdFlagsListener(NerdFlagsPlugin plugin, WorldGuardPlugin worldguard, WorldEditPlugin worldedit) {
        this.plugin = plugin;
        this.worldguard = worldguard;
        this.worldedit = worldedit;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        ApplicableRegionSet setAtLocation = worldguard.getGlobalRegionManager().get(event.getLocation().getWorld()).getApplicableRegions(event.getLocation());
        if (!setAtLocation.allows(plugin.ALLOW_DROPS)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        ApplicableRegionSet setAtLocation = worldguard.getGlobalRegionManager().get(event.getEntity().getLocation().getWorld()).getApplicableRegions(event.getEntity().getLocation());
        if (!setAtLocation.allows(plugin.ALLOW_MOB_DROPS)) {
            event.getDrops().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        Environment fromDimension = event.getFrom().getWorld().getEnvironment();
        Environment toDimension = event.getTo().getWorld().getEnvironment();
        ApplicableRegionSet setAtLocation = worldguard.getRegionContainer().get(event.getFrom().getWorld()).getApplicableRegions(event.getFrom());
        if (fromDimension == Environment.THE_END && toDimension == Environment.NORMAL || toDimension == Environment.THE_END) {
            if (setAtLocation.queryState(null, plugin.END_PORTAL) == State.DENY) {
                event.setCancelled(true);
            }
        } else {
            if (setAtLocation.queryState(null, plugin.NETHER_PORTAL) == State.DENY) {
                event.setCancelled(true);
            }
        }
    }

    // WE checks this at a NORMAL priority, so we'll intercept it beforehand.
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getTypeId() == worldedit.getWorldEdit().getConfiguration().navigationWand) {
            plugin.expectTeleport(event.getPlayer());
        }
        if (!event.isCancelled() && event.getClickedBlock() != null) {
            Location location = event.getClickedBlock().getLocation();
            LocalPlayer player = worldguard.wrapPlayer(event.getPlayer());

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                switch (event.getClickedBlock().getType()) {
                case DISPENSER:
                    cancelEvent(event, !allows(plugin.USE_DISPENSER, location, player), true);
                    break;
                case NOTE_BLOCK:
                    cancelEvent(event, !allows(plugin.USE_NOTE_BLOCK, location, player), true);
                    break;
                case WORKBENCH:
                    cancelEvent(event, !allows(plugin.USE_WORKBENCH, location, player), true);
                    break;
                case WOODEN_DOOR:
                    cancelEvent(event, !allows(plugin.USE_DOOR, location, player), true);
                    break;
                case LEVER:
                    cancelEvent(event, !allows(plugin.USE_LEVER, location, player), true);
                    break;
                case STONE_BUTTON:
                case WOOD_BUTTON:
                    cancelEvent(event, !allows(plugin.USE_BUTTON, location, player), true);
                    break;
                case JUKEBOX:
                    cancelEvent(event, !allows(plugin.USE_JUKEBOX, location, player), true);
                    break;
                case DIODE_BLOCK_OFF:
                case DIODE_BLOCK_ON:
                    cancelEvent(event, !allows(plugin.USE_REPEATER, location, player), true, true);
                    break;
                case TRAP_DOOR:
                    cancelEvent(event, !allows(plugin.USE_TRAP_DOOR, location, player), true);
                    break;
                case FENCE_GATE:
                    cancelEvent(event, !allows(plugin.USE_FENCE_GATE, location, player), true);
                    break;
                case BREWING_STAND:
                    cancelEvent(event, !allows(plugin.USE_BREWING_STAND, location, player), true);
                    break;
                case CAULDRON:
                    cancelEvent(event, !allows(plugin.USE_CAULDRON, location, player), true);
                    break;
                case ENCHANTMENT_TABLE:
                    cancelEvent(event, !allows(plugin.USE_ENCHANTMENT_TABLE, location, player), true);
                    break;
                case ENDER_CHEST:
                    cancelEvent(event, !allows(plugin.USE_ENDER_CHEST, location, player), true);
                    break;
                case BEACON:
                    cancelEvent(event, !allows(plugin.USE_BEACON, location, player), true);
                    break;
                case ANVIL:
                    cancelEvent(event, !allows(plugin.USE_ANVIL, location, player), true);
                    break;
                case REDSTONE_COMPARATOR_OFF:
                case REDSTONE_COMPARATOR_ON:
                    cancelEvent(event, !allows(plugin.USE_COMPARATOR, location, player), true, true);
                    break;
                case HOPPER:
                    cancelEvent(event, !allows(plugin.USE_HOPPER, location, player), true);
                    break;
                case DROPPER:
                    cancelEvent(event, !allows(plugin.USE_DROPPER, location, player), true);
                    break;
                default:
                }
            }
            if (event.getAction() == Action.PHYSICAL) {
                Material mat = event.getClickedBlock().getType();
                if (mat == Material.STONE_PLATE || mat == Material.WOOD_PLATE || mat == Material.GOLD_PLATE || mat == Material.IRON_PLATE) {
                    cancelEvent(event, !allows(plugin.USE_PRESSURE_PLATE, location, player), false);
                }
                else if (mat == Material.TRIPWIRE) {
                    cancelEvent(event, !allows(plugin.USE_TRIPWIRE, location, player), false);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/jumpto") || event.getMessage().equalsIgnoreCase("/j") || event.getMessage().equalsIgnoreCase("/thru")) {
            plugin.expectTeleport(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (plugin.hasCompassed(event.getPlayer())) {
            ApplicableRegionSet setAtLocation = worldguard.getGlobalRegionManager().get(event.getFrom().getWorld()).getApplicableRegions(event.getFrom());
            ApplicableRegionSet setAtTeleport = worldguard.getGlobalRegionManager().get(event.getTo().getWorld()).getApplicableRegions(event.getTo());
            LocalPlayer player = worldguard.wrapPlayer(event.getPlayer());
            if (!worldguard.getGlobalRegionManager().hasBypass(player, event.getPlayer().getWorld()) && (!setAtLocation.canBuild(player) && !setAtLocation.allows(plugin.COMPASS, player) || !setAtTeleport.canBuild(player) && !setAtTeleport.allows(plugin.COMPASS, player))) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to use that in this area.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        ApplicableRegionSet setAtLocation = worldguard.getRegionContainer().get(event.getFrom().getWorld()).getApplicableRegions(event.getFrom());
        if (event.getCause() == TeleportCause.END_PORTAL) {
            if (setAtLocation.queryState(null, plugin.END_PORTAL) == State.DENY) {
                event.setCancelled(true);
            }
        } else if (event.getCause() == TeleportCause.NETHER_PORTAL) {
            if (setAtLocation.queryState(null, plugin.NETHER_PORTAL) == State.DENY) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntityType() == EntityType.SNOWBALL) {
            Block b = event.getEntity().getLocation().getBlock();
            if (b.getType() == Material.FIRE) {
                ApplicableRegionSet setAtLocation = worldguard.getGlobalRegionManager().get(b.getWorld()).getApplicableRegions(b.getLocation());
                if (setAtLocation.allows(plugin.SNOWBALL_FIREFIGHT)) {
                    b.setType(Material.AIR);
                    b.getWorld().playEffect(b.getLocation(), Effect.EXTINGUISH, 0);
                }
            }
        }
    }

    private boolean allows(StateFlag flag, Location location, LocalPlayer player) {
        ApplicableRegionSet set = worldguard.getGlobalRegionManager().get(location.getWorld()).getApplicableRegions(location);
        return worldguard.getGlobalRegionManager().hasBypass(player, location.getWorld()) || set.canBuild(player) || set.allows(flag, player);
    }

    private void cancelEvent(PlayerInteractEvent e, boolean cancel, boolean notifyPlayer) {
        cancelEvent(e, cancel, notifyPlayer, false);
    }

    // Override for repeaters and comparators, since WG cancels these without
    // any checks.
    private void cancelEvent(PlayerInteractEvent e, boolean cancel, boolean notifyPlayer, boolean override) {
        e.setCancelled(cancel);
        if (e.isCancelled() && notifyPlayer) {
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to use that in this area.");
        }
    }
}
