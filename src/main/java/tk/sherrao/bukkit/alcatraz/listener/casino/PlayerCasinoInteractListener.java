package tk.sherrao.bukkit.alcatraz.listener.casino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import tk.sherrao.bukkit.alcatraz.AlcatrazCore;
import tk.sherrao.bukkit.alcatraz.EconomyHook;
import tk.sherrao.bukkit.alcatraz.data.CasinoManager;
import tk.sherrao.bukkit.alcatraz.misc.RewardItem;
import tk.sherrao.bukkit.utils.ItemBuilder;
import tk.sherrao.bukkit.utils.config.SherConfigurationAlt;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerCasinoInteractListener extends SherEventListener {

	protected SherConfigurationAlt casinoConfig;
	protected CasinoManager casinoMgr;
	protected double cost;
	
	protected int guiUpdateDelay;
	protected String guiTitle;
	protected ItemStack guiDefaultBackground, guiWinBackground;
	protected Sound spinSound;
	
	protected List<ItemStack> guiItems;
	protected List<RewardItem> items;
	
	protected String successMsg, noPermsMsg, noMoneyMsg;
	protected Sound successSound, noPermsSound, noMoneySound;
	
	public PlayerCasinoInteractListener( AlcatrazCore pl ) {
		super(pl);
		
		this.casinoConfig = pl.getCasinoConfig();
		this.casinoMgr = pl.getCasinoManager();
		this.cost = 0;
		
		this.guiUpdateDelay = casinoConfig.getInt( "delay-between-updates" );
		this.guiTitle = casinoConfig.getString( "title" );
		this.guiDefaultBackground = new ItemBuilder( Material.valueOf( casinoConfig.getString( "background-default-item.type" ) ) )
				.setName( casinoConfig.getString( "background-default-item.title") )
				.setLore( casinoConfig.getStringList( "background-default-item.lore" ) )
				.setDurability( (short) casinoConfig.getInt( "background-default-item.data" ) )
				.toItemStack();
		this.guiWinBackground = new ItemBuilder( Material.valueOf( casinoConfig.getString( "background-win-item.type" ) ) )
				.setName( casinoConfig.getString( "background-win-item.title") )
				.setLore( casinoConfig.getStringList( "background-win-item.lore" ) )
				.setDurability( (short) casinoConfig.getInt( "background-win-item.data" ) )
				.toItemStack();
		this.spinSound = pl.getSoundsConfig().getSound( "casino.spin" );
		
		this.guiItems = Collections.synchronizedList( new ArrayList<>() );
		this.items = Collections.synchronizedList( new ArrayList<>() );
		
		this.successMsg = pl.getMessagesConfig().getString( "casino.win" );
		this.noPermsMsg = pl.getMessagesConfig().getString( "general.no-perms" );
		this.noMoneyMsg = pl.getMessagesConfig().getString( "general.no-money" );

		this.successSound = pl.getSoundsConfig().getSound( "casino.win" );
		this.noPermsSound = pl.getSoundsConfig().getSound( "general.no-perms" );
		this.noMoneySound = pl.getSoundsConfig().getSound( "general.no-money" );
		
		ConfigurationSection cs = casinoConfig.getConfigurationSection( "rewards" );
		for( Iterator<String> it = cs.getKeys( false ).iterator(); it.hasNext(); ) {
			String key = it.next();
			try {
				double chance = casinoConfig.getDouble( "rewards." + key + ".chance" );
				ItemStack item = new ItemBuilder( Material.valueOf( casinoConfig.getString( "rewards." + key + ".item.type" ) ) )
						.setName( casinoConfig.getString( "rewards." + key + ".item.title" ) )
						.setLore( casinoConfig.getStringList( "rewards." + key + ".item.lore" ) )
						.setDurability( (short) casinoConfig.getInt( "rewards." + key + ".item.data" ) ) 
						.toItemStack();
				List<String> commands = casinoConfig.getStringList( "rewards." + key + ".commands" ); 

				items.add( new RewardItem( item, chance, commands, key.replace( "_", " " ) ) );
				for( int i = 0; i < chance * 100; i++ ) 
					guiItems.add( item );
				
			} catch( NumberFormatException e ) {
				log.warning( "Found invalid key-pair value for items for casino: " + key + "! Skipping..." );
				
			}	
		}
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerCasinoInteract( PlayerInteractEvent event ) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		Action action = event.getAction();
		if( block != null && block.getType() == Material.ENDER_CHEST && action == Action.RIGHT_CLICK_BLOCK ) {
			if( casinoMgr.exists( block ) ) {
				event.setCancelled( true );
				if( player.hasPermission( "alcatraz.casino.use" ) ) {
					if( EconomyHook.withdrawBalanceFrom( player, cost ) ) {
						new GuiTask( player );

					} else {
						player.sendMessage( noMoneyMsg.replace( "[money]", String.valueOf( cost - EconomyHook.getBalanceOf( player ) ) ) );
						pl.playSound( player, noMoneySound );

					}

				} else {
					player.sendMessage( noPermsMsg );
					pl.playSound( player, noPermsSound );

				}

			} else
				return;

		} else
			return;
				
	}
	
	class GuiTask implements Runnable {
		
		protected Player player;
		protected Inventory gui;
		protected int iteration;
		protected List<ItemStack> localGuiItems;
		protected List<RewardItem> localItems;
		
		protected BukkitTask task;
		
		public GuiTask( Player player ) {
			this.player = player;
			this.gui = Bukkit.createInventory( player, 27, guiTitle );
			this.localGuiItems = new ArrayList<>( guiItems );
			this.localItems = new ArrayList<>( items );
			for( int i = 0; i < 27; i++ )
				gui.setItem( i, guiDefaultBackground );
			
			player.openInventory( gui );
			Collections.shuffle( localGuiItems );
			this.task = Bukkit.getScheduler().runTaskTimer( pl, this, 0, guiUpdateDelay );
			
		}
		
		@Override
		public void run() {
			if( iteration < 40 ) {
				if( gui.getViewers().contains( player ) ) {
					gui.setItem( 11, localGuiItems.get( iteration ) );
					gui.setItem( 12, localGuiItems.get( iteration + 1 ) );
					gui.setItem( 13, localGuiItems.get( iteration + 2 ) );
					gui.setItem( 14, localGuiItems.get( iteration + 3 ) );
					gui.setItem( 15, localGuiItems.get( iteration + 4 ) );
					player.playSound( player.getLocation(), spinSound, 1F, 0F + (iteration / 40) );
					iteration++;
			
				} else 
					player.openInventory( gui );
				
			} else {
				ItemStack is = gui.getItem(13);
				for( RewardItem item : localItems ) {
					if( item.getItem().isSimilar(is) ) {
						task.cancel();
						for( int i = 0; i < 27; i++ )
							if( i == 13 ) 
								continue;
							else 
								gui.setItem( i, guiWinBackground );
						
						for( String cmd : item.getCommands() ) 
							Bukkit.dispatchCommand( Bukkit.getConsoleSender(), cmd.replace( "[player]", player.getName() ) );
						
						player.sendMessage( successMsg.replace( "[reward]", item.toString() ) );
						pl.playSound( player, successSound );
						Firework firework = player.getWorld().spawn( player.getLocation(), Firework.class );
						firework.detonate();
						firework.getFireworkMeta().addEffect( FireworkEffect
								.builder()
								.with( Type.BURST )
								.trail( true )
								.flicker( true )
								.withColor( Color.GREEN, Color.ORANGE )
								.withFade( Color.BLUE )
								.build() );

						break;
						
					} else
						continue;
					
				}
			}	
		}
		
	}
	
}