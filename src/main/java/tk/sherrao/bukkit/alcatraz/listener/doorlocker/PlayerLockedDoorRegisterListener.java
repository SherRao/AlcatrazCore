package tk.sherrao.bukkit.alcatraz.listener.doorlocker;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.LocketteManager;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerLockedDoorRegisterListener extends SherEventListener {

	protected LocketteManager lockMgr;
	protected String registerText;
	protected List<String> lines;
	
	protected String successMsg, deniedMsg;
	protected Sound successSound, deniedSound;

	public PlayerLockedDoorRegisterListener( AlcatrazCore pl ) {
		super(pl);
		
		this.lockMgr = pl.getLocketteManager();
		this.registerText = lockMgr.getRegisteringText();
		this.lines = pl.getDoorLockerConfig().getStringList( "replace-with" );		
		
		this.successMsg = pl.getMessagesConfig().getString( "lockette.register" );
		this.successSound = pl.getSoundsConfig().getSound( "lockette.register" );
		this.deniedMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.deniedSound = pl.getSoundsConfig().getSound( "general.no-perms" );

	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onLockedDoorPlace( SignChangeEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
		org.bukkit.material.Sign signData = (org.bukkit.material.Sign) block.getState().getData();
		if( event.getLine(0).contains( registerText ) ) {
			Block against = block.getRelative( signData.getAttachedFace() );
			if( against.getType() == Material.IRON_DOOR || against.getType() == Material.IRON_DOOR_BLOCK ) {
				if( player.hasPermission( "alcatraz.door.register" ) ) {
					lockMgr.add( against, sign, player );
					event.setLine( 0, lines.get(0).replace( "[player]", player.getName() ) );
					event.setLine( 1, lines.get(1).replace( "[player]", player.getName() ) );
					event.setLine( 2, lines.get(2).replace( "[player]", player.getName() ) );
					event.setLine( 3, lines.get(3).replace( "[player]", player.getName() ) );
					
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