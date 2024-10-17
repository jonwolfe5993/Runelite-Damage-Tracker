package com.damagecounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Damage Counter")
public interface DamageCounterConfig extends Config
{
	@ConfigItem(
		keyName = "Damage....",
		name = "Can adjust damage stuff here",
		description = "Don't know yet"
	)
	default String greeting()
	{
		return "";
	}
}
