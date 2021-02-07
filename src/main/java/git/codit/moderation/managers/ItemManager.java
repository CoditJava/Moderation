package git.codit.moderation.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import git.codit.moderation.utils.CC;
import git.codit.moderation.utils.ItemUtil;
import lombok.Getter;

@Getter
public class ItemManager {
	
	private ItemStack[] moderationItems;
	
	public ItemManager() {
		
		this.moderationItems = new ItemStack[] {
				
				ItemUtil.vanishItem(CC.PRIMARY + "Vanish " + CC.GRAY + "[" + CC.GREEN + "ON" + CC.GRAY + "]", true),
				ItemUtil.createItem(Material.PACKED_ICE, CC.PRIMARY + "Freeze"),
				null,
				ItemUtil.createItem(Material.CHEST, CC.PRIMARY + "Reports"),
				ItemUtil.createItem(Material.EYE_OF_ENDER, CC.PRIMARY + "Random teleportation"),
				ItemUtil.createItem(Material.BOOK, CC.PRIMARY + "Inventory inspector"),
				null,
				ItemUtil.knockbackItem(CC.PRIMARY + "Knockback player", 10),
				ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "Kick player"),
				
		};
		
	}
	
}
