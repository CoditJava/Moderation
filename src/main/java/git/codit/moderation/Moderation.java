package git.codit.moderation;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import git.codit.moderation.commands.ChatCommand;
import git.codit.moderation.commands.FreezeCommand;
import git.codit.moderation.commands.ModerationCommand;
import git.codit.moderation.commands.ReportCommand;
import git.codit.moderation.listeners.EntityListeners;
import git.codit.moderation.listeners.PlayerListeners;
import git.codit.moderation.managers.ChatManager;
import git.codit.moderation.managers.InventoryManager;
import git.codit.moderation.managers.ItemManager;
import git.codit.moderation.managers.PlayerManager;
import git.codit.moderation.managers.ReportManager;
import git.codit.moderation.runnables.FrozenInventoryRunnable;
import git.codit.moderation.utils.inventory.UIListener;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;

@Getter
public class Moderation extends JavaPlugin {
	
	@Getter private static Moderation instance;
	
	public PlayerManager playerManager;
	public ItemManager itemManager;
	public ChatManager chatManager;
	public ReportManager reportManager;
	public InventoryManager inventoryManager;
	
	@Override
	public void onEnable() {
		instance = this;
		registerConfigs();
		registerManagers();
		registerListeners();
		registerCommands();
		startRunnables();
	}
	
	private void startRunnables() {
		this.getServer().getScheduler().runTaskTimer(this, new FrozenInventoryRunnable(), 20L, 20L);
	}

	private void registerConfigs() {
		saveDefaultConfig();
	}

	private void registerListeners() {
		Arrays.asList(
				new PlayerListeners(),
				new EntityListeners(),
				new UIListener(),
				new InventoryManager()).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
	}

	private void registerCommands() {
		Arrays.asList(
				new ModerationCommand(),
				new FreezeCommand(),
				new ChatCommand(),
				new ReportCommand()).forEach(command -> this.bukkitCommand(getName(), command));
	}
	
	private void bukkitCommand(String prefix, Command command) {
		MinecraftServer.getServer().server.getCommandMap().register(prefix, command);
	}

	private void registerManagers() {
		this.playerManager = new PlayerManager();
		this.itemManager = new ItemManager();
		this.chatManager = new ChatManager();
		this.reportManager = new ReportManager();
		this.inventoryManager = new InventoryManager();
	}

	@Override
	public void onDisable() {
		this.chatManager.saveRestrictedPlayers();
	}
	

}
