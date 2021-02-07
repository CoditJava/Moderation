package git.codit.moderation.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import git.codit.moderation.Moderation;
import git.codit.moderation.player.PlayerData;
import git.codit.moderation.player.PlayerState;
import git.codit.moderation.utils.CC;
import git.codit.moderation.utils.MessagesUtil;

public class FreezeCommand extends Command {
	
	private Moderation plugin;
	
	public FreezeCommand() {
		super("freeze");
		this.plugin = Moderation.getInstance();
		setUsage(CC.RED + "Usage: /freeze <player>");
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
		
		if(args.length == 0 || args.length >= 2) {
			player.sendMessage(getUsage());
			return false;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == null) {
			player.sendMessage(MessagesUtil.TARGET_NULL.replace("%s", args[0]));
			return false;
		}
		
		PlayerData targetData = plugin.getPlayerManager().getPlayerData(target);
		PlayerState targetState = targetData.getPlayerState();
		
		targetData.setPlayerState(targetState == PlayerState.FROZEN ? PlayerState.PLAYING : PlayerState.FROZEN);
		player.sendMessage(CC.PRIMARY + "You've successfully " + (targetState == PlayerState.FROZEN ? "unfrozen " : "frozen ") + CC.SECONDARY + target.getName());
		target.sendMessage(CC.PRIMARY + "You've been " + (targetState == PlayerState.FROZEN ? "unfrozen " : "frozen"));
		target.closeInventory();
		
		return false;
	}

}
