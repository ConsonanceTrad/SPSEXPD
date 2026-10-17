/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

/**
 * SPSEXPD: 「它先动了手」标记——挂在_先对英雄造成过伤害_的敌人身上。
 *
 * <p>神木圆盾 / 队长之盾的「先攻」只认可真正的先手：如果这个敌人已经伤害过你，
 * 它就不再被视为被你抢先，{@link FirstStrike} 也不会再挂到它身上
 * （{@code RoundShieldUnique.absorbFirstStrike} 因此对它无效）。</p>
 */
public class StruckHeroFirst extends Buff {

	{
		type = buffType.NEUTRAL;
	}

	//SPSEXPD: 纯标记 buff，不需要每回合做任何事，也不会自己消失
	@Override
	public boolean act() {
		spend(TICK);
		return true;
	}
}
