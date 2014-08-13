package com.comze_instancelabs.zerog.nms;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.EntityBat;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;

public class MEBat extends EntityBat {

	private double r = (Math.random() + 0.5D);

	private double mY = 0.03D;

	boolean c = true;

	public MEBat(World arg0, Location t, boolean c) {
		super(arg0);

		this.c = c;

		// doesn't work, what was I expecting
		this.setInvisible(true);

		this.setPosition(t.getX(), t.getY(), t.getZ());

		try {
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
			bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void hinit() {
		super.h();
	}

	@Override
	public void h() {
		if(c){
			motX = 0D;
			motZ = 0D;
			move(0D, motY, 0D);
		}else{
			super.h();
		}
		
		//super.h();
	}
	
	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		return false;
	}
	
	public double getmY() {
		return mY;
	}

	public void setmY(double mY) {
		this.mY = mY;
	}

	public double getR() {
		return r;
	}

}
