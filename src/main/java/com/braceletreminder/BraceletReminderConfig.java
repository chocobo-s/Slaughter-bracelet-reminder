package com.braceletreminder;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("BraceletReminder")
public interface BraceletReminderConfig extends Config {
	@ConfigItem(keyName = "slaughter", name = "Enable Bracelet of Slaughter", description = "Highlights Bracelets of Slaughter", position = 1)
	default boolean slaughter() {
		return true;
	}

	@ConfigItem(keyName = "expeditious", name = "Enable Expeditious Bracelet", description = "Highlights Expeditious Bracelets", position = 2)
	default boolean expeditious() {
		return true;
	}

	@ConfigItem(keyName = "LowHPThreshold", name = "Health Threshold", description = "Choose what HP threshold you want to be notified to wear your bracelet", position = 7)

	default int healthThreshold() {
		return 15;
	}

	@ConfigItem(keyName = "shouldFlash", name = "Disco Mode", description = "make the popup box flash between two colours of your choosing", position = 3)
	default boolean shouldFlash() { return false; }

	@Alpha
	@ConfigItem(keyName = "flashColor1", name = "Main Colour", description = "pick the main colour of the popup reminder", position = 4)
	default Color flashColor1() {
		return new Color(23, 255, 177, 150);
	}
	@Alpha
	@ConfigItem(keyName = "flashColor2", name = "Secondary Colour", description = "pick the secondary colour if you have toggled the reminder to flash", position = 5)
	default Color flashColor2() {
		return new Color(151, 88, 221, 150);
	}
	@ConfigItem(keyName = "ReminderStyle", name = "Reminder Style", description = "Changes the style of the reminder overlay", position = 6)
	default BraceletOverlayStyle reminderStyle() { return BraceletOverlayStyle.LONG_TEXT; }
}