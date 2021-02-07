package git.codit.moderation.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import git.codit.moderation.Moderation;
import git.codit.moderation.utils.CC;
import git.codit.moderation.utils.MessagesUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ReportCommand extends Command {
	
	private Moderation plugin;
	
	public ReportCommand() {
		super("report");
		this.plugin = Moderation.getInstance();
		setUsage(CC.RED + "Usage: /report <player> <reason>");
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(MessagesUtil.MUST_PLAYER);
			return false;
		}
		
		Player player = (Player) sender;
		
		if(args.length != 2) {
			player.sendMessage(getUsage());
			return false;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == null) {
			player.sendMessage(MessagesUtil.TARGET_NULL.replace("%s", args[0]));
			return false;
		}
		
		if(args.length == 2 && args[1].length() > 32) {
			player.sendMessage(CC.RED + "Please provide a short and clear reason.");
			return false;
		}
		
		if(target == player) {
			player.sendMessage(CC.RED + "You cannot report yourself.");
			return false;
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i < args.length; i++) {
			sb.append(args[i]);
			sb.append(" ");
		}
		
		plugin.getReportManager().createReport(target, player, sb.toString());
		player.sendMessage(CC.GREEN + "Your report has been submitted.");
		plugin.getServer().getOnlinePlayers().stream().filter(players -> players.hasPermission("moderation.staff")).forEach(players -> {
			TextComponent reportMessage = new TextComponent(CC.SECONDARY + player.getName() + CC.PRIMARY + " has reported " + CC.SECONDARY + target.getName() + CC.PRIMARY + " for " + CC.SECONDARY + sb.toString());
			reportMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.I_GRAY + "Click to teleport onto " + target.getName()).create()));
			reportMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + target.getName()));
			players.spigot().sendMessage(reportMessage);
		});
		
		return false;
	}

}
