package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.messages.InlineText;

public class GoldenJelly extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoldenJelly.class)
			.t("name", "凝胶团")
			.t("desc", "与其说这是一种菌类，倒不如说这是一袋种子。它是这种菌类的繁殖的表现:受到挤压后，大量的孢子飞向四周，减缓生物的移动的同时寄生目标。\n使用_1份水，1份蔬菜，1份风暴藤种子_炼金");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	public GoldenJelly() { this(1); }
	public GoldenJelly(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) Buff.affect(mob, GrowSeed.class).set(10f);
		Buff.affect(hero, Vertigo.class, 10f);
	}
}
