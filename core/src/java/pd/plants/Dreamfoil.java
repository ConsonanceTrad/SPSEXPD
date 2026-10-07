package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.BlobImmunity;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicalSleep;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.items.consum.food.vegetable.DreamLeaf;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.equipment.weapon.missiles.arrows.CharmFruit;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

public class Dreamfoil extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Dreamfoil.class)
			.t("name", "梦叶花")
			.t("refreshed", "你感觉浑身清爽。")
			.t("desc", "梦叶花含有强力中和成分。它会净化英雄、令其他生物陷入魔法睡眠。")
			.t("warden_desc", "_守望者_同样会被完全净化，而不会因此沉睡，并能在短时间内免疫所有环境影响。")
			.t("$seed.name", "梦叶花之种")
			.t("$exdreamfoil.name", "梦叶花果丛")
			.t("$exdreamfoil.desc", "生长梦叶果实的果丛。");
	}



	{ image = 10; seedClass = Seed.class; }

	//SPSEXPD: 破碎4.0 的 Mageroyal 与梦叶花是同一种植物，已合并到本类：
	//保留 Mageroyal 的守望者环境免疫与“浑身清爽”提示，同时保留本类原有的魔法睡眠。
	@Override public void activate(Char ch) {
		Dungeon.level.drop(new DreamLeaf(), pos).sprite.drop();
		if (ch != null) {
			PotionOfHealing.cure(ch);
			if (ch instanceof Hero) {
				GLog.i( Messages.get(this, "refreshed") );
				if (((Hero) ch).subClass == HeroSubClass.WARDEN){
					Buff.affect(ch, BlobImmunity.class, BlobImmunity.DURATION/2f);
				}
			} else {
				Buff.affect(ch, MagicalSleep.class);
			}
		}
	}
	public static class Seed extends Plant.Seed {
		{ image = ConsumPotionSeedSeedDict.SEED_MAGEROYAL_0; plantClass = Dreamfoil.class; explantClass = ExDreamfoil.class; }
	}
	public static class ExDreamfoil extends SpsFruitBush {
		{ image = 10; }
	}
}
