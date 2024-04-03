package bymrshocker.swp.commands;

import bymrshocker.swp.ShockerWeaponsPlugin;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class swdCommands implements CommandExecutor {

    final public ShockerWeaponsPlugin plugin;

    public swdCommands(ShockerWeaponsPlugin pluginInstance) {
        this.plugin = pluginInstance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (command.getName().equalsIgnoreCase("swd")) {
                selectCommand(commandSender, command, s, strings);
            }

        }
        return true;
    }


    private void selectCommand(CommandSender sender, Command command, String s, String[] strings) {

        boolean result = false;

        if (strings.length == 0) return;

        System.out.println(strings.length);

        switch (strings[0]) {
            case "mag": result = givemag(sender, strings);
            case "weapon": result = giveweapon(sender, strings);
        }
    }

    private boolean givemag(CommandSender sender, String[] args){

        //if (args.length != 2) {
        //    String id = plugin.getConfig().getConfigurationSection("mag").getString(args[1]);
        //    //plugin.getConfigMagString(args[1], "name")
        //}
        return false;
    }

    private boolean giveweapon(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Set<String> keys = plugin.getConfig().getConfigurationSection("weapons").getKeys(false);

            if (!keys.contains(args[1])) return false;

            System.out.println(!keys.contains(args[1]));
            System.out.println(args[1]);

            if (true) return false;

            String id = plugin.getConfig().getConfigurationSection("weapons").getString(args[1]);

            Material material;
            String loadedMaterial = plugin.getConfig().getConfigurationSection("Global").getString("weaponMaterial");
            material = Material.valueOf(loadedMaterial);
            ItemStack itemToGive = new ItemStack(material);

            ReadWriteNBT nbt = NBT.itemStackToNBT(itemToGive);
            nbt.getOrCreateCompound("tag").getOrCreateCompound("ShockerWeapons");
            nbt.getCompound("tag").getCompound("ShockerWeapons").setString("magID", id);
            nbt.getCompound("tag").getCompound("ShockerWeapons").setString("durability", plugin.getConfig().getConfigurationSection("weapons").getConfigurationSection(id).getString("durability"));
            nbt.getCompound("tag").getCompound("ShockerWeapons").setString("state", "idle");

            itemToGive = NBT.itemStackFromNBT(nbt);

            ItemMeta meta = itemToGive.getItemMeta();

            meta.displayName(LegacyComponentSerializer.legacy('&').deserialize(plugin.getConfig().getConfigurationSection("weapons").getConfigurationSection(id).getString("name")).decoration(TextDecoration.ITALIC, false));
            List<String> loreStrings = plugin.getConfig().getConfigurationSection("Global").getStringList("lore");
            meta = plugin.getFunctionLibrary().setItemLore(meta, loreStrings, itemToGive);

            itemToGive.setItemMeta(meta);

            return true;


        }
        return false;
    }




}
