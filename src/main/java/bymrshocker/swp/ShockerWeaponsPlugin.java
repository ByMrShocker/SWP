package bymrshocker.swp;

import bymrshocker.swp.commands.swdCommands;
import bymrshocker.swp.data.UCache;
import bymrshocker.swp.events.eventJoin;
import bymrshocker.swp.events.eventPlayerClick;
import bymrshocker.swp.playerfuncs.UFunctionLibrary;
import bymrshocker.swp.schedulers.schedulerMain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

//ходят слухи что где-то тут дуралей наговнокодил.

public final class ShockerWeaponsPlugin extends JavaPlugin {
    private UCache Cache;
    private UFunctionLibrary FunctionLibrary;

    private swdCommands swdCommands;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.FunctionLibrary = new UFunctionLibrary(this);
        new schedulerMain(this);
        this.Cache = new UCache(this);
        //Bukkit.getPluginManager().registerEvents(new eventJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new eventPlayerClick(this), this);
        this.saveDefaultConfig();
        swdCommands = new swdCommands(this);
        getCommand("swd").setExecutor(swdCommands);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public UCache getCache() {
        return Cache;
    }

    public UFunctionLibrary getFunctionLibrary() { return FunctionLibrary; }

    private void generateDefaultConfig() {
        FileConfiguration config = getConfig();
        config.addDefault("test", true);
        config.options().copyDefaults(true);
        saveDefaultConfig();
    }


    public String getConfigWeaponString(String weaponID, String key) {

        return this.getConfig().getConfigurationSection("weapons").getConfigurationSection(weaponID).getString(key);

    }

    public int getConfigWeaponInt(String weaponID, String key) {

        return this.getConfig().getConfigurationSection("weapons").getConfigurationSection(weaponID).getInt(key);

    }

    public String getConfigMagString(String magID, String key) {

        return this.getConfig().getConfigurationSection("mag").getConfigurationSection(magID).getString(key);

    }

    public int getConfigMagInt(String magID, String key) {

        return this.getConfig().getConfigurationSection("mag").getConfigurationSection(magID).getInt(key);

    }



}

