package tk.sherrao.bukkit.alcatraz.enchants.tools;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.plugin.SherEnchantment;

public class NeverBreakEnchantment extends SherEnchantment {

	public NeverBreakEnchantment( AlcatrazCore pl ) {
		super( pl, "NeverBreak", 204, 1, 		
				Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.GOLD_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, 
				Material.WOOD_AXE, Material.STONE_AXE, Material.GOLD_AXE, Material.IRON_AXE,Material.DIAMOND_AXE, 
				Material.WOOD_SPADE, Material.STONE_SPADE, Material.GOLD_SPADE, Material.IRON_SPADE, Material.DIAMOND_SPADE, 
				Material.WOOD_HOE, Material.STONE_HOE, Material.GOLD_HOE, Material.IRON_HOE, Material.DIAMOND_HOE, 
				Material.WOOD_SWORD, Material.STONE_SWORD ,Material.GOLD_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH ) 
	public void onPlayerBlockMine( PlayerItemBreakEvent event ) {
		ItemStack item = event.getBrokenItem();
		if( item != null && super.hasEnchant( item ) )
			item.setDurability( (short) 0 );
			
		else
			return;
		
	}
	
}