/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Link Sword protection: incoming damage is cancelled and partly reflected. */
public class MirrorShield extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MirrorShield.class)
			.t("name", "智慧守护")
			.t("desc", "接下来%s回合内免疫所受伤害，并将其中一部分反射给攻击者。");
	}

	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon(){ return BuffIndicator.ARMOR; }
	@Override public String desc(){ return Messages.get(this,"desc",dispTurns()); }
}
