package git.codit.moderation.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import git.codit.moderation.Moderation;
import git.codit.moderation.chat.ChatState;
import git.codit.moderation.player.PlayerData;
import git.codit.moderation.utils.CC;
import git.codit.moderation.utils.MessagesUtil;

public class ChatCommand extends Command {
	
	private Moderation plugin;
	
	public ChatCommand() {
		super("chat");
		this.plugin = Moderation.getInstance();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(MessagesUtil.MUST_PLAYER);
			return false;
		}
		
		Player player = (Player) sender;
		
		if(!player.hasPermission("moderation.admin")) {
			player.sendMessage(MessagesUtil.NO_PERMISSION);
			return false;
		}
		
		String[] usage = new String[] {
				CC.RED + "/chat setState <normal|muted|resticted|slowed>",
				CC.RED + "/chat setDelay <delay>",
				CC.RED + "/chat addRestricted <player>",
				CC.RED + "/chat removeRestricted <player>",
				CC.RED + "/chat listRestricted"
		};
		
		if(args.length == 0) {
			player.sendMessage(usage);
			return false;
		}
		
		String usedArgument = args[0];
		
		if(usedArgument.equalsIgnoreCase("setState")) {
			if(args.length != 2) {
				player.sendMessage(CC.RED + "/chat setState <normal|muted|resticted|slowed>");
				return false;
			}
			ChatState cs = plugin.getChatManager().getByName(args[1]);
			if(cs == null) {
				player.sendMessage(CC.RED + "Please set the chat state between the current states: NORMAL, MUTED, RESTRICTED, SLOWED");
				return false;
			}
			plugin.getChatManager().setChatState(cs);
			if(cs == ChatState.SLOWED) {
				plugin.getServer().getOnlinePlayers().forEach(players -> {
					PlayerData datas = plugin.getPlayerManager().getPlayerData(players);
					datas.setSlowed(false);
				});
			}
			Bukkit.broadcastMessage(CC.SECONDARY + player.getName() + CC.PRIMARY + " has set the public chat state to " + CC.SECONDARY + cs.getName());
		} else if(usedArgument.equalsIgnoreCase("setDelay")) {
			if(args.length != 2) {
				player.sendMessage(CC.RED + "/chat setDelay <delay>");
				return false;
			}
			if(!StringUtils.isNumeric(args[1])) {
				player.sendMessage(CC.RED + "The delay value must be number (in seconds).");
				return false;
			}
			if(plugin.getChatManager().getChatState() != ChatState.SLOWED) {
				player.sendMessage(CC.RED + "The current chat state is set to " + plugin.getChatManager().getChatState().getName() + ", for the delay to work, you must set it to slowed.");
			}
			int delay = Integer.parseInt(args[1]);
			plugin.getChatManager().setDelay(delay);
			player.sendMessage(CC.PRIMARY + "Successfully set the chat delay to " + CC.SECONDARY + delay + " second" + (delay >= 2 ? "s" : ""));
		} else if(usedArgument.equalsIgnoreCase("addRestricted")) {
			if(args.length != 2) {
				player.sendMessage(CC.RED + "/chat addRestricted <player>");
				return false;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if(plugin.getChatManager().getRestrictedPlayers().contains(target.getUniqueId())) {
				player.sendMessage(CC.RED + target.getName() + " is already in the restricted list!");
				return false;
			}
			plugin.getChatManager().addRestrictedPlayer(target);
			player.sendMessage(CC.PRIMARY + "Successfully added " + CC.SECONDARY + target.getName() + CC.PRIMARY + " to the list.");
		} else if(usedArgument.equalsIgnoreCase("removeRestricted")) {
			if(args.length != 2) {
				player.sendMessage(CC.RED + "/chat removeRestricted <player>");
				return false;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if(!plugin.getChatManager().getRestrictedPlayers().contains(target.getUniqueId())) {
				player.sendMessage(CC.RED + target.getName() + " isn't in the restricted list!");
				return false;
			}
			plugin.getChatManager().removeRestrictedPlayer(target);
			player.sendMessage(CC.PRIMARY + "Successfully removed " + CC.SECONDARY + target.getName() + CC.PRIMARY + " to the list.");
		} else if(usedArgument.equalsIgnoreCase("listRestricted")) {
			player.sendMessage(CC.PRIMARY + "Current player that are allowed to talk while chat is restricted:");
			if(plugin.getChatManager().getRestrictedPlayers().size() == 0) {
				player.sendMessage(CC.SECONDARY + "None");
			} else {
				plugin.getChatManager().getRestrictedPlayers().forEach(uuid -> {
					OfflinePlayer rps = Bukkit.getOfflinePlayer(uuid);
					player.sendMessage(CC.DARK_GRAY + "- " + CC.SECONDARY + rps.getName());
				});
			}
		} else {
			player.sendMessage(usage);
		}
		return false;
	}

}
