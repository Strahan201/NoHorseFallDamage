package com.sylvcraft.events;

import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.sylvcraft.NoHorseFallDamage;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {
  NoHorseFallDamage plugin;
  
  public EntityDamage(NoHorseFallDamage instance) {
    plugin = instance;
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent e) {
    if (!(e.getEntity() instanceof Horse)) return;
    
    Horse horse = (Horse)e.getEntity();
    if (!horse.isTamed() && !plugin.getConfig().getBoolean("config.protect-wild")) return;
    
    if (horse.isTamed() && horse.getPassengers().size() == 0 && !plugin.getConfig().getBoolean("config.protect-riderless")) return;
    
    Boolean defaultStatus = plugin.getConfig().getBoolean("config.defaultstatus", true);
    if (horse.getPassengers().size() > 0 && 
        !plugin.getConfig().getBoolean("players." + horse.getPassengers().get(0).getUniqueId().toString() + ".enabled", defaultStatus)) return;

    e.setCancelled(true);
  }
}