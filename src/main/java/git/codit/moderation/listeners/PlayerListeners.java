package git.codit.moderation.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import git.codit.moderation.Moderation;
import git.codit.moderation.player.PlayerData;
import git.codit.moderation.player.PlayerState;
import git.codit.moderation.utils.CC;
import git.codit.moderation.utils.ItemUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerListeners implements Listener {
	
	private Moderation plugin;
	
	public PlayerListeners() {
		this.plugin = Moderation.getInstance();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		plugin.getPlayerManager().createData(player);
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		data.setPlayerState(PlayerState.PLAYING);
		plugin.getServer().getOnlinePlayers().forEach(players -> {
			PlayerData datas = plugin.getPlayerManager().getPlayerData(players);
			if(datas.isVanished()) {
				Player vPlayers = Bukkit.getPlayer(datas.getPlayerID());
				player.hidePlayer(vPlayers);
			}
		});
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		if(data.getPlayerState() == PlayerState.FROZEN) {
			plugin.getServer().getOnlinePlayers().stream().filter(players -> players.hasPermission("moderation.staff")).forEach(players -> {
				players.sendMessage(CC.DARK_GRAY + "[" + CC.DARK_RED + "!" + CC.DARK_GRAY + "] " + CC.SECONDARY + player.getName() + CC.PRIMARY + " has left the server while he was frozen!");
				TextComponent clickToBan = new TextComponent(CC.RED + "[Click to ban]");
				clickToBan.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.I_GRAY + "Click to ban " + player.getName()).create()));
				clickToBan.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ban " + player.getName() + " 30 days Disconnected while frozen."));
				players.spigot().sendMessage(clickToBan);
			});
		}
		plugin.getPlayerManager().destroyData(player);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getPlayer().getItemInHand();
		if(item == null || !item.hasItemMeta() || item.getItemMeta().getDisplayName() == null) return;
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) return;
		
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		
		if(data == null) return;
		
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			switch(data.getPlayerState()) {
			
			case MODERATOR:
				e.setCancelled(true);
				switch(item.getType()) {
				
				case INK_SACK:
					data.setVanished(!data.isVanished());
					player.getInventory().setItem(0, ItemUtil.vanishItem(CC.PRIMARY + "Vanish " + CC.GRAY + "[" + (data.isVanished() ? CC.GREEN + "ON" : CC.RED + "OFF") + CC.GRAY + "]", data.isVanished()));
					player.updateInventory();
					player.sendMessage(CC.PRIMARY + "Your vanish has been " + CC.SECONDARY + (data.isVanished() ? "enabled" : "disabled"));
					break;
				case EYE_OF_ENDER:
					List<Player> onlinePlayers = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
					onlinePlayers.remove(player);
					if(onlinePlayers.size() == 0) {
						player.sendMessage(CC.RED + "There's no player to teleport onto.");
						return;
					}
					Player target = Bukkit.getPlayer(onlinePlayers.get(new Random().nextInt(onlinePlayers.size())).getUniqueId());
					player.teleport(target);
					player.sendMessage(CC.PRIMARY + "You've been teleported on " + CC.SECONDARY + target.getName());
					break;
					default:
						break;
				}
				
				break;
			
			
				default:
					break;
			}
		}
		
	}
	
	@EventHandler
	public void onPlayerInteractWithEntity(PlayerInteractAtEntityEvent e) {
		Player player = e.getPlayer();
		if(!(e.getRightClicked() instanceof Player)) return;
		Player target = (Player) e.getRightClicked();
		ItemStack item = e.getPlayer().getItemInHand();
		
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
		PlayerData targetData = plugin.getPlayerManager().getPlayerData(target);
		
		PlayerState targetState = targetData.getPlayerState();
		
		switch(playerData.getPlayerState()) {
		
		case MODERATOR:
			e.setCancelled(true);
			
			switch(item.getType()) {
			
			case PACKED_ICE:
				if(target.hasPermission("moderation.staff")) {
					player.sendMessage(CC.RED + "You cannot interact with other staff members.");
					return;
				}
				targetData.setPlayerState(targetState == PlayerState.FROZEN ? PlayerState.PLAYING : PlayerState.FROZEN);
				player.sendMessage(CC.PRIMARY + "You've successfully " + (targetState == PlayerState.FROZEN ? "unfrozen " : "frozen ") + CC.SECONDARY + target.getName());
				target.sendMessage(CC.PRIMARY + "You've been " + (targetState == PlayerState.FROZEN ? "unfrozen " : "frozen "));
				target.closeInventory();
				break;
			
			case BLAZE_ROD:
				if(target.hasPermission("moderation.staff")) {
					player.sendMessage(CC.RED + "You cannot interact with other staff members.");
					return;
				}
				if(targetState != PlayerState.PLAYING) targetData.setPlayerState(PlayerState.PLAYING);
				target.kickPlayer(CC.PRIMARY + "You've been kicked by " + CC.SECONDARY + player.getName());
				break;
				
				default:
					break;
			}
			
			break;
		
		case FROZEN:
			e.setCancelled(true);
			player.sendMessage(CC.PRIMARY + "You are frozen, you may not interact with other players.");
			break;
			
			default:
				break;
		}
		
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		e.setCancelled(data.getPlayerState() != PlayerState.PLAYING);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		e.setCancelled(data.getPlayerState() != PlayerState.PLAYING);
	}
	
	@EventHandler
	public void onItemDropEvent(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		e.setCancelled(data.getPlayerState() != PlayerState.PLAYING);
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		e.setCancelled(data.getPlayerState() != PlayerState.PLAYING);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		if(data.getPlayerState() == PlayerState.FROZEN) {
			e.setTo(e.getFrom());
		}
	}
	
	@EventHandler
	public void asyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		
		switch(plugin.getChatManager().getChatState()) {
		
		case MUTED:
			if(!player.hasPermission("moderation.mute.bypass")) {
				e.setCancelled(true);
				player.sendMessage(CC.RED + "The chat is currently muted.");
				return;
			}
			break;
		case RESTRICTED:
			if(!plugin.getChatManager().getRestrictedPlayers().contains(player.getUniqueId())) {
				e.setCancelled(true);
				player.sendMessage(CC.RED + "Sorry, but the chat is currently restricted.");
				return;
			}
			break;
		case SLOWED:
			if(!player.hasPermission("moderation.slowed.bypass")) {
				int delay = plugin.getChatManager().getDelay();
				int toSeconds = delay / 1000;
				if(plugin.getChatManager().getSlowedTimer().containsKey(player.getUniqueId())) {
					e.setCancelled(true);
					player.sendMessage(CC.RED + "The chat is currently slowed down (" + toSeconds + " second" + (toSeconds >= 2 ? "s" : "") + " delay)");
					if(System.currentTimeMillis() > (plugin.getChatManager().getSlowedTimer().get(player.getUniqueId()) + delay)) {
						data.setSlowed(false);
						plugin.getChatManager().getSlowedTimer().remove(player.getUniqueId());
					}
					return;
				}
				data.setSlowed(true);
				plugin.getChatManager().getSlowedTimer().put(player.getUniqueId(), System.currentTimeMillis());
			}
			default:
				break;
		}
		
		if(plugin.getConfig().getBoolean("chat-filter.enabled")) {
			plugin.getChatManager().getBlockedWords().forEach(word -> {
				if((e.getMessage().length() >= 2 && e.getMessage().contains(" " + word + " ")) || (e.getMessage().length() <= 1 && e.getMessage().contains(word))) {
					e.setCancelled(true);
					player.sendMessage(CC.RED + "Your message contained a block word!");
				}
			});
		}
		
		
	}
	
}
