package nl.florisburger.IrisServer.main;

import net.minecraft.server.v1_12_R1.MinecraftServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryDoubleChest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.*;

public class Main extends JavaPlugin {

    private static Main instance;

    public void onEnable(){

        createConfig(); //Create warps file or load it if it exists.
    }

    Map<String, Long> tpaCooldown = new HashMap<String, Long>();
    Map<String, String> currentTpaRequest = new HashMap<String, String>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        //Command: warp
        if(command.getName().equalsIgnoreCase("warp")){
            if(args.length == 0){
                listWarps(p);
            }else if(args.length == 1){
                if(!getConfig().getBoolean("warps."+args[0]+".op")){
                    if(getConfig().getString("warps."+args[0]) != null){
                        String w = getConfig().getString("warps."+args[0]+".world");
                        int x = getConfig().getInt("warps."+args[0]+".x");
                        int y = getConfig().getInt("warps."+args[0]+".y");
                        int z = getConfig().getInt("warps."+args[0]+".z");
                        Block b = getServer().getWorld(w).getBlockAt(x, y, z);

                        if (b != null) {
                            p.teleport(b.getLocation());
                            p.sendMessage("Woosh");
                        }
                    }else{
                        p.sendMessage("Warp doesn't exist!");
                    }
                }else if(getConfig().getBoolean("warps."+args[0]+".op") && p.isOp()){
                    if(getConfig().getString("warps."+args[0]) != null){
                        String w = getConfig().getString("warps."+args[0]+".world");
                        int x = getConfig().getInt("warps."+args[0]+".x");
                        int y = getConfig().getInt("warps."+args[0]+".y");
                        int z = getConfig().getInt("warps."+args[0]+".z");
                        Block b = getServer().getWorld(w).getBlockAt(x, y, z);

                        if (b != null) {
                            p.teleport(b.getLocation());
                            p.sendMessage("Woosh");
                        }
                    }else{
                        p.sendMessage("Warp doesn't exist!");
                    }
                }else{
                    p.sendMessage("You can't use this warp.");
                }
            }else if (args.length >1){
                p.sendMessage("Usage: /warp [warp]");
            }
        }
        //Command: setwarp
        if(command.getName().equalsIgnoreCase("setwarp")){
            if(p.isOp()){
                if(args.length == 1){
                    if(getConfig().getString("warps."+args[0]) == null){
                        getConfig().set("warps."+args[0]+".x", p.getLocation().getX());
                        getConfig().set("warps."+args[0]+".y", p.getLocation().getY());
                        getConfig().set("warps."+args[0]+".z", p.getLocation().getZ());
                        getConfig().set("warps."+args[0]+".world", p.getWorld().getName());
                        getConfig().set("warps."+args[0]+".op", false);
                        saveConfig();
                        p.sendMessage("You have created warp: "+args[0]);
                    }else{
                        p.sendMessage("This warp already exists!");
                    }
                }else if(args.length == 2){
                    if(getConfig().getString("warps."+args[0]) == null){
                        if(args[1].equalsIgnoreCase("true")){
                            getConfig().set("warps."+args[0]+".x", p.getLocation().getX());
                            getConfig().set("warps."+args[0]+".y", p.getLocation().getY());
                            getConfig().set("warps."+args[0]+".z", p.getLocation().getZ());
                            getConfig().set("warps."+args[0]+".world", p.getWorld().getName());
                            getConfig().set("warps."+args[0]+".op", true);
                            saveConfig();
                        }else if(args[1].equalsIgnoreCase("false")){
                            getConfig().set("warps."+args[0]+".x", p.getLocation().getX());
                            getConfig().set("warps."+args[0]+".y", p.getLocation().getY());
                            getConfig().set("warps."+args[0]+".z", p.getLocation().getZ());
                            getConfig().set("warps."+args[0]+".world", p.getWorld().getName());
                            getConfig().set("warps."+args[0]+".op", false);
                            saveConfig();
                            p.sendMessage("You have created warp: "+args[0]);
                        }else{
                            p.sendMessage("Usage: /setwarp [name] [onlyOP]");
                        }
                    }else{
                        p.sendMessage("This warp already exists!");
                    }
                }else{
                    p.sendMessage("Usage: /setwarp [name]");
                }
            }else{
                p.sendMessage("You can't use this command.");
            }

        }

        //Command: delwarp
        if(command.getName().equalsIgnoreCase("delwarp")){
            if(p.isOp()){
                if(args.length == 1){
                    if(getConfig().getString("warps."+args[0]) == null){
                        p.sendMessage("This warp doesn't exist!");
                    }else{
                        getConfig().set("warps."+args[0], null);
                        saveConfig();
                        p.sendMessage("You have deleted warp: "+args[0]);
                    }
                }else{
                    p.sendMessage("Usage: /delwarp [name]");
                }
            }else{
                p.sendMessage("You can't use this command.");
            }
        }

        //Command: listwarp
        if(command.getName().equalsIgnoreCase("listwarp")){
            if(args.length == 0){
                listWarps(p);
            }else if(args.length == 1){
                if(getConfig().getString("warps."+args[0]) == null){
                    p.sendMessage("This warp doesn't exist!");
                }else{
                    p.sendMessage("Cords of "+args[0]+" are: "+getConfig().getString("warps."+args[0]+".x").toString()+", "+getConfig().getString("warps."+args[0]+".y").toString()+", "+getConfig().getString("warps."+args[0]+".z").toString());
                }
            }else{
                p.sendMessage("Usage: /listwarp");
            }
        }

        //Command: spawn
        if(command.getName().equalsIgnoreCase("spawn")){
            if(getConfig().getString("spawn") != null){
                String w = getConfig().getString("spawn.world");
                int x = getConfig().getInt("spawn.x");
                int y = getConfig().getInt("spawn.y");
                int z = getConfig().getInt("spawn.z");
                Block b = getServer().getWorld(w).getBlockAt(x, y, z);

                if (b != null) {
                    p.teleport(b.getLocation());
                    p.sendMessage("Woosh");
                }
            }else{
                p.sendMessage("/spawn isn't enabled on this server.");
            }
        }

        //Command: setspawn
        if(command.getName().equalsIgnoreCase("setspawn")){
            if(p.isOp()){
                getConfig().set("spawn.x", p.getLocation().getX());
                getConfig().set("spawn.y", p.getLocation().getY());
                getConfig().set("spawn.z", p.getLocation().getZ());
                getConfig().set("spawn.world", p.getLocation().getWorld().getName());
                saveConfig();
                p.sendMessage("Spawnpoint set!");
            }else{
                p.sendMessage("You can't use this command.");
            }
        }

        //Command: delspawn
        if(command.getName().equalsIgnoreCase("delspawn")){
            if(p.isOp()){
                if(getConfig().getString("spawn") != null){
                    getConfig().set("spawn", null);
                    saveConfig();
                }else{
                    p.sendMessage("There is no spawnpoint set.");
                }
            }else{
                p.sendMessage("You can't use this command!");
            }
        }

        //Command: tpa
        if(command.getName().equalsIgnoreCase("tpa")){
            if(args.length == 0 || args.length >= 2){
                p.sendMessage("Usage: /tpa [player]");
            }else{
                int cooldown = 5;
                if(tpaCooldown.containsKey(p.getName())){
                    long diff = (System.currentTimeMillis() - tpaCooldown.get(p.getName())) / 1000;
                    if (diff < cooldown){
                        p.sendMessage("You need to wait "+cooldown+" seconds between requests.");
                        return false;
                    }
                }

                Player target = getServer().getPlayer(args[0]);
                long keepAlive = 2 * 60 * 20;

                if(target == null){
                    p.sendMessage("The player is not online.");
                    return false;
                }

                if (target == p){
                    p.sendMessage("You can't teleport to yourself");
                    return false;
                }

                sendTpaRequest(p, target);

                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        killTpaRequest(target.getName());
                    }
                }, keepAlive);

                tpaCooldown.put(p.getName(), System.currentTimeMillis());

            }
        }

        if(command.getName().equalsIgnoreCase("tpaccept")){
            if(currentTpaRequest.containsKey(p.getName())){
                Player originalSender = getServer().getPlayer(currentTpaRequest.get(p.getName()));
                currentTpaRequest.remove(p.getName());

                if(!(originalSender == null)){
                    originalSender.teleport(p);
                    originalSender.sendMessage("woosh");
                }else{
                    p.sendMessage("The player that wanted to teleport to you isn't here anymore.");
                    return false;
                }

            }else{
                p.sendMessage("nobody wants you");
                return false;
            }
        }

        if(command.getName().equalsIgnoreCase("tpdeny")){
            if(currentTpaRequest.containsKey(p.getName())){
                Player originalSender = getServer().getPlayer(currentTpaRequest.get(p.getName()));
                currentTpaRequest.remove(p.getName());

                if(!(originalSender == null)){
                    originalSender.sendMessage("Your teleport request to "+p.getDisplayName()+" denied your request.");
                    p.sendMessage("You denied "+originalSender.getDisplayName());
                    return true;
                }
            }else{
                p.sendMessage("this player doesn't want to teleport to you");
                return false;
            }
        }

        if(command.getName().equalsIgnoreCase("sethome")){
            if(args.length == 1){
                int homeCount = 0;
                if(getConfig().get("homes."+p.getName()) != null){
                    homeCount = getConfig().getConfigurationSection("homes."+p.getName()).getKeys(false).size();
                }
                if(homeCount<2 || p.isOp()){
                    getConfig().set("homes."+p.getName()+"."+args[0]+".x",p.getLocation().getX());
                    getConfig().set("homes."+p.getName()+"."+args[0]+".y",p.getLocation().getY());
                    getConfig().set("homes."+p.getName()+"."+args[0]+".z",p.getLocation().getZ());
                    getConfig().set("homes."+p.getName()+"."+args[0]+".world",p.getLocation().getWorld().getName());
                    saveConfig();

                    p.sendMessage("Very nice, home set!");

                }else{
                    p.sendMessage("too many homes.");
                }
            }else{
                p.sendMessage("Usage: /sethome [name]");
            }
        }

        if(command.getName().equalsIgnoreCase("home")){
            if(args.length == 1){
                if(getConfig().get("homes."+p.getName()) != null){
                    if(getConfig().get("homes."+p.getName()+"."+args[0]) != null){
                        String w = getConfig().getString("homes."+p.getName()+"."+args[0]+".world");
                        int x = getConfig().getInt("homes."+p.getName()+"."+args[0]+".x");
                        int y = getConfig().getInt("homes."+p.getName()+"."+args[0]+".y");
                        int z = getConfig().getInt("homes."+p.getName()+"."+args[0]+".z");
                        Location l = getServer().getWorld(w).getBlockAt(x, y, z).getLocation();

                        p.sendMessage("Woosh");
                        p.teleport(l);
                    }else{
                        p.sendMessage("That home doesn't exist!");
                    }
                }else{
                    p.sendMessage("You don't have any homes. If you want to make a home, use: /sethome [name]");
                }

            }else if(args.length == 0){
                Set<String> homes = getConfig().getConfigurationSection("homes."+p.getName()+".").getKeys(false);
                String[] warpList = homes.toArray(new String[homes.size()]);
                p.sendMessage("Your homes are: "+org.apache.commons.lang.StringUtils.join(warpList, "§7, ", 0, warpList.length));

            }else{
                p.sendMessage("Usage: /home [name]");
            }
        }

        if(command.getName().equalsIgnoreCase("delhome")){
            if(args.length == 1){
                if(getConfig().get("homes."+p.getName()) != null){
                    if(getConfig().get("homes."+p.getName()+"."+args[0]) != null){
                        getConfig().set("homes."+p.getName()+"."+args[0], null);
                        saveConfig();

                        p.sendMessage("And it's gone..");
                    }else{
                        p.sendMessage("That home doens't exist.");
                    }
                }else{
                    p.sendMessage("You don't have any homes. If you want to make a home, use: /sethome [name]");
                }
            }else{
                p.sendMessage("Usage: /delhome [name]");
            }
        }

        if(command.getName().equalsIgnoreCase("wc")){
            Bukkit.dispatchCommand(p, "weather clear");
            if(p.isOp()){
                p.sendMessage(ChatColor.AQUA+"Regendans gedaan.");
            }
        }

        if(command.getName().equalsIgnoreCase("wb")){
            p.openWorkbench(null, true);
        }

        if(command.getName().equalsIgnoreCase("trashcan")){
            Inventory inv = Bukkit.createInventory(null, 54, "Trashcan §4ITEMS WILL BE DELETED");
            p.openInventory(inv);
        }

        if(command.getName().equalsIgnoreCase("maakbank")){
            Location loc = new Location(p.getWorld(), p.getLocation().getBlockX()+0.5, p.getLocation().getBlockY()-1, p.getLocation().getBlockZ()+0.5);

            p.getWorld().spawnEntity(loc, EntityType.MINECART);
        }

        return false;
    }

    public void listWarps(Player p){
        Set<String> warps = getConfig().getConfigurationSection("warps.").getKeys(false);
        String[] warpList = warps.toArray(new String[warps.size()]);
        p.sendMessage("Warps: " + org.apache.commons.lang.StringUtils.join(warpList, "§7, ", 0, warpList.length));
    }

    public boolean killTpaRequest(String key){
        if(currentTpaRequest.containsKey(key)){
            Player loser = getServer().getPlayer(currentTpaRequest.get(key));
            if (!(loser == null)) {
                loser.sendMessage("Your teleport request timed out.");
            }

            currentTpaRequest.remove(key);
            return true;
        }else{
            return false;
        }
    }

    public void sendTpaRequest(Player sender, Player target){
        sender.sendMessage("Teleport request sent to "+target.getDisplayName()+".");
        sender.sendMessage("They have 2 minutes to accept.");

        target.sendMessage("Teleport Request recieved!");
        target.sendMessage(sender.getDisplayName()+" has requested to teleport to you.");
        target.sendMessage("Do /tpaccept to accept or /tpdeny to deny.");
        target.sendMessage("The request will stay for 2 minutes.");

        currentTpaRequest.put(target.getName(), sender.getName());

    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
