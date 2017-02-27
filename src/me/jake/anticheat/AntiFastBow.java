package me.jake.anticheat;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AntiFastBow
  implements Listener
{
  MainAC MainAC;
  
  public void setup(MainAC _MainAC)
  {
    this.MainAC = _MainAC;
  }
  
  public ArrayList<Player> drawing = new ArrayList();
  public HashMap<Player, Integer> warnings = new HashMap();
  
  @EventHandler
  public void shootBow(EntityShootBowEvent e)
  {
    if ((e.getEntity() instanceof Player))
    {
      Player p = (Player)e.getEntity();
      if ((this.drawing.contains(p)) && (e.getForce() == 1.0D))
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
                p.getName() + ChatColor.WHITE + ", possible fast bow hacks");
            }
          }
          resetAC(p);
        }
      }
    }
  }
  
  @EventHandler
  public void rightClickItem(PlayerInteractEvent e)
  {
    Player p = e.getPlayer();
    if (((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) && 
      (e.getItem() != null) && (e.getItem().getType() == Material.BOW))
    {
      if (!this.drawing.contains(p)) {
        this.drawing.add(p);
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
          if (AntiFastBow.this.drawing.contains(p)) {
            AntiFastBow.this.drawing.remove(p);
          }
        }
      }, 19L);
  }
  
  public ArrayList<Player> AC = new ArrayList();
  
  public void resetAC(final Player p)
  {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(MainAC.class), 
      new Runnable()
      {
        public void run()
        {
          AntiFastBow.this.AC.remove(p);
        }
      }, 120L);
  }
}
