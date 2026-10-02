/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.Assets;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.skills.ClassSkill;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Converts into the reader's SPS-PD class-skill item. */
public class SkillBook extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SkillBook.class)
			.t("name", "职业技能书")
			.t("ac_apply", "使用")
			.t("desc", "阅读后会生成一本与当前职业对应、可反复使用的职业技能。")
			.t("not_ready", "这个职业的技能仍在复刻中，技能书没有被消耗。")
			.t("no_space", "背包里没有容纳职业技能的空间。");
	}




	private static final float TIME_TO_APPLY = 2f;
	private static final String AC_APPLY = "APPLY";

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
		defaultAction = AC_APPLY;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_APPLY.equals(action)) {
			super.execute(hero, action);
			return;
		}
		ClassSkill classSkill = ClassSkill.createFor(hero.heroClass);
		if (classSkill == null) {
			GLog.w(Messages.get(this, "not_ready"));
			return;
		}
		if (!classSkill.collect(hero.belongings.backpack)) {
			GLog.w(Messages.get(this, "no_space"));
			return;
		}
		detach(hero.belongings.backpack);
		ClassSkill.resetCooldown();
		if (hero.sprite != null) {
			hero.sprite.centerEmitter().start(Speck.factory(Speck.EVOKE), 0.05f, 10);
			hero.spend(TIME_TO_APPLY);
			hero.busy();
			hero.sprite.operate(hero.pos);
		} else {
			hero.spendAndNext(TIME_TO_APPLY);
		}
		Sample.INSTANCE.play(Assets.Sounds.EVOKE);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 200 * quantity; }
}
