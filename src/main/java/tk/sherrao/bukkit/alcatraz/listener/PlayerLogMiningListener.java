package tk.sherrao.bukkit.alcatraz.listener;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerLogMiningListener extends SherEventListener {

	protected MaterialData leafData, logData;
	
	public PlayerLogMiningListener( AlcatrazCore pl ) {
		super(pl);
		
		this.leafData = new MaterialData( Material.LEAVES );
		this.logData = new MaterialData( Material.LOG );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerLogMine( BlockBreakEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if( player.getItemInHand().getType().name().contains( "_AXE" ) && ( block.getType().name().contains( "LOG" ) ) ) {
			for( int y = -2; y <= 8; y++ ) {
				for( int x = -5; x <= 5; x++ ) {
					for( int z = -5; z <= 5; z++ ) {
						Block b = block.getRelative( x, y, z );
						String name = b.getType().name();
						if( name.contains( "LEAVES" ) || name.contains( "LOG" ) ) {
							Bukkit.getScheduler().runTaskLater( pl, () -> {
								for( ItemStack item : player.getInventory().addItem( b.getDrops( player.getItemInHand() ).toArray( new ItemStack[] {} ) ).values() )
									player.getWorld().dropItem( player.getLocation(), item );

								b.breakNaturally();
								if( name.contains( "LEAVES" ) ) 
									b.getWorld().playEffect( b.getLocation(), Effect.TILE_BREAK, leafData );	
								
								else 
									b.getWorld().playEffect( b.getLocation(), Effect.TILE_BREAK, logData );
						
							}, (x + 1) * (y + 2) * (z + 3) * 2 );

						} else
							continue;
					}
				}
			}

		} else
			return;
		
	}

}