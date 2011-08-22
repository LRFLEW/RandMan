package com.LRFLEW.bukkit.randman;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ca.xshade.bukkit.towny.Towny;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import com.wimbli.WorldBorder.WorldBorder;

public class RandMan extends JavaPlugin {
	private final Random rand = new Random();
	private WorldBorder wb = null;
	private TownyHook th = null;

	@Override
	public void onDisable() {
		wb = null;
		th = null;
		
		// NOTE: All registered events are automatically unregistered when a plugin is disabled
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " says Goodbye!" );
	}

	@Override
	public void onEnable() {
		
		{
			Plugin p = getServer().getPluginManager().getPlugin("WorldBorder");

			if (p != null) {
				if (p.getClass().getName().equals("com.wimbli.WorldBorder.WorldBorder")) {
					wb = (WorldBorder)p;
					System.out.println("[RandMan] hooked into WorldBorder.");
				}
			}
		}
		{
			Plugin p = getServer().getPluginManager().getPlugin("Towny");
			
			if (p != null) {
				if (p.getClass().getName().equals("ca.xshade.bukkit.towny.Towny")) {
					th = new TownyHook((Towny)p);
					System.out.println("[RandMan] hooked into Towny.");
				}
			}
		}
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	@Override
	public boolean onCommand (CommandSender sender, Command com, String label, String[] args) {
		if (com.getLabel().equals("randman")) {
			if (wb != null) {
				if (sender instanceof Player) {
					Player p = (Player)sender;
					if (!p.hasPermission("RandMan")) return true;
					World w = p.getWorld();
					if (w.getEnvironment() == Environment.NETHER) {
						p.sendMessage("RandMan currentally doesn't support nether");
						return true;
					}
					BorderData bd = wb.GetWorldBorder(w.getName());
					if (bd == null) return true;
					int r = bd.getRadius()-1;
					boolean shape;
					if (bd.getShape() == null) shape = Config.ShapeRound();
					else shape = bd.getShape();
					if (shape) {
						for (short i=0; i<256; i++) {
							double rrel = rand.nextDouble()*r;
							double trel = rand.nextDouble()*2*Math.PI;
							int x = Location.locToBlock(bd.getX()+(rrel*Math.cos(trel)));
							int z = Location.locToBlock(bd.getZ()+(rrel*Math.sin(trel)));
							if (grabRand(p,w,x,z)) return true;
						}
					} else {
						for (short i=0; i<256; i++) {
							int xrel = rand.nextInt(r*2)-r;
							int zrel = rand.nextInt(r*2)-r;
							int x = Location.locToBlock(bd.getX()+xrel);
							int z = Location.locToBlock(bd.getZ()+zrel);
							if (grabRand(p,w,x,z)) return true;
						}
					}
					p.sendMessage(ChatColor.RED + "Error, teleport timed out");
					getServer().getLogger().log(Level.WARNING, 
							"[RandMan] teleport timed out");
					return true;
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean grabRand (Player p, World w, int x, int z) {
		if (th!=null) if (!th.isWilderness(w,x,z)) return false;
		Chunk c = w.getChunkAt(new Location(w,x,0,z));
		w.loadChunk(c);
		int y = w.getHighestBlockYAt(x, z);
		if (y == 0) {
			getServer().getLogger().log(Level.WARNING, 
					"[RandMan] Unable to find the ground");
			w.unloadChunk(c.getX(), c.getZ());
			return false;
		}
		int t = w.getBlockTypeIdAt(x, y-1, z);
		if ((t >= 8 && t < 12) || t == 79) {
			w.unloadChunk(c.getX(), c.getZ());
			return false;
		}
		Location l = new Location(w, x+.5, y, z+.5);
		p.teleport(l);
		return true;
	}

}
