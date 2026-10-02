/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.SkillRecharge;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import render.utils.math.Random;
import pd.messages.InlineText;

/** REN's cursed phone reproduces its original one-in-ten periodic status burst. */
public class CursePhone extends MiscEquippable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CursePhone.class)
			.t("name", "被诅咒的电话")
			.t("desc", "来自ren的世界的东西，说真的我不太懂，但据说只有诅咒的时候才有效果。");
	}




	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
