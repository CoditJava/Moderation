package git.codit.moderation.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import git.codit.moderation.player.PlayerData;

public class PlayerManager {
	
	//private Moderation plugin;
	private Map<UUID, PlayerData> playerDatas;
	
	public PlayerManager() {
	//	this.plugin = Moderation.getInstance();
		this.playerDatas = new HashMap<UUID, PlayerData>();
	}
	
	public void createData(Player player) {
		this.playerDatas.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
	}
	
	public void destroyData(Player player) {
		this.playerDatas.remove(player.getUniqueId());
	}
	
	public PlayerData getPlayerData(Player player) {
		return this.playerDatas.get(player.getUniqueId());
	}
	
	public Collection<PlayerData> getAllDatas() {
		return this.playerDatas.values();
	}
	
}
