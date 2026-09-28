/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BoxStar;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StoneIce;
import com.shatteredpixel.shatteredpixeldungeon.sprites.IceRabbit2Sprite;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.EasterEgg;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/** FrostNova's invulnerable opening phase and faster final form. */
public class UIcecorps2 extends UIcecorps {
	private int shieldTurns = 30;

	{
		spriteClass = IceRabbit2Sprite.class;
		baseSpeed = 1.5f;
		HP = HT = 1500;
		EXP = 20;
		loot = new EasterEgg();
		lootChance = 1f;
	}

	@Override protected boolean act() {
		if (shieldTurns > 1) {
			Buff.prolong(this, BoxStar.class, 3f);
			shieldTurns--;
		}
		return super.act();
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) Buff.affect(enemy, StoneIce.class).level(3f);
		enemy.damage(damageRoll() * 3 / 4, Frost.class);
		return damage / 4;
	}

	@Override protected Class<? extends BossRushBoss> nextBoss() { return UYog.class; }

	private static final String SHIELD = "shield";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(SHIELD, shieldTurns); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); shieldTurns = bundle.contains(SHIELD) ? bundle.getInt(SHIELD) : 30; }
}
