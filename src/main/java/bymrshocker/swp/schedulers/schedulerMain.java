package bymrshocker.swp.schedulers;

import bymrshocker.swp.NbtTest;
import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.data.UCache;
import bymrshocker.swp.data.UPlayer;
import bymrshocker.swp.playerfuncs.playerfuncs;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.TimeUnit;

public class schedulerMain {
    private final UCache cache;

    private final ShockerWeaponsPlugin plugin;
    private final AsyncScheduler asyncScheduler;
    private final AsyncScheduler aSchedulerWeapons;
    private final NbtTest nbtTest;
    public schedulerMain(ShockerWeaponsPlugin plugin) {
        this.plugin = plugin;
        this.asyncScheduler = Bukkit.getAsyncScheduler();
        this.nbtTest = new NbtTest();
        this.cache = plugin.getCache();
        this.aSchedulerWeapons = Bukkit.getAsyncScheduler();

        startScheduler();
    }
    private void startScheduler() {
        asyncScheduler.runAtFixedRate(plugin, scheduledTask -> {
            checkOnlinePlayers();
        }, 0l, 1l, TimeUnit.SECONDS);


        aSchedulerWeapons.runAtFixedRate(plugin, scheduledTask -> {
            checkUseWeaponList();
        }, 0l, 50l, TimeUnit.MILLISECONDS);
    }

    private void checkOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String nickname = player.getName();
            if (cache == null) continue;
            UPlayer UPlayer = cache.getPlayer(nickname);
            if (UPlayer == null) continue;

            playerfuncs.playerfuncsMain(UPlayer);
        }
    }

    private void checkUseWeaponList() {



    }
}