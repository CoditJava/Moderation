package git.codit.moderation.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import git.codit.moderation.Moderation;
import git.codit.moderation.player.PlayerData;
import git.codit.moderation.player.PlayerState;
import git.codit.moderation.utils.CC;
import git.codit.moderation.utils.MessagesUtil;

public class ModerationCommand extends Command {
	
	private Moderation plugin;
	
	public ModerationCommand() {
		super("moderation");
		this.plugin = Moderation.getInstance();
		setAliases(Arrays.asList("mod", "staff"));
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(MessagesUtil.MUST_PLAYER);
			return false;
		}
		
		Player player = (Player) sender;
		
		if(!player.hasPermission("moderation.staff")) {
			player.sendMessage(MessagesUtil.NO_PERMISSION);
			return false;
		}
		
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("reports")) {
				plugin.getInventoryManager().setupReportInventory();
				player.openInventory(plugin.getInventoryManager().getReportInventory().getCurrentPage());
			}
			return false;
		}
		
		PlayerData data = plugin.getPlayerManager().getPlayerData(player);
		
		if(data == null) {
			player.sendMessage(MessagesUtil.NULL_DATA);
			return false;
		}
		
		if(data.getPlayerState() == PlayerState.MODERATOR) {
			data.out();
			player.sendMessage(CC.RED + "You successfully left moderation mode.");
			return false;
		}
		
		data.in();
		player.sendMessage(CC.GREEN + "You successfully entered moderation mode.");
		
		return false;
	}

}
