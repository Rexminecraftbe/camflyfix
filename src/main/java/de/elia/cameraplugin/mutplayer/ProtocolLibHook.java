package de.elia.cameraplugin.mutplayer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;

public class ProtocolLibHook {

    public ProtocolLibHook(JavaPlugin plugin, final Set<UUID> soundsDisabled, boolean muteAttack, boolean muteFootsteps) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                try {
                    PacketContainer packet = event.getPacket();
                    String soundName = packet.getSoundEffects().read(0).getKey().getKey();

                    boolean isAttackSound = muteAttack && (soundName.startsWith("entity.player.attack") || soundName.startsWith("entity.player.sweep"));
                    boolean isFootstepSound = muteFootsteps && soundName.startsWith("block.") && soundName.endsWith(".step");

                    if (isAttackSound || isFootstepSound) {
                        if (packet.getIntegers().size() < 3) return;

                        Player emitter = findNearbyPlayer(packet.getIntegers().read(0) / 8.0,
                                packet.getIntegers().read(1) / 8.0,
                                packet.getIntegers().read(2) / 8.0,
                                event.getPlayer().getWorld());

                        if (emitter != null && soundsDisabled.contains(emitter.getUniqueId())) {
                            event.setCancelled(true);
                        }
                    }
                } catch (Exception ignored) {
                    // ignore
                }
            }
        });
    }

    private Player findNearbyPlayer(double x, double y, double z, World world) {
        Location effectLocation = new Location(world, x, y, z);
        Player closestPlayer = null;
        double closestDistanceSq = 4.0;

        for (Player player : world.getPlayers()) {
            double distanceSq = player.getLocation().distanceSquared(effectLocation);
            if (distanceSq < closestDistanceSq) {
                closestPlayer = player;
                closestDistanceSq = distanceSq;
            }
        }
        return closestPlayer;
    }
}