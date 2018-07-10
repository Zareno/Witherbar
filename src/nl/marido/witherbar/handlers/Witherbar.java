package nl.marido.witherbar.handlers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

public class Witherbar extends BukkitRunnable {

	private static String title;
	private static HashMap<UUID, EntityWither> withers = new HashMap<UUID, EntityWither>();

	public Witherbar(String title) {
		Witherbar.title = title;
		JavaPlugin instance = nl.marido.witherbar.Witherbar.getInstance();
		runTaskTimer(instance, 0, 0);
	}

	public static void addPlayer(Player player) {
		EntityWither wither = new EntityWither(((CraftWorld) player.getWorld()).getHandle());
		Location location = getWitherLocation(player.getLocation());
		wither.setCustomName(title);
		wither.setInvisible(true);
		wither.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		withers.put(player.getUniqueId(), wither);
	}

	public static void removePlayer(Player player) {
		if (withers.containsKey(player.getUniqueId())) {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(withers.get(player.getUniqueId()).getId());
			withers.remove(player.getUniqueId());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void setTitle(String title) {
		Witherbar.title = title;
		for (Entry<UUID, EntityWither> entry : withers.entrySet()) {
			EntityWither wither = entry.getValue();
			wither.setCustomName(title);
			PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
			((CraftPlayer) Bukkit.getPlayer(entry.getKey())).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void setProgress(float progress) {
		for (Entry<UUID, EntityWither> entry : withers.entrySet()) {
			EntityWither wither = entry.getValue();
			if (progress <= 0)
				progress = (float) 0.001;
			wither.setHealth(progress * wither.getMaxHealth());
			PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
			((CraftPlayer) Bukkit.getPlayer(entry.getKey())).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static Location getWitherLocation(Location location) {
		return location.add(location.getDirection().normalize().multiply(20).add(new Vector(0, 5, 0)));
	}

	public void run() {
		for (Entry<UUID, EntityWither> entry : withers.entrySet()) {
			EntityWither wither = entry.getValue();
			Location location = getWitherLocation(Bukkit.getPlayer(entry.getKey()).getEyeLocation());
			wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(wither);
			((CraftPlayer) Bukkit.getPlayer(entry.getKey())).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static boolean hasPlayer(Player player) {
		return withers.containsKey(player.getUniqueId());
	}

}