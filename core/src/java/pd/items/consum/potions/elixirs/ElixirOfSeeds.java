/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.elixirs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

/**
 * SPSEXPD: 占位秘药（种子荚果对应）——效果待设计，目前饮用没有任何作用。
 */
public class ElixirOfSeeds extends Elixir {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ElixirOfSeeds.class)
			.t("name", "种子秘药")
			.t("desc", "由种子荚果炼制的秘药。它的效果尚未定型，目前饮用没有任何作用。");
	}

	{
		//SPSEXPD: 贴图待指认，暂用秘药占位图
		image = SpecificPlaceHolderDict.ELIXIR_HOLDER_0;
		unique = true;
		talentFactor = 2f;
	}

	@Override public void apply(Hero hero) {
		identify();
		//SPSEXPD: 占位秘药，效果待补
	}
}
