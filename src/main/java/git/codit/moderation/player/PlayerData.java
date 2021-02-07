package git.codit.moderation.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import git.codit.moderation.Moderation;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerData {
	
	private Moderation plugin;
	private UUID playerID;
	private PlayerState playerState;
	private long lastTalked;
	private boolean vanished = false, isSlowed = false;
	private ItemStack[] items = new ItemStack[40];
	
	public PlayerData(UUID playerID) {
		this.plugin = Moderation.getInstance();
		this.playerID = playerID;
		this.playerState = PlayerState.LOADING;
	}
	
	public void setModItems() {
		Player player = Bukkit.getPlayer(this.playerID);
		this.saveItems();
		player.getInventory().clear();
		player.getInventory().setContents(plugin.getItemManager().getModerationItems());
		player.updateInventory();
	}
	
	public void setVanished(boolean vanished) {
		Player player = Bukkit.getPlayer(this.playerID);
		if(vanished) {
			this.vanished = true;
			plugin.getServer().getOnlinePlayers().stream().filter(players -> !players.hasPermission("moderation.staff")).forEach(players -> players.hidePlayer(player));
		} else {
			this.vanished = false;
			plugin.getServer().getOnlinePlayers().forEach(players -> players.showPlayer(player));
		}
	}
	
	public void in() {
		playerState = PlayerState.MODERATOR;
		this.saveItems();
		setModItems();
		setVanished(true);
	}
	
	public void out() {
		playerState = PlayerState.PLAYING;
		this.giveItems();
		setVanished(false);
	}
	
	public void saveItems() {
		Player player = Bukkit.getPlayer(this.playerID);
		for(int i = 0; i < 36; i++) {
			ItemStack item = player.getInventory().getItem(i);
			if(item != null) {
				this.items[i] = item;
			}
		}
		
		this.items[36] = player.getInventory().getHelmet();
		this.items[37] = player.getInventory().getChestplate();
		this.items[38] = player.getInventory().getLeggings();
		this.items[39] = player.getInventory().getBoots();
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
	}
	
	public void giveItems() {
		Player player = Bukkit.getPlayer(this.playerID);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		for(int i = 0; i < 36; i++) {
			ItemStack item = this.items[i];
			if(item != null) {
				player.getInventory().setItem(i, item);
			}
		}
		player.getInventory().setHelmet(this.items[36]);
		player.getInventory().setChestplate(this.items[37]);
		player.getInventory().setLeggings(this.items[38]);
		player.getInventory().setBoots(this.items[39]);
		player.updateInventory();
		
	}
	
}
