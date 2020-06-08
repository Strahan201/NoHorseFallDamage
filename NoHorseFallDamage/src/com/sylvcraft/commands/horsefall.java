package com.sylvcraft.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import com.sylvcraft.NoHorseFallDamage;

public class horsefall implements TabExecutor {
  NoHorseFallDamage plugin;
  
  public horsefall(NoHorseFallDamage instance) {
    plugin = instance;
  }
  
  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    List<String> options = new ArrayList<>();
    if (args.length == 1) {
      options.add("status");
      if (sender.hasPermission("horsefall.toggle")) options.add("toggle");
      if (sender.hasPermission("horsefall.admin")) options.add("protect-wild");
      if (sender.hasPermission("horsefall.admin")) options.add("protect-riderless");
      return getMatchedAsType(args[0], options);
    }
    return null;
  }
  
  List<String> getMatchedAsType(String typed, List<String> values) {
    List<String> ret = new ArrayList<String>();
    for (String element : values) if (element.startsWith(typed)) ret.add(element);
    return ret;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      if (args.length == 0) {
        showHelp(sender);
        return true;
      }

      Map<String, String> data = new HashMap<String, String>();
      Player p = (sender instanceof Player)?(Player)sender:null;
      Boolean defaultStatus = plugin.getConfig().getBoolean("config.defaultstatus", true);
      
      switch (args[0].toLowerCase()) {
      case "status":
        if (sender.hasPermission("horsefall.toggle") && p != null) {
          data.put("%status%", plugin.getConfig().getBoolean("players." + p.getUniqueId().toString() + ".enabled", defaultStatus)?"enabled":"disabled");
          plugin.msg("toggle", sender, data);
        }
        
        if (sender.hasPermission("horsefall.admin")) {
          data.put("%status%", plugin.getConfig().getBoolean("config.protect-wild", defaultStatus)?"enabled":"disabled");
          plugin.msg("protect-wild", sender, data);
          data.put("%status%", plugin.getConfig().getBoolean("config.protect-riderless", defaultStatus)?"enabled":"disabled");
          plugin.msg("protect-riderless", sender, data);
        }
        break;
        
      case "toggle":
        if (!sender.hasPermission("horsefall.toggle")) {
          plugin.msg("access-denied", sender);
          return true;
        }
        
        if (!(sender instanceof Player)) {
          plugin.msg("player-only", null);
          return true;
        }

        plugin.getConfig().set("players." + p.getUniqueId().toString() + ".enabled", !plugin.getConfig().getBoolean("players." + p.getUniqueId().toString() + ".enabled", defaultStatus));
        plugin.saveConfig();
        data.put("%status%", plugin.getConfig().getBoolean("players." + p.getUniqueId().toString() + ".enabled")?"enabled":"disabled");
        plugin.msg("toggle", sender, data);
        break;
        
      case "protect-wild":
        if (!sender.hasPermission("horsefall.admin")) {
          plugin.msg("access-denied", sender);
          return true;
        }
        
        plugin.getConfig().set("config.protect-wild", !plugin.getConfig().getBoolean("config.protect-wild"));
        plugin.saveConfig();
        data.put("%status%", plugin.getConfig().getBoolean("config.protect-wild")?"enabled":"disabled");
        plugin.msg("protect-wild", sender, data);
        break;
        
      case "protect-riderless":
        if (!sender.hasPermission("horsefall.admin")) {
          plugin.msg("access-denied", sender);
          return true;
        }
        
        plugin.getConfig().set("config.protect-riderless", !plugin.getConfig().getBoolean("config.protect-riderless"));
        plugin.saveConfig();
        data.put("%status%", plugin.getConfig().getBoolean("config.protect-riderless")?"enabled":"disabled");
        plugin.msg("protect-riderless", sender, data);
        break;
      }

      return true;
    } catch (Exception ex) {
      return false;
    }
  }

	void showHelp(CommandSender sender) {
	  int displayed = 0;
	  if (sender.hasPermission("horsefall.toggle")) { plugin.msg("help-toggle", sender); displayed++; }
	  if (sender.hasPermission("horsefall.admin")) { plugin.msg("help-protectwild", sender); displayed++; }
	  if (sender.hasPermission("horsefall.admin")) { plugin.msg("help-protectriderless", sender); displayed++; }
	  if (displayed == 0) plugin.msg("access-denied", sender);
  }
}
