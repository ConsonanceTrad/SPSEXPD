package pd.items.equipment.weapon.melee.relic;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicImmunity;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.enchantments.CromLuck;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class CromCruachAxe extends RelicMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CromCruachAxe.class)
			.t("name", "碎肉巨斧")
			.t("desc", "这把巨型战斧无坚不摧，重如泰山，需要举过肩才有空间挥舞。\n借由橙色魔法石的能量，它会反复取更高的伤害结果，并在充能完毕后赋予魔法免疫。")
			.t("ac_dispel", "魔法免疫")
			.t("stats_desc", "");
	}




	public static final String AC_DISPEL = "DISPEL";

	public CromCruachAxe() {
		super(1.2f, 1f, 1);
		image = EquipmentEquipWeaponBasicWeaponDict.BONE_SAW_GREATAXE;
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
