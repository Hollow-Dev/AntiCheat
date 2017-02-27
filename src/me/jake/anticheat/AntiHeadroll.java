package me.jake.anticheat;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AntiHeadroll
  implements Listener
{
  MainAC MainAC;
  
  public void setup(MainAC _MainAC)
  {
    this.MainAC = _MainAC;
  }
  
  public HashMap<Player, Integer> warnings = new HashMap();
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e)
  {
    Player p = e.getPlayer();
    Location loc = p.getLocation();
    boolean b = false;
    if (loc.getPitch() > 90.0F)
    {
      p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 90.0F));
      b = true;
    }
    else if (loc.getPitch() < -90.0F)
    {
      p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), -90.0F));
      b = true;
    }
    if ((b) && 
      (!this.AC.contains(p)))
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
      this.MainAC.addWarning(p, "Headroll");
      p.sendMessage(this.MainAC.prefix + "Unusual movements detected...");
      this.AC.add(p);
      for (Player pl : Bukkit.getOnlinePlayers()) {
        if (pl.isOp()) {
          pl.sendMessage(this.MainAC.prefix + "Unusual movements detected from " + ChatColor.DARK_RED + 
            p.getName() + ChatColor.WHITE + ", possible headroll hacks");
        }
      }
      resetAC(p);
    }
  }
  
  public ArrayList<Player> AC = new ArrayList();
  
  public void resetAC(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          AntiHeadroll.this.AC.remove(p);
        }
      }, 120L);
  }
}
