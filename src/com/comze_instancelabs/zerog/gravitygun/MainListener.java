package com.comze_instancelabs.zerog.gravitygun;

import java.util.UUID;

import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftFallingSand;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.zerog.Main;
import com.comze_instancelabs.zerog.nms.MEBat;
import com.comze_instancelabs.zerog.nms.MEFallingBlock1_7_10;
import com.comze_instancelabs.zerog.nms.MEFallingBlock1_7_9;

public class MainListener implements Listener {

	public static final UUID ID = UUID.fromString("5f473430-3275-4951-abc9-d39f4be8ce26");

	private Main plugin;

	public MainListener(Main g) {
		this.plugin = g;
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent e) {
		if (Main.map.containsKey(e.getPlayer().getName())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Main.map.remove(e.getPlayer().getName());
	}

	@SuppressWarnings({ "unused", "deprecation" })
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL) {
			return;
		}

		final Player p = e.getPlayer();
		if (e.getItem() == null) {
			return;
		}

		AttributeStorage store = AttributeStorage.newTarget(NbtFactory.getCraftItemStack(e.getItem()), ID);
		if (store.getData(null) == null) {
			return;
		}

		if (!(store.getData(null).startsWith("gravitygun;"))) {
			return;
		}

		String[] split = store.getData(null).split(";");
		if ((!split[0].equals("gravitygun"))) {
			return;
		}
		for (String s : Main.reg.keySet()) {
			if (s.equals(split[1])) {
				GravityGun g = Main.reg.get(s);

				if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (g.isCanthrow()) {
						if (!(Main.map.containsKey(p.getName()))) {
							return;
						}
						Vector v = Main.map.get(p.getName()).getLocation().toVector().subtract(p.getLocation().toVector());
						Entity ent = Main.map.get(p.getName()).getPassenger();
						Main.map.get(p.getName()).remove();
						Main.map.remove(p.getName());
						if (e == null) {
							e.setCancelled(true);
							return;
						}
						if (ent instanceof FallingBlock) {
							FallingBlock fb = (FallingBlock) ent;
							fb.setMetadata("1337", new FixedMetadataValue(plugin, p.getName()));
							if (fb.getMaterial().equals(Material.TNT)) {
								TNTPrimed tnt = (TNTPrimed) p.getWorld().spawnEntity(fb.getLocation(), EntityType.PRIMED_TNT);
								tnt.setVelocity(v.multiply(plugin.getConfig().getDouble("Vector")));
								fb.remove();
							} else {
								ent.setVelocity(v.multiply(plugin.getConfig().getDouble("Vector")));
								Main.thrown.put(((CraftFallingSand) ent).getHandle(), p.getName());
								((MEFallingBlock1_7_10) ((CraftFallingSand) ent).getHandle()).setF(false);
							}
						} else {
							ent.setVelocity(v.multiply(0.2));
						}

						e.setCancelled(true);
						return;
					}
				}

				if (Main.map.containsKey(p.getName())) {
					Entity ent = Main.map.get(p.getName());
					if (ent.getPassenger() instanceof FallingBlock) {
						FallingBlock fb = (FallingBlock) ent.getPassenger();
						final org.bukkit.block.Block b = ent.getWorld().getBlockAt(ent.getLocation());
						b.setType(fb.getMaterial());
						b.setData(fb.getBlockData());

						Object o = Main.tilentities.get(p.getName());

						if (o != null) {
							this.setMeta(o, b.getLocation());
						}

						Main.map.get(p.getName()).remove();
						Main.map.remove(p.getName());
						fb.remove();

						e.setCancelled(true);
						p.getWorld().playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound").toUpperCase()), plugin.getConfig().getInt("Sound-volume"), plugin.getConfig().getInt("Sound-pitch"));
						return;
					}

					p.getWorld().playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound").toUpperCase()), plugin.getConfig().getInt("Sound-volume"), plugin.getConfig().getInt("Sound-pitch"));

					Main.map.get(p.getName()).remove();
					Main.map.remove(p.getName());

					e.setCancelled(true);
					return;
				}

				if (e.getClickedBlock() == null) {
					return;
				}
				if (!(g.getBlockpickupexeptions().contains(e.getClickedBlock().getType().toString())) || ((g.getBlockpickupexeptions().contains("ALL")))) {
					if (g.getBlockpickup().contains(e.getClickedBlock().getType().toString()) || g.getBlockpickup().contains("ALL")) {

						Main.tilentities.put(p.getName(), getMeta(e.getClickedBlock()));
						World mcWorld = ((CraftWorld) e.getPlayer().getWorld()).getHandle();

						//MEBat bat = new MEBat(mcWorld, e.getClickedBlock().getLocation());
						//Bat bukkitbat = (Bat) bat.getBukkitEntity();
						//bat.setPosition(e.getClickedBlock().getLocation().getX(), e.getClickedBlock().getLocation().getY(), e.getClickedBlock().getLocation().getZ());

						Block b = CraftMagicNumbers.getBlock(e.getClickedBlock().getType());
						
						String arena = "";
						if(MinigamesAPI.getAPI().pinstances.get(plugin).global_players.containsKey(e.getPlayer().getName())){
							arena = MinigamesAPI.getAPI().pinstances.get(plugin).global_players.get(e.getPlayer().getName()).getName();
						}
						//MEFallingBlock1_7_10 falling = new MEFallingBlock1_7_10(mcWorld, e.getClickedBlock().getLocation().getX(), e.getClickedBlock().getLocation().getY() + 1, e.getClickedBlock().getLocation().getZ(), b, e.getClickedBlock().getData());
						//MEFallingBlock1_7_10 falling = new MEFallingBlock1_7_10(plugin, arena, e.getClickedBlock().getLocation(), (net.minecraft.server.v1_7_R4.World) ((CraftWorld) e.getClickedBlock().getWorld()).getHandle());
						//falling.id = Block.getById(e.getClickedBlock().getTypeId());
						//falling.data = e.getClickedBlock().getData();
						//FallingBlock bukkitfalling = (FallingBlock) falling.getBukkitEntity();
						//bukkitbat.setPassenger(bukkitfalling);
						//bukkitbat.setCustomNameVisible(false);

						final Object w = ((CraftWorld) e.getClickedBlock().getWorld()).getHandle();
						final MEBat b_ = new MEBat((net.minecraft.server.v1_7_R4.World) ((CraftWorld) e.getClickedBlock().getWorld()).getHandle(), e.getClickedBlock().getLocation(), false);
						final MEFallingBlock1_7_10 t_ = new MEFallingBlock1_7_10(plugin, arena, e.getClickedBlock().getLocation(), (net.minecraft.server.v1_7_R4.World) ((CraftWorld) e.getClickedBlock().getWorld()).getHandle(), true);

						t_.id = Block.getById(e.getClickedBlock().getTypeId());
						t_.data = e.getClickedBlock().getData();
						Bat bb = (Bat) b_.getBukkitEntity();
						FallingBlock bf = (FallingBlock) t_.getBukkitEntity();
						bb.setPassenger(bf);

						((net.minecraft.server.v1_7_R4.World) w).addEntity(b_, CreatureSpawnEvent.SpawnReason.CUSTOM);
						((net.minecraft.server.v1_7_R4.World) w).addEntity(t_, CreatureSpawnEvent.SpawnReason.CUSTOM);

						Bat bukkitbat = bb;
						e.getClickedBlock().setType(Material.AIR);
						
						//mcWorld.addEntity(bat);
						//mcWorld.addEntity(falling);

						PotionEffect pot = new PotionEffect(PotionEffectType.INVISIBILITY, 10000, 1);
						bukkitbat.addPotionEffect(pot);
						Main.map.put(p.getName(), bukkitbat);
						p.getWorld().playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound").toUpperCase()), plugin.getConfig().getInt("Sound-volume"), plugin.getConfig().getInt("Sound-pitch"));
						e.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	public static void setMeta(Object o, Location loc) {
		BlockState bs = loc.getWorld().getBlockAt(loc).getState();
		if (bs instanceof InventoryHolder && o instanceof ItemStack[]) {
			ItemStack[] i = (ItemStack[]) o;
			((InventoryHolder) bs).getInventory().setContents(i);
			bs.update(true);
			return;
		}

		if (bs instanceof Sign && o instanceof String[]) {
			String[] s = (String[]) o;

			((Sign) bs).setLine(0, s[0]);
			((Sign) bs).setLine(1, s[1]);
			((Sign) bs).setLine(2, s[2]);
			((Sign) bs).setLine(3, s[3]);

			bs.update(true);
			return;
		}

	}

	public static Object getMeta(org.bukkit.block.Block b) {
		BlockState bs = b.getState();
		if (bs instanceof InventoryHolder) {
			return ((InventoryHolder) bs).getInventory().getContents();
		}
		if (bs instanceof Sign) {
			Sign s = (Sign) bs;
			return new String[] { s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3) };
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEntityEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				final Player p = e.getPlayer();
				if (e.getPlayer().getItemInHand().getType() == Material.AIR) {
					return;
				}

				AttributeStorage store = AttributeStorage.newTarget(NbtFactory.getCraftItemStack(e.getPlayer().getItemInHand()), ID);
				if (store.getData(null) == null) {
					return;
				}

				if (!(store.getData(null).startsWith("gravitygun;"))) {
					return;
				}

				final String[] split = store.getData(null).split(";");
				if ((!split[0].equals("gravitygun"))) {
					return;
				}

				if (Main.map.containsKey(p.getName())) {
					Entity ent = Main.map.get(p.getName());
					if (ent.getPassenger() instanceof FallingBlock) {
						FallingBlock fb = (FallingBlock) ent.getPassenger();
						org.bukkit.block.Block b = ent.getWorld().getBlockAt(ent.getLocation());

						b.setType(fb.getMaterial());
						b.setData(fb.getBlockData());

						Main.map.get(p.getName()).remove();
						fb.remove();

						e.setCancelled(true);
						p.getWorld().playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound").toUpperCase()), plugin.getConfig().getInt("Sound-volume"), plugin.getConfig().getInt("Sound-pitch"));
						return;
					}

					Main.map.get(p.getName()).remove();

				}

				for (String s : Main.reg.keySet()) {

					if (!(p.hasPermission("gravitygun.use." + s))) {

						p.sendMessage("§4You don't have permission to use this GravityGun!");

						e.setCancelled(true);
						return;
					}

					if (s.equals(split[1])) {
						GravityGun g = Main.reg.get(s);

						for (Entity ent : p.getNearbyEntities(10, 10, 10)) {

							if (ent instanceof MEBat) {
								ent.remove();
							}

						}

						if (!(g.getEntitypickupexeptions().contains(e.getRightClicked().getType().toString())) || ((g.getEntitypickupexeptions().contains("ALL")))) {

							if (g.getEntitypickup().contains(e.getRightClicked().getType().toString()) || g.getEntitypickup().contains("ALL")) {

								World mcWorld = ((CraftWorld) e.getPlayer().getWorld()).getHandle();

								MEBat bat = new MEBat(mcWorld, e.getRightClicked().getLocation(), false);
								Bat bukkitbat = (Bat) bat.getBukkitEntity();
								bat.setPosition(e.getRightClicked().getLocation().getX(), e.getRightClicked().getLocation().getY(), e.getRightClicked().getLocation().getZ());

								bukkitbat.setPassenger(e.getRightClicked());
								bukkitbat.setCustomName("invulnerable");
								bukkitbat.setCustomNameVisible(false);

								mcWorld.addEntity(bat);
								PotionEffect pot = new PotionEffect(PotionEffectType.INVISIBILITY, 10000, 1);
								bukkitbat.addPotionEffect(pot);
								Main.map.put(p.getName(), bukkitbat);
								e.setCancelled(true);
								p.getWorld().playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound").toUpperCase()), plugin.getConfig().getInt("Sound-volume"), plugin.getConfig().getInt("Sound-pitch"));
								return;
							}
						}
					}
				}
			}
		}, 1);
	}

}
