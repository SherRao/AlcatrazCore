package tk.sherrao.bukkit.alcatraz.listener.crates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.CrateManager;
import tk.sherrao.bukkit.alcatraz.misc.Crate;
import tk.sherrao.bukkit.alcatraz.misc.RewardItem;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;
import tk.sherrao.utils.Random;

public class PlayerCrateInteractListener extends SherEventListener {

	protected CrateManager crateMgr;
	
	public PlayerCrateInteractListener( AlcatrazCore pl ) {
		super(pl);
		
		this.crateMgr = pl.getCrateManager();
		
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerCrateInteract( PlayerInteractEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		Action action = event.getAction();
		if( block != null && block.getType() == Material.CHEST && action == Action.RIGHT_CLICK_BLOCK ) {
			Crate crate = crateMgr.exists( block );
			if( crate != null ) {
				event.setCancelled( true );
				if( player.getItemInHand() != null && player.getItemInHand().isSimilar( crate.getKey() ) ) { 
					new GuiTask( player, crate, block.getLocation() );
					
				} else {
					
					
				}
				
			} else
				return;
			
		} else
			return;
	
	}
	
	class GuiTask implements Runnable {

		protected Player player;
		protected Crate crate;
		protected Location location;
		protected World world;
		protected int iteration;
		protected List<Item> removal;
		
		protected BukkitTask task;
		
		public GuiTask( Player player, Crate crate, Location location ) {
			this.player = player;
			this.crate = crate;
			this.location = location.add( .5, 1, .5 );
			this.world = player.getWorld();
			this.removal = new ArrayList<>();
			
			Collections.shuffle( crate.getItems() );
			this.task = Bukkit.getScheduler().runTaskTimer( pl, this, 0, 5 );
			
		}
		
		@Override
		public void run() {
			RewardItem item = crate.getItems().get( iteration );
			if( iteration < 20 ) {
				Item drop = world.dropItemNaturally( Random.genBool() ? location.clone().add( Math.random(), 0, Math.random() ) : location.clone().subtract( Math.random(), 0, Math.random() ), item.getItem() );
				drop.setPickupDelay( 32767 );
				drop.setCustomName( item.getItem().getItemMeta().getDisplayName() );
				drop.setCustomNameVisible( true );
				removal.add( drop );
				iteration++;
				
			} else {
				removal.forEach( (Item it) -> { it.remove(); } );

				for( int i = 0; i < removal.size(); i++ ) {
					removal.remove(i);
					world.playEffect( location.add( 0, 3, 0 ), Effect.COLOURED_DUST, (short) i );
				
				}
				
				Item drop = world.dropItem( location.add( 0, 3, 0 ), item.getItem() );
				drop.setPickupDelay( 32767 );
				drop.setCustomName( item.getItem().getItemMeta().getDisplayName() );
				drop.setCustomNameVisible( true );
				drop.setVelocity( drop.getVelocity().zero() );
				
				Firework firework = player.getWorld().spawn( location, Firework.class );
				firework.getFireworkMeta().addEffect( FireworkEffect
						.builder()
						.with( Type.BURST )
						.trail( true )
						.flicker( true )
						.withColor( Color.GREEN, Color.ORANGE )
						.withFade( Color.BLUE )
						.build() );
				
				for( String cmd : item.getCommands() )
					Bukkit.dispatchCommand( Bukkit.getConsoleSender(), cmd.replace( "[player]", player.getName() ) );

				Bukkit.getScheduler().runTaskLater( pl, () -> { 
					drop.remove(); 
					
				}, 20 * 5 );
				task.cancel();
					
				
			}
		}
		
	}
	
}