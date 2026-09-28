/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ErrorSprite;
import com.watabou.utils.Random;

/** Dormant SPS-PD lotus summoner type, retained with its original save identity. */
public class LotusSummoner extends Mob {
	public boolean summoning;
	protected boolean firstSummon = true;
	{
		spriteClass = ErrorSprite.class;
		HP = HT = 40;
		defenseSkill = 14;
		EXP = 7;
		maxLvl = 14;
		loot = PotionOfHealing.class;
		lootChance = 0.2f;
		properties.add(Property.DEMONIC);
	}
	@Override protected boolean act() {
		if (summoning && state != HUNTING) summoning = false;
		return super.act();
	}
	@Override public int drRoll() { return super.drRoll() + Random.NormalIntRange(0, 5); }
}
