/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.InlineText;

/** SPS-PD charm does not lose duration when its target takes damage. */
public class SpsCharm extends Charm {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpsCharm.class)
			.t("name", "魅惑")
			.t("heromsg", "你被魅惑了！")
			.t("desc", "被魅惑的单位无法直接攻击魅惑施行者，但依然可以攻击其他敌人。与现代魅惑不同，受到伤害不会缩短此效果。\n\n魅惑效果剩余时间：%s回合");
	}




	{
		announced = false;
	}

	@Override
	public void recover(Object src) {
		// Damage did not shorten charm in SPS-PD 0.9.8.
	}
}
