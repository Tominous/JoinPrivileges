package me.omegaweapondev.joinprivileges.events;

import me.omegaweapondev.joinprivileges.JoinPrivileges;
import me.omegaweapondev.joinprivileges.utilities.JoinLeaveUtil;
import me.omegaweapondev.joinprivileges.utilities.SettingsHandler;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
  private final SettingsHandler settingsHandler = new SettingsHandler(JoinPrivileges.getInstance().getConfigFile().getConfig());

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    final Player player = playerJoinEvent.getPlayer();
    playerJoinEvent.setJoinMessage(null);

    if(!Utilities.checkPermissions(player, true, "joinprivileges.join.silent", "joinprivileges.admin")) {
      playerJoin(player);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    final Player player = playerQuitEvent.getPlayer();
    playerQuitEvent.setQuitMessage(null);

    if(!Utilities.checkPermissions(player, true, "joinprivileges.quit.silent", "joinprivileges.admin")) {
      playerQuit(player);
    }
  }

  private void playerJoin(final Player player) {
    final JoinLeaveUtil joinLeaveUtil = new JoinLeaveUtil(player);

    if(settingsHandler.getBoolean("Join_Effects.Enabled")) {
      joinEffects(player);
    }

    if(!settingsHandler.getBoolean("Join_Settings.Enabled")) {
      return;
    }

    if(settingsHandler.getBoolean("Join_Settings.Join_Commands")) {
      joinLeaveUtil.commandExecutor(settingsHandler.getStringList("Join_Settings.Join_Commands.Commands"));
    }

    if(!settingsHandler.getBoolean("Join_Settings.Group_Join_Settings.Enabled")) {
      Utilities.broadcast(joinLeaveUtil.defaultJoinMessage());
      return;
    }

    for(String groupName : settingsHandler.getSection("Join_Settings.Group_Join_Settings.Groups").getKeys(false)) {
      if(Utilities.checkPermission(player, false, "joinprivileges.join.groups." + groupName)) {
        joinLeaveUtil.playerMessage("Join_Settings.Group_Join_Settings.Groups." + groupName + ".Join_Message");

        if(settingsHandler.getBoolean("Join_Settings.Group_Join_Settings.Groups." + groupName + ".Group_Commands.Enabled")) {
          joinLeaveUtil.commandExecutor(settingsHandler.getStringList("Join_Settings.Group_Join_Settings.Groups." + groupName + ".Group_Commands.Commands"));
          return;
        }
        return;
      }
    }

    Utilities.broadcast(joinLeaveUtil.defaultJoinMessage());
  }

  private void playerQuit(final Player player) {
    final JoinLeaveUtil joinLeaveUtil = new JoinLeaveUtil(player);

    if(!settingsHandler.getBoolean("Quit_Settings.Enabled")) {
      return;
    }

    if(settingsHandler.getBoolean("Quit_Settings.Quit_Commands")) {
      joinLeaveUtil.commandExecutor(settingsHandler.getStringList("Quit_Settings.Quit_Commands.Commands"));
    }

    if(!settingsHandler.getBoolean("Quit_Settings.Group_Quit_Settings.Enabled")) {
      Utilities.broadcast(joinLeaveUtil.defaultQuitMessage());
      return;
    }

    for(String groupName : settingsHandler.getSection("Quit_Settings.Group_Quit_Settings.Groups").getKeys(false)) {
      if(Utilities.checkPermission(player, false, "joinprivileges.quit.groups." + groupName)) {
        joinLeaveUtil.playerMessage("Quit_Settings.Group_Quit_Settings.Groups." + groupName + ".Quit_Message");

        if(settingsHandler.getBoolean("Quit_Settings.Group_Quit_Settings.Groups." + groupName + ".Group_Commands.Enabled")) {
          joinLeaveUtil.commandExecutor(settingsHandler.getStringList("Quit_Settings.Group_Quit_Settings.Groups." + groupName + ".Group_Commands.Commands"));
          return;
        }
        return;
      }
    }

    Utilities.broadcast(joinLeaveUtil.defaultQuitMessage());
  }

  private void joinEffects(final Player player) {

    if(Utilities.checkPermissions(player, true, "joinprivileges.joineffects.bypass", "joinprivileges.admin")) {
      return;
    }

    player.playSound(player.getLocation(), Sound.valueOf(settingsHandler.getString("Join_Effects.Sound")), 1 , 1);
    new BukkitRunnable() {
      @Override
      public void run() {
        player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(), settingsHandler.getInt("Join_Effects.Particle_Amount"));
      }
    }.runTaskLaterAsynchronously(JoinPrivileges.getInstance(), 20);

  }

  private void spigotUpdateNotify(final Player player) {
    if(!settingsHandler.getBoolean("Update_Notify")) {
      return;
    }

    if(!Utilities.checkPermissions(player, true, "joinprivileges.update", "joinprivileges.admin")) {
      return;
    }

    new SpigotUpdater(JoinPrivileges.getInstance(), 74788).getVersion(version -> {
      if(JoinPrivileges.getInstance().getDescription().getVersion().equalsIgnoreCase(version)) {
        Utilities.logInfo(true, "You are already running the latest version");
        return;
      }

      PluginDescriptionFile pdf = JoinPrivileges.getInstance().getDescription();
      Utilities.logWarning(true,
        "A new version of " + pdf.getName() + " is available!",
        "Current Version: " + pdf.getVersion() + " > New Version: " + version,
        "Grab it here: https://spigotmc.org/resources/74788"
      );
    });
  }

}
