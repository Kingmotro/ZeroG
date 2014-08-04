package com.comze_instancelabs.zerog;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.ArenaType;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.util.Util;

public class IArena extends Arena {

	public static Main m;

	int blue = 0;
	int red = 0;

	int winscore = 50;

	boolean cteam = true;

	int c = 30;
	boolean cgravity = false;

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
	public void spectate(String playername) {
		// TODO tp to spawn back
	}

	BukkitTask tt;
	int currentingamecount;

	@Override
	public void start(boolean tp) {
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

	public void toggleGravity(boolean t) {
		if (t) {
			broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + "Zero Gravity Mode " + ChatColor.DARK_GREEN + "Activitated");

			// TODO
		} else {
			broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + "Gravity restored");

			// TODO
		}
	}

	public void getSurfaceBlocks(int percentage) {
		// TODO
	}

	public void clearBlocks() {
		// TODO clear all fallingblock entities after stop
	}

	public void broadcast(String msg) {
		for (String p_ : this.getAllPlayers()) {
			Player p = Bukkit.getPlayer(p_);
			p.sendMessage(msg);
		}
	}

}
