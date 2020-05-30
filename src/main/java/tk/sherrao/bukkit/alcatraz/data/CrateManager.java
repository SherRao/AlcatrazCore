package tk.sherrao.bukkit.alcatraz.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.misc.Crate;
import tk.sherrao.bukkit.alcatraz.misc.RewardItem;
import tk.sherrao.bukkit.utils.ItemBuilder;
import tk.sherrao.bukkit.utils.config.SherConfigurationAlt;
import tk.sherrao.bukkit.utils.plugin.SherPluginFeature;

public class CrateManager extends SherPluginFeature {

	private final class Lock {}
	private final Object lock = new Lock();
	
	protected File crateConfigFile, crateDataFile;
	protected SherConfigurationAlt crateConfig;
	protected FileConfiguration crateData;
	 
	protected Map<String, Crate> crateTypes;
	protected Map<Location, String> registeredCrates;
	
	public CrateManager( AlcatrazCore pl ) {
		super(pl);
		
		this.crateConfigFile = pl.getCrateConfigFile();
		this.crateDataFile = pl.getCrateDataFile();
		this.crateConfig = pl.getCrateConfig();
		this.crateData = pl.getCrateData();
		
		this.crateTypes = Collections.synchronizedMap( new HashMap<>() );
		this.registeredCrates = Collections.synchronizedMap( new HashMap<>() );
		
	}
	
	
	public void load() {
		synchronized( lock ) {
			ConfigurationSection cs = crateConfig.getConfigurationSection( "crates" );
			for( String crate : cs.getKeys( false ) ) {
				String keyName = ChatColor.translateAlternateColorCodes( '&', crateConfig.getString( "crates." + crate + ".key.title" ) );
				List<String> keyLore = new ArrayList<>();
				for( String str : crateConfig.getStringList( "crates." + crate + ".key.lore" ) )
					keyLore.add( ChatColor.translateAlternateColorCodes( '&', str ) );
				
				ItemStack key = new ItemBuilder( Material.valueOf( crateConfig.getString( "crates." + crate + ".key.type" ) ) )
						.setName( keyName )
						.setLore( keyLore )
						.setDurability( (short) crateConfig.getInt( "crates." + crate + ".key.data" ) )
						.toItemStack();
				
				List<RewardItem> rewards = new ArrayList<>();
				ConfigurationSection cs1 = cs.getConfigurationSection( crate + ".rewards" );
				for( String reward : cs1.getKeys( false ) ) {
					String itemName = ChatColor.translateAlternateColorCodes( '&', crateConfig.getString( "crates." + crate + ".rewards." + reward + ".item.title" ) );
					List<String> itemLore = new ArrayList<>();
					for( String str : crateConfig.getStringList( "crates." + crate + ".rewards." + reward + ".item.lore" ) )
						itemLore.add( ChatColor.translateAlternateColorCodes( '&', str ) );
					
					ItemStack item = new ItemBuilder( Material.valueOf( crateConfig.getString( "crates." + crate + ".rewards." + reward + ".item.type" ) ) )
							.setName( itemName )
							.setLore( itemLore )
							.setDurability( (short) crateConfig.getInt( "crates." + crate + ".rewards." + reward + ".item.data" ) )
							.toItemStack();
					
					double chance = crateConfig.getDouble( "crates." + crate + ".rewards." + reward + ".chance" );
					List<String> commands = crateConfig.getStringList( "crates." + crate + ".rewards." + reward + ".commands" );
					rewards.add( new RewardItem( item, chance, commands, reward ) );
					
				}
				
				crateTypes.put( crate, new Crate( crate, key, rewards ) );
			
			}
			
			try {
				if( !crateData.contains( "crates" ) ) {
					crateData.set( "crates", new ArrayList<>() );
					crateData.save( crateDataFile );
				
				} else {
					for( String entry : crateData.getStringList( "crates" ) ) {
						String[] coords = entry.split( ", " );
						Location loc = new Location( Bukkit.getWorld( UUID.fromString( coords[0].replace( "world=", "" ) ) ), Integer.valueOf( coords[1].replace( "x=", "" ) ), 
								Integer.valueOf( coords[2].replace( "y=", "" ) ), Integer.valueOf( coords[3].replace( "z=", "" ) ) );
				
						String type = coords[4].replace( "type=", "" );
						registeredCrates.put( loc, type );
						
					}
				}

			} catch( IOException e ) { log.severe( "Failed to load data for CrateManager! (crates.data.yml)", e ); }
		}
	}
	
	
	public void add( Block crate, String type ) {
		synchronized( lock ) {
			try {
				registeredCrates.put( crate.getLocation(), type );
				List<String> list = crateData.getStringList( "crates" );
				list.add( new StringBuilder()
						.append( "world=" )
						.append( crate.getWorld().getUID() )
						.append( ", x=" )
						.append( crate.getX() )
						.append( ", y=" )
						.append( crate.getY() )
						.append( ", z=" )
						.append( crate.getZ() )
						.append( ", type=" )
						.append( type )
						.toString() );
		
				crateData.set( "crates", list );
				crateData.save( crateDataFile );
				
			} catch( IOException e ) { log.severe( "Failed to save data for CrateManager File! (\"crates.data.yml\")", e ); }
		}
	}
	
	
	public void remove( Block crate ) {
		synchronized( lock ) {
			try {	
				crateTypes.remove( crate.getLocation() );
				List<String> list = crateData.getStringList( "crates" );
				list.remove( new StringBuilder()
						.append( "world=" )
						.append( crate.getWorld().getUID() )
						.append( ",x=" )
						.append( crate.getX() )
						.append( ",y=" )
						.append( crate.getY() )
						.append( ",z=" )
						.append( crate.getZ() )
						.append( ",type=" )
						.append( registeredCrates.get( crate.getLocation() ) )
						.toString() );
			
				crateData.set( "crates", list );	
				crateData.save( crateDataFile );
				
			} catch( IOException e ) { log.severe( "Failed to save data for CrateManager File! (\"crates.data.yml\")", e ); }
		}
	}
	
	
	public Crate exists( Block block ) {
		synchronized( lock ) {
			for( Entry<Location, String> entry : registeredCrates.entrySet() ) {
				if( entry.getKey().equals( block.getLocation() ) ) {
					String crateName = entry.getValue();	
					return exists( crateName );
					
				} else
					continue;
				
			} 
			
			return null;
			
		}
	}
	
	public Crate exists( String crate ) {
		synchronized( lock ) {
			for( Entry<String, Crate> entry : crateTypes.entrySet() ) {
				if( crate.equalsIgnoreCase( entry.getKey() ) )
					return entry.getValue();
				
				else
					continue;
				
			}
			
			return null;
			
		}
	}
	
}