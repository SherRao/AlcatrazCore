package tk.sherrao.bukkit.alcatraz.misc;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class RewardItem {

	protected ItemStack item;
	protected double chance;
	protected List<String> commands;
	protected String name;
	
	public RewardItem( ItemStack item, double chance, List<String> commands, String name ) {
		this.item = item;
		this.chance = chance;
		this.commands = commands;
		this.name = name;
		
	}
	
	public ItemStack getItem() {
		return item;
		
	}
	
	public double getChance() {
		return chance;
		
	}
	
	public List<String> getCommands() {
		return commands;
		
	}

	@Override
	public String toString() {
		return name;
		
	}
	
}