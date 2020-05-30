package tk.sherrao.bukkit.alcatraz.enchants.armour;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.plugin.SherEnchantment;

public class BlastBackEnchantment extends SherEnchantment {

	protected float baseDamage;
	protected float multiplier;
	
	public BlastBackEnchantment( AlcatrazCore pl ) {
		super( pl, "BlastBack", 200, 5, 
				Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
				Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
				Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
				Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
				Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS );
		
		this.baseDamage = (float) pl.getEnchantsConfig().getDouble( "blastback.base-damage" ); 
		this.multiplier = (float) pl.getEnchantsConfig().getDouble( "blastback.multiplier" );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onEntityHitPlayer( EntityDamageByEntityEvent event ) {
		if( event.getEntity() instanceof Player && event.getCause() == DamageCause.ENTITY_ATTACK ) {
			Player player = (Player) event.getEntity();
			World world = player.getWorld();
			for( ItemStack item : player.getInventory().getArmorContents() ) {
				if( item != null && super.hasEnchant( item ) ) {
					int level = item.getEnchantmentLevel( enchant );
					((CraftWorld) world).getHandle().createExplosion( ((CraftPlayer) player).getHandle(), player.getLocation().getX(), player.getLocation().getY(), 
							player.getLocation().getZ(), 1F * ( baseDamage + (multiplier * level)), true, true ).a( true );
					
					world.playEffect( player.getLocation(), Effect.EXPLOSION_HUGE, 4 );					
					return;
					
				} else
					continue;
				
			} 
							
		} else 
			return;
		
	}
	
}