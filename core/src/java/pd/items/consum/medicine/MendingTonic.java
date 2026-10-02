package pd.items.consum.medicine;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

import pd.Assets;
import pd.Challenges;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class MendingTonic extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MendingTonic.class)
			.t("name", "修复补剂")
			.t("ac_drink", "饮用")
			.t("mend", "补剂持续修复着你的伤口。")
			.t("desc", "一种经过谨慎稀释的异界恢复药剂。它的即时治疗能力弱于治疗药剂，能提供实用补给，但不会取代主地牢原有的核心治疗资源。");
	}




	public static final String AC_DRINK = "DRINK";

	{
		image = SpecificPlaceHolderDict.POTION_HOLDER_0;
		stackable = true;
		defaultAction = AC_DRINK;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_DRINK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_DRINK.equals(action)) return;
		detach(hero.belongings.backpack);
		PotionOfHealing.cure(hero);
		if (Dungeon.isChallenged(Challenges.NO_HEALING)) {
			PotionOfHealing.pharmacophobiaProc(hero);
		} else {
			int healing = 8 + hero.HT / 3;
			Buff.affect(hero, Healing.class).setHeal(healing, 0.2f, 0, true);
			GLog.p(Messages.get(this, "mend"));
		}
		Sample.INSTANCE.play(Assets.Sounds.DRINK);
		hero.sprite.operate(hero.pos);
		hero.spendAndNext(1f);
	}

	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public int value() { return 20 * quantity; }
}
