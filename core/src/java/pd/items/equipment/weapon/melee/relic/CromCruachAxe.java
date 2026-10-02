package pd.items.equipment.weapon.melee.relic;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicImmunity;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.enchantments.CromLuck;

public class CromCruachAxe extends RelicMeleeWeapon {

	public static final String AC_DISPEL = "DISPEL";

	public CromCruachAxe() {
		super(1.2f, 1f, 1);
		image = SpecificPlaceHolderDict.SOMETHING_0;
		enchant(new CromLuck());
	}

	@Override
	protected String relicAction() {
		return AC_DISPEL;
	}

	@Override
	protected void useRelicPower(Hero hero) {
		Buff.prolong(hero, MagicImmunity.class, 2f * (level() / 10));
	}

}
