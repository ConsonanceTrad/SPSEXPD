package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Earthstar extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Earthstar.class)
			.t("name", "地裂星")
			.t("desc", "这种菌类并不属于地球。它可能来自虚空...或死星什么的...反正不会是小马国。食用它会撕裂这片区域所有人的身体。\n使用_1份水，1份蔬菜，1份地缚根种子_炼金");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	public Earthstar() { this(1); }
	public Earthstar(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			int min = mob.HP / 8;
			int max = mob.HP / 4;
			Buff.affect(mob, Bleeding.class).set(max > min ? Random.Int(min, max) : Math.max(1, max));
		}
		hero.damage(Math.max(1, hero.HP / 4), this);
		Buff.prolong(hero, Blindness.class, Random.IntRange(5, 7));
	}
}
