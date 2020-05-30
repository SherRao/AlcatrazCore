package tk.sherrao.bukkit.alcatraz.command;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.data.CrateManager;
import tk.sherrao.bukkit.alcatraz.misc.Crate;
import tk.sherrao.bukkit.utils.command.CommandBundle;
import tk.sherrao.bukkit.utils.command.SherCommand;

public class CrateKeyCommand extends SherCommand {
	
	protected CrateManager crateMgr;
	
	protected String commandUsageMsg, noPermsMsg, notPlayerMsg, invalidPlayerMsg, invalidCrateMsg, givenMsg, receivedMsg;
	protected Sound commandUsageSound, noPermsSound, notPlayerSound, invalidPlayerSound, invalidCrateSound, givenSound, receivedSound;
	
	public CrateKeyCommand( AlcatrazCore pl ) {
		super( "cratekey", pl );
		
		this.crateMgr = pl.getCrateManager();
		
		this.commandUsageMsg = pl.getMessagesConfig().getString( "commands.cratekey.command-usage" );
		this.noPermsMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.notPlayerMsg = pl.getMessagesConfig().getString( "commands.executor-not-player" );
		this.invalidCrateMsg = pl.getMessagesConfig().getString( "commands.cratekey.invalid-crate-specified" );
		this.invalidPlayerMsg = pl.getMessagesConfig().getString( "commands.invalid-player-specified" );
		this.givenMsg = pl.getMessagesConfig().getString( "commands.cratekey.given" );
		this.receivedMsg = pl.getMessagesConfig().getString( "commands.cratekey.received" );
	
		this.commandUsageSound = pl.getSoundsConfig().getSound( "commands.cratekey.command-usage" );
		this.noPermsSound = pl.getSoundsConfig().getSound( "general.no-perms" );
		this.notPlayerSound = pl.getSoundsConfig().getSound( "commands.executor-not-player" );
		this.invalidCrateSound = pl.getSoundsConfig().getSound( "commands.cratekey.invalid-crate-specified" );
		this.invalidPlayerSound = pl.getSoundsConfig().getSound( "commands.invalid-player-specified" );
		this.givenSound = pl.getSoundsConfig().getSound( "commands.cratekey.given" );
		this.receivedSound = pl.getSoundsConfig().getSound( "commands.cratekey.received" );
		
	}

	@Override
	protected void onExecute( CommandBundle bundle ) {
		if( bundle.hasArgs() ) {
			if( bundle.isPlayer() ) {
				if( bundle.hasPermission( "alcatrazcore.command.cratekey" ) ) {
					try {
						Player player = Bukkit.matchPlayer( bundle.argAt(0) ).get(0);
						String crate = bundle.argAt(1);
						Crate result = crateMgr.exists( crate );
						if( result != null ) {
							bundle.messageSound( givenMsg.replace( "[player]", player.getName() )
									.replace( "[crate]", crate ), givenSound );

							player.sendMessage( receivedMsg.replace( "[player]", player.getName() )
									.replace( "[crate]", crate ) );
							pl.playSound( player, receivedSound );
							
							HashMap<Integer, ItemStack> drop = player.getInventory().addItem( result.getKey() );
							if( drop.size() == 1 )
								player.getWorld().dropItem( player.getLocation(), drop.get(0) );
							
							else
								return;
							
						} else 
							bundle.messageSound( invalidCrateMsg, invalidCrateSound );
				
					} catch( IndexOutOfBoundsException e ) {
						bundle.messageSound( invalidPlayerMsg, invalidPlayerSound );
						
					}
						
				} else
					bundle.messageSound( noPermsMsg, noPermsSound );
				
			} else 
				bundle.messageSound( notPlayerMsg, notPlayerSound );
			
		} else 
			bundle.messageSound( commandUsageMsg.replace( "[command]", bundle.alias ), commandUsageSound );
		
	}
	
}