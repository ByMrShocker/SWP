package bymrshocker.swp.commands;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.commands.args.swd_give;
import bymrshocker.swp.commands.args.swd_list;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandsHandler implements TabExecutor {

    private final ArrayList<BaseCommandArg> argCommands = new ArrayList<>();
    private final ShockerWeaponsPlugin plugin;

    public CommandsHandler(ShockerWeaponsPlugin plugin){
        //список говна
        argCommands.add(new swd_give());
        argCommands.add(new swd_list());
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {


        if (args.length > 0) {
            for (BaseCommandArg argCommand : argCommands) {
                if (args[0].equalsIgnoreCase(argCommand.getName())) {
                    if (commandSender instanceof Player)
                        argCommand.execute(plugin, (Player) commandSender, args);
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length <= 1) {
            List<String> list = argCommands.stream().map(BaseCommandArg::getName).filter(name -> name.startsWith(strings[0])).toList();
            return list;
        }

        return new ArrayList<>();
    }
}
