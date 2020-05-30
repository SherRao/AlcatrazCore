package tk.sherrao.bukkit.alcatraz.enchants.weapons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.plugin.SherEnchantment;

public class RoadRunnerEnchantment extends SherEnchantment {

	protected PotionEffect effect1, effect2, effect3;
	
	public RoadRunnerEnchantment( AlcatrazCore pl ) {
		super( pl, "RoadRunner", 207, 3, 
				Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD );
		
		this.effect1 = new PotionEffect( PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true, false );
		this.effect2 = new PotionEffect( PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false );
		this.effect3 = new PotionEffect( PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, false );

	}

	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerItemSwitch( PlayerItemHeldEvent event ) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem( event.getNewSlot() );
		if( item != null && super.hasEnchant( item ) ) {
			int level = item.getEnchantmentLevel( super.enchant );
			switch( level ) {
				case 0:
					player.addPotionEffect( effect1, false );
					break;
					
				case 1:
					player.addPotionEffect( effect2, false );
					break;
					
				case 2:
					player.addPotionEffect( effect3, false );
					break;
				
			}
			
		} else {
			item = player.getInventory().getItem( event.getPreviousSlot() );
			if( item != null && super.hasEnchant( item ) )
				player.removePotionEffect( PotionEffectType.SPEED );
				
			else
				return;
			
		}
	}
	
}
