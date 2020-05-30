package tk.sherrao.bukkit.alcatraz.misc;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class LockedDoor {

	protected final Block doorTop, doorBottom;
	protected final Sign sign;
	protected final UUID owner;
	
	public LockedDoor( final Block upperDoor, final Block lowerDoor, final Sign sign, final UUID owner ) {
		this.doorTop = upperDoor;
		this.doorBottom = lowerDoor;
		this.sign = sign;
		this.owner = owner;
		
	}
	
	public Block getUpperDoor() { 
		return doorTop;
		
	}

	public Block getLowerDoor() {
		return doorBottom;
		
	}
	
	public Sign getSign() {
		return sign;
		
	}
	
	public UUID getOwner() {
		return owner;
		
	}
	
}
