package tk.sherrao.bukkit.alcatraz.listener.doorlocker;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.LocketteManager;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerLockedDoorUnregisterListener extends SherEventListener {

	protected LocketteManager lockMgr;
	protected List<String> lines;
	
	protected String successMsg, deniedMsg;
	protected Sound successSound, deniedSound;
	
	public PlayerLockedDoorUnregisterListener( AlcatrazCore pl ) {
		super(pl);
		
		this.lockMgr = pl.getLocketteManager();
		this.lines = pl.getDoorLockerConfig().getStringList( "replace-with" );
	
		this.successMsg = pl.getMessagesConfig().getString( "lockette.unregister" );
		this.successSound = pl.getSoundsConfig().getSound( "lockette.unregister" );
		this.deniedMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.deniedSound = pl.getSoundsConfig().getSound( "general.no-perms" );

	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onLockedDoorPlace( BlockBreakEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if( block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN || block.getType() == Material.WALL_SIGN ) {
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
			if( lockMgr.exists( sign ) ) {
				if( player.hasPermission( "alcatraz.door.unregister" ) ) {
					lockMgr.remove( sign );
					player.sendMessage( successMsg.replace( "[x]", String.valueOf( block.getX() ) ) 
						.replace( "[y]", String.valueOf( block.getY() ) )
						.replace( "[z]", String.valueOf( block.getZ() ) ) );
					pl.playSound( player, successSound );
				
				} else {
					player.sendMessage( deniedMsg );
					pl.playSound( player, deniedSound );
					
				}
			
			} else
				return;
			
		} else
			return;
		
	}

}