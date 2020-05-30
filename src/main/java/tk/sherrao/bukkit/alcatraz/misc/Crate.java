package tk.sherrao.bukkit.alcatraz.misc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Crate {

	protected String name;
	protected ItemStack key;
	protected List<RewardItem> rewards;
	protected List<RewardItem> items;
	
	public Crate( String name, ItemStack key, List<RewardItem> rewards ) {
		this.name = name;
		this.key = key;
		this.rewards = rewards;
		this.items = new ArrayList<>();
		for( RewardItem reward : rewards ) 
			for( int i = 0; i < reward.getChance() * 100; i++ ) 
				items.add( reward );
		
	}
	
	public ItemStack getKey() {
		return key;
		
	}
	
	public List<RewardItem> getRewards() {
		return rewards;
		
	}
	
	public List<RewardItem> getItems() {
		return items;
		
	}

	@Override
	public String toString() {
		return name;
		
	}
	
}
