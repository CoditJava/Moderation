package git.codit.moderation.managers.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import git.codit.moderation.utils.CC;
import git.codit.moderation.utils.ItemUtil;

public class FrozenInventory {
	
	public static Inventory getFrozenInventory() {
		
		Inventory inv = Bukkit.createInventory(null, 9, CC.RED + "You are frozen");
		inv.setItem(4, ItemUtil.createItem(Material.PAPER, CC.PRIMARY + "You are frozen, come on discord."));
		fillEmptySpaces(inv);
		return inv;
		
	}
	
	private static void fillEmptySpaces(Inventory inv) {
		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
				inv.setItem(i, ItemUtil.createItem(Material.STAINED_GLASS_PANE, CC.DARK_GRAY + " ", 1, (short)7));
			}
		}
	}

}
