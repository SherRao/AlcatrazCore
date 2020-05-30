package tk.sherrao.bukkit.alcatraz;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import tk.sherrao.bukkit.utils.plugin.SherPluginFeature;

public class EconomyHook extends SherPluginFeature {

	protected static Economy economy;

	protected EconomyHook( AlcatrazCore pl ) {
		super(pl);
		
		RegisteredServiceProvider<Economy> provider = pl.getServer().getServicesManager().getRegistration( Economy.class );
        if( provider != null ) {
            economy = provider.getProvider();
            log.info( "Hooked onto Vault for Economy..." );
            
        } else
            log.severe( "Vault could not be detected for hooking onto Economy, disabling...", new RuntimeException() );
            
	}
	
	public static boolean depositBalanceTo( Player player, double amount ) {
		return economy.depositPlayer( player, amount ).transactionSuccess();
		
	}
	
	public static boolean withdrawBalanceFrom( Player player, double amount ) {
		return economy.withdrawPlayer( player, amount ).transactionSuccess();
		
	}
	
	public static double getBalanceOf( Player player ) {
		return economy.getBalance( player );
		
	}
	
}