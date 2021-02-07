package git.codit.moderation.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import git.codit.moderation.Moderation;
import git.codit.moderation.managers.inventories.FrozenInventory;
import git.codit.moderation.player.PlayerData;
import git.codit.moderation.utils.CC;

public class FrozenInventoryRunnable implements Runnable {
	
	private Moderation plugin;
	
	public FrozenInventoryRunnable() {
		this.plugin = Moderation.getInstance();
	}
	
	@Override
	public void run() {
		
		plugin.getServer().getOnlinePlayers().forEach(players -> {
			
			PlayerData datas = plugin.getPlayerManager().getPlayerData(players);
			Player player = Bukkit.getPlayer(datas.getPlayerID());
			
			switch(datas.getPlayerState()) {
			
			case FROZEN:
				if(player.getOpenInventory() == null || !player.getOpenInventory().getTitle().equals(CC.RED + "You are frozen!")) {
					player.openInventory(FrozenInventory.getFrozenInventory());
				}
				break;
				
				default:
					break;
			}
			
		});
		
	}

}
