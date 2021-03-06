package com.comze_instancelabs.zerog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.ArenaType;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.util.Cuboid;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.zerog.nms.ZGBat;
import com.comze_instancelabs.zerog.nms.ZGFallingBlock1_7_10;
import com.comze_instancelabs.zerog.nms.register1_7_10;

public class IArena extends Arena {

	public static Main m;

	int blue = 0;
	int red = 0;

	int winscore = 50;

	boolean cteam = true;

	int c = 30;
	boolean cgravity = false;

	protected HashMap<UUID, Vector> velocities;
	protected HashMap<UUID, Location> positions;
	protected HashMap<UUID, Boolean> onGround;

	public IArena(Main m, String arena_id) {
		super(m, arena_id, ArenaType.REGENERATION);
		this.m = m;
	}

	public boolean addBluePoints() {
		blue++;
		if (blue > 100) {
			for (String p_ : this.getAllPlayers()) {
				if (m.pteam.containsKey(p_)) {
					if (m.pteam.get(p_).equalsIgnoreCase("red")) {
						MinigamesAPI.getAPI().pinstances.get(m).global_lost.put(p_, this);
					}
				}
			}
			this.stop();
			return true;
		}
		return false;
	}

	public boolean addRedPoints() {
		red++;
		if (red > 100) {
			for (String p_ : this.getAllPlayers()) {
				if (m.pteam.containsKey(p_)) {
					if (m.pteam.get(p_).equalsIgnoreCase("blue")) {
						MinigamesAPI.getAPI().pinstances.get(m).global_lost.put(p_, this);
					}
				}
			}
			this.stop();
			return true;
		}
		return false;
	}

	@Override
	public void joinPlayerLobby(String playername) {
		super.joinPlayerLobby(playername);
		if (cteam) {
			m.pteam.put(playername, "red");
			cteam = false;
		} else {
			m.pteam.put(playername, "blue");
			cteam = true;
		}
	}

	@Override
	public void leavePlayer(String player, boolean full) {
		super.leavePlayer(player, full);
		Player p = Bukkit.getPlayer(player);
		p.removePotionEffect(PotionEffectType.JUMP);
		p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
	}

	@Override
	public void spectate(String playername) {

	}

	BukkitTask tt;
	int currentingamecount;

	@Override
	public void start(boolean tp) {
		this.velocities = new HashMap();
		this.onGround = new HashMap();
		this.positions = new HashMap();
		red = 0;
		blue = 0;
		final IArena a = this;

		for (String p_ : a.getArena().getAllPlayers()) {
			Player p = Bukkit.getPlayer(p_);
			if (m.pteam.get(p_).equalsIgnoreCase("red")) {
				Util.teleportPlayerFixed(p, a.getSpawns().get(0));
			} else if (m.pteam.get(p_).equalsIgnoreCase("blue")) {
				Util.teleportPlayerFixed(p, a.getSpawns().get(1));
			}
		}

		super.start(false);

		m.scoreboard.updateScoreboard(this);
		tt = Bukkit.getScheduler().runTaskTimer(m, new Runnable() {
			public void run() {
				if (a.getArenaState() == ArenaState.INGAME) {
					c--;
					m.scoreboard.updateScoreboard(a);
					if (c < 1) {
						c = 30;
						cgravity = !cgravity;
						toggleGravity(cgravity);
					}
				}
			}
		}, 20L, 20L);
	}

	@Override
	public void started() {
		final IArena a = this;
		Bukkit.getScheduler().runTaskLater(m, new Runnable() {
			public void run() {
				for (String p_ : a.getAllPlayers()) {
					m.addGear(p_);
				}
			}
		}, 20L);
	}

	@Override
	public void stop() {
		super.stop();
		if (tt != null) {
			tt.cancel();
		}
		clearBlocks();
	}

	public void toggleGravity(boolean t) {
		if (t) {
			for (String p_ : this.getAllPlayers()) {
				Player p = Bukkit.getPlayer(p_);
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100000, 7));
				// p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 1));
				p.setFoodLevel(5);
				p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Zero Gravity Mode " + ChatColor.DARK_GREEN + "Activitated");
			}

			ArrayList<Block> blocks = new ArrayList<Block>();
			ArrayList<Location> locs = new ArrayList<Location>(getSurfaceBlocks(10));
			World w = locs.get(0).getWorld();
			for (Location loc : locs) {
				Location temp = w.getHighestBlockAt(loc).getLocation().add(0D, -1D, 0D);
				blocks.add(w.getBlockAt(temp));
			}
			floatUpTimer(blocks);
		} else {
			for (String p_ : this.getAllPlayers()) {
				Player p = Bukkit.getPlayer(p_);
				p.removePotionEffect(PotionEffectType.JUMP);
				p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
				// p.removePotionEffect(PotionEffectType.SLOW);
				p.setFoodLevel(20);
				p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Gravity restored");
			}

			if (timertask != null) {
				timertask.cancel();
			}

			for (ZGBat t_ : bats) {
				// t_.setmY(-0.1D);
				t_.motY = -0.1D;
			}

			Bukkit.getScheduler().runTaskLater(m, new Runnable() {
				public void run() {
					clearBlocks();
				}
			}, 50L);
		}
	}

	Random r = new Random();

	public ArrayList<Location> getSurfaceBlocks(int percentage) {
		ArrayList<Location> ret = new ArrayList<Location>();
		// get low/high border, calculate 10% of x*z surface and get highest block with world.getHighestY
		// put into arraylist of blocks
		// iterate through list and remove each block and spawn a fallingblock
		// start timer to move all blocks up (with random height)
		Cuboid c = new Cuboid(Util.getComponentForArena(m, this.getName(), "bounds.low"), Util.getComponentForArena(m, this.getName(), "bounds.high"));
		World w = c.getLowLoc().getWorld();
		int minx = c.getLowLoc().getBlockX();
		int minz = c.getLowLoc().getBlockZ();
		int maxx = c.getHighLoc().getBlockX();
		int maxz = c.getHighLoc().getBlockZ();
		for (int x = minx; x < maxx; x++) {
			for (int z = minz; z < maxz; z++) {
				if (r.nextInt(100) < 10) {
					ret.add(new Location(w, x, 0, z));
				}
			}
		}
		return ret;
	}

	int ccount = 0;
	BukkitTask timertask = null;
	ArrayList<ZGFallingBlock1_7_10> fallingblocks = new ArrayList<ZGFallingBlock1_7_10>();
	ArrayList<ZGBat> bats = new ArrayList<ZGBat>();

	HashMap<Location, Material> oldblocks = new HashMap<Location, Material>();

	public void floatUpTimer(final ArrayList<Block> blocks) {
		for (Block b : blocks) {
			oldblocks.put(b.getLocation(), b.getType());
			ZGBat t = register1_7_10.spawnBlock(m, this.getName(), b);
			bats.add(t);
			Bat b_ = (Bat) t.getBukkitEntity();
			b_.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000, 1));
			t.hinit();
		}
		Bukkit.getScheduler().runTaskLater(m, new Runnable() {
			public void run() {
				for (Block b : blocks) {
					b.setType(Material.AIR);
				}
			}
		}, 7L);
		timertask = Bukkit.getScheduler().runTaskTimer(m, new Runnable() {
			public void run() {
				for (ZGBat t : bats) {
					// t.getBukkitEntity().setVelocity(new Vector(0D, 1.5D, 0D));
					t.motY = 0.04D;
					t.setmY(0.04D);
					((Bat) t.getBukkitEntity()).setVelocity(new Vector(0D, 0.04D * t.getR(), 0D));
					// t.setPosition(t.locX, t.locY + 0.05D, t.locZ);
					// t.setmY(0.2D * Math.random());
				}
				ccount++;
				if (ccount > 100) {
					for (ZGBat t : bats) {
						t.motY = 0.001D;
						t.setmY(0.001D);
						// t.setmY(0D);
					}
					// timertask.cancel();
				}
			}
		}, 0L, 1L);
	}

	public void clearBlocks() {
		for (Location l : oldblocks.keySet()) {
			l.getWorld().getBlockAt(l).setType(oldblocks.get(l));
		}
		for (ZGBat t : bats) {
			Bat b = (Bat) t.getBukkitEntity();
			if (b.getPassenger() != null) {
				b.getPassenger().remove();
			}
			b.remove();
		}
		bats.clear();
		fallingblocks.clear();
		oldblocks.clear();
		ccount = 0;
	}

	public void updateVelocities() {
		if (!this.cgravity) {
			return;
		}
		// Iterator<Entity> j;
		for (World i : m.getServer().getWorlds()) {
			World world = i;
			for (Entity e : world.getEntities()) {
				if (e instanceof Player) {
					if (!this.getAllPlayers().contains(((Player) e).getName())) {
						continue;
					}
				}
				Vector newv = e.getVelocity().clone();
				UUID uuid = e.getUniqueId();
				if ((this.velocities.containsKey(uuid)) && (this.onGround.containsKey(uuid)) && (!e.isOnGround()) && (!e.isInsideVehicle())) {
					Vector oldv = (Vector) this.velocities.get(uuid);
					if (!((Boolean) this.onGround.get(uuid)).booleanValue()) {
						Vector d = oldv.clone();
						d.subtract(newv);
						double dy = d.getY();
						if ((dy > 0.0D) && ((newv.getY() < -0.01D) || (newv.getY() > 0.01D))) {
							Location loc = e.getLocation().clone();
							double gravity = 0.2D;
							// double gravity = 1.0D;
							/*
							 * while (loc.getBlockY() >= 0) { Block block = loc.getBlock(); if (block.getType() == Material.WOOL) { if
							 * (block.getData() == 5) { gravity = 0.2D; } else if (block.getData() == 14) { gravity = 5.0D; } else if (block.getData()
							 * == 9) { gravity = -0.2D; } else if (block.getData() == 2) { gravity = -5.0D; } } if (block.getType() != Material.AIR) {
							 * break; } loc.setY(loc.getY() - 1.0D); }
							 */
							newv.setY(oldv.getY() - dy * gravity);
							boolean newxchanged = (newv.getX() < -0.001D) || (newv.getX() > 0.001D);
							boolean oldxchanged = (oldv.getX() < -0.001D) || (oldv.getX() > 0.001D);
							if ((newxchanged) && (oldxchanged)) {
								newv.setX(oldv.getX());
							}
							boolean newzchanged = (newv.getZ() < -0.001D) || (newv.getZ() > 0.001D);
							boolean oldzchanged = (oldv.getZ() < -0.001D) || (oldv.getZ() > 0.001D);
							if ((newzchanged) && (oldzchanged)) {
								newv.setZ(oldv.getZ());
							}
							e.setVelocity(newv.clone());
						}
					}
					if (((e instanceof Player)) && (this.positions.containsKey(uuid))) {
						Vector pos = e.getLocation().toVector();
						Vector oldpos = ((Location) this.positions.get(uuid)).toVector();
						Vector velocity = pos.subtract(oldpos);
						newv.setX(velocity.getX());
						newv.setZ(velocity.getZ());
					}
					e.setVelocity(newv.clone());
				}
				this.velocities.put(uuid, newv.clone());
				this.onGround.put(uuid, Boolean.valueOf(e.isOnGround()));
				this.positions.put(uuid, e.getLocation());
			}
		}
	}

}
