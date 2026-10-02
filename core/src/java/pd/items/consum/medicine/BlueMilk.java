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
			.t("name", "蓝奶伞")
			.t("desc", "这种蘑菇像浆果一样鲜嫩多汁，食用它可以大幅度加快你的行动速度和恢复速度。而它对其他生物的效果是降低。\n使用_1份水，1份蔬菜，1份阳春草种子_炼金");
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
