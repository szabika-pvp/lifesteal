package hu.szatomi.lifesteal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public interface MessageTemplate {

    Component NO_PERMISSION = Component.text("Erre nincs engedélyed").color(TextColor.color(0xFF0000));
    Component PLAYER_NOT_FOUND = Component.text("Nem található ez a játékos").color(TextColor.color(0xFF0000));

}
