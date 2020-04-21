package me.infinityz.hangar;

import java.util.HashMap;
import java.util.Map;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import fr.mrmicky.fastboard.FastBoard;
import me.infinityz.hangar.listeners.GlobalListeners;
import net.md_5.bungee.api.ChatColor;

public class Hangar extends JavaPlugin implements PluginMessageListener{
    public Map<String, FastBoard> scoreboard;
    int bungee_players = 0;

    @Override
    public void onEnable() {
        scoreboard = new HashMap<>();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
      
        Bukkit.getPluginManager().registerEvents(new GlobalListeners(this), this);
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            Bukkit.getOnlinePlayers().stream().filter(it -> it.hasPermission("vip.perm")).forEach(it->{
                ItemStack ie = it.getInventory().getItemInOffHand();
                if(ie == null || ie.getType() == Material.AIR){
                    {
                        ItemStack oi = new ItemStack(Material.FIREWORK_ROCKET, 16);
                        ItemMeta meta = oi.getItemMeta();
                        meta.setDisplayName(colorize("&6VIP Rocket"));
                        meta.setUnbreakable(true);
                        oi.setItemMeta(meta);
                        it.getInventory().setItemInOffHand(oi);
                    }
                }else if( ie.getAmount() < 16){
                    ie.setAmount(ie.getAmount()+1);
                }
            });
            scoreboard.values().forEach(it-> it.updateLines(colorize("&aZone: &fHangar"),
            "", colorize("&aPlayers: &f" + bungee_players), "", colorize("&bsurvival.rip")));
            try {
                getOnline();
            } catch (Exception e) {
            }
        }, 0, 10);
    }
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
      if (!channel.equals("BungeeCord")) {
        return;
      }
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      String subchannel = in.readUTF();
      if (subchannel.equalsIgnoreCase("PlayerCount")) {
          in.readUTF();
          bungee_players = in.readInt();
      }
      
    }

    void getOnline(){        
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF("ALL");
        Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(it->{
            it.sendPluginMessage(this, "BungeeCord", out.toByteArray());
        });
    }
    public String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}