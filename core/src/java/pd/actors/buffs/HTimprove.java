/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Temporarily raises the hero's unmodified maximum HP by 20%. */
public class HTimprove extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HTimprove.class)
			.t("name", "生命强化")
			.t("desc", "未经装备修正的生命上限暂时提高20%%。\n\n剩余效果时长：%s回合。");
	}



	{ type = buffType.NEUTRAL; announced = true; }
	@Override public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (target instanceof Hero) ((Hero) target).updateHT(true);
		return true;
	}
	@Override public void detach() {
		Char oldTarget = target;
		super.detach();
		if (oldTarget instanceof Hero) ((Hero) oldTarget).updateHT(false);
	}
	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
