/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CurseWeb;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LightShootAttack;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShadowCurse;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ErrorSprite;
import com.watabou.utils.Random;

/** Dormant SPS-PD summoner type, retained with its original behavior and save identity. */
public class DemonSummoner extends Mob {
	{
		spriteClass = ErrorSprite.class;
		HP = HT = 300;
		defenseSkill = 0;
		EXP = 10;
		maxLvl = 29;
		loot = Generator.Category.RANGED;
		lootChance = 0.2f;
		properties.add(Property.DEMONIC);
		resistances.add(LightShootAttack.class);
		immunities.add(CurseWeb.class);
		immunities.add(ShadowCurse.class);
		FLEEING = new SummonerFleeing();
	}
	@Override public int damageRoll() { return Random.NormalIntRange(30, 50); }
	@Override public int attackSkill(Char target) { return 50; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 6); }
	private class SummonerFleeing extends Fleeing {
		@Override protected void nowhereToRun() {
			if (buff(Terror.class) == null) state = HUNTING;
			else super.nowhereToRun();
		}
	}
}
