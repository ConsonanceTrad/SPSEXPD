/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.actors.buffs.FirstStrike;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.weapon.melee.RoundShield;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 神木圆盾（独特）—— 决斗一档（面具 B 路线）的副手武器。
 *
 * <p>保留神木圆盾本身的防护；另加「先攻」：你_先手命中_过的敌人第一次对你造成物理伤害时，
 * 这一击会被盾牌_完整吸收_。</p>
 */
public class RoundShieldUnique extends RoundShield implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.DUELIST_SHIELD_T1_2_ROUND;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(RoundShieldUnique.class)
			.t("name", "神木圆盾")
			.t("absorbed", "圆盾抢先迎上，%s的这一击被完整卸掉了。")
			.t("desc", "一面由神木整块削成的圆盾，边缘还留着树皮的纹路。\n\n除了本身的格挡能力，它还能_抢下先手_：只要你带着它_先手命中_过某个敌人，那个敌人_第一次对你造成的物理伤害会被盾牌完整吸收_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	/** SPSEXPD: 主手或副手任一挂着这面盾就算装备。 */
	public static RoundShieldUnique equippedShield(Hero hero) {
		if (hero == null) return null;
		if (hero.belongings.weapon instanceof RoundShieldUnique) return (RoundShieldUnique) hero.belongings.weapon;
		if (hero.belongings.secondWep instanceof RoundShieldUnique) return (RoundShieldUnique) hero.belongings.secondWep;
		return null;
	}

	/** SPSEXPD: 这面盾没有主动技能——只保留「先攻」这个被动。
	 *  （武器战技入口已在 MeleeWeapon 里整体移除，这里不再需要单独 remove AC_ABILITY。） */

	/** SPSEXPD: 英雄是否带着任意一种「先攻盾」（圆盾 / 队长之盾）。 */
	public static boolean hasFirstStrikeShield(Hero hero) {
		return equippedShield(hero) != null;
	}

	/**
	 * SPSEXPD: 在 Hero.damage 里调用——若伤害来自「先攻」目标，则吸收这次物理伤害。
	 * 返回调整后的伤害。
	 */
	public static float absorbFirstStrike(Hero hero, Char src, float damage) {
		if (hero == null || src == null || damage <= 0f) return damage;
		RoundShieldUnique shield = equippedShield(hero);
		if (shield == null) return damage;
		FirstStrike mark = src.buff(FirstStrike.class);
		if (mark == null) return damage;
		mark.detach();
		GLog.p(Messages.get(RoundShieldUnique.class, "absorbed", src.name()));
		shield.onAbsorbed(hero, src, damage);
		return 0f;
	}

	/** SPSEXPD: 吸收后的附带效果（神木圆盾只吸收；队长之盾覆写为反射）。 */
	protected void onAbsorbed(Hero hero, Char src, float damage) {
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
