package ru.redline.opprison.player.event;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import ru.redline.opprison.player.OpPlayer;

@Getter
public class PlayerLoadedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final OpPlayer data;

    public PlayerLoadedEvent(OpPlayer data) {
        super(data.getHandle());
        this.data = data;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
