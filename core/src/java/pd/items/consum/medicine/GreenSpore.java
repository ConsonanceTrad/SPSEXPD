package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class GreenSpore extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GreenSpore.class)
			.t("name", "露珠能量瓶")
			.t("not_time", "奇怪的能量流入了你的背包，但是什么也没发生。")
			.t("desc", "露珠研究者提取出的蘑菇精华，可以让露珠产出更加频繁。");
	}



	{ image = ConsumPotionSeedBasicPotionDict.DEW_FUNGUS_SPORE; }
	@Override protected void onUse(Hero hero) {
		if (!Dungeon.dewWater && !Dungeon.dewDraw) {
			GLog.w(Messages.get(this, "not_time"));
			return;
		}
		Dewcharge.charge(hero, 100f);
	}
	@Override public int value() { return 20 * quantity; }
}
