// BSD 2-Clause License
//
//Copyright (c) 2021, PortAGuy
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//1. Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
//
//2. Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
//FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
//DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
//CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    private final BraceletReminderPlugin plugin;
    private final String LONG_TEXT = "You're not wearing a bracelet";
    private final String SHORT_TEXT = "Bracelet";

    @Inject
    private BraceletReminderOverlay(BraceletReminderConfig config, BraceletReminderPlugin plugin, Client client) {
        this.config = config;
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (panelComponent == null) {
            return null;
        }
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
                panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(SHORT_TEXT) + 10, 0));
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

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        return panelComponent.render(graphics);
    }
}