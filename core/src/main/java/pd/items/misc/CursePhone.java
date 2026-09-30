/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.Char;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.SkillRecharge;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

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
