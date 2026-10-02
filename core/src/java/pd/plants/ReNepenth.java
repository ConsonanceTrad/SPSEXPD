package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.TransmutationBall;
import pd.messages.InlineText;

public class ReNepenth extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(ReNepenth.class)
			.t("name", "转换笼")
			.t("desc", "无人知道它从何而来，但果实中蕴含着与转换之井相同的魔法。")
			.t("warden_desc", "_守望者_可以安全收取其中的转换魔力。")
			.t("$seed.name", "转换笼之种")
			.t("$exrenepenth.name", "转换笼果丛")
			.t("$exrenepenth.desc", "生长转换球的果丛。");
	}



	{ image = 14; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(new TransmutationBall(), pos).sprite.drop();
		Dungeon.level.drop(Generator.random(Generator.Category.SPS_BERRY), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = SpecificPlaceHolderDict.SOMETHING_0; plantClass = ReNepenth.class; explantClass = ExReNepenth.class; }
	}
	public static class ExReNepenth extends SpsFruitBush {
		{ image = 14; harvestCount = 2; harvestClass = TransmutationBall.class; }
	}
}
