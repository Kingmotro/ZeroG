package com.comze_instancelabs.zerog.nms;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.EntityTypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Bat;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.comze_instancelabs.zerog.Main;

public class register1_7_10 {
	public static boolean registerEntities() {

		try {
			Class entityTypeClass = EntityTypes.class;

			Field c = entityTypeClass.getDeclaredField("c");
			c.setAccessible(true);
			HashMap c_map = (HashMap) c.get(null);
			c_map.put("ZGBlock", ZGFallingBlock1_7_10.class);

			Field d = entityTypeClass.getDeclaredField("d");
			d.setAccessible(true);
			HashMap d_map = (HashMap) d.get(null);
			d_map.put(ZGFallingBlock1_7_10.class, "ZGBlock");

			Field e = entityTypeClass.getDeclaredField("e");
			e.setAccessible(true);
			HashMap e_map = (HashMap) e.get(null);
			e_map.put(Integer.valueOf(21), ZGFallingBlock1_7_10.class);

			Field f = entityTypeClass.getDeclaredField("f");
			f.setAccessible(true);
			HashMap f_map = (HashMap) f.get(null);
			f_map.put(ZGFallingBlock1_7_10.class, Integer.valueOf(21));

			Field g = entityTypeClass.getDeclaredField("g");
			g.setAccessible(true);
			HashMap g_map = (HashMap) g.get(null);
			g_map.put("ZGBlock", Integer.valueOf(21));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		

		try {
			Class entityTypeClass = EntityTypes.class;

			Field c = entityTypeClass.getDeclaredField("c");
			c.setAccessible(true);
			HashMap c_map = (HashMap) c.get(null);
			c_map.put("ZGBat", ZGBat.class);

			Field d = entityTypeClass.getDeclaredField("d");
			d.setAccessible(true);
			HashMap d_map = (HashMap) d.get(null);
			d_map.put(ZGBat.class, "ZGBat");

			Field e = entityTypeClass.getDeclaredField("e");
			e.setAccessible(true);
			HashMap e_map = (HashMap) e.get(null);
			e_map.put(Integer.valueOf(65), ZGBat.class);

			Field f = entityTypeClass.getDeclaredField("f");
			f.setAccessible(true);
			HashMap f_map = (HashMap) f.get(null);
			f_map.put(ZGBat.class, Integer.valueOf(65));

			Field g = entityTypeClass.getDeclaredField("g");
			g.setAccessible(true);
			HashMap g_map = (HashMap) g.get(null);
			g_map.put("ZGBat", Integer.valueOf(65));

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public static ZGBat spawnBlock(Main m, String arena, final org.bukkit.block.Block b) {
		final Object w = ((CraftWorld) b.getWorld()).getHandle();
		final ZGBat b_ = new ZGBat((net.minecraft.server.v1_7_R4.World) ((CraftWorld) b.getWorld()).getHandle(), b.getLocation(), true);
		final ZGFallingBlock1_7_10 t_ = new ZGFallingBlock1_7_10(m, arena, b.getLocation(), (net.minecraft.server.v1_7_R4.World) ((CraftWorld) b.getWorld()).getHandle(), false);

		Bukkit.getScheduler().runTask(m, new Runnable() {
			public void run() {
				t_.id = Block.getById(b.getTypeId());
				t_.data = b.getData();
				Bat bb = (Bat) b_.getBukkitEntity();
				FallingBlock bf = (FallingBlock) t_.getBukkitEntity();
				bb.setPassenger(bf);

				((net.minecraft.server.v1_7_R4.World) w).addEntity(b_, CreatureSpawnEvent.SpawnReason.CUSTOM);
				((net.minecraft.server.v1_7_R4.World) w).addEntity(t_, CreatureSpawnEvent.SpawnReason.CUSTOM);

			}
		});

		return b_;
	}
}
