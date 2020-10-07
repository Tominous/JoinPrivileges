package me.omegaweapondev.joinprivileges.commands;

import me.omegaweapondev.joinprivileges.JoinPrivileges;
import me.omegaweapondev.joinprivileges.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.GlobalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CoreCommand extends GlobalCommand {
  private final JoinPrivileges plugin = JoinPrivileges.getInstance();
  private final MessageHandler messageHandler = new MessageHandler(plugin.getMessagesFile().getConfig());

  @Override
  protected void execute(final CommandSender sender, final String[] strings) {

    if(strings.length == 0) {
      invalidArgsCommand(sender);
      return;
    }

    switch(strings[0]) {
      case "version":
        versionCommand(sender);
        break;
      case "reload":
        reloadCommand(sender);
        break;
      case "debug":
        debugCommand(sender);
      default:
        invalidArgsCommand(sender);
    }
  }

  private void debugCommand(final CommandSender sender) {

    if(!(sender instanceof Player)) {
      Utilities.logInfo(true,
        "===========================================",
        "JoinPrivileges v" + plugin.getDescription().getVersion() + " By OmegaWeaponDev",
        "Server Brand: " + Bukkit.getName(),
        "Server Version: " + Bukkit.getServer().getVersion(),
        "JoinPrivileges Commands: " + Utilities.setCommand().size(),
        "Currently Installed Plugins...",
        " " + generatePluginList(),
        "==========================================="
      );
      return;
    }

    Player player = (Player) sender;

    if(!Utilities.checkPermissions(player, true, "joinprivileges.debug", "joinprivileges.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to use this command."));
      return;
    }

    Utilities.message(player,
      "&b===========================================",
      " &bJoinPrivileges &cv" + plugin.getDescription().getVersion() + " &bBy OmegaWeaponDev",
      "&b===========================================",
      " &bServer Brand: &c" + Bukkit.getName(),
      " &bServer Version: &c" + Bukkit.getServer().getVersion(),
      " &bJoinPrivileges Commands: &c" + Utilities.setCommand().size(),
      " &bCurrently Installed Plugins...",
      " " + generatePluginList(),
      "&b==========================================="
    );
  }

  private void versionCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      final Player player = (Player) sender;
      Utilities.message(player, messageHandler.getPrefix() + "&3JoinPrivileges &cv" + plugin.getDescription().getVersion() + " &3By OmegaWeaponDev");
      return;
    }

    Utilities.logInfo(true, "JoinPrivileges v" + plugin.getDescription().getVersion() + " By OmegaWeaponDev");
  }

  private void reloadCommand(final CommandSender sender) {

    if(!(sender instanceof Player)) {
      Utilities.logInfo(true, messageHandler.console("Plugin_Reload", "JoinPrivileges has successfully been reloaded"));
      plugin.onReload();
      return;
    }

    final Player player = (Player) sender;

    if(!Utilities.checkPermissions(player, true, "joinprivileges.reload", "joinprivileges.admin")) {
      Utilities.message(player, messageHandler.string( "No_Permission", "&cSorry, but you do not have permission to use this command."));
      return;
    }

    Utilities.message(player, messageHandler.string("Plugin_Reload", "&bJoinPrivileges has successfully been reloaded"));
    plugin.onReload();
  }

  private void invalidArgsCommand(final CommandSender sender) {
    if(!(sender instanceof Player)) {
      Utilities.logInfo(true,
        "JoinPrivileges v" + plugin.getDescription().getVersion() + " By OmegaWeaponDev",
        "Reload Command: /joinprivileges reload",
        "Version Command: /joinprivileges version"
      );
      return;
    }

    final Player player = (Player) sender;

    Utilities.message(player,
      messageHandler.getPrefix() + "&3JoinPrivileges &cv" + plugin.getDescription().getVersion() + " &3By OmegaWeaponDev",
      messageHandler.getPrefix() + "&bReload Command: &c/joinprivileges reload",
      messageHandler.getPrefix() + "&bVersion Command: &c/joinprivileges version"
    );
  }

  private String generatePluginList() {
    StringBuilder plugins = new StringBuilder();

    for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      plugins.append("&c").append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append("&b, ");
    }

    return plugins.toString();

  }
}
