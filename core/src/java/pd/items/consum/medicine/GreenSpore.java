package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

public class GreenSpore extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GreenSpore.class)
			.t("name", "绿菌孢")
			.t("not_time", "奇怪的能量流入了你的背包，但是什么也没发生。")
			.t("desc", "露珠研究者培育出来的新品种蘑菇，相比其他蘑菇这个完全没有副作用，并且可以让露珠产出更加频繁。\n使用_1份水，1份蔬菜，1份集露草种子_炼金");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) {
		if (!Dungeon.dewWater && !Dungeon.dewDraw) {
			GLog.w(Messages.get(this, "not_time"));
			return;
		}
		Buff.affect(hero, Dewcharge.class, 100f);
	}
	@Override public int value() { return 20 * quantity; }
}
