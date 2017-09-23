package funkemunky.Daedalus.check.combat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;

public class FastBow extends Check
{
    public static Map<Player, Long> bowPull;
    public static Map<Player, Integer> count;

    public FastBow(final Daedalus Daedalus) {
        super("FastBow", "FastBow", Daedalus);
        this.bowPull = new HashMap<Player, Long>();
        this.count = new HashMap<Player, Integer>();
        this.setViolationsToNotify(2);
        this.setMaxViolations(7);
        this.setBannable(true);
    }
    
    @EventHandler
    public void Interact(final PlayerInteractEvent e) {
        final Player Player = e.getPlayer();
        if (Player.getItemInHand() != null && Player.getItemInHand().getType().equals((Object)Material.BOW)) {
            this.bowPull.put(Player, System.currentTimeMillis());
        }
    }
    
    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
    	if(bowPull.containsKey(e.getPlayer())) {
    		bowPull.remove(e.getPlayer());
    	}
    	
    	if(count.containsKey(e.getPlayer())) {
    		count.remove(e.getPlayer());
    	}
    }
    
    @EventHandler
    public void onShoot(final ProjectileLaunchEvent e) {
        if (!this.isEnabled()) {
            return;
        }
        if (e.getEntity() instanceof Arrow) {
            final Arrow arrow = (Arrow)e.getEntity();
            if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
                final Player player = (Player)arrow.getShooter();
                if (this.bowPull.containsKey(player)) {
                    Long time = System.currentTimeMillis() - this.bowPull.get(player);
                    double power = arrow.getVelocity().length();
                    Long timeLimit = 300L;
                    int Count = 0;
                    if(count.containsKey(player)) {
                    	Count = count.get(player);
                    }
                    if (power > 2.5 && time < timeLimit) {
                        count.put(player, Count + 1);
                    } else {
                    	count.put(player, Count - 1);
                    }
           	     if(player.hasPermission("daedalus.bypass")) {
        	         return;
        	     }
                    if (Count > 8) {
                        getDaedalus().logCheat(this, player, time + " ms", Chance.HIGH, new String[0]);
                        count.remove(player);
                    }
                }
            }
        }
    }
}