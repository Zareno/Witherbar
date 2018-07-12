package nl.marido.witherbar.handlers;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Witherbar extends BukkitRunnable {

	private static String title;
	private static String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	private static HashMap<UUID, Object> withers = new HashMap<>();

	public Witherbar(String title) {
		Witherbar.title = title;
		JavaPlugin fixed = nl.marido.witherbar.Witherbar.getInstance();
		runTaskTimer(fixed, 0, 0);
	}

	public static void addPlayer(Player player) {
		try {
			Class<?> craftworldclass = getObcClass("CraftWorld");
			Class<?> entitywitherclass = getNmsClass("EntityWither");
			Object craftworld = craftworldclass.cast(player.getWorld());
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
			Object craftplayer = craftplayerclass.cast(player);
			Object entityplayer = craftplayerclass.getMethod("getHandle").invoke(craftplayer);
			Class<?> packetclass = getNmsClass("Packet");
			Class<?> packetconnection = getNmsClass("PlayerConnection");
			Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
			packetconnection.getMethod("sendPacket", packetclass).invoke(packetconnection.cast(playerconnection), packet);
			withers.put(player.getUniqueId(), wither);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void removePlayer(Player player) {
		try {
			if (withers.containsKey(player.getUniqueId())) {
				Class<?> packetplayoutdestroy = getNmsClass("PacketPlayOutEntityDestroy");
				Object packet = packetplayoutdestroy.getConstructor(getNmsClass("EntityLiving")).newInstance(withers.get(player.getUniqueId()));
				withers.remove(player.getUniqueId());
				Class<?> packetconnection = getNmsClass("PlayerConnection");
				Class<?> packetclass = getNmsClass("Packet");
				Class<?> craftplayerclass = getObcClass("entity.CraftPlayer");
				Object craftplayer = craftplayerclass.cast(player);
				Object entityplayer = craftplayerclass.getMethod("getHandle").invoke(craftplayer);
				Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
				packetconnection.getMethod("sendPacket", packetclass).invoke(packetconnection.cast(playerconnection), packet);
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void setTitle(String title) {
		try {
			Witherbar.title = title;
			for (Entry<UUID, Object> entry : withers.entrySet()) {
				Object wither = entry.getValue();
				Class<?> entitywitherclass = getNmsClass("EntityWither");
				wither.getClass().getMethod("setCustomName", String.class).invoke(wither, title);
				Class<?> packetPlayOutEntityMetadata = getNmsClass("PacketPlayOutEntityMetadata");
				Object packet = packetPlayOutEntityMetadata.getConstructor(int.class, getNmsClass("DataWatcher"), boolean.class).newInstance((entitywitherclass.getMethod("getId").invoke(wither)), entitywitherclass.getMethod("getDataWatcher").invoke(wither), true);
				Class<?> craftplayerclass = getObcClass("entity.CraftPlayer");
				assert Bukkit.getPlayer(entry.getKey()) != null;
				Object craftplayer = craftplayerclass.cast(Bukkit.getPlayer(entry.getKey()));
				Object entityplayer = craftplayerclass.getMethod("getHandle").invoke(craftplayer);
				Class<?> packetclass = getNmsClass("Packet");
				Class<?> packetconnection = getNmsClass("PlayerConnection");
				Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
				packetconnection.getMethod("sendPacket", packetclass).invoke(packetconnection.cast(playerconnection), packet);
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void setProgress(float progress) {
		try {
			if (progress <= 0) {
				progress = (float) 0.001;
			}
			for (Entry<UUID, Object> entry : withers.entrySet()) {
				Object wither = entry.getValue();
				Class<?> entitywitherclass = getNmsClass("EntityWither");
				wither.getClass().getMethod("setHealth", float.class).invoke(wither, progress * (float) entitywitherclass.getMethod("getMaxHealth").invoke(wither));
				Class<?> packetPlayOutEntityMetadata = getNmsClass("PacketPlayOutEntityMetadata");
				Object packet = packetPlayOutEntityMetadata.getConstructor(int.class, getNmsClass("DataWatcher"), boolean.class).newInstance((entitywitherclass.getMethod("getId").invoke(wither)), entitywitherclass.getMethod("getDataWatcher").invoke(wither), true);
				Class<?> craftplayerclass = getObcClass("entity.CraftPlayer");
				assert Bukkit.getPlayer(entry.getKey()) != null;
				Object craftplayer = craftplayerclass.cast(Bukkit.getPlayer(entry.getKey()));
				Object entityplayer = craftplayerclass.getMethod("getHandle").invoke(craftplayer);
				Class<?> packetclass = getNmsClass("Packet");
				Class<?> packetconnection = getNmsClass("PlayerConnection");
				Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
				packetconnection.getMethod("sendPacket", packetclass).invoke(packetconnection.cast(playerconnection), packet);
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static boolean hasPlayer(Player player) {
		return withers.containsKey(player.getUniqueId());
	}

	public void run() {
		for (Entry<UUID, Object> entry : withers.entrySet()) {
			try {
				Object wither = entry.getValue();
				assert Bukkit.getPlayer(entry.getKey()) != null;
				Location location = getWitherLocation(Bukkit.getPlayer(entry.getKey()).getEyeLocation());
				Class<?> entitywitherclass = getNmsClass("EntityWither");
				entitywitherclass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(wither, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
				Class<?> packetPlayOutEntityTeleport = getNmsClass("PacketPlayOutEntityTeleport");
				Object packet = packetPlayOutEntityTeleport.getConstructor(getNmsClass("Entity")).newInstance(wither);
				Class<?> craftplayerclass = getObcClass("entity.CraftPlayer");
				assert Bukkit.getPlayer(entry.getKey()) != null;
				Object craftplayer = craftplayerclass.cast(Bukkit.getPlayer(entry.getKey()));
				Object entityplayer = craftplayerclass.getMethod("getHandle").invoke(craftplayer);
				Class<?> packetclass = getNmsClass("Packet");
				Class<?> packetconnection = getNmsClass("PlayerConnection");
				Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
				packetconnection.getMethod("sendPacket", packetclass).invoke(packetconnection.cast(playerconnection), packet);
			} catch (Exception error) {
				error.printStackTrace();
			}
		}
	}

	public static Location getWitherLocation(Location location) {
		return location.add(location.getDirection().normalize().multiply(20).add(new Vector(0, 5, 0)));
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