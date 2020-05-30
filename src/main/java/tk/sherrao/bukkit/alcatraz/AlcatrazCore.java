package tk.sherrao.bukkit.alcatraz;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tk.sherrao.bukkit.alcatraz.command.CrateKeyCommand;
import tk.sherrao.bukkit.alcatraz.command.CustomEnchantCommand;
import tk.sherrao.bukkit.alcatraz.data.CasinoManager;
import tk.sherrao.bukkit.alcatraz.data.CrateManager;
import tk.sherrao.bukkit.alcatraz.data.LocketteManager;
import tk.sherrao.bukkit.alcatraz.enchants.armour.BlastBackEnchantment;
import tk.sherrao.bukkit.alcatraz.enchants.armour.KnockBackEnchantment;
import tk.sherrao.bukkit.alcatraz.enchants.tools.BlastMineEnchantment;
import tk.sherrao.bukkit.alcatraz.enchants.tools.MoarExperienceEnchantment;
import tk.sherrao.bukkit.alcatraz.enchants.tools.NeverBreakEnchantment;
import tk.sherrao.bukkit.alcatraz.enchants.weapons.BlastHitterEnchantment;
import tk.sherrao.bukkit.alcatraz.enchants.weapons.JumperEnchantment;
import tk.sherrao.bukkit.alcatraz.enchants.weapons.RoadRunnerEnchantment;
import tk.sherrao.bukkit.alcatraz.listener.PlayerLogMiningListener;
import tk.sherrao.bukkit.alcatraz.listener.casino.PlayerCasinoInteractListener;
import tk.sherrao.bukkit.alcatraz.listener.casino.PlayerCasinoRegisterListener;
import tk.sherrao.bukkit.alcatraz.listener.casino.PlayerCasinoUnregisterListener;
import tk.sherrao.bukkit.alcatraz.listener.crates.PlayerCrateInteractListener;
import tk.sherrao.bukkit.alcatraz.listener.crates.PlayerCrateRegisterListener;
import tk.sherrao.bukkit.alcatraz.listener.crates.PlayerCrateUnregisterListener;
import tk.sherrao.bukkit.alcatraz.listener.doorlocker.PlayerLockedDoorInteractListener;
import tk.sherrao.bukkit.alcatraz.listener.doorlocker.PlayerLockedDoorRegisterListener;
import tk.sherrao.bukkit.alcatraz.listener.doorlocker.PlayerLockedDoorUnregisterListener;
import tk.sherrao.bukkit.utils.config.SherConfiguration;
import tk.sherrao.bukkit.utils.config.SherConfigurationAlt;
import tk.sherrao.bukkit.utils.plugin.SherPlugin;

public class AlcatrazCore extends SherPlugin {

	protected File messagesConfigFile, soundsConfigFile, enchantsConfigFile, casinoConfigFile, crateConfigFile, locketteConfigFile, 
		casinoDataFile, crateDataFile, locketteDataFile;
	protected SherConfiguration messagesConfig, soundsConfig;
	protected FileConfiguration casinoData, crateData, locketteData;
	protected SherConfigurationAlt enchantsConfig, casinoConfig, crateConfig, doorlockerConfig;

	protected EconomyHook economyHook;
	protected CasinoManager casinoMgr;
	protected CrateManager crateMgr;
	protected LocketteManager locketteMgr;
	
	@Override
	public void onLoad() {
		super.onLoad();
		
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		super.saveResource( "messages.conf.yml", false );
		super.saveResource( "sounds.conf.yml", false );
		super.saveResource( "enchants.conf.yml", false );
		super.saveResource( "casino.conf.yml", false );
		super.saveResource( "crates.conf.yml", false );
		super.saveResource( "lockette.conf.yml", false );

		
		messagesConfigFile = super.createFile( "messages.conf.yml" );
		soundsConfigFile = super.createFile( "sounds.conf.yml" );
		enchantsConfigFile = super.createFile( "enchants.conf.yml" );
		casinoConfigFile = super.createFile( "casino.conf.yml" );
		crateConfigFile = super.createFile( "crates.conf.yml" );
		locketteConfigFile = super.createFile( "lockette.conf.yml" );
		casinoDataFile = super.createFile( "casino.data.yml" );
		crateDataFile = super.createFile( "crates.data.yml" );
		locketteDataFile = super.createFile( "lockette.data.yml" );
		
		messagesConfig = new SherConfiguration( this, messagesConfigFile );
		soundsConfig = new SherConfiguration( this, soundsConfigFile );
		enchantsConfig = new SherConfigurationAlt( enchantsConfigFile );
		casinoConfig =  new SherConfigurationAlt( casinoConfigFile );
		crateConfig =  new SherConfigurationAlt( crateConfigFile );
		doorlockerConfig =  new SherConfigurationAlt( locketteConfigFile );
		casinoData = YamlConfiguration.loadConfiguration( casinoDataFile );
		crateData = YamlConfiguration.loadConfiguration( crateDataFile );
		locketteData = YamlConfiguration.loadConfiguration( locketteDataFile );
		
		economyHook = new EconomyHook( this );
		locketteMgr = new LocketteManager( this );
		crateMgr = new CrateManager( this );
		casinoMgr = new CasinoManager( this );
		casinoMgr.load();
		crateMgr.load();
		locketteMgr.load();
		
		super.registerEnchantment( new BlastBackEnchantment( this ) );
		super.registerEnchantment( new KnockBackEnchantment( this ) );
		super.registerEnchantment( new BlastMineEnchantment( this ) );
		super.registerEnchantment( new MoarExperienceEnchantment( this ) );
		super.registerEnchantment( new NeverBreakEnchantment( this ) );
		super.registerEnchantment( new BlastHitterEnchantment( this ) );
		super.registerEnchantment( new RoadRunnerEnchantment( this ) );
		super.registerEnchantment( new JumperEnchantment( this ) );

		
		super.registerEventListener( new PlayerCasinoInteractListener( this ) );
		super.registerEventListener( new PlayerCasinoRegisterListener( this ) );
		super.registerEventListener( new PlayerCasinoUnregisterListener( this ) );
		
		super.registerEventListener( new PlayerCrateInteractListener( this ) );
		super.registerEventListener( new PlayerCrateRegisterListener( this ) );
		super.registerEventListener( new PlayerCrateUnregisterListener( this ) );
		
		super.registerEventListener( new PlayerLockedDoorInteractListener( this ) );
		super.registerEventListener( new PlayerLockedDoorRegisterListener( this ) );
		super.registerEventListener( new PlayerLockedDoorUnregisterListener( this ) );
		
		super.registerEventListener( new PlayerLogMiningListener( this ) );
		
		super.registerCommand( "customenchants", new CustomEnchantCommand( this ) );
		super.registerCommand( "cratekey", new CrateKeyCommand( this ) );
		super.complete();
		
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
	}

	
	public SherConfiguration getMessagesConfig() {
		return messagesConfig;
		
	}
	
	public SherConfiguration getSoundsConfig() {
		return soundsConfig;
		
	}
	
	public FileConfiguration getEnchantsConfig() {
		return enchantsConfig;
		
	}
	
	public SherConfigurationAlt getCasinoConfig() {
		return casinoConfig;
		
	}
	
	public SherConfigurationAlt getCrateConfig() {
		return crateConfig;
		
	}
	
	public SherConfigurationAlt getDoorLockerConfig() {
		return doorlockerConfig;
		
	}
	
	public FileConfiguration getCasinoData() {
		return casinoData;
		
	}
	
	public FileConfiguration getCrateData() {
		return crateData;
		
	}
	
	public FileConfiguration getLocketteData() {
		return locketteData;
		
	}
	
	
	
	public File getMessagesConfigFile() {
		return messagesConfigFile;
		
	}
	
	public File getSoundsConfigFile() {
		return soundsConfigFile;
		
	}
	
	public File getEnchantsConfigFile() {
		return enchantsConfigFile;
		
	}
	
	public File getCasinoConfigFile() {
		return casinoConfigFile;
		
	}
	
	public File getCrateConfigFile() {
		return crateConfigFile;
		
	}
	
	public File getDoorLockerConfigFile() {
		return locketteConfigFile;
		
	}
	
	public File getCasinoDataFile() {
		return casinoDataFile;
		
	}
	
	public File getCrateDataFile() {
		return crateDataFile;
		
	}
	
	public File getLocketteDataFile() {
		return locketteDataFile;
		
	}
	
	
	
	public EconomyHook getEconomyHook() {
		return economyHook;
		
	}
	
	public CasinoManager getCasinoManager() {
		return casinoMgr;
		
	}
	
	public CrateManager getCrateManager() {
		return crateMgr;
		
	}

	public LocketteManager getLocketteManager() {
		return locketteMgr;
		
	}
	
}