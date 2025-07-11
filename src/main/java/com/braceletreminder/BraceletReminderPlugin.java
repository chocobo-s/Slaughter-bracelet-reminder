package com.braceletreminder;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import java.time.Duration;
import java.time.Instant;


@Slf4j
@PluginDescriptor(name = "Bracelet Reminder")

public class BraceletReminderPlugin extends Plugin {

	private int overlayVisible;

	private static final int BRACELET_OF_SLAUGHTER = ItemID.BRACELET_OF_SLAUGHTER;
	private static final int EXPEDITIOUS_BRACELET = ItemID.EXPEDITIOUS_BRACELET;
	private static final int SALVE_AMULETEI = ItemID.NZONE_SALVE_AMULET_E;
	private static final int SALVE_AMULET_E = ItemID.LOTR_CRYSTALSHARD_NECKLACE_UPGRADE;
	private static final int AMULET_OF_AVARICE = ItemID.WILD_CAVE_AMULET;
	private static final int SLAYER_HELMET = ItemID.SLAYER_HELM;
	private static final int BLACK_MASK = ItemID.HARMLESS_BLACK_MASK;
	private static final int BLACK_MASK_I = ItemID.NZONE_BLACK_MASK;
	private static final int SPINY_HELMET = ItemID.WALLBEAST_SPIKE_HELMET;
	private static final int NOSE_PEG = ItemID.SLAYER_NOSEPEG;
	private static final int FACEMASK = ItemID.SLAYER_FACEMASK;
	private static final int EARMUFFS = ItemID.SLAYER_EARMUFFS;
	private static final int REINFORCED_GOGGLES  = ItemID.SLAYER_REINFORCED_GOGGLES;

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
		final ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
		if (equipment == null)
		{
			return false;
		}
		return equipment.contains(BRACELET_OF_SLAUGHTER) || equipment.contains(EXPEDITIOUS_BRACELET);
	}

	boolean checkInventory() {
		final ItemContainer inventory = client.getItemContainer(InventoryID.WORN);
		return (config.slaughter() && inventory.contains(BRACELET_OF_SLAUGHTER)) || (config.expeditious() && inventory.contains(EXPEDITIOUS_BRACELET));
	}
	boolean checkAmulet() {
		ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
		Item neck = equipment != null ? equipment.getItem(EquipmentInventorySlot.AMULET.getSlotIdx()) : null;
		if (neck == null) {
			return false;
		}
		boolean amuletEquipped = ItemVariationMapping.getVariations(SALVE_AMULETEI).contains(neck.getId());
		boolean amuletEquipped2 = ItemVariationMapping.getVariations(SALVE_AMULET_E).contains(neck.getId());
		boolean amuletEquipped3 = ItemVariationMapping.getVariations(AMULET_OF_AVARICE).contains(neck.getId());
		return (amuletEquipped || amuletEquipped2 || amuletEquipped3);

	}

	boolean checkHelmet() {
		ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
		Item hat = equipment != null ? equipment.getItem(EquipmentInventorySlot.HEAD.getSlotIdx()) : null;
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
			lastOpponent = null;
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
		if (lastTime != null && Duration.between(lastTime, Instant.now()).getSeconds() >= config.overlayDuration()) {
			lastOpponent = null;
			lastTime = null;
		}
		if (overlayVisible != -1) {
			checkOverlay();
		}

		ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
		Item gloves = equipment != null ? equipment.getItem(EquipmentInventorySlot.GLOVES.getSlotIdx()) : null;
		boolean shouldAddOverlay =
				(gloves == null && checkInventory() && lastOpponent != null) ||
						(lastOpponent != null && getOpponentHealth() < (double) config.healthThreshold() && !checkBracelet() && (checkAmulet() || checkHelmet()) && checkInventory());

		if (shouldAddOverlay) {
			if (overlayVisible == -1) {
				addOverlay();
			}
		} else {
			if (overlayVisible != -1) {
				removeOverlay();
			}
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
		overlayVisible = -1;
	}

	@Override
	protected void shutDown() throws Exception {
		if (overlayManager != null)
			overlayManager.remove(braceletOverlay);


	}

	private void addOverlay() {
		if (overlayManager != null) {
			overlayManager.add(braceletOverlay);
			overlayVisible = client.getTickCount();
		}
	}

	private void removeOverlay() {

		overlayManager.remove(braceletOverlay);
		overlayVisible = -1;
	}

	private void checkOverlay() {
		if (checkBracelet() && lastOpponent == null) {
			removeOverlay();
		}
	}
}