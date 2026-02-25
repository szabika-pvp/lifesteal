package hu.szatomi.lifesteal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public interface MessageTemplate {

    Component NO_PERMISSION = Component.text("Erre nincs engedélyed").color(Colors.RED);
    Component PLAYER_NOT_FOUND = Component.text("Nem található ez a játékos").color(Colors.RED);

}
