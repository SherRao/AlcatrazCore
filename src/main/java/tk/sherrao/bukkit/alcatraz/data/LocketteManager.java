package tk.sherrao.bukkit.alcatraz.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.misc.DoorInteractPermission;
import tk.sherrao.bukkit.alcatraz.misc.LockedDoor;
import tk.sherrao.bukkit.utils.plugin.SherPluginFeature;
import tk.sherrao.utils.collections.Pair;

public class LocketteManager extends SherPluginFeature {

	private final class Lock {}
	private final Object lock = new Lock();
	
	protected File locketteDataFile;
	protected FileConfiguration locketteData;
	
	protected String registerText;
	protected List<LockedDoor> doors;
	
	public LocketteManager( AlcatrazCore pl ) {
		super(pl);
	
		this.locketteDataFile = pl.getLocketteDataFile();
		this.locketteData = pl.getLocketteData();

		this.registerText = "lock";
		this.doors = Collections.synchronizedList( new ArrayList<>() );
		
	}
	
	
	public void load() {
		synchronized( lock ) {
			try {
				if( !locketteData.contains( "doors" ) ) {
					locketteData.set( "doors", new ArrayList<>() );
					locketteData.save( locketteDataFile );
				
				} else {
					for( String entry : locketteData.getStringList( "doors" ) ) {
						String[] info = entry.replace( "owner=", "" )
								.replace( "world=", "" )
								.replace( "door=[", "" )
								.replace( "x=", ""  )
								.replace( "y=", "" )
								.replace( "z=", "" )
								.replace( "isTopBlock=", "" )
								.replace( "sign=[", "" )
								.replace( "lines={", "" )
								.replace( "1=", "" )
								.replace( "2=", "" )
								.replace( "3=", "" )
								.replace( "4=", "" )
								.replace( "]", "" )
								.replace( "}", "" )
								.split( ", " );

						Bukkit.broadcastMessage( info[1] );
						
						UUID owner = UUID.fromString( info[0] );
						World world = Bukkit.getWorld( UUID.fromString( info[1] ) );
						Sign sign = (Sign) world.getBlockAt( Integer.valueOf( info[6] ), Integer.valueOf( info[7] ), Integer.valueOf( info[8] ) ).getState();
						sign.setLine( 0, info[9] );
						sign.setLine( 1, info[10] );
						sign.setLine( 2, info[11] );
						sign.setLine( 3, info[12] );
						
						Block door = world.getBlockAt( Integer.valueOf( info[2] ), Integer.valueOf( info[3] ), Integer.valueOf( info[4] ) );
						LockedDoor lockedDoor = info[5].equals( "true" ) ? new LockedDoor( door, door.getRelative( BlockFace.DOWN ), sign, owner ) : new LockedDoor( door.getRelative( BlockFace.UP ), door, sign, owner );
					
						doors.add( lockedDoor );

					}
				}

			} catch( IOException e ) { log.severe( "Failed to load data for LocketteManager! (lockette.data.yml)", e ); }	
		}
	}
	
	
	public void add( Block door, Sign sign, Player owner ) {
		synchronized( lock ) {
			try {
				Door doorData = (Door) door.getState().getData();
				LockedDoor lockedDoor = doorData.isTopHalf() ? new LockedDoor( door, door.getRelative( BlockFace.DOWN ), sign, owner.getUniqueId() ) : new LockedDoor( door.getRelative( BlockFace.UP ), door, sign, owner.getUniqueId() );
				doors.add( lockedDoor );
					
				List<String> list = locketteData.getStringList( "doors" );
				list.add( new StringBuilder()
						.append( "owner=" ) //0
						.append( owner.getUniqueId() )
						.append( ", world=" ) //1
						.append( door.getWorld().getUID() )
					
						
						.append( ", door=[" )
						.append( "x=" ) //2
						.append( door.getX() )
						.append( ", y=" ) //3
						.append( door.getY() )
						.append( ", z=" ) //4
						.append( door.getZ() )
						.append( ", isTopBlock=" ) //5
						.append( doorData.isTopHalf() )
						.append( "]" )
						
						
						.append( ", sign=[" )
						.append( "x=" ) //6
						.append( sign.getX() )
						.append( ", y=" ) //7
						.append( sign.getY() )
						.append( ", z=" ) //8
						.append( sign.getZ() )		
						
						.append( ", lines={" )
						.append( "1=" ) //9
						.append( sign.getLine(0) )
						.append( " , 2=" ) //10
						.append( sign.getLine(1) )					
						.append( " , 3=" ) //11
						.append( sign.getLine(2) )					
						.append( " , 4=" ) //12
						.append( sign.getLine(3) )
						.append( " }]" )
						.toString() );
				
				locketteData.set( "doors", list );
				locketteData.save( locketteDataFile );
				
			} catch( IOException e ) { log.severe( "Failed to save data for LocketteManager File! (\"lockette.data.yml\")", e ); }
		}
	}
	
	
	public boolean remove( Sign sign ) {
		synchronized( lock ) {
			for( Iterator<LockedDoor> it = doors.iterator(); it.hasNext(); ) {
				LockedDoor door = it.next();
				if( door.getSign().equals( sign ) ) {
					it.remove();
					return true;
				
				} else
					continue;
				
			} 
			
			return false;
			
		}
	}
	
	
	public boolean remove( Block block ) {
		synchronized( lock ) {
			for( Iterator<LockedDoor> it = doors.iterator(); it.hasNext(); ) {
				LockedDoor door = it.next();
				if( door.getUpperDoor().equals( block ) || door.getLowerDoor().equals( block ) ) { 
					it.remove();
					return true;
					
				} else
					continue;
				
			}
			
			return true;
			
		}
	}
	
	
	public boolean exists( Sign sign ) {
		synchronized( lock ) {
			for( LockedDoor door : doors ) {
				if( door.getSign().equals( sign ) )
					return true;
				
				else
					continue;
				
			}
			
			return false;
			
		}
	}
	
	
	public boolean exists( Block block ) {
		synchronized( lock ) {
			Sign sign = (Sign) block.getState();
			for( LockedDoor door : doors ) {
				if( door.getSign().equals( sign ) )
					return true;
				
				else
					continue;
				
			}
			
			return false;
			
		}
	}
	
	
	public Pair<OfflinePlayer, DoorInteractPermission> allowedToOpen( Block block, Player player ) {
		synchronized( lock ) {
			for( LockedDoor door : doors ) {
				if( door.getUpperDoor().equals( block ) || door.getLowerDoor().equals( block ) ) {
					if( door.getOwner().equals( player.getUniqueId() ) )
						return Pair.from( Bukkit.getOfflinePlayer( door.getOwner() ), DoorInteractPermission.DOOR_UNLOCKED );
					
					else
						return Pair.from( Bukkit.getOfflinePlayer( door.getOwner() ), DoorInteractPermission.DOOR_LOCKED );
					
				} else
					continue;
				
			}

			return Pair.from( null, DoorInteractPermission.WILD_DOOR );
			
		}
	}
	
	
	public LockedDoor getLockedDoor( Block block ) {
		synchronized( lock ) {
			for( LockedDoor door : doors ) {
				if( door.getUpperDoor().equals( block ) || door.getLowerDoor().equals( block ) )
					return door;
				
				else
					continue;
				
			}
			
			return null;
			
		}
	}
	
	
	@SuppressWarnings( "deprecation" )
	public void toggleDoor( Block door ) {
		if( isDoorClosed( door ) ) {
			byte data = door.getData();
	        if( (data & 0x8) == 0x8 ) {
	        	door = door.getRelative( BlockFace.DOWN );
	            data = door.getData();
	            
	        }
	        
	        data = (byte) (data | 0x4);
	        door.setData( data, true );
			
		} else {
			byte data = door.getData();
	        if( (data & 0x8) == 0x8 ) {
	        	door = door.getRelative( BlockFace.DOWN );
	            data = door.getData();
	        
	        }
	        
	        data = (byte) (data & 0xb);
	        door.setData( data, true );
			
		}

        door.getWorld().playEffect( door.getLocation(), Effect.DOOR_TOGGLE, 0 );
        door.getState().update();
		
	}
	
	
	@SuppressWarnings( "deprecation" )
	public void closeDoor( Block door ) {
        byte data = door.getData();
        if( (data & 0x8) == 0x8 ) {
        	door = door.getRelative( BlockFace.DOWN );
            data = door.getData();
        
        }
        
        if( !isDoorClosed( door ) ) {
            data = (byte) (data & 0xb);
            door.setData( data, true );
            door.getWorld().playEffect( door.getLocation(), Effect.DOOR_TOGGLE, 0 );
            door.getState().update();

        }
	}
	
	
	@SuppressWarnings( "deprecation" )
	public boolean isDoorClosed( Block door ) {
		byte data = door.getData();
		if( (data & 0x8) == 0x8 ) {
			door = door.getRelative( BlockFace.DOWN );
	        data = door.getData();
		
		} 
		
		return ((data & 0x4) == 0);
		
	}
	
	
	public String getRegisteringText() {
		return registerText;
		
	}
	
}