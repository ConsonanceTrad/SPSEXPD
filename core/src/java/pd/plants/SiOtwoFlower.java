package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.items.consum.food.vegetable.NutVegetable;
import pd.items.equipment.weapon.missiles.arrows.GlassFruit;
import pd.messages.InlineText;

public class SiOtwoFlower extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(SiOtwoFlower.class)
			.t("name", "石英花")
			.t("desc", "富含玻璃成分的沙漠花朵，会为接触者提供护盾，并留下一颗可食用坚果。")
			.t("warden_desc", "_守望者_会获得石英花完整的玻璃护盾。")
			.t("$seed.name", "石英花之种")
			.t("$exsiotwoflower.name", "石英花果丛")
			.t("$exsiotwoflower.desc", "生长水晶果的果丛。");
	}



	{ image = 18; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		if (ch != null) Buff.affect(ch, Barrier.class).setShield(Math.max(4, ch.HT / 3));
		Dungeon.level.drop(new NutVegetable(), pos).sprite.drop();
		Dungeon.level.drop(new GlassFruit(), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = SpecificPlaceHolderDict.SOMETHING_0; plantClass = SiOtwoFlower.class; explantClass = ExSiOtwoFlower.class; }
	}
	public static class ExSiOtwoFlower extends SpsFruitBush {
		{ image = 18; harvestCount = 2; harvestClass = GlassFruit.class; }
	}
}
