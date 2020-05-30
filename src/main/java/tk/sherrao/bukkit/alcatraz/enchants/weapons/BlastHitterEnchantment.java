package tk.sherrao.bukkit.alcatraz.enchants.weapons;

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

public class BlastHitterEnchantment extends SherEnchantment {

	protected float baseDamage;
	protected float multiplier;
	
	public BlastHitterEnchantment( AlcatrazCore pl ) {
		super( pl, "BlastHitter", 205, 3, 
				Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD );
		
		this.baseDamage = (float) pl.getEnchantsConfig().getDouble( "blasthitter.base-damage" );
 		this.multiplier = (float) pl.getEnchantsConfig().getDouble( "blasthitter.multiplier" );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerHitEntity( EntityDamageByEntityEvent event ) {
		if( event.getDamager() instanceof Player && event.getCause() == DamageCause.ENTITY_ATTACK ) {
			Player player = (Player) event.getDamager();
			ItemStack item = player.getItemInHand();
			World world = player.getWorld();
			if( item != null && super.hasEnchant( item ) ) {
				int level = item.getEnchantmentLevel( super.enchant );
				((CraftWorld) world).getHandle().createExplosion( ((CraftPlayer) player).getHandle(), player.getLocation().getX(), player.getLocation().getY(), 
						player.getLocation().getZ(), 1F * (baseDamage + (multiplier * level)), true, true ).a( true );
				
				world.playEffect( player.getLocation(), Effect.EXPLOSION_HUGE, 4 );							
				
			} else
				return;
			
		} else
			return;
		
	}

}