/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Triples attacks against full-health mobs and adds 50% against critically wounded mobs. */
public class SpAttack extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpAttack.class)
			.t("name", "特殊攻击")
			.t("desc", "攻击满血敌人时造成三倍伤害，攻击生命低于四分之一的敌人时额外造成50%%伤害。\n\n剩余效果时长：%s回合。");
	}



	{ type = buffType.NEUTRAL; announced = true; }
	@Override public int icon() { return BuffIndicator.WEAPON; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
