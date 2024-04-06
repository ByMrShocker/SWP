package bymrshocker.swp;

import bymrshocker.swp.commands.BaseCommandArg;
import bymrshocker.swp.commands.CommandsHandler;
import bymrshocker.swp.commands.swdCommands;
import bymrshocker.swp.data.UCache;
import bymrshocker.swp.events.eventJoin;
import bymrshocker.swp.events.eventPlayerClick;
import bymrshocker.swp.playerfuncs.UFunctionLibrary;
import bymrshocker.swp.schedulers.schedulerMain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

//ходят слухи что где-то тут дуралей наговнокодил.

public final class ShockerWeaponsPlugin extends JavaPlugin {
    private UCache Cache;
    private UFunctionLibrary FunctionLibrary;
    private CommandsHandler commandsHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.FunctionLibrary = new UFunctionLibrary(this);
        new schedulerMain(this);
        this.Cache = new UCache(this);
        //Bukkit.getPluginManager().registerEvents(new eventJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new eventPlayerClick(this), this);


        //configs
        loadCFGs();


        commandsHandler = new CommandsHandler(this);

        getCommand("swd").setExecutor(commandsHandler);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public UCache getCache() {
        return Cache;
    }

    public UFunctionLibrary getFunctionLibrary() { return FunctionLibrary; }


    private void loadCFGs() {
        this.saveDefaultConfig();

        //weapons config
        //weaponsCFG = new YamlConfiguration();
        //try {
        //    weaponsCFG.load(new File("weapons.yml"));
        //} catch (IOException | InvalidConfigurationException e) {
        //    e.printStackTrace();
        //}
//
        //ammoCFG = new YamlConfiguration();
        //try {
        //    ammoCFG.load(new File("ammo.yml"));
        //} catch (IOException | InvalidConfigurationException e) {
        //    e.printStackTrace();
        //}
//
        //magCFG = new YamlConfiguration();
        //try {
        //    magCFG.load(new File("mags.yml"));
        //} catch (IOException | InvalidConfigurationException e) {
        //    e.printStackTrace();
        //}

    }

    //public YamlConfiguration getAmmoCFG() {
    //    return ammoCFG;
    //}
    //public YamlConfiguration getMagCFG() {
    //    return magCFG;
    //}
    //public YamlConfiguration getWeaponsCFG() {
    //    return weaponsCFG;
    //}

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

