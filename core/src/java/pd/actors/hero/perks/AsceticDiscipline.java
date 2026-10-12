/*
 * 苦修 —— 苦修者的开局装备没有任何免费强化。
 *
 * SPSEXPD: 苦修者的人物定位（源自 ascetic_desc）。本条为展示型特质：
 * 不对应代码里的隐藏判定，只让玩家在选角与特质页上立刻看清该职业的取舍。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class AsceticDiscipline extends Perk {

	static {
		InlineText.of(AsceticDiscipline.class)
				.t("title", "苦修")
				.t("desc", "你的开局装备不带任何免费强化，一切提升都要靠自己争取；"
						+ "作为回报，你能在近身格斗与受控施法之间灵活切换。");
	}

	public AsceticDiscipline() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.ASCETIC;
	}
}
