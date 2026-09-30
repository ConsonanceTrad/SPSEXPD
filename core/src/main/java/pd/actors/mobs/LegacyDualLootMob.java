/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.items.Generator;
import pd.items.Item;
import pd.items.misc.LuckyBadge;
import watabou.utils.Random;
import watabou.utils.Reflection;

/** Reproduces SPS-PD's primary-roll, then secondary-roll loot sequence. */
abstract class LegacyDualLootMob extends Mob {

	private Object primaryLoot;
	private Object secondaryLoot;
	private float primaryChance;
	private float secondaryChance;

	protected final void setupLegacyDualLoot(Object primary, float firstChance,
			Object secondary, float secondChance) {
		primaryLoot = primary;
		secondaryLoot = secondary;
		primaryChance = firstChance;
		secondaryChance = secondChance;
		loot = primary;
		lootChance = combinedChance(firstChance, secondChance, 0);
	}

	@Override
	public float lootChance() {
		return combinedChance(primaryChance, secondaryChance,
				LuckyBadge.luckBonus(Dungeon.hero));
	}

	@Override
	public Item createLoot() {
		float primaryShare = primaryShare(primaryChance, secondaryChance,
				LuckyBadge.luckBonus(Dungeon.hero));
		return generateLoot(Random.Float() < primaryShare ? primaryLoot : secondaryLoot);
	}

	static float combinedChance(float firstChance, float secondChance, int luckBonus) {
		float bonus = 0.02f * luckBonus;
		float first = clampChance(firstChance + bonus);
		float second = clampChance(secondChance + bonus);
		return first + (1f - first) * second;
	}

	static float primaryShare(float firstChance, float secondChance, int luckBonus) {
		float first = clampChance(firstChance + 0.02f * luckBonus);
		float combined = combinedChance(firstChance, secondChance, luckBonus);
		return combined <= 0f ? 0f : first / combined;
	}

	private static float clampChance(float chance) {
		return Math.max(0f, Math.min(1f, chance));
	}

	@SuppressWarnings("unchecked")
	private Item generateLoot(Object source) {
		if (source instanceof Generator.Category) {
			return Generator.random((Generator.Category) source);
		}
		if (source instanceof Class<?>) {
			return Reflection.newInstance((Class<? extends Item>) source);
		}
		return (Item) source;
	}
}
