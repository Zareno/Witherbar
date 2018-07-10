package nl.marido.witherbar;

import org.bukkit.plugin.java.JavaPlugin;

import nl.marido.witherbar.handlers.Updater;

public class Resource extends JavaPlugin {

	public static Resource instance;

	public void onEnable() {
		System.out.println("Thank you for using the Witherbar resource.");
		instance = this;
		saveDefaultConfig();
		Updater.runUpdate();
	}

	public void onDisable() {
		System.out.println("Thank you for using the Witherbar resource.");
	}

	public static Resource getInstance() {
		return instance;
	}

}