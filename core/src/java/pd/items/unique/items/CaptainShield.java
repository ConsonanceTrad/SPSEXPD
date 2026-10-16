/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 队长之盾 —— 决斗二档（王冠 B 路线）的副手武器。
 *
 * <p>与神木圆盾同源的「先攻」吸收，但吸收之后还会把这一击_原样反射_给来源。</p>
 */
public class CaptainShield extends RoundShieldUnique {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.DUELIST_SHIELD_T2_2_CAPTAIN;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(CaptainShield.class)
			.t("name", "队长之盾")
			.t("absorbed", "队长之盾抢先迎上，%s的这一击被完整卸掉了。")
			.t("reflected", "盾面把这一击原样弹回了%s身上。")
			.t("desc", "一面艾德曼合金打造的盾牌，盾面亮得能照出对手脸上的错愕。\n\n只要你带着它_先手命中_过某个敌人，那个敌人_第一次对你造成的物理伤害会被它完整吸收_，并_原样反射_回对方身上。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	/** SPSEXPD: 吸收之后把这一击反射回来源。 */
	@Override
	protected void onAbsorbed(Hero hero, Char src, float damage) {
		if (src == null || !src.isAlive()) return;
		GLog.p(Messages.get(this, "reflected", src.name()));
		src.damage(Math.max(1, Math.round(damage)), this);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
