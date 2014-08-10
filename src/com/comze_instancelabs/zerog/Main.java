package com.comze_instancelabs.zerog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import net.minecraft.server.v1_7_R3.EntityFallingBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaSetup;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.config.DefaultConfig;
import com.comze_instancelabs.minigamesapi.config.MessagesConfig;
import com.comze_instancelabs.minigamesapi.config.StatsConfig;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;
import com.comze_instancelabs.zerog.gravitygun.CustomEntityType;
import com.comze_instancelabs.zerog.gravitygun.GravityGun;
import com.comze_instancelabs.zerog.gravitygun.MainListener;
import com.comze_instancelabs.zerog.nms.register1_7_9;
import com.shampaggon.crackshot.CSUtility;

public class Main extends JavaPlugin implements Listener {

	// allow selecting team

	// GRAVITY GUN

	public static int moveid;
	public static int invisibiltyid;

	public static HashMap<String, Entity> map = new HashMap<>();
	public static HashMap<EntityFallingBlock, String> thrown = new HashMap<>();
	public static HashMap<String, ItemStack[]> inventorys = new HashMap<>();
	public static HashMap<String, CreatureSpawner> spawners = new HashMap<>();
	public static HashMap<String, Object> tilentities = new HashMap<>();

	public static final UUID ID = UUID.fromString("5f473430-3275-4951-abc9-d39f4be8ce26");

	static File f = new File("plugins/ZeroG/GravityGuns.yml");
	@SuppressWarnings("static-access")
	static FileConfiguration con = new YamlConfiguration().loadConfiguration(f);

	public static HashMap<String, GravityGun> reg = new HashMap<>();

	public static Logger log;

	// /GG

	MinigamesAPI api = null;
	PluginInstance pli = null;
	static Main m = null;
	IArenaScoreboard scoreboard = new IArenaScoreboard(this);
	ICommandHandler cmdhandler = new ICommandHandler();

	public static HashMap<String, String> pteam = new HashMap<String, String>();

	public void onEnable() {
		m = this;
		api = MinigamesAPI.getAPI().setupAPI(this, "zerog", IArena.class, new ArenasConfig(this), new MessagesConfig(this), new IClassesConfig(this), new StatsConfig(this, false), new DefaultConfig(this, false), true);
		PluginInstance pinstance = api.pinstances.get(this);
		pinstance.addLoadedArenas(loadArenas(this, pinstance.getArenasConfig()));
		Bukkit.getPluginManager().registerEvents(this, this);
		pinstance.scoreboardManager = new IArenaScoreboard(this);
		pinstance.arenaSetup = new IArenaSetup();
		IArenaListener listener = new IArenaListener(this, pinstance, "zerog");
		pinstance.setArenaListener(listener);
		MinigamesAPI.getAPI().registerArenaListenerLater(this, listener);
		pli = pinstance;

		register1_7_9.registerEntities();

		log = this.getLogger();

		CustomEntityType.registerEntities();

		if (con.getList("GravityGuns") == null) {
			reg = new HashMap<>();
			GravityGun g = new GravityGun("default");

			ArrayList<String> lore = new ArrayList<>();
			ArrayList<String> blockpickup = new ArrayList<>();
			ArrayList<String> blockpickupexceptions = new ArrayList<>();
			ArrayList<String> entitypickup = new ArrayList<>();
			ArrayList<String> entitypickupexception = new ArrayList<>();

			lore.add("§6Move Blocks and Entitys!");
			g.setLore(lore);
			blockpickup.add("all");
			g.setBlockpickup(blockpickup);
			blockpickupexceptions.add("bedrock");
			g.setBlockpickupexeptions(blockpickupexceptions);
			entitypickup.add("all");
			g.setEntitypickup(entitypickup);

			entitypickupexception.add("player");
			entitypickupexception.add("wither");
			entitypickupexception.add("enderdragon");
			g.setEntitypickupexeptions(entitypickupexception);

			g.setCanthrow(true);
			g.setDisplay(new ItemStack(Material.COAL));

			ArrayList<Map<String, Object>> gravl = new ArrayList<>();
			gravl.add(g.serialize());

			con.set("GravityGuns", gravl);

			try {
				con.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}

			reg.put(g.getName(), g);

		} else {
			@SuppressWarnings("unchecked")
			ArrayList<Map<String, Object>> temp = (ArrayList<Map<String, Object>>) con.getList("GravityGuns");

			for (Map<String, Object> map : temp) {

				GravityGun g = GravityGun.deserialize(map);
				g.setName(g.getName().replace(";", ":"));

				reg.put(GravityGun.deserialize(map).getName().replace(";", ":"), GravityGun.deserialize(map));
			}
		}

		Bukkit.getPluginManager().registerEvents(new MainListener(this), this);

		this.getConfig().addDefault("Movement-update-intervall", 1);
		this.getConfig().addDefault("Sound", "note_pling");
		this.getConfig().addDefault("Sound-pitch", 1);
		this.getConfig().addDefault("Sound-volume", 1);
		this.getConfig().addDefault("Auto-ignite-tnt", true);
		this.getConfig().addDefault("Tnt-fuseticks", 50);
		this.getConfig().addDefault("Vector", 0.2);
		this.getConfig().addDefault("Distance", 5);

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		final JavaPlugin plugin = this;
		moveid = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (String s : Main.map.keySet()) {
					Player p = Bukkit.getPlayerExact(s);
					Vector vec = Main.getTargetLocation(p, plugin.getConfig().getInt("Distance")).toVector().subtract(Main.map.get(s).getLocation().toVector());
					try {
						Main.map.get(s).setVelocity(vec);
					} catch (Exception exc) {
						Main.map.remove(p.getName());
					}
				}

				for (String p_ : pli.global_players.keySet()) {
					Player p = Bukkit.getPlayer(p_);
					for (Entity ent : p.getNearbyEntities(2D, 2D, 2D)) {
						if (ent.getType() == EntityType.FALLING_BLOCK && ent.hasMetadata("1337")) {
							List<MetadataValue> data = ent.getMetadata("1337");
							String p__ = data.get(0).asString();
							if(p_ != p__){
								Location l = ent.getLocation();
								l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 1F, false, false);
								ent.remove();
							}
						}
					}
				}
			}
		}, this.getConfig().getLong("Movement-update-intervall"), 1);
		invisibiltyid = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Entity ent : Main.map.values()) {
					if (ent instanceof LivingEntity) {
						PotionEffect pot = new PotionEffect(PotionEffectType.INVISIBILITY, 6000, 1);
						((LivingEntity) ent).addPotionEffect(pot);
					}
				}
			}
		}, 5000, 1);
	}

	public static ArrayList<Arena> loadArenas(JavaPlugin plugin, ArenasConfig cf) {
		ArrayList<Arena> ret = new ArrayList<Arena>();
		FileConfiguration config = cf.getConfig();
		if (!config.isSet("arenas")) {
			return ret;
		}
		for (String arena : config.getConfigurationSection("arenas.").getKeys(false)) {
			if (Validator.isArenaValid(plugin, arena, cf.getConfig())) {
				ret.add(initArena(arena));
			}
		}
		return ret;
	}

	public static IArena initArena(String arena) {
		IArena a = new IArena(m, arena);
		ArenaSetup s = MinigamesAPI.getAPI().pinstances.get(m).arenaSetup;
		a.init(Util.getSignLocationFromArena(m, arena), Util.getAllSpawns(m, arena), Util.getMainLobby(m), Util.getComponentForArena(m, arena, "lobby"), s.getPlayerCount(m, arena, true), s.getPlayerCount(m, arena, false), s.getArenaVIP(m, arena));
		return a;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cmdhandler.handleArgs(this, "mgzerog", "/" + cmd.getName(), sender, args);
	}

	public void addGear(String p_) {
		Player p = Bukkit.getPlayer(p_);

		ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
		LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();

		ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS, 1);
		LeatherArmorMeta lam1 = (LeatherArmorMeta) lboots.getItemMeta();

		ItemStack lchestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		LeatherArmorMeta lam2 = (LeatherArmorMeta) lchestplate.getItemMeta();

		ItemStack lleggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		LeatherArmorMeta lam3 = (LeatherArmorMeta) lleggings.getItemMeta();

		if (m.pteam.containsKey(p_)) {
			Color c = Color.BLACK;
			if (m.pteam.get(p_).equalsIgnoreCase("red")) {
				c = Color.RED;
			} else {
				c = Color.BLUE;
			}
			lam3.setColor(c);
			lam2.setColor(c);
			lam1.setColor(c);
			lam.setColor(c);
		}

		lhelmet.setItemMeta(lam);
		lboots.setItemMeta(lam1);
		lchestplate.setItemMeta(lam2);
		lleggings.setItemMeta(lam3);

		p.getInventory().setBoots(lboots);
		// p.getInventory().setHelmet(lhelmet);
		p.getInventory().setChestplate(lchestplate);
		// p.getInventory().setLeggings(lleggings);
		p.updateInventory();

		CSUtility cs = new CSUtility();
		String[] guns = getGunsFromClass(pli.getPClasses().get(p.getName()).getName());
		if (guns == null) {
			cs.giveWeapon(p, "python", 1);
		} else {
			for (String g : guns) {
				cs.giveWeapon(p, g, 1);
			}
		}

		GravityGun g = Main.reg.get("default");
		if (g != null) {
			p.getInventory().addItem(g.getItemStack());
		}

		p.updateInventory();
	}

	public String[] getGunsFromClass(String kit) {
		String all = pli.getClassesConfig().getConfig().getString("config.kits." + kit + ".guns");
		if (all != null) {
			return all.split(";");
		}
		return null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		if (pli.global_players.containsKey(p.getName())) {
			IArena a = (IArena) pli.global_players.get(p.getName());
			if (a.getArenaState() == ArenaState.INGAME) {
				if (p.getLocation().getY() < 0) {
					// player fell
					if (pteam.containsKey(p.getName())) {
						String team = pteam.get(p.getName());
						if (team.equalsIgnoreCase("red")) {
							if (!a.addBluePoints()) {
								Util.teleportPlayerFixed(p, a.getSpawns().get(0));
								m.addGear(p.getName());
							}
						} else {
							if (!a.addRedPoints()) {
								Util.teleportPlayerFixed(p, a.getSpawns().get(1));
								m.addGear(p.getName());
							}
						}
						scoreboard.updateScoreboard(a);
					}
					return;
				}

				if (a.cgravity) {
					Vector v = p.getVelocity();
					Vector v2 = v.multiply(new Vector(0.9D, 0.95D, 0.9D));
					// p.setVelocity(p.getVelocity().multiply(0.6D));
					p.setVelocity(v2);
				}

				// TODO chicken idea

			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (pli.global_players.containsKey(p.getName())) {
				event.setDeathMessage(null);
				IArena a = (IArena) pli.global_players.get(p.getName());
				if (a.getArenaState() == ArenaState.INGAME) {
					p.setHealth(20D);
					if (p.getKiller() instanceof Player) {
						Player killer = (Player) p.getKiller();
						if (pteam.get(killer.getName()).equalsIgnoreCase("red")) {
							if (!a.addRedPoints()) {
								Util.teleportPlayerFixed(p, a.getSpawns().get(0));
								m.addGear(p.getName());
							}
						} else {
							if (!a.addBluePoints()) {
								Util.teleportPlayerFixed(p, a.getSpawns().get(1));
								m.addGear(p.getName());
							}
						}
						p.sendMessage(ChatColor.RED + "You have been disintegrated by " + ChatColor.DARK_RED + killer.getName() + ChatColor.RED + ".");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (pli.global_players.containsKey(p.getName())) {
			IArena a = (IArena) pli.global_players.get(p.getName());
			if (a.getArenaState() == ArenaState.INGAME) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		final Player p = event.getPlayer();
		if (pli.global_players.containsKey(p.getName())) {
			IArena a = (IArena) pli.global_players.get(p.getName());
			if (a.getArenaState() == ArenaState.INGAME) {
				if (event.getBlock().getType() == Material.STAINED_GLASS) {
					event.setCancelled(true);
				}
			}
		}
	}

	// GRAVITY GUN

	@Override
	public void onDisable() {

		this.getServer().getScheduler().cancelTask(moveid);
		this.getServer().getScheduler().cancelTask(invisibiltyid);

		this.getServer().resetRecipes();

		map.clear();
		reg.clear();

	}

	public static Location getTargetLocation(Player p, int maxDistance) {

		Location loc = p.getEyeLocation();

		Vector v = loc.getDirection().normalize();

		for (int i = 1; i <= maxDistance; i++) {
			loc.add(v);
			if (loc.getBlock().getType() != Material.AIR)
				return loc;
		}

		return loc;

	}

	public static ArrayList<String> toUpperCase(ArrayList<String> strings) {
		for (int i = 0, l = strings.size(); i < l; ++i) {
			strings.set(i, strings.get(i).toUpperCase());
		}
		return strings;

	}

}
