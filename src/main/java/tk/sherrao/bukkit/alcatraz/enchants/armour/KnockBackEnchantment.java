package tk.sherrao.bukkit.alcatraz.enchants.armour;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.plugin.SherEnchantment;

public class KnockBackEnchantment extends SherEnchantment {

	protected double baseDistance;
	protected double multiplier;
	
	public KnockBackEnchantment( AlcatrazCore pl ) {
		super( pl, "KnockBack", 201, 5, 
				Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
				Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.LEATHER_BOOTS,
				Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
				Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
				Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS );
	
		this.baseDistance = pl.getEnchantsConfig().getInt( "knockback.base-distance" );
		this.multiplier = pl.getEnchantsConfig().getDouble( "knockback.multiplier" );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onEntityHitPlayer( EntityDamageByEntityEvent event ) {
		if( event.getEntity() instanceof Player && event.getCause() == DamageCause.ENTITY_ATTACK ) {
			Player player = (Player) event.getEntity();
			Entity entity = event.getDamager();
			for( ItemStack item : player.getInventory().getArmorContents() ) {
				if( item != null && super.hasEnchant( item ) ) {
					int level = item.getEnchantmentLevel( enchant );
					entity.setVelocity( entity.getVelocity().subtract( new Vector( 1D * (baseDistance + (multiplier * level)), 1, 1D * (baseDistance + (multiplier * level)))) );
					return;
					
				} else
					continue;
				
			} 
			
		} else 
			return;
		
	}

}
