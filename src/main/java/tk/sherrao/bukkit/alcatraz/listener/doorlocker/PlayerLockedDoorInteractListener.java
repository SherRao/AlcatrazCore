package tk.sherrao.bukkit.alcatraz.listener.doorlocker;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.LocketteManager;
import tk.sherrao.bukkit.alcatraz.misc.DoorInteractPermission;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;
import tk.sherrao.utils.collections.Pair;

public class PlayerLockedDoorInteractListener extends SherEventListener {

	protected LocketteManager lockMgr;
	
	protected String deniedMsg, allowedMsg;
	protected Sound deniedSound, allowedSound;
	
	public PlayerLockedDoorInteractListener( AlcatrazCore pl ) {
		super(pl);
		
		this.lockMgr = pl.getLocketteManager();
		this.deniedMsg = pl.getMessagesConfig().getString( "lockette.use-locked.deny" );
		this.allowedMsg = pl.getMessagesConfig().getString( "lockette.use-locked.allow" );
		this.deniedSound = pl.getSoundsConfig().getSound( "lockette.use-locked.deny" );
		this.allowedSound = pl.getSoundsConfig().getSound( "lockette.use-locked.allow" );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerLockedDoorInteract( PlayerInteractEvent event ) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if( action == Action.RIGHT_CLICK_BLOCK ) {
			if( block.getType() == Material.IRON_DOOR_BLOCK ) {
				Pair<OfflinePlayer, DoorInteractPermission> result = lockMgr.allowedToOpen( block, player );
				switch( result.getValue() ) {
					case DOOR_UNLOCKED:
						lockMgr.toggleDoor( block );
						player.sendMessage( allowedMsg );
						pl.playSound( player, allowedSound );
						break;
					
					case DOOR_LOCKED:
						player.sendMessage( deniedMsg.replace( "[player]", result.getKey().getName() ) );
						pl.playSound( player, deniedSound );
						break;
						
					case WILD_DOOR:
						break;
					
				}	
				
			} else
				return;
			
		} else
			return;
		
	}
	
}
