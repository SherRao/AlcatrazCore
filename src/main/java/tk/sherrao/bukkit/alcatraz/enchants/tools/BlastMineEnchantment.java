package tk.sherrao.bukkit.alcatraz.enchants.tools;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.plugin.SherEnchantment;

public class BlastMineEnchantment extends SherEnchantment {

	protected int baseRadius;
	protected int multiplier; 
	
	public BlastMineEnchantment( AlcatrazCore pl ) {
		super( pl, "BlastMine", 202, 5,
				Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE );
		
		this.baseRadius = pl.getEnchantsConfig().getInt( "blastmine.base-radius" );
		this.multiplier = pl.getEnchantsConfig().getInt( "blastmine.multiplier" );
				
	}

	@EventHandler( priority = EventPriority.HIGH )
	public void onEnchantmentUsage( BlockBreakEvent event ) {
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		Block block = event.getBlock();
		if( item != null && super.hasEnchant( item ) ) {
			int level = item.getEnchantmentLevel( super.enchant );
			for( int x = -level - multiplier ; x <= level + multiplier; x++ ) {
				for( int y = -level - multiplier; y <= level + multiplier; y++ ) {
					for( int z = -level - multiplier; z <= level + multiplier; z++ ) {
						Block b = block.getRelative( x, y, z );
						b.getWorld().playEffect( b.getLocation(), Effect.ENDER_SIGNAL, 2003 );
						b.setType( Material.AIR );
						for( ItemStack is : b.getDrops() ) {
							HashMap<Integer, ItemStack> drops = player.getInventory().addItem( is );
							if( drops.size() != 0 )
								for( ItemStack is1 : drops.values() )
									player.getWorld().dropItem( player.getLocation(), is1 );
							
						}
					}
				}
			}
			
			block.getWorld().playEffect( block.getLocation(), Effect.EXPLOSION_HUGE, 0 );
			block.getWorld().playSound( block.getLocation(), Sound.EXPLODE, 1F, 1F );
			
		} else 
			return;
		
	}

}