package com.damagecounter;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Hitsplat;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcDespawned;


@Slf4j
@PluginDescriptor(
	name = "DamageCounter",
	description = "counts damage and gives ratio of 0s hit"
)
public class DamageCounterPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private DamageCounterConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("");
	}

	private int damage = 0;
	private int numZeros = 0;
	private int total = 0;
	private int streak = 0;
	private int lastHit = 1;



	@Subscribe
	// Gets hitsplat
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied){
		Hitsplat hitsplat = hitsplatApplied.getHitsplat();
		Actor actor = hitsplatApplied.getActor();

		int hitType = hitsplat.getHitsplatType();
		// client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "hit type: " + hitType, null);

		if (hitType == 74){
			damage += hitsplat.getAmount();
		}

		if (!(actor instanceof NPC))
		{
			return;
		}

		// If damage is from you and > 0 add to total damage, else increase number of zeros by 1
		// increase total hits
		if(hitsplat.isMine()){
			int hit = hitsplat.getAmount();

			if(hit > 0){
				damage += hit;

				if(lastHit == 0){
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("Streak: %d", streak), null);
					streak = 0;
					lastHit = hit;
				}else{
					lastHit = hit;
				}
			}else{
				numZeros++;
				streak++;
				lastHit = hit;
			}
			total++;


			
		}
		
	}

	@Subscribe
	// Get area sounds
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event){
		int splashSound = event.getSoundId();
		// If splash sound increase number of zeros and total by 1
		if(splashSound == 227){
			numZeros++;
			total++;
			streak++;
			lastHit = 0;
		}
	}

	@Subscribe
	// NPC dies
	public void onNpcDespawned(NpcDespawned npcDespawned){
		NPC npc = npcDespawned.getNpc();
		// Get percentage of 0's
		float ratio = ((float)numZeros / (float)total) * 100;
		// When npc dies, output damate, number of zeros, and percentage then reset
		if(npc.isDead()){
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("Damage: %d, Zeros: %d/%d %.2f%%", damage, numZeros, total, ratio ), null);
			damage = 0;
			numZeros = 0;
			ratio = 0;
			total = 0;
			streak = 0;
			lastHit = 1;
		}
		

	}

	@Provides
	DamageCounterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DamageCounterConfig.class);
	}
}
