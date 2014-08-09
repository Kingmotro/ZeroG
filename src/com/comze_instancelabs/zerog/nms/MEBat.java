package com.comze_instancelabs.zerog.nms;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R3.EntityBat;
import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R3.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;

public class MEBat extends EntityBat {

	double r = Math.random() + 0.01D;

	private double mY = 0.03D;

	public MEBat(World arg0, Location t) {
		super(arg0);

		// doesn't work, what was I expecting
		this.setInvisible(true);

		this.setPosition(t.getX(), t.getY(), t.getZ());
	}

	public void hinit() {
		super.h();
	}

	@Override
	public void h() {
		motX = 0D;
		motZ = 0D;
		move(0D, motY, 0D);
		// super.h();
	}

	public double getmY() {
		return mY;
	}

	public void setmY(double mY) {
		this.mY = mY;
	}

}
