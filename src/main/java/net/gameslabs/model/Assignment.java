package net.gameslabs.model;

import net.gameslabs.api.Component;
import net.gameslabs.api.ComponentRegistry;
import net.gameslabs.api.Player;
import net.gameslabs.components.ChartComponent;
import net.gameslabs.events.GetPlayerLevel;
import net.gameslabs.events.GiveXpEvent;
import net.gameslabs.implem.PlayerImplem;

import java.util.Arrays;
import assignment.events.*;

public class Assignment {

    protected final ComponentRegistry registry;
    private final Player mainPlayer;

    public Assignment(Component ... myComponentsToAdd) {
        registry = new ComponentRegistry();
        Arrays.asList(myComponentsToAdd).forEach(registry::registerComponent);
        registry.registerComponent(new ChartComponent());
        registry.load();
        mainPlayer = PlayerImplem.newPlayer("MyPlayer");
    }

    public final void run() {
        registry.sendEvent(new GiveXpEvent(mainPlayer, Skill.CONSTRUCTION, 25));
        registry.sendEvent(new GiveXpEvent(mainPlayer, Skill.EXPLORATION, 25));
        GetPlayerLevel getPlayerLevel = new GetPlayerLevel(mainPlayer, Skill.CONSTRUCTION);
        log("Player level", mainPlayer, getPlayerLevel.getLevel());
        runChecks();
        registry.unload();
    }

    private void runChecks() {
        // built-in checks
        if (getLevel(Skill.EXPLORATION) != 1) throw new AssignmentFailed("Exploration XP should be set to level 1");
        if (getLevel(Skill.CONSTRUCTION) != 2) throw new AssignmentFailed("Construction XP should be set to level 2");

        // inventory checks
        mainPlayer.getInventory().add("3rd Age Bow");
        if (!hasItem("3rd Age Bow")) throw new AssignmentFailed("Player should have 3rd Age Bow in inventory");
        if (hasItem("3rd Age Longsword")) throw new AssignmentFailed("Player should not have 3rd Age Longsword in inventory");

        if (!giveItem("3rd Age Longsword")) throw new AssignmentFailed("Giving 3rd Age Longsword failed");
        if (!giveItem("3rd Age Pickaxe")) throw new AssignmentFailed("Giving 3rd Age Pickaxe failed");
        if (!giveItem("3rd Age Axe")) throw new AssignmentFailed("Giving 3rd Age Axe failed");

        if (!hasItem("3rd Age Longsword")) throw new AssignmentFailed("Player should have 3rd Age Longsword in inventory");
        if (!hasItem("3rd Age Pickaxe")) throw new AssignmentFailed("Player should have 3rd Age Pickaxe in inventory");
        if (!hasItem("3rd Age Axe")) throw new AssignmentFailed("Player should have 3rd Age Axe in inventory");
    }

    private int getLevel(Skill skill) {
        GetPlayerLevel getPlayerLevel = new GetPlayerLevel(mainPlayer, skill);
        registry.sendEvent(getPlayerLevel);
        return getPlayerLevel.getLevel();
    }

    private boolean hasItem(String item) {
        CheckItemEvent checkItemEvent = new CheckItemEvent(mainPlayer, item);
        registry.sendEvent(checkItemEvent);
        return checkItemEvent.hasItem();
    }

    private boolean giveItem(String item) {
        GiveItemEvent giveItemEvent = new GiveItemEvent(mainPlayer, item);
        registry.sendEvent(giveItemEvent);
        return giveItemEvent.giveSuccessful();
    }

    private boolean removeItem(String item) {
        RemoveItemEvent removeItemEvent = new RemoveItemEvent(mainPlayer, item);
        registry.sendEvent(removeItemEvent);
        return removeItemEvent.removeSuccessful();
    }


    public void log(Object ... arguments) {
        System.out.println(Arrays.asList(arguments).toString());
    }
}
