/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.actors.Char;
import pd.items.equipment.artifacts.MasterThievesArmband;
import pd.messages.InlineText;

/**
 * SPSEXPD: 白银之手 —— 魔术之手法杖（MasterThievesArmband）的强化版独特法杖。
 * 与魔术之手法杖机制相同（偷窃/隔空开箱等），但**不再随时间自动充能**：
 * 覆写 {@link #charge(Char)} 为空，不挂 Wand.Charger；改为**击杀敌人**时获得充能。
 */
public class SilverHand extends MasterThievesArmband {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SilverHand.class)
			.t("name", "白银之手")
			.t("desc", "魔术之手法杖的白银版本：杖顶的银手更加贪婪，却也更加挑剔。它不会随时间自行蓄力，只有当主人亲手终结一个敌人时，它才会重新充盈。其余能力与魔术之手法杖一致——摸到活物就伤人取物，摸到货架就按价钱掂量着偷。")
			.t("charge_desc", "本杖不随时间自动充能，而是_每击杀一个敌人获得 1 点充能_。");
	}

	{
		//SPSEXPD: 独特物品
		unique = true;
	}

	/** SPSEXPD: 覆写为空——不挂 Wand.Charger，彻底关掉「随时间自动充能」。 */
	@Override public void charge( Char owner ) { }

	/** SPSEXPD: 击杀敌人时由 Mob.die 调用，获得 1 点充能。 */
	public void onKill() {
		gainCharge( 1f );
	}
}
