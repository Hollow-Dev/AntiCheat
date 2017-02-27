package me.jake.anticheat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.plugin.java.JavaPlugin;

public class Config
{
  public void loadConfig()
  {
    MainAC.getProvidingPlugin(MainAC.class).getConfig().addDefault("WatchListWarns", Integer.valueOf(5));
    MainAC.getProvidingPlugin(MainAC.class).getConfig().addDefault("AutoBan", Boolean.valueOf(true));
    MainAC.getProvidingPlugin(MainAC.class).getConfig().addDefault("BanWarns", Integer.valueOf(8));
    MainAC.getProvidingPlugin(MainAC.class).getConfig().options().copyDefaults(true);
    MainAC.getProvidingPlugin(MainAC.class).saveConfig();
  }
  
  public int getWatchListWarns()
  {
    return MainAC.getProvidingPlugin(MainAC.class).getConfig().getInt("WatchListWarns");
  }
  
  public boolean getAutoBan()
  {
    return MainAC.getProvidingPlugin(MainAC.class).getConfig().getBoolean("AutoBan");
  }
  
  public int getBanWarns()
  {
    return MainAC.getProvidingPlugin(MainAC.class).getConfig().getInt("BanWarns");
  }
}
