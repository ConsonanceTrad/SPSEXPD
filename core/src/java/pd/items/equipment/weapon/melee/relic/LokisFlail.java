package pd.items.equipment.weapon.melee.relic;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shadows;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.enchantments.LokisPoison;
import render.noosa.audio.Sample;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class LokisFlail extends RelicMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LokisFlail.class)
			.t("name", "重型链枷")
			.t("desc", "铁链上附着着一个带刺钢球。这件武器笨重难用，但只要结实命中便极具破坏力。\n借由绿色魔法石的能量，它可以施加猛毒，并在充能完毕后隐藏使用者的身形。")
			.t("ac_stealth", "魔法隐形")
			.t("stats_desc", "");
	}




	public static final String AC_STEALTH = "STEALTH";

	public LokisFlail() {
		super(0.8f, 1.2f, 2);
		image = EquipmentEquipWeaponBasicWeaponDict.VENOMOUS_HEAVY_FLAIL;
		hitSound = Assets.Sounds.HIT_CRUSH;
		enchant(new LokisPoison());
	}

	@Override
	protected String relicAction() {
		return AC_STEALTH;
	}

	@Override
	protected void useRelicPower(Hero hero) {
		Buff.affect(hero, Shadows.class, 10f);
		Sample.INSTANCE.play(Assets.Sounds.MELD);
	}

}
