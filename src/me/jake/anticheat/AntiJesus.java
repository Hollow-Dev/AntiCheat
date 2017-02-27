package me.jake.anticheat;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AntiJesus
  implements Listener
{
  MainAC MainAC;
  
  public void setup(MainAC _MainAC)
  {
    this.MainAC = _MainAC;
  }
  
  public ArrayList<Player> onWater = new ArrayList();
  public ArrayList<Player> offWater = new ArrayList();
  public HashMap<Player, Location> origin = new HashMap();
  public HashMap<Player, Integer> warnings = new HashMap();
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e)
  {
    Player p = e.getPlayer();
    Location loc = p.getLocation();
    World w = loc.getWorld();
    Block under = w.getBlockAt(new Location(w, loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()));
    Block under2 = w.getBlockAt(new Location(w, loc.getBlockX(), loc.getBlockY() - 2, loc.getBlockZ()));
    Block on = w.getBlockAt(new Location(w, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    if (((under.getType() == Material.STATIONARY_WATER) || (under.getType() == Material.WATER) || (under2.getType() == Material.STATIONARY_WATER) || (under2.getType() == Material.WATER)) && (on.getType() == Material.AIR))
    {
      if (!this.onWater.contains(p))
      {
        this.onWater.add(p);
        if (!this.origin.containsKey(p)) {
          this.origin.put(p, loc);
        }
        checkStillOnWater(p);
      }
    }
    else if (this.onWater.contains(p))
    {
      this.offWater.add(p);
      this.onWater.remove(p);
      this.origin.remove(p);
    }
    else
    {
      this.offWater.remove(p);
    }
  }
  
  public void checkStillOnWater(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          if ((AntiJesus.this.onWater.contains(p)) && (!AntiJesus.this.offWater.contains(p))) {
            AntiJesus.this.kickJesus(p);
          }
        }
      }, 15L);
  }
  
  public ArrayList<Player> AC = new ArrayList();
  
  public void kickJesus(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          if ((AntiJesus.this.onWater.contains(p)) && 
            (!p.getAllowFlight()))
          {
            if (!AntiJesus.this.AC.contains(p))
            {
              if (!AntiJesus.this.warnings.containsKey(p))
              {
                AntiJesus.this.warnings.put(p, Integer.valueOf(1));
              }
              else
              {
                int curWarnings = ((Integer)AntiJesus.this.warnings.get(p)).intValue();
                AntiJesus.this.warnings.remove(p);
                AntiJesus.this.warnings.put(p, Integer.valueOf(curWarnings + 1));
              }
              AntiJesus.this.MainAC.addWarning(p, "Jesus");
              p.sendMessage(AntiJesus.this.MainAC.prefix + "Unusual movements detected...");
              AntiJesus.this.AC.add(p);
              for (Player pl : Bukkit.getOnlinePlayers()) {
                if (pl.isOp()) {
                  pl.sendMessage(AntiJesus.this.MainAC.prefix + "Unusual movements detected from " + ChatColor.DARK_RED + 
                    p.getName() + ChatColor.WHITE + ", possible jesus hacks");
                }
              }
              AntiJesus.this.resetAC(p);
            }
            p.teleport((Location)AntiJesus.this.origin.get(p));
          }
        }
      }, 1L);
  }
  
  public void resetAC(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          AntiJesus.this.AC.remove(p);
        }
      }, 120L);
  }
}
