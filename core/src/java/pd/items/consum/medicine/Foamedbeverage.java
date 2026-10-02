package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.EarthImbue;
import pd.actors.buffs.FireImbue;
import pd.actors.buffs.FrostImbue;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.ToxicImbue;
import pd.actors.hero.Hero;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Foamedbeverage extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Foamedbeverage.class)
			.t("name", "发泡饮料")
			.t("desc", "一起哈啤！\n使用_2份水，1份蔬菜，1份种子，1份水果_炼金");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	public Foamedbeverage() {}
	public Foamedbeverage(int number) { quantity = number; }
	@Override protected void onUse(Hero hero) {
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.affect(hero, Bless.class, 30f);
		Buff.affect(hero, BerryRegeneration.class).level(hero.HT / 4);
		switch (Random.Int(4)) {
			case 0: Buff.affect(hero, FireImbue.class).set(60f); break;
			case 1: Buff.affect(hero, FrostImbue.class, 60f); break;
			case 2: Buff.affect(hero, ToxicImbue.class).set(60f); break;
			default: Buff.affect(hero, EarthImbue.class, 60f); break;
		}
	}
}
