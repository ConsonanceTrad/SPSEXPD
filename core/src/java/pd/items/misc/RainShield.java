/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;

/** Rain's training shield converts missing health into a physical shield each turn. */
public class RainShield extends MiscEquippable {

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }

	@Override protected RainShieldBuff createBuff() { return new RainShieldBuff(); }

	public class RainShieldBuff extends MiscBuff {
		@Override
		public boolean act() {
			if (target.HP > target.HT / 10) target.HP = Math.max(target.HT / 10, target.HP - 1);
			Buff.affect(target, ShieldArmor.class).level(Math.max(0, target.HT - target.HP));
			spend(TICK);
			return true;
		}
	}

	@Override public int value() { return 500 * quantity; }
}
