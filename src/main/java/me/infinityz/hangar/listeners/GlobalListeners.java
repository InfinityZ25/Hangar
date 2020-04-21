package me.infinityz.hangar.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import fr.mrmicky.fastboard.FastBoard;
import me.infinityz.hangar.Hangar;

public class GlobalListeners implements Listener {
    Hangar instance;

    public GlobalListeners(Hangar instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        final FastBoard fb = new FastBoard(e.getPlayer());

        fb.updateTitle(instance.colorize("&b&lSurvival Universe"));

        instance.scoreboard.put(e.getPlayer().getUniqueId().toString(), fb);
        final Location loc = Bukkit.getWorlds().get(1).getSpawnLocation();
        loc.setYaw(90f);
        loc.setPitch(10f);
        e.getPlayer().teleport(loc);
        e.getPlayer().setHealth(20.0);
        e.getPlayer().setFoodLevel(20);
        inventory(e.getPlayer());
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (!e.getPlayer().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                e.getPlayer().teleport(loc);
        }, 5);
        e.getPlayer().setSaturation(100000.9999F);
    }

    void inventory(Player p) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setArmorContents(null);
        {
            ItemStack it = new ItemStack(Material.GLASS);
            ItemMeta meta = it.getItemMeta();
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Astronaut's Helmet");
            meta.setUnbreakable(true);
            it.setItemMeta(meta);
            inv.setHelmet(it);
        }
        if (p.hasPermission("vip.perm")) {
            {
                ItemStack it = new ItemStack(Material.ELYTRA);
                ItemMeta meta = it.getItemMeta();
                meta.setDisplayName(instance.colorize("&6VIP Elytro"));
                meta.setUnbreakable(true);
                it.setItemMeta(meta);
                inv.setChestplate(it);
            }
            {
                ItemStack it = new ItemStack(Material.TRIDENT);
                ItemMeta meta = it.getItemMeta();
                meta.setDisplayName(instance.colorize("&6VIP Tridento"));
                meta.setUnbreakable(true);
                it.setItemMeta(meta);
                it.addEnchantment(Enchantment.LOYALTY, 3);
                inv.setItem(0, it);
                inv.setHeldItemSlot(0);
            }
            {
                ItemStack it = new ItemStack(Material.FIREWORK_ROCKET, 16);
                ItemMeta meta = it.getItemMeta();
                meta.setDisplayName(instance.colorize("&6VIP Rocket"));
                meta.setUnbreakable(true);
                it.setItemMeta(meta);
                inv.setItemInOffHand(it);
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");
        final FastBoard fb = instance.scoreboard.remove(e.getPlayer().getUniqueId().toString());
        if (fb != null)
            fb.delete();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(InventoryClickEvent e) {
        if (e.getSlotType() == SlotType.ARMOR || e.getSlotType() == SlotType.QUICKBAR)
            if (e.getSlot() == 39 || e.getSlot() == 40)
                e.setCancelled(true);
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getY() < 0 || e.getTo().getY() > 400 || Math.abs(e.getTo().getX()) > 400
                || Math.abs(e.getTo().getZ()) > 400) {
            final Location loc = Bukkit.getWorlds().get(1).getSpawnLocation();
            loc.setYaw(90f);
            loc.setPitch(10f);
            e.getPlayer().teleport(loc);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getPlayer().isOp())
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockPlaceEvent e) {
        if (e.getPlayer().isOp())
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(HangingBreakByEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!event.isCancelled() && event.getRightClicked() instanceof ItemFrame
                && !((ItemFrame) event.getRightClicked()).getItem().getType().equals(Material.AIR)) {
            event.setCancelled(true);
        }
    }

}