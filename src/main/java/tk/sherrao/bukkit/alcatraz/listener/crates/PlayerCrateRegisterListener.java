package tk.sherrao.bukkit.alcatraz.listener.crates;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.CrateManager;
import tk.sherrao.bukkit.alcatraz.misc.Crate;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerCrateRegisterListener extends SherEventListener {

	protected CrateManager crateMgr;
	protected String noPermsMsg, successMsg;
	protected Sound noPermsSound, successSound;
	
	public PlayerCrateRegisterListener( AlcatrazCore pl ) {
		super(pl);
		
		this.crateMgr = pl.getCrateManager();
		this.noPermsMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.successMsg = pl.getMessagesConfig().getString( "crates.register" );
		this.noPermsSound = pl.getSoundsConfig().getSound( "general.no-perms" );
		this.successSound = pl.getSoundsConfig().getSound( "crates.register" );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerCrateRegister( BlockPlaceEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if( block.getType() == Material.CHEST ) {
			ItemStack item = event.getItemInHand();
			Crate crate = null;
			if( item != null && item.getType() == Material.CHEST && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && (crate = crateMgr.exists( item.getItemMeta().getDisplayName() ) ) != null ) {
				if( player.hasPermission( "alcatrazcore.op" ) ) {
					crateMgr.add( block, item.getItemMeta().getDisplayName() );
					player.sendMessage( successMsg.replace( "[x]", String.valueOf( block.getX() ) )
							.replace( "[y]", String.valueOf( block.getY() ) )
							.replace( "[z]", String.valueOf( block.getZ() ) )
							.replace( "[type]", crate.toString() ) );
					pl.playSound( player, successSound );
					
				} else {
					event.setCancelled( true );
					player.sendMessage( noPermsMsg );
					pl.playSound( player, noPermsSound );
					
				}
				 
			} else
				return;
			
		} else
			return;
		
	}

}
