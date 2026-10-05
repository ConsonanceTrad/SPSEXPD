package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BeCorrupt;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class DeathCap extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DeathCap.class)
			.t("name", "死亡溶剂瓶")
			.t("desc", "这种标签的瓶子肯定装着具有致命效果的东西。希望它也会对其他生物有效。");
	}



	{ image = ConsumPotionSeedBasicPotionDict.POISON_MUSHROOM; }
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
