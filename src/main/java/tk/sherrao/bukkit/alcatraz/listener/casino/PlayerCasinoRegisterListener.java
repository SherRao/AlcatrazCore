package tk.sherrao.bukkit.alcatraz.listener.casino;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.CasinoManager;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerCasinoRegisterListener extends SherEventListener {

	protected CasinoManager casinoMgr;
	protected String registerBlockName;
	
	protected String noPermsMsg, successMsg;
	protected Sound noPermsSound, successSound;
	
	public PlayerCasinoRegisterListener( AlcatrazCore pl ) {
		super(pl);
		
		this.casinoMgr = pl.getCasinoManager();
		this.registerBlockName = casinoMgr.getRegisteringText();
		
		this.noPermsMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.successMsg = pl.getMessagesConfig().getString( "casino.register" );
		this.noPermsSound = pl.getSoundsConfig().getSound( "general.no-perms" );
		this.successSound = pl.getSoundsConfig().getSound( "casino.register" );
		
	}
	
	@EventHandler( priority = EventPriority.HIGH ) 
	public void onPlayerCasinoPlace( BlockPlaceEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if( block.getType() == Material.ENDER_CHEST ) {
			ItemStack item = event.getItemInHand();
			if( item.getType() == Material.ENDER_CHEST && item.hasItemMeta() && item.getItemMeta().hasDisplayName() 
					&& item.getItemMeta().getDisplayName().equals( registerBlockName ) ) {
				if( player.hasPermission( "alcatrazcore.op" ) ) {
					casinoMgr.add( block );
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