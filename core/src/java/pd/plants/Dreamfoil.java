package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicalSleep;
import pd.actors.hero.Hero;
import pd.items.consum.food.vegetable.DreamLeaf;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.equipment.weapon.missiles.arrows.CharmFruit;
import pd.messages.InlineText;

public class Dreamfoil extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Dreamfoil.class)
			.t("name", "梦夜花")
			.t("desc", "梦夜花含有强力中和成分。它会净化英雄、令其他生物陷入魔法睡眠，并结出一颗鲜莓。")
			.t("warden_desc", "_守望者_同样会被完全净化，而不会因此沉睡。")
			.t("$seed.name", "梦夜花之种")
			.t("$exdreamfoil.name", "梦夜花果丛")
			.t("$exdreamfoil.desc", "生长魅惑果的果丛。");
	}



	{ image = 10; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(new DreamLeaf(), pos).sprite.drop();
		if (ch instanceof Hero) PotionOfHealing.cure(ch);
		else if (ch != null) Buff.affect(ch, MagicalSleep.class);
	}
	public static class Seed extends Plant.Seed {
		{ image = SpecificPlaceHolderDict.SOMETHING_0; plantClass = Dreamfoil.class; explantClass = ExDreamfoil.class; }
	}
	public static class ExDreamfoil extends SpsFruitBush {
		{ image = 10; harvestCount = 3; harvestClass = CharmFruit.class; }
	}
}
