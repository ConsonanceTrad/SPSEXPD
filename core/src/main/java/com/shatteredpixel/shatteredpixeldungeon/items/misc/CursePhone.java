/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Arcane;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SkillRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/** REN's cursed phone reproduces its original one-in-ten periodic status burst. */
public class CursePhone extends MiscEquippable {

	{
		image = ItemSpriteSheet.CURSE_PHONE;
		cursed = true;
	}

	@Override protected CurseTell createBuff() { return new CurseTell(); }

	void applyCurseEffects(Char target) {
		Buff.prolong(target, Terror.class, 10f).object = target.id();
		Buff.prolong(target, Vertigo.class, 10f);
		Buff.affect(target, ArmorBreak.class, 10f).level(30);
		Buff.prolong(target, Arcane.class, 2f);
		Buff.prolong(target, SkillRecharge.class, 10f);
	}

	public class CurseTell extends MiscBuff {
		@Override
		public boolean act() {
			if (cursed && Random.Int(10) == 0) applyCurseEffects(target);
			spend(TICK);
			return true;
		}
	}

	@Override public int value() { return 300 * quantity; }
}
