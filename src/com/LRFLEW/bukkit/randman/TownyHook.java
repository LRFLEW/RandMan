package com.LRFLEW.bukkit.randman;

import org.bukkit.World;

import ca.xshade.bukkit.towny.NotRegisteredException;
import ca.xshade.bukkit.towny.Towny;
import ca.xshade.bukkit.towny.object.Coord;
import ca.xshade.bukkit.towny.object.TownyWorld;
import ca.xshade.bukkit.towny.object.WorldCoord;

public class TownyHook {
	private final Towny ty;
	
	public TownyHook(Towny ty) {
		this.ty = ty;
	}
	
	public boolean isWilderness ( World w, int x, int z ) {
		if (ty != null) {
			try {
				TownyWorld tworld = ty.getTownyUniverse().getWorld(w.getName());
				Coord cord = Coord.parseCoord(x, z);
				WorldCoord wcord = new WorldCoord(tworld, cord);
				wcord.getTownBlock();
			} catch (NotRegisteredException e) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}
	
}
