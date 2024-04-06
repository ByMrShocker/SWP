package bymrshocker.swp.events;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.data.SWeapon;
import bymrshocker.swp.playerfuncs.UFunctionLibrary;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;


import java.util.Arrays;


public class eventPlayerClick implements Listener {

    private final ShockerWeaponsPlugin plugin;

    public eventPlayerClick(ShockerWeaponsPlugin plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void eventOnPlayerClick(PlayerInteractEvent event) {

        if (event.getAction().isRightClick()) OnPlayerRightClick(event);
        if (event.getAction().isLeftClick()) OnPlayerLeftClick(event);

        //System.out.println("sex");

    }

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event) {
        if (plugin.getFunctionLibrary().isWeapon(event.getPlayer().getInventory().getItemInMainHand())) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

            plugin.getFunctionLibrary().getSerialNumber(event.getPlayer());

            String state = plugin.getFunctionLibrary().getItemNBTString(item, "state");

            if (state == "fire" || state == "reload") {
                if (state == "fire") {
                    plugin.getFunctionLibrary().setItemNBTString(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer(), "state", "idle");
                }
                if (state == "reload") {
                    SWeapon sWeapon = new SWeapon();
                    sWeapon.playerLeftClick(event.getPlayer(), plugin);
                }
            }


        }
    }



    private void OnPlayerRightClick(PlayerInteractEvent event) {
       // Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"ВЛАДУСМАН ГЕЙ\",\"color\":\"light_purple\"}");



        if (plugin.getFunctionLibrary().isWeapon(event.getItem())) {
            SWeapon sWeapon = new SWeapon();
            sWeapon.playerRightClick(event, plugin);
        } else if (plugin.getFunctionLibrary().isMag(event.getItem())) {
            plugin.getFunctionLibrary().tryAddAmmo(event.getPlayer());
        }

    }

    private void OnPlayerLeftClick(PlayerInteractEvent event) {
      //  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"ВЛАДУСМАН ХУЙЛО\",\"color\":\"light_purple\"}");


        if (plugin.getFunctionLibrary().isWeapon(event.getItem())) {
            SWeapon sWeapon = new SWeapon();
            sWeapon.playerLeftClick(event.getPlayer(), plugin);
        }

    }





    //@EventHandler
    //public void eventOnShoot(EntityShootBowEvent event) {
    //    System.out.println("ShootEvent");
    //    ItemStack item = event.getBow();
    //    CrossbowMeta crossbowMeta = (CrossbowMeta) item.getItemMeta();
    //    System.out.println (crossbowMeta.getPersistentDataContainer().getKeys());
    //    ReadWriteNBT nbt = NBT.itemStackToNBT(item);
    //    nbt.getCompound("tag").setByte("Charged", Byte.valueOf("1"));
    //    //nbt.getCompound("tag").getCompound("tag").getCompoundList("ChargedProjectiles").get(0);
    //    //System.out.println(nbt.getCompound("tag").getItemStack("ChargedProjectiles"));
    //    nbt.getCompound("tag").getCompoundList("ChargedProjectiles").get(0).setString("id", "minecraft:air");
    //    System.out.println(nbt.getCompound("tag").getCompoundList("ChargedProjectiles").get(0));
    //    item.setItemMeta(crossbowMeta);
    //    Bukkit.getPlayer("ByMrShocker").getInventory().setItemInMainHand(item);
    //    new EntityShootBowEvent(event.getEntity(), event.getBow(), event.getProjectile(), )
    //}


}

