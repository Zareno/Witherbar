package nl.marido.witherbar;

import org.bukkit.plugin.java.JavaPlugin;

import nl.marido.witherbar.handlers.Updater;

public class Witherbar extends JavaPlugin {

	public static Witherbar instance;

	public void onEnable() {
		instance = this;
		System.out.println("Thank you for using the Witherbar resource.");
		saveDefaultConfig();
		Updater.runChecks();
	}

	public void onDisable() {
		System.out.println("Thank you for using the Witherbar resource.");
	}

	public static Witherbar getInstance() {
		return instance;
	}

}