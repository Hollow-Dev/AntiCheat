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

public class AntiFly
  implements Listener
{
  MainAC MainAC;
  
  public void setup(MainAC _MainAC)
  {
    this.MainAC = _MainAC;
  }
  
  public ArrayList<Player> inAir = new ArrayList();
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
    if ((under.getType() == Material.AIR) && (under2.getType() == Material.AIR))
    {
      if (!this.inAir.contains(p))
      {
        this.inAir.add(p);
        if (!this.origin.containsKey(p)) {
          this.origin.put(p, loc);
        }
      }
      if ((((Location)this.origin.get(p)).distance(loc) >= 4.0D) && 
        (((Location)this.origin.get(p)).getY() <= loc.getY())) {
        kickFly(p);
      }
    }
    else if (this.inAir.contains(p))
    {
      this.inAir.remove(p);
      this.origin.remove(p);
    }
  }
  
  public ArrayList<Player> AC = new ArrayList();
  
  public void kickFly(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          if ((AntiFly.this.inAir.contains(p)) && 
            (!p.getAllowFlight()))
          {
            if (!AntiFly.this.AC.contains(p))
            {
              if (!AntiFly.this.warnings.containsKey(p))
              {
                AntiFly.this.warnings.put(p, Integer.valueOf(1));
              }
              else
              {
                int curWarnings = ((Integer)AntiFly.this.warnings.get(p)).intValue();
                AntiFly.this.warnings.remove(p);
                AntiFly.this.warnings.put(p, Integer.valueOf(curWarnings + 1));
              }
              AntiFly.this.MainAC.addWarning(p, "Fly");
              p.sendMessage(AntiFly.this.MainAC.prefix + "Unusual movements detected...");
              AntiFly.this.AC.add(p);
              for (Player pl : Bukkit.getOnlinePlayers()) {
                if (pl.isOp()) {
                  pl.sendMessage(AntiFly.this.MainAC.prefix + "Unusual movements detected from " + ChatColor.DARK_RED + 
                    p.getName() + ChatColor.WHITE + ", possible fly hacks");
                }
              }
              AntiFly.this.resetAC(p);
            }
            p.teleport((Location)AntiFly.this.origin.get(p));
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
          AntiFly.this.AC.remove(p);
        }
      }, 120L);
  }
}
