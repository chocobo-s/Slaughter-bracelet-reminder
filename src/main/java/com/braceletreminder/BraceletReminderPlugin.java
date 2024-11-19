package com.braceletreminder;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;

import static net.runelite.api.ItemID.*;

@Slf4j
@PluginDescriptor(name = "Bracelet Reminder")

public class BraceletReminderPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BraceletReminderOverlay braceletOverlay;

	@Inject
	private BraceletReminderConfig config;

	@Provides
	BraceletReminderConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(BraceletReminderConfig.class);
	}

	boolean checkBracelet() {
		final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		return equipment.contains(BRACELET_OF_SLAUGHTER) || equipment.contains(EXPEDITIOUS_BRACELET);
	}

	boolean checkInventory() {
		final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		return (config.slaughter() && inventory.contains(BRACELET_OF_SLAUGHTER)) || (config.expeditious() && inventory.contains(EXPEDITIOUS_BRACELET));
	}

	boolean checkHelmet() {
		Item hat = client.getItemContainer(InventoryID.EQUIPMENT).getItem(EquipmentInventorySlot.HEAD.getSlotIdx());
		if (hat == null) {
			return false;
		}
		boolean slayerHelmEquipped = ItemVariationMapping.getVariations(SLAYER_HELMET).contains(hat.getId());
		boolean blackMaskEquipped = ItemVariationMapping.getVariations(BLACK_MASK).contains(hat.getId());
		boolean blackMaskiEquipped = ItemVariationMapping.getVariations(BLACK_MASK_I).contains(hat.getId());
		boolean spinyHelmEquipped = ItemVariationMapping.getVariations(SPINY_HELMET).contains(hat.getId());
		boolean nosePegEquipped = ItemVariationMapping.getVariations(NOSE_PEG).contains(hat.getId());
		boolean faceMaskEquipped = ItemVariationMapping.getVariations(FACEMASK).contains(hat.getId());
		boolean earMuffsEquipped = ItemVariationMapping.getVariations(EARMUFFS).contains(hat.getId());
		boolean gogglesEquipped = ItemVariationMapping.getVariations(REINFORCED_GOGGLES).contains(hat.getId());
		return (slayerHelmEquipped || blackMaskEquipped || blackMaskiEquipped || spinyHelmEquipped || nosePegEquipped || faceMaskEquipped || earMuffsEquipped || gogglesEquipped);
	}

	private Actor lastOpponent = null;
	private Instant lastTime = Instant.now();

	@Subscribe
	public void onInteractingChanged(InteractingChanged event) {
		if (event.getSource() != client.getLocalPlayer()) {
			return;
		}

		Actor opponent = event.getTarget();

		if (opponent == null) {
			lastTime = Instant.now();
			return;
		}
		if (opponent instanceof NPC) {
			NPC npc = (NPC) opponent;
			int combatLevel = npc.getCombatLevel();
			if (combatLevel >= 1) {
				lastOpponent = opponent;
			}
		}
	}

	public double getOpponentHealth() {
		int healthRatio = lastOpponent.getHealthRatio();
		int healthScale = lastOpponent.getHealthScale();
		return (healthRatio / (double) healthScale) * 100;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {

		if (lastTime != null && Duration.between(lastTime, Instant.now()).getSeconds() >= 10) {
			lastOpponent = null;
			lastTime = null;
		}
		Item gloves = client.getItemContainer(InventoryID.EQUIPMENT).getItem(EquipmentInventorySlot.GLOVES.getSlotIdx());
		if (gloves == null && checkInventory() && lastOpponent != null) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Your bracelet has broken", null);
		}
		//no gloves equipped and bracelets in inventory
		else if (lastOpponent != null && getOpponentHealth() < (double) config.healthThreshold() && !checkBracelet() && checkHelmet() && checkInventory()) {
			// enemy hp is below the threshold here so you can do stuff based on that
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You should equip bracelet :dinkdonk:!", null);
		} else {
			//we can hide the overlay now, since none of the previous conditions matched
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event) {
		if (lastOpponent != null && event.getActor().equals(lastOpponent)) {
			lastOpponent = null;
		}
	}

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(braceletOverlay);
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(braceletOverlay);
	}
}