package nl.marido.witherbar.handlers;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;

public class Witherbar extends BukkitRunnable {

	private static String title;
	private static String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	private static HashMap<Player, Object> withers = new HashMap<>();

	public Witherbar(String title) {
		Witherbar.title = title;
		JavaPlugin fixed = nl.marido.witherbar.Witherbar.getInstance();
		runTaskTimer(fixed, 0, 0);
	}

	public static void addPlayer(Player player) {
		try {
			Class<?> craftworldclass = getObcClass("CraftWorld");
			Class<?> entitywitherclass = getNmsClass("EntityWither");
			Object craftworld = entitywitherclass.cast(player.getWorld());
			Object worldserver = craftworldclass.getMethod("getHandle").invoke(craftworld);
			Constructor<?> witherconstructor = entitywitherclass.getConstructor(getNmsClass("World"));
			Object wither = witherconstructor.newInstance(worldserver);
			Location location = getWitherLocation(player.getLocation());
			wither.getClass().getMethod("setCustomName", String.class).invoke(wither, title);
			wither.getClass().getMethod("setInvisible", boolean.class).invoke(wither, true);
			wither.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(wither, location.getX(), location.getY(), location.getZ(), 0, 0);
			Class<?> packetentinitylivingout = getNmsClass("PacketPlayOutSpawnEntityLiving");
			Object packet = packetentinitylivingout.getConstructor(getNmsClass("EntityLiving")).newInstance(wither);
			Class<?> craftplayerclass = getObcClass("entity.CraftPlayer");
			System.out.println(craftplayerclass);
			Object craftplayer = craftplayerclass.cast(player);
			Object entityplayer = craftplayerclass.getMethod("getHandle").invoke(craftplayer);
			Class<?> packetclass = getNmsClass("Packet");
			Class<?> packetconnection = getNmsClass("PlayerConnection");
			Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
			packetconnection.getMethod("sendPacket", packetclass).invoke(packetconnection.cast(playerconnection), packet);
			withers.put(player, wither);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static Location getWitherLocation(Location location) {
		return location.add(location.getDirection().normalize().multiply(20).add(new Vector(0, 5, 0)));
	}

	public void run() {
		for (Entry<Player, Object> entry : withers.entrySet()) {
			EntityWither wither = (EntityWither) entry.getValue();
			Location location = getWitherLocation(entry.getKey().getEyeLocation());
			wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(wither);
			((CraftPlayer) entry.getKey()).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static boolean hasPlayer(Player player) {
		return withers.containsKey(player);
	}

	public static Class<?> getNmsClass(String classname) {
		String fullname = "net.minecraft.server." + version + classname;
		Class<?> realclass = null;
		try {
			realclass = Class.forName(fullname);
		} catch (Exception error) {
			error.printStackTrace();
		}
		return realclass;
	}

	public static Class<?> getObcClass(String classname) {
		String fullname = "org.bukkit.craftbukkit." + version + classname;
		Class<?> realclass = null;
		try {
			realclass = Class.forName(fullname);
		} catch (Exception error) {
			error.printStackTrace();
		}
		return realclass;
	}

}