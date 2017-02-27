package me.jake.anticheat;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AntiFastEat
  implements Listener
{
  MainAC MainAC;
  
  public void setup(MainAC _MainAC)
  {
    this.MainAC = _MainAC;
  }
  
  public ArrayList<Player> eating = new ArrayList();
  public HashMap<Player, Integer> warnings = new HashMap();
  
  @EventHandler
  public void eatItem(PlayerItemConsumeEvent e)
  {
    Player p = e.getPlayer();
    if (this.eating.contains(p))
    {
      e.setCancelled(true);
      if (!this.AC.contains(p))
      {
        if (!this.warnings.containsKey(p))
        {
          this.warnings.put(p, Integer.valueOf(1));
        }
        else
        {
          int curWarnings = ((Integer)this.warnings.get(p)).intValue();
          this.warnings.remove(p);
          this.warnings.put(p, Integer.valueOf(curWarnings + 1));
        }
        this.MainAC.addWarning(p, "Fast-Eat");
        p.sendMessage(this.MainAC.prefix + "Unusual movements detected...");
        this.AC.add(p);
        for (Player pl : Bukkit.getOnlinePlayers()) {
          if (pl.isOp()) {
            pl.sendMessage(this.MainAC.prefix + "Unusual movements detected from " + ChatColor.DARK_RED + 
              p.getName() + ChatColor.WHITE + ", possible fast eat hacks");
          }
        }
        resetAC(p);
      }
    }
  }
  
  @EventHandler
  public void rightClickItem(PlayerInteractEvent e)
  {
    Player p = e.getPlayer();
    if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK))
    {
      if (!this.eating.contains(p)) {
        this.eating.add(p);
      }
      checkEat(p);
    }
  }
  
  public void checkEat(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          if (AntiFastEat.this.eating.contains(p)) {
            AntiFastEat.this.eating.remove(p);
          }
        }
      }, 29L);
  }
  
  public ArrayList<Player> AC = new ArrayList();
  
  public void resetAC(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          AntiFastEat.this.AC.remove(p);
        }
      }, 120L);
  }
}
