package git.codit.moderation.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import git.codit.moderation.Moderation;
import git.codit.moderation.chat.ChatState;
import git.codit.moderation.utils.UUIDUtil;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatManager {
	
	private Moderation plugin;
	private ChatState chatState;
	private int delay; // delay in seconds
	private List<UUID> restrictedPlayers = new ArrayList<UUID>();
	private List<String> blockedWords = new ArrayList<String>();
	private Map<UUID, Long> slowedTimer = new HashMap<UUID, Long>();
	
	public ChatManager() {
		this.plugin = Moderation.getInstance();
		this.chatState = ChatState.NORMAL;
		this.delay = 0;
		this.loadRestrictedPlayers();
		this.loadWords();
	}
	
	private void loadWords() {
		this.blockedWords.addAll(plugin.getConfig().getStringList("chat-filter.blocked-words"));
	}

	private void loadRestrictedPlayers() {
		FileConfiguration config = plugin.getConfig();
		this.restrictedPlayers.addAll(UUIDUtil.loadList(config.getStringList("chat.restrictedPlayers")));
	}
	
	public void saveRestrictedPlayers() {
		FileConfiguration config = plugin.getConfig();
		config.set("chat.restrictedPlayers", null);
		config.set("chat.restrictedPlayers", UUIDUtil.readUUIDList(this.restrictedPlayers));
		plugin.saveConfig();
	}
	
	public void addRestrictedPlayer(OfflinePlayer player) {
		this.restrictedPlayers.add(player.getUniqueId());
	}
	
	public void removeRestrictedPlayer(OfflinePlayer player) {
		this.restrictedPlayers.remove(player.getUniqueId());
	}
	
	public void setDelay(int delay) {
		this.delay = delay * 1000; // Put in ms since we are using unix timestamp
	}
	
	public ChatState getByName(String name) {
		for(ChatState cs : ChatState.values()) {
			if(cs.getName().equalsIgnoreCase(name)) {
				return cs;
			}
		}
		return null;
	}
	
}
