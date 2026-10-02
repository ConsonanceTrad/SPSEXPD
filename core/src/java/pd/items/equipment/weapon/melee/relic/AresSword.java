package pd.items.equipment.weapon.melee.relic;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.enchantments.AresLeech;
import pd.messages.InlineText;

public class AresSword extends RelicMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AresSword.class)
			.t("name", "萃魂长剑")
			.t("desc", "这柄剑的厚长利刃闪烁着令人安心的寒光，不过它的长度也确实让它非常沉重。\n借由紫色魔法石的能量，它可以抽取周围敌人的生命精华并缓慢回馈给持有者。")
			.t("ac_regen", "灵魂萃取")
			.t("stats_desc", "");
	}




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
