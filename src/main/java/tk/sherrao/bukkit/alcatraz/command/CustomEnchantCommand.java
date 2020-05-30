package tk.sherrao.bukkit.alcatraz.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.command.CommandBundle;
import tk.sherrao.bukkit.utils.command.SherCommand;
import tk.sherrao.bukkit.utils.plugin.SherEnchantment;
import tk.sherrao.bukkit.utils.plugin.SherEnchantment.EnchantmentLevel;

public class CustomEnchantCommand extends SherCommand {
	
	protected Map<String, SherEnchantment> enchants;
	
	protected String commandUsageMsg, noPermsMsg, notPlayerMsg, invalidPlayerMsg, invalidEnchantMsg, invalidLevelMsg, invalidItemMsg, givenMsg, receivedMsg;
	protected Sound commandUsageSound, noPermsSound, notPlayerSound, invalidPlayerSound, invalidEnchantSound, invalidLevelSound, invalidItemSound, givenSound, receivedSound;
	
	public CustomEnchantCommand( AlcatrazCore pl ) {
		super( "customenchants", pl );

		this.enchants = new HashMap<>();
		for( Entry<String, SherEnchantment> entry : pl.getRegisteredEnchantments().entrySet() )
			enchants.put( entry.getKey().toLowerCase(), entry.getValue() );
		
		this.commandUsageMsg = pl.getMessagesConfig().getString( "commands.enchant.command-usage" );
		this.noPermsMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.notPlayerMsg = pl.getMessagesConfig().getString( "commands.executor-not-player" );
		this.invalidPlayerMsg = pl.getMessagesConfig().getString( "commands.invalid-player-specified" );
		this.invalidEnchantMsg = pl.getMessagesConfig().getString( "commands.enchant.invalid-enchant-specified" );
		this.invalidLevelMsg = pl.getMessagesConfig().getString( "commands.enchant.invalid-level-specified" );
		this.invalidItemMsg = pl.getMessagesConfig().getString( "commands.enchant.invalid-item-specified" );
		this.givenMsg = pl.getMessagesConfig().getString( "commands.enchant.given" );
		this.receivedMsg = pl.getMessagesConfig().getString( "commands.enchant.received" );
	
		this.commandUsageSound = pl.getSoundsConfig().getSound( "commands.command-usage" );
		this.noPermsSound = pl.getSoundsConfig().getSound( "general.no-perms" );
		this.notPlayerSound = pl.getSoundsConfig().getSound( "commands.executor-not-player" );
		this.invalidPlayerSound = pl.getSoundsConfig().getSound( "commands.invalid-player-specified" );
		this.invalidEnchantSound = pl.getSoundsConfig().getSound( "commands.enchant.invalid-enchant-specified" );
		this.invalidLevelSound = pl.getSoundsConfig().getSound( "commands.enchant.invalid-level-specified" );
		this.invalidItemSound = pl.getSoundsConfig().getSound( "commands.enchant.invalid-item-specified" );
		this.givenSound = pl.getSoundsConfig().getSound( "commands.enchant.given" );
		this.receivedSound = pl.getSoundsConfig().getSound( "commands.enchant.received" );
		
	}

	@Override
	protected void onExecute( CommandBundle bundle ) {
		if( bundle.hasArgs() ) {
			if( bundle.isPlayer() ) {
				if( bundle.argsMoreThan(2) ) {
					if( bundle.hasPermission( "alcatrazcore.command.customenchant" ) ) {
						try {
							Player player = Bukkit.matchPlayer( bundle.argAt(0) ).get(0);
							String enchant = bundle.argAt(1);
							String level = bundle.argAt(2);
							if( player != null ) {
								if( enchants.containsKey( enchant ) ) {
									try {
										ItemStack item = player.getItemInHand();
										SherEnchantment ench = enchants.get( enchant.toLowerCase() );
										EnchantmentLevel lvl = SherEnchantment.toEnchLevel( Integer.valueOf( level ) );
										if( lvl != null ) {
											if( item != null &&  ench.canEnchantItem( item ) ) {
												ench.addEnchantment( player.getItemInHand(), lvl );
												bundle.messageSound( givenMsg.replace( "[player]", player.getName() )
														.replace( "[enchant]", ench.getName() )
														.replace( "[level]", lvl.numeral() ), givenSound );
									
												player.sendMessage( receivedMsg.replace( "[player]", bundle.sender.getName() )
														.replace( "[enchant]", ench.getName() )
														.replace( "[level]", lvl.numeral() ) );
												pl.playSound( player, receivedSound );
									
											} else 
												bundle.messageSound( invalidItemMsg, invalidItemSound );
								
										} else 
											throw new NumberFormatException( "Level cannot be more than 5!" );
						
									} catch ( NumberFormatException e ) {
										bundle.messageSound( invalidLevelMsg, invalidLevelSound );

									}

								} else
									bundle.messageSound( invalidEnchantMsg, invalidEnchantSound );

							} else
								bundle.messageSound( invalidPlayerMsg, invalidPlayerSound );
					
						} catch( IndexOutOfBoundsException e ) {
							bundle.messageSound( invalidPlayerMsg, invalidPlayerSound );
							
						}
							
					} else
						bundle.messageSound( noPermsMsg, noPermsSound );
					
				} else
					bundle.messageSound( commandUsageMsg.replace( "[command]", bundle.alias ), commandUsageSound );

			} else
				bundle.messageSound( notPlayerMsg, notPlayerSound );

		} else
			bundle.messageSound( commandUsageMsg.replace( "[command]", bundle.alias ), commandUsageSound );
		
	}
	
}