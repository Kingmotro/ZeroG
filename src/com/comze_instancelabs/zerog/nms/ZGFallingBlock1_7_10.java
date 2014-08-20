package com.comze_instancelabs.zerog.nms;

import java.util.Iterator;

import net.minecraft.server.v1_7_R4.BlockFalling;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityComplexPart;
import net.minecraft.server.v1_7_R4.EntityFallingBlock;
import net.minecraft.server.v1_7_R4.IContainer;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.Material;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.NBTBase;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.TileEntity;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.comze_instancelabs.zerog.Main;

public class ZGFallingBlock1_7_10 extends EntityFallingBlock {

	boolean f = true;
	private boolean onGround = false;
	private Main m;
	private String arena;
	private double mY = 0.2D;

	boolean coll = false;

	public ZGFallingBlock1_7_10(World world) {
		super(world);
		System.out.println("Fail");
	}
	
	public ZGFallingBlock1_7_10(Main m, String arena, Location loc, World world, boolean collide) {
		super(world);
		this.m = m;
		this.coll = collide;
		this.arena = arena;
		this.dropItem = false;
		setPosition(loc.getX(), loc.getY() + 1, loc.getZ());
	}

	int X;
	int Y;
	int Z;

	public void setYaw(Location target) {
		double disX = (this.locX - target.getX());
		double disY = (this.locY - target.getY());
		double disZ = (this.locZ - target.getZ());

		this.X = (int) (Math.abs(disX));
		this.Y = (int) (Math.abs(disY));
		this.Z = (int) (Math.abs(disZ));

		if (this.locX <= target.getX()) {
			if (this.locZ >= target.getZ()) {
				this.yaw = getLookAtYaw(new Vector(this.X, this.Y, this.Z)) + 180F;
			} else {
				this.yaw = getLookAtYaw(new Vector(this.X, this.Y, this.Z)) - 90F;
			}
		} else { // (this.locX > target.getX())
			if (this.locZ >= target.getZ()) {
				this.yaw = getLookAtYaw(new Vector(this.X, this.Y, this.Z)) + 90F;
			} else {
				this.yaw = getLookAtYaw(new Vector(this.X, this.Y, this.Z));
			}
		}
	}

	public static float getLookAtYaw(Vector motion) {
		double dx = motion.getX();
		double dz = motion.getZ();
		double yaw = 0;

		if (dx != 0) {
			if (dx < 0) {
				yaw = 1.5 * Math.PI;
			} else {
				yaw = 0.5 * Math.PI;
			}
			yaw -= Math.atan(dz / dx);
		} else if (dz < 0) {
			yaw = Math.PI;
		}
		return (float) (-yaw * 180 / Math.PI - 90);
	}

	/*
	 * @Override public void h() { motY = 0; move(motX, motY, motZ); }
	 */

	@Override
	public void h() {

		if (this.id.getMaterial() == Material.AIR) {
			this.die();
		} else {
			this.lastX = this.locX;
			this.lastY = this.locY;
			this.lastZ = this.locZ;
			++this.ticksLived;
			this.motY -= 0.03999999910593033D;
			this.move(this.motX, this.motY, this.motZ);
			this.motX *= 0.9800000190734863D;
			this.motY *= 0.9800000190734863D;
			this.motZ *= 0.9800000190734863D;
			if (!this.world.isStatic) {
				int i = MathHelper.floor(this.locX);
				int j = MathHelper.floor(this.locY);
				int k = MathHelper.floor(this.locZ);

				if (this.ticksLived == 1) {
					if (this.world.getType(i, j, k) != this.id) {
						return;
					}

					this.world.setAir(i, j, k);
				}

				if(this.ticksLived > 20 && !f && this.world.getType(i, j - 1, k) != Blocks.AIR){
					this.world.setTypeAndData(i, j, k, this.id, this.data, 3);
					this.die();
				}
			}
		}

	}

	/*
	 * @Override public void g(double x, double y, double z) { if(coll){ super.g(x, y, z); } }
	 * 
	 * public boolean damageEntity(DamageSource damagesource, int i) { return false; }
	 * 
	 * public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, int i) { return false; }
	 */

	public double getmY() {
		return mY;
	}

	public void setmY(double mY) {
		this.mY = mY;
	}
	
	public void setF(boolean f){
		this.f = f;
	}

}