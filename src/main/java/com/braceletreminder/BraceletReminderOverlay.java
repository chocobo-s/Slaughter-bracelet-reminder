package com.braceletreminder;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class BraceletReminderOverlay extends OverlayPanel {
    private final BraceletReminderConfig config;
    private final Client client;

    private final String LONG_TEXT = "You're not wearing a bracelet";
    private final String SHORT_TEXT = "bracelet";

    @Inject
    private BraceletReminderOverlay(BraceletReminderConfig config, Client client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        switch (config.reminderStyle()) {
            case LONG_TEXT:
                panelComponent.getChildren().add((LineComponent.builder())
                        .left(LONG_TEXT)
                        .build());

                panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(LONG_TEXT) - 20, 0));
                break;
            case SHORT_TEXT:
                panelComponent.getChildren().add((LineComponent.builder())
                        .left(SHORT_TEXT)
                        .build());
                panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(SHORT_TEXT) - 10, 0));
                break;

        }
        if (config.shouldFlash()) {
            if (client.getGameCycle() % 40 >= 20) {
                panelComponent.setBackgroundColor(config.flashColor1());
            } else {
                panelComponent.setBackgroundColor(config.flashColor2());
            }
        } else {
            panelComponent.setBackgroundColor(config.flashColor1());
        }

        setPosition(OverlayPosition.TOP_LEFT);
        return panelComponent.getPreferredSize();

    }

}