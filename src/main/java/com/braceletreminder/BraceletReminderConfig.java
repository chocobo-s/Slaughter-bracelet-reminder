package com.braceletreminder;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import java.awt.*;

@ConfigGroup("BraceletReminder")
public interface BraceletReminderConfig extends Config {
	@ConfigItem(keyName = "slaughter", name = "Bracelet of Slaughter", description = "Highlights Bracelets of Slaughter.", position = 1)
	default boolean slaughter() {
		return true;
	}

	@ConfigItem(keyName = "expeditious", name = "Expeditious Bracelet", description = "Highlights Expeditious Bracelets.", position = 2)
	default boolean expeditious() {
		return true;
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "LowHPThreshold", name = "Health Threshold", description = "Remind you to wear your bracelet when the monster's health drops to or below this percentage.", position = 7)
	default int healthThreshold() {
		return 15;
	}

	@ConfigItem(keyName = "shouldFlash", name = "Disco Mode", description = "make the popup box flash between two colours of your choosing.", position = 3)
	default boolean shouldFlash() { return false; }

	@Alpha
	@ConfigItem(keyName = "flashColor1", name = "Main Colour", description = "pick the main colour of the popup reminder.", position = 4)
	default Color flashColor1() {
		return new Color(23, 255, 177, 150);
	}
	@Alpha
	@ConfigItem(keyName = "flashColor2", name = "Secondary Colour", description = "pick the secondary colour if you have toggled the reminder to flash.", position = 5)
	default Color flashColor2() {
		return new Color(151, 88, 221, 150);
	}
	@ConfigItem(keyName = "ReminderStyle", name = "Reminder Style", description = "Changes the style of the reminder overlay.", position = 6)
	default BraceletOverlayStyle reminderStyle() { return BraceletOverlayStyle.LONG_TEXT;}
	@ConfigItem(keyName = "overlayDuration", name = "Overlay Duration", description = "How long the overlay will stay up in seconds.", position = 8)
	default int overlayDuration() {return 10;}
}
