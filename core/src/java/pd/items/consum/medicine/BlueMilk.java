package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackDown;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class BlueMilk extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BlueMilk.class)
			.t("name", "蓝浆果汁瓶")
			.t("desc", "一种特殊的鲜榨果汁，使类人生物的外伤愈合加速，而其他生物的效果是降低。");
	}



	{ image = ConsumPotionSeedBasicPotionDict.BLUE_CAP_MUSHROOM; }
	public BlueMilk() { this(1); }
	public BlueMilk(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, Slow.class, 50f);
			Buff.affect(mob, AttackDown.class, 50f).level(50);
		}
		Buff.affect(hero, HasteBuff.class, 10f);
		Buff.affect(hero, BerryRegeneration.class).level(hero.HP / 2);
	}
}
