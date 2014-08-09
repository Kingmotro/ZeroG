package com.comze_instancelabs.zerog.nms;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_7_R3.EntityTypes;
import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.MobEffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Bat;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.comze_instancelabs.zerog.Main;

public class register1_7_9 {
	public static boolean registerEntities() {

		try {
			Class entityTypeClass = EntityTypes.class;

			Field c = entityTypeClass.getDeclaredField("c");
			c.setAccessible(true);
			HashMap c_map = (HashMap) c.get(null);
			c_map.put("MEFallingBlock1_7_9", MEFallingBlock1_7_9.class);

			Field d = entityTypeClass.getDeclaredField("d");
			d.setAccessible(true);
			HashMap d_map = (HashMap) d.get(null);
			d_map.put(MEFallingBlock1_7_9.class, "MEFallingBlock1_7_9");

			Field e = entityTypeClass.getDeclaredField("e");
			e.setAccessible(true);
			HashMap e_map = (HashMap) e.get(null);
			e_map.put(Integer.valueOf(21), MEFallingBlock1_7_9.class);

			Field f = entityTypeClass.getDeclaredField("f");
			f.setAccessible(true);
			HashMap f_map = (HashMap) f.get(null);
			f_map.put(MEFallingBlock1_7_9.class, Integer.valueOf(21));

			Field g = entityTypeClass.getDeclaredField("g");
			g.setAccessible(true);
			HashMap g_map = (HashMap) g.get(null);
			g_map.put("MEFallingBlock1_7_9", Integer.valueOf(21));

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

		try {
			Class entityTypeClass = EntityTypes.class;

			Field c = entityTypeClass.getDeclaredField("c");
			c.setAccessible(true);
			HashMap c_map = (HashMap) c.get(null);
			c_map.put("MEBat", MEBat.class);

			Field d = entityTypeClass.getDeclaredField("d");
			d.setAccessible(true);
			HashMap d_map = (HashMap) d.get(null);
			d_map.put(MEBat.class, "MEBat");

			Field e = entityTypeClass.getDeclaredField("e");
			e.setAccessible(true);
			HashMap e_map = (HashMap) e.get(null);
			e_map.put(Integer.valueOf(65), MEBat.class);

			Field f = entityTypeClass.getDeclaredField("f");
			f.setAccessible(true);
			HashMap f_map = (HashMap) f.get(null);
			f_map.put(MEBat.class, Integer.valueOf(65));

			Field g = entityTypeClass.getDeclaredField("g");
			g.setAccessible(true);
			HashMap g_map = (HashMap) g.get(null);
			g_map.put("MEBat", Integer.valueOf(65));

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public static MEBat spawnBlock(Main m, String arena, final org.bukkit.block.Block b) {
		final Object w = ((CraftWorld) b.getWorld()).getHandle();
		System.out.println(b);
		final MEBat b_ = new MEBat((net.minecraft.server.v1_7_R3.World) ((CraftWorld) b.getWorld()).getHandle(), b.getLocation());
		final MEFallingBlock1_7_9 t_ = new MEFallingBlock1_7_9(m, arena, b.getLocation(), (net.minecraft.server.v1_7_R3.World) ((CraftWorld) b.getWorld()).getHandle());

		Bukkit.getScheduler().runTask(m, new Runnable() {
			public void run() {
				t_.id = Block.e(b.getTypeId());
				t_.data = b.getData();
				Bat bb = (Bat) b_.getBukkitEntity();
				FallingBlock bf = (FallingBlock) t_.getBukkitEntity();
				bb.setPassenger(bf);

				((net.minecraft.server.v1_7_R3.World) w).addEntity(b_, CreatureSpawnEvent.SpawnReason.CUSTOM);
				((net.minecraft.server.v1_7_R3.World) w).addEntity(t_, CreatureSpawnEvent.SpawnReason.CUSTOM);

			}
		});

		return b_;
	}
}
