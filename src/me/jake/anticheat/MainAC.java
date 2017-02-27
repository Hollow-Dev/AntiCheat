package me.jake.anticheat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MainAC
  extends JavaPlugin
  implements Listener
{
  AntiFly AntiFly = new AntiFly();
  AntiSpeed AntiSpeed = new AntiSpeed();
  AntiFastEat AntiFastEat = new AntiFastEat();
  AntiReach AntiReach = new AntiReach();
  AntiHeadroll AntiHeadroll = new AntiHeadroll();
  AntiJesus AntiJesus = new AntiJesus();
  AntiFastBow AntiFastBow = new AntiFastBow();
  Config Config = new Config();
  public String prefix = ChatColor.DARK_RED + ChatColor.MAGIC.toString() + "|" + ChatColor.DARK_RED + "AntiCheat" + ChatColor.MAGIC + "|" + ChatColor.WHITE + " ";
  public HashMap<Player, Integer> totalWarnings = new HashMap();
  
  public void onEnable()
  {
    getServer().getPluginManager().registerEvents(this, this);
    getServer().getPluginManager().registerEvents(this.AntiFly, this);
    getServer().getPluginManager().registerEvents(this.AntiSpeed, this);
    getServer().getPluginManager().registerEvents(this.AntiFastEat, this);
    getServer().getPluginManager().registerEvents(this.AntiReach, this);
    getServer().getPluginManager().registerEvents(this.AntiHeadroll, this);
    getServer().getPluginManager().registerEvents(this.AntiJesus, this);
    getServer().getPluginManager().registerEvents(this.AntiFastBow, this);
    
    this.AntiFly.setup(this);
    this.AntiSpeed.setup(this);
    this.AntiFastEat.setup(this);
    this.AntiReach.setup(this);
    this.AntiHeadroll.setup(this);
    this.AntiJesus.setup(this);
    this.AntiFastBow.setup(this);
    
    files();
    this.Config.loadConfig();
    
    Bukkit.getConsoleSender().sendMessage("[AntiCheat] Has been enabled!");
    Bukkit.getConsoleSender().sendMessage("");
    Bukkit.getConsoleSender().sendMessage("[AntiCheat] Please report any bugs/ideas to Jake's skype: JakeRusso28");
    Bukkit.getConsoleSender().sendMessage("");
    Bukkit.getConsoleSender().sendMessage("[AntiCheat] Copyright - Jake Russo ");
    Bukkit.getConsoleSender().sendMessage("");
  }
  
  @EventHandler
  public void onPlayerLogin(PlayerLoginEvent e)
  {
    if (isBanned(e.getPlayer())) {
      e.disallow(null, this.prefix + "You are banned permenantly: " + ChatColor.DARK_RED + banReason(e.getPlayer()));
    }
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e)
  {
    Player p = e.getPlayer();
    if (inWatchlist(p))
    {
      int warns = watchlistWarns(p);
      for (Player ply : Bukkit.getOnlinePlayers()) {
        if (ply.isOp())
        {
          ply.sendMessage(this.prefix + ChatColor.DARK_RED + p.getName() + ChatColor.WHITE + " joined the game and is on the watchlist. Currently " + ChatColor.DARK_RED + warns + ChatColor.WHITE + " accounts of possible hacking");
          this.totalWarnings.put(p, Integer.valueOf(warns));
        }
      }
    }
  }
  
  public void addWarning(Player p, String reason)
  {
    if (!inWatchlist(p))
    {
      if (this.totalWarnings.containsKey(p))
      {
        int total = ((Integer)this.totalWarnings.get(p)).intValue();
        this.totalWarnings.remove(p);
        this.totalWarnings.put(p, Integer.valueOf(total + 1));
      }
      else
      {
        this.totalWarnings.put(p, Integer.valueOf(1));
      }
      if (((Integer)this.totalWarnings.get(p)).intValue() == this.Config.getWatchListWarns())
      {
        addWatchlist(p);
        for (Player ply : Bukkit.getOnlinePlayers()) {
          if (ply.isOp()) {
            ply.sendMessage(this.prefix + "The player " + ChatColor.DARK_RED + p.getName() + ChatColor.WHITE + " has had 5 accounts of possible hacks within the last 30 minutes. Adding to the watchlist");
          }
        }
      }
    }
    else
    {
      addWatchlistWarn(p);
      if ((watchlistWarns(p) >= this.Config.getBanWarns()) && (this.Config.getAutoBan()))
      {
        p.kickPlayer(this.prefix + "You are banned permenantly: " + ChatColor.DARK_RED + reason);
        addBan(p, reason);
      }
    }
  }
  
  public void files()
  {
    File Main = new File("plugins/AntiCheat");
    if (!Main.exists()) {
      try
      {
        Main.mkdir();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    File watchlist = new File("plugins/AntiCheat/Watchlist.txt");
    if (!watchlist.exists()) {
      try
      {
        watchlist.createNewFile();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    File banned = new File("plugins/AntiCheat/Banned.txt");
    if (!banned.exists()) {
      try
      {
        banned.createNewFile();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
  
  public boolean inWatchlist(Player p)
  {
    if (getWatchlistText().contains(p.getUniqueId().toString())) {
      return true;
    }
    return false;
  }
  
  public int watchlistWarns(Player p)
  {
    String[] different = getWatchlistText().split(" ");
    String[] arrayOfString1;
    int j = (arrayOfString1 = different).length;
    for (int i = 0; i < j; i++)
    {
      String s = arrayOfString1[i];
      if (s.contains(p.getUniqueId().toString())) {
        return Integer.valueOf(s.split(":")[1]).intValue();
      }
    }
    return 0;
  }
  
  public void addWatchlist(Player p)
  {
    try
    {
      FileWriter fw = new FileWriter("plugins/AntiCheat/Watchlist.txt");
      fw.write(getWatchlistText() + p.getUniqueId() + ":" + this.totalWarnings.get(p) + " ");
      fw.flush();
      fw.close();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public void addWatchlistWarn(Player p)
  {
    int warns = watchlistWarns(p) + 1;
    try
    {
      FileWriter fw = new FileWriter("plugins/AntiCheat/Watchlist.txt");
      fw.write(getWatchlistText() + p.getUniqueId() + ":" + warns + " ");
      fw.flush();
      fw.close();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public boolean isBanned(Player p)
  {
    if (getBannedText().contains(p.getUniqueId().toString())) {
      return true;
    }
    return false;
  }
  
  public String banReason(Player p)
  {
    String[] different = getBannedText().split(" ");
    String[] arrayOfString1;
    int j = (arrayOfString1 = different).length;
    for (int i = 0; i < j; i++)
    {
      String s = arrayOfString1[i];
      if (s.contains(p.getUniqueId().toString())) {
        return s.split(":")[1];
      }
    }
    return "";
  }
  
  public void addBan(Player p, String reason)
  {
    try
    {
      FileWriter fw = new FileWriter("plugins/AntiCheat/Banned.txt");
      fw.write(getBannedText() + p.getUniqueId() + ":" + reason + " ");
      fw.flush();
      fw.close();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public String getWatchlistText()
  {
    try
    {
      FileReader fr = new FileReader("plugins/AntiCheat/Watchlist.txt");
      BufferedReader br = new BufferedReader(fr);
      
      String line = br.readLine();
      br.close();
      if (line != null) {
        return line;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "";
  }
  
  public String getBannedText()
  {
    try
    {
      FileReader fr = new FileReader("plugins/AntiCheat/Banned.txt");
      BufferedReader br = new BufferedReader(fr);
      
      String line = br.readLine();
      br.close();
      if (line != null) {
        return line;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "";
  }
}
