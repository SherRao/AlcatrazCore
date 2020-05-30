package tk.sherrao.bukkit.alcatraz.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.utils.plugin.SherPluginFeature;

public class CasinoManager extends SherPluginFeature {

	private final class Lock {}
	private final Object lock = new Lock();
	
	protected File casinoDataFile;
	protected FileConfiguration casinoData;
	
	protected String registerText;
	protected List<Location> casinos;
	
	public CasinoManager( AlcatrazCore pl ) {
		super(pl);

		this.casinoDataFile = pl.getCasinoDataFile();
		this.casinoData = pl.getCasinoData();
		
		this.registerText = "casino";
		this.casinos = Collections.synchronizedList( new ArrayList<>() );
		
	}

	public void load() {
		synchronized( lock ) {
			try {
				if( !casinoData.contains( "casinos" ) ) {
					casinoData.set( "casinos", new ArrayList<>() );
					casinoData.save( casinoDataFile );
				
				} else {
					for( String entry : casinoData.getStringList( "casinos" ) ) {
						String[] coords = entry.split( "," );
						Location loc = new Location( Bukkit.getWorld( UUID.fromString( coords[0].replace( "world=", "" ) ) ), Integer.valueOf( coords[1].replace( "x=", "" ) ), 
								Integer.valueOf( coords[2].replace( "y=", "" ) ), Integer.valueOf( coords[3].replace( "z=", "" ) ) );
				
						casinos.add( loc );
					}
				}

			} catch( IOException e ) { log.severe( "Failed to load data for CasinoManager! (casinos.data.yml)", e ); }
		}
	}

	public void add( Block block ) {
		synchronized( lock ) {
			try {
				casinos.add( block.getLocation() );
				List<String> list = casinoData.getStringList( "casinos" );
				list.add( new StringBuilder()
						.append( "world=" )
						.append( block.getWorld().getUID() )
						.append( ",x=" )
						.append( block.getX() )
						.append( ",y=" )
						.append( block.getY() )
						.append( ",z=" )
						.append( block.getZ() )
						.toString() );
			
				casinoData.set( "casinos", list );
				casinoData.save( casinoDataFile );
				
			} catch( IOException e ) { log.severe( "Failed to save data for CasinoManager File! (\"casino.data.yml\")", e ); }
		}
	}
	
	public void remove( Block block ) {
		synchronized( lock ) {
			try {	
				casinos.remove( block.getLocation() );
				List<String> list = casinoData.getStringList( "casinos" );
				list.remove( new StringBuilder()
						.append( "world=" )
						.append( block.getWorld().getUID() )		
						.append( ",x=" )
						.append( block.getX() )
						.append( ",y=" )
						.append( block.getY() )
						.append( ",z=" )
						.append( block.getZ() )
						.toString() );
			
				casinoData.set( "casinos", list );	
				casinoData.save( casinoDataFile );
				
			} catch( IOException e ) { log.severe( "Failed to save data for CasinoManager File! (\"casino.data.yml\")", e ); }
		}
	}
	
	public boolean exists( Block block ) {
		synchronized( lock ) {
			return casinos.contains( block.getLocation() );
			
		}
	}
	
	public String getRegisteringText() {
		return registerText;
		
	}
	
}