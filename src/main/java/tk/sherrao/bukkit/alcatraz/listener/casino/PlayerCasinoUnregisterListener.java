package tk.sherrao.bukkit.alcatraz.listener.casino;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.CasinoManager;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerCasinoUnregisterListener extends SherEventListener {
	
	protected CasinoManager casinoMgr;
	protected String noPermsMsg, successMsg;
	protected Sound noPermsSound, successSound;
	
	public PlayerCasinoUnregisterListener( AlcatrazCore pl ) {
		super(pl);
		
		this.casinoMgr = pl.getCasinoManager();
		this.noPermsMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.successMsg = pl.getMessagesConfig().getString( "casino.unregister" );
		this.noPermsSound = pl.getSoundsConfig().getSound( "general.no-perms" );
		this.successSound = pl.getSoundsConfig().getSound( "casino.unregister" );
		
	}

	@EventHandler( priority = EventPriority.HIGH ) 
	public void onPlayerCasinoBreak( BlockBreakEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if( block.getType() == Material.ENDER_CHEST ) {
			if( casinoMgr.exists( block ) ) {
				if( player.hasPermission( "alcatrazcore.op" ) ) {
					casinoMgr.remove( block );
					player.sendMessage( successMsg.replace( "[x]", String.valueOf( block.getX() ) )
							.replace( "[y]", String.valueOf( block.getY() ) )
							.replace( "[z]", String.valueOf( block.getZ() ) ) );
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
