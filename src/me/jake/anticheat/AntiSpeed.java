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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class AntiSpeed
  implements Listener
{
  MainAC MainAC;
  
  public void setup(MainAC _MainAC)
  {
    this.MainAC = _MainAC;
  }
  
  public HashMap<Player, Location> origin = new HashMap();
  public HashMap<Player, Integer> warnings = new HashMap();
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e)
  {
    Player p = e.getPlayer();
    Location loc = p.getLocation();
    if (!this.origin.containsKey(p)) {
      this.origin.put(p, loc);
    }
    detectChange(p, loc);
  }
  
  public void detectChange(final Player p, final Location loc)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          if (!p.hasPotionEffect(PotionEffectType.SPEED)) {
            if (((Location)AntiSpeed.this.origin.get(p)).distance(loc) >= 2.0D)
            {
              if ((((Location)AntiSpeed.this.origin.get(p)).getY() >= loc.getY() - 1.0D) && (((Location)AntiSpeed.this.origin.get(p)).getY() <= loc.getY() + 1.0D)) {
                AntiSpeed.this.kickSpeed(p);
              }
            }
            else {
              AntiSpeed.this.origin.remove(p);
            }
          }
        }
      }, 5L);
  }
  
  public ArrayList<Player> AC = new ArrayList();
  
  public void kickSpeed(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          if (!p.getAllowFlight())
          {
            if (!AntiSpeed.this.AC.contains(p))
            {
              if (!AntiSpeed.this.warnings.containsKey(p))
              {
                AntiSpeed.this.warnings.put(p, Integer.valueOf(1));
              }
              else
              {
                int curWarnings = ((Integer)AntiSpeed.this.warnings.get(p)).intValue();
                AntiSpeed.this.warnings.remove(p);
                AntiSpeed.this.warnings.put(p, Integer.valueOf(curWarnings + 1));
              }
              AntiSpeed.this.MainAC.addWarning(p, "Speed");
              p.sendMessage(AntiSpeed.this.MainAC.prefix + "Unusual movements detected...");
              AntiSpeed.this.AC.add(p);
              for (Player pl : Bukkit.getOnlinePlayers()) {
                if (pl.isOp()) {
                  pl.sendMessage(AntiSpeed.this.MainAC.prefix + "Unusual movements detected from " + ChatColor.DARK_RED + 
                    p.getName() + ChatColor.WHITE + ", possible speed hacks");
                }
              }
              AntiSpeed.this.resetAC(p);
            }
            p.teleport((Location)AntiSpeed.this.origin.get(p));
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
          AntiSpeed.this.AC.remove(p);
        }
      }, 120L);
  }
}
