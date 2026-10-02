package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BeCorrupt;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.messages.InlineText;

public class DeathCap extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DeathCap.class)
			.t("name", "致死帽")
			.t("desc", "这种头上布满白斑的红色菌类肯定是具有致命效果的东西。希望它也会对其他生物有效。\n使用_1份水，1份蔬菜，1份毒草种子_炼金");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	public DeathCap() { this(1); }
	public DeathCap(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, BeOld.class).set(50f);
			Buff.affect(mob, BeCorrupt.class).level(50);
		}
		hero.damage(Math.max(1, hero.HP / 2), this);
		Buff.prolong(hero, Cripple.class, Cripple.DURATION);
	}
}
