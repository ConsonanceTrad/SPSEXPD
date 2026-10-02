package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

public class PotionOfMixing extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfMixing.class)
			.t("name", "调和药水")
			.t("skillup", "你的战斗本能得到提升，魔法装备也开始加速充能。")
			.t("desc", "以种荚种子酿成的稀有药水。它能永久提升命中与闪避，并暂时加快魔法充能。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public void apply(Hero hero) {
		hero.improveCombatSkills(1);
		Buff.prolong(hero, Recharging.class, 30f);
		hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
		GLog.p(Messages.get(this, "skillup"));
	}
	@Override public int value() { return 100 * quantity; }
}
