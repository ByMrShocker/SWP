package bymrshocker.swp.commands;


import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.data.UPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public interface BaseCommandArg {

    HashMap<String, String[]> argListMap = new HashMap<>();

    String[] getTabList(String key);

    String getName();

    String getDescription();

    String getSyntax();

    void execute (ShockerWeaponsPlugin plugin, Player player, String[] args);

}
