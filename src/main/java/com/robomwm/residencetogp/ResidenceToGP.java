package com.robomwm.residencetogp;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import me.ryanhamshire.GriefPrevention.CreateClaimResult;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

/**
 * Created on 5/21/2018.
 *
 * @author RoboMWM
 */
public class ResidenceToGP extends JavaPlugin
{
    public void onEnable()
    {
        DataStore dataStore = ((GriefPrevention)getServer().getPluginManager().getPlugin("GriefPrevention")).dataStore;

        Map<String, ClaimedResidence> residenceMap = Residence.getInstance().getResidenceManager().getResidences();

        //Apparently residences are keyed by name???? Odd.

        for (String name : residenceMap.keySet())
        {
            ClaimedResidence residence = residenceMap.get(name);

            //So um, apparently they can have multiple cuboids in one claim?
            CuboidArea area = residence.getMainArea();
            int x1 = area.getLowLoc().getBlockX(), x2 = area.getHighLoc().getBlockX();
            int y1 = area.getLowLoc().getBlockY(), y2 = area.getHighLoc().getBlockY();
            int z1 = area.getLowLoc().getBlockZ(), z2 = area.getHighLoc().getBlockZ();

            UUID owner = residence.getOwnerUUID();

            //Create claim
            CreateClaimResult claimResult = dataStore.createClaim(area.getWorld(), x1, x2, y1, y2, z1, z2, owner, null, null, null);

            if (!claimResult.succeeded)
            {
                getLogger().severe("Unable to convert residence claim " + name + " due to a conflicting GP claim or WorldGuard region.");
                continue;
            }

            //Save it (also IDs it)
            dataStore.saveClaim(claimResult.claim);
            getLogger().info("Converted residence claim " + name + " to GP claim " + claimResult.claim.getID());
        }
    }
}
