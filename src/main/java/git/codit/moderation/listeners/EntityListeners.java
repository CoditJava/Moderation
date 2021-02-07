package git.codit.moderation.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import git.codit.moderation.Moderation;
import git.codit.moderation.managers.inventories.FrozenInventory;
import git.codit.moderation.player.PlayerData;
import git.codit.moderation.player.PlayerState;

public class EntityListeners implements Listener {

	private Moderation plugin;
	
	public EntityListeners() {
		this.plugin = Moderation.getInstance();
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getEntity();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		ItemStack item = player.getItemInHand();
		if(item == null || !item.hasItemMeta() || item.getItemMeta().getDisplayName() == null) return;
		if(data == null) return;
		
		switch(data.getPlayerState()) {
		
		case MODERATOR:
			e.setCancelled(true);
			break;
			
			default:
				break;
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player)) return;
		if(!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getDamager();
		Player victim = (Player) e.getEntity();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		PlayerData victimData = plugin.getPlayerManager().getPlayerData(victim);
		ItemStack item = player.getItemInHand();
		if(item == null || !item.hasItemMeta() || item.getItemMeta().getDisplayName() == null) return;
		if(data == null) return;
		
		switch(data.getPlayerState()) {
		
		case MODERATOR:
			e.setCancelled(item.getType() != Material.STICK || item.getType() == Material.AIR);
			break;
		case PLAYING:
			e.setCancelled(victimData.getPlayerState() != PlayerState.PLAYING);
			default:
				break;
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		e.setCancelled(data.getPlayerState() != PlayerState.PLAYING);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		
		if(data != null) {
			switch(data.getPlayerState()) {
			
			case FROZEN:
				new BukkitRunnable() {
					
					@Override
					public void run() {
						player.openInventory(FrozenInventory.getFrozenInventory());
					}
				}.runTaskLater(plugin, 20L);
				break;
			
				default:
					break;
			}
		}
		
	}
	
	
}
