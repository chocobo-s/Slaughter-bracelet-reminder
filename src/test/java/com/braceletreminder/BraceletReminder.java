package com.braceletreminder;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BraceletReminder
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BraceletReminderPlugin.class);
		RuneLite.main(args);
	}
}