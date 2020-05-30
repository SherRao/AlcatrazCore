package tk.sherrao.bukkit.alcatraz.enchants.tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.plugin.SherEnchantment;

public class MoarExperienceEnchantment extends SherEnchantment {

	protected int baseMultiplier;
	protected int levelMultiplier;
	
	public MoarExperienceEnchantment( AlcatrazCore pl ) {
		super( pl, "MoarExperience", 203, 5, 
				Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE );
		
		this.baseMultiplier = pl.getEnchantsConfig().getInt( "moarexp.base-xp" );
		this.levelMultiplier = pl.getEnchantsConfig().getInt( "moarexp.multiplier" );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH ) 
	public void onPlayerBlockMine( BlockBreakEvent event ) {
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		Block block = event.getBlock();
		if( item != null && super.hasEnchant( item ) && block.getType().name().contains( "ORE" ) ) {
			int level = item.getEnchantmentLevel( super.enchant );
			event.setExpToDrop( event.getExpToDrop() * baseMultiplier * (level * levelMultiplier) );
			
		} else
			return;
		
	}

}