package pd.items.weapon.melee.relic;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.weapon.enchantments.AresLeech;

public class AresSword extends RelicMeleeWeapon {

	public static final String AC_REGEN = "REGEN";

	public AresSword() {
		 super(1f, 1f, 1);
		image = SpecificPlaceHolderDict.SOMETHING_0;
		enchant(new AresLeech());
	}

	@Override
	protected String relicAction() {
		return AC_REGEN;
	}

	@Override
	protected void useRelicPower(Hero hero) {
		Buff.affect(hero, BerryRegeneration.class).level(Math.max(1, level()));
	}

}
