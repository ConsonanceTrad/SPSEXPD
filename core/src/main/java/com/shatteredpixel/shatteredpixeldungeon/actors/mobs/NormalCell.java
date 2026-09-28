/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CellmobSprite;
import com.watabou.utils.Random;

/** The stationary life-cell produced by legacy evolve ammunition. */
public class NormalCell extends Mob {
	{
		spriteClass = CellmobSprite.class;
		HP = HT = 1;
		defenseSkill = 0;
		baseSpeed = 0.5f;
		properties.add(Property.BOSS);
		properties.add(Property.MINIBOSS);
	}

	@Override public int damageRoll() {
		int heroHT = Dungeon.hero == null ? 20 : Dungeon.hero.HT;
		return Random.NormalIntRange(heroHT / 20, heroHT / 10);
	}
	@Override public int attackSkill(Char target) { return 99; }
	@Override public int drRoll() { return 0; }
}
