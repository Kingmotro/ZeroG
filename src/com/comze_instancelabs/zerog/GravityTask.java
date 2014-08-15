package com.comze_instancelabs.zerog;

import org.bukkit.scheduler.BukkitRunnable;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;

// by sethbling!

public class GravityTask extends BukkitRunnable {
	private final Main plugin;
	private final PluginInstance pli;

	public GravityTask(Main plugin) {
		this.plugin = plugin;
		this.pli = MinigamesAPI.getAPI().pinstances.get(plugin);
	}

	public void run() {
		for (Arena a : pli.getArenas()) {
			if (a.getArenaState() != ArenaState.INGAME) {
				continue;
			}
			IArena a_ = (IArena) a;
			a_.updateVelocities();
		}
		new GravityTask(this.plugin).runTaskLater(this.plugin, 1L);
	}
}
