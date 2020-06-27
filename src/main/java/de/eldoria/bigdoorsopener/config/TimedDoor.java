package de.eldoria.bigdoorsopener.config;

import com.google.common.base.Objects;
import de.eldoria.bigdoorsopener.util.serialization.SerializationUtil;
import de.eldoria.bigdoorsopener.util.serialization.TypeResolvingMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import java.util.Map;

@Getter
@Setter
public class TimedDoor implements ConfigurationSerializable {
    /**
     * UUID of the door.
     */
    private final long doorUID;
    /**
     * Name of the world the door is in.
     */
    private final String world;
    /**
     * Mass center of the door.
     */
    private final Vector position;
    /**
     * The ticks from when to door should be closed
     */
    private int ticksClose;
    /**
     * The ticks from when the door should be open.
     */
    private int ticksOpen;
    /**
     * If a player is in this range the door will open.
     * If not the door will be closed if open.
     */
    private double openRange = 10;
    /**
     * Represents the current required state of the door.
     */
    private boolean closed;

    public TimedDoor(long doorUID, String world, Vector position) {
        this.doorUID = doorUID;
        this.world = world;
        this.position = position;
    }

    public TimedDoor(long doorUID, String world, Vector position, int ticksClose, int ticksOpen, double openRange) {
        this.doorUID = doorUID;
        this.world = world;
        this.position = position;
        this.ticksClose = ticksClose;
        this.ticksOpen = ticksOpen;
        this.openRange = openRange;
    }

    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("doorUID", String.valueOf(doorUID))
                .add("world", world.toString())
                .add("position", position)
                .add("ticksClose", ticksClose)
                .add("ticksOpen", ticksOpen)
                .add("range", openRange).build();
    }

    public static TimedDoor deserialize(Map<String, Object> map) {
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        long doorUID = Long.parseLong(resolvingMap.getValue("doorUID"));
        String world = resolvingMap.getValue("world");
        Vector position = resolvingMap.getValue("position");
        int ticksClose = resolvingMap.getValue("ticksClose");
        int ticksOpen = resolvingMap.getValue("ticksOpen");
        double range = resolvingMap.getValue("range");
        return new TimedDoor(doorUID, world, position, ticksClose, ticksOpen, range);
    }

    public boolean shouldBeOpen(long fulltime) {
        long openInTicks = getDiff(fulltime, getTicksOpen());
        long closedInTicks = getDiff(fulltime, getTicksClose());
        if (openInTicks > closedInTicks) {
            return true;
        }
        return false;
    }

    private long getDiff(long fullTime, long nextTime) {
        long currentTime = fullTime % 24000;
        return currentTime > nextTime ? 24000 - currentTime + nextTime : nextTime - currentTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimedDoor timedDoor = (TimedDoor) o;
        return doorUID == timedDoor.doorUID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(doorUID);
    }
}
