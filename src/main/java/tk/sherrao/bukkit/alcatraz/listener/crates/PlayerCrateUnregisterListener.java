package tk.sherrao.bukkit.alcatraz.listener.crates;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.CrateManager;
import tk.sherrao.bukkit.alcatraz.misc.Crate;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerCrateUnregisterListener extends SherEventListener {

	protected CrateManager crateMgr;
	protected String noPermsMsg, successMsg;
	protected Sound noPermsSound, successSound;
	
	public PlayerCrateUnregisterListener( AlcatrazCore pl ) {
		super(pl);
	
		this.crateMgr = pl.getCrateManager();
		this.noPermsMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.successMsg = pl.getMessagesConfig().getString( "casino.unregister" );
		this.noPermsSound = pl.getSoundsConfig().getSound( "general.no-perms" );
		this.successSound = pl.getSoundsConfig().getSound( "casino.unregister" );
				
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerCrateBreak( BlockBreakEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Crate crate = null;
		if( block.getType() == Material.CHEST ) {
			if( (crate = crateMgr.exists( block )) != null ) {
				if( player.hasPermission( "alcatrazcore.op" ) ) {
					crateMgr.remove( block );
					player.sendMessage( successMsg.replace( "[x]", String.valueOf( block.getX() ) )
							.replace( "[y]", String.valueOf( block.getY() ) )
							.replace( "[z]", String.valueOf( block.getZ() ) ) 
							.replace( "[crate]", crate.toString() ) );
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
