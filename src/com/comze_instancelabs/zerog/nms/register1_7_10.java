package com.comze_instancelabs.zerog.nms;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.EntityTypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.comze_instancelabs.zerog.Main;

public class register1_7_10 {
	public static boolean registerEntities() {

		try {
			Class entityTypeClass = EntityTypes.class;

			Field c = entityTypeClass.getDeclaredField("c");
			c.setAccessible(true);
			HashMap c_map = (HashMap) c.get(null);
			c_map.put("MESheep", MEFallingBlock1_7_10.class);

			Field d = entityTypeClass.getDeclaredField("d");
			d.setAccessible(true);
			HashMap d_map = (HashMap) d.get(null);
			d_map.put(MEFallingBlock1_7_10.class, "MESheep");

			Field e = entityTypeClass.getDeclaredField("e");
			e.setAccessible(true);
			HashMap e_map = (HashMap) e.get(null);
			e_map.put(Integer.valueOf(21), MEFallingBlock1_7_10.class);

			Field f = entityTypeClass.getDeclaredField("f");
			f.setAccessible(true);
			HashMap f_map = (HashMap) f.get(null);
			f_map.put(MEFallingBlock1_7_10.class, Integer.valueOf(21));

			Field g = entityTypeClass.getDeclaredField("g");
			g.setAccessible(true);
			HashMap g_map = (HashMap) g.get(null);
			g_map.put("MESheep", Integer.valueOf(21));

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static MEFallingBlock1_7_10 spawnSheep(Main m, String arena, Location t, final int color) {
		final Object w = ((CraftWorld) t.getWorld()).getHandle();
		final MEFallingBlock1_7_10 t_ = new MEFallingBlock1_7_10(m, arena, t, (net.minecraft.server.v1_7_R4.World) ((CraftWorld) t.getWorld()).getHandle());

		Bukkit.getScheduler().runTask(m, new Runnable() {
			public void run() {
				t_.id = Block.getById(35);
				t_.data = color;
				((net.minecraft.server.v1_7_R4.World) w).addEntity(t_, CreatureSpawnEvent.SpawnReason.CUSTOM);
			}
		});

		return t_;
	}
}
