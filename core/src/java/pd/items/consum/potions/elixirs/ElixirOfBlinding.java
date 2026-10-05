/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.elixirs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

/**
 * SPSEXPD: 占位秘药（致盲果对应）——效果待设计，目前饮用没有任何作用。
 */
public class ElixirOfBlinding extends Elixir {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ElixirOfBlinding.class)
			.t("name", "致盲秘药")
			.t("desc", "由致盲果炼制的秘药。它的效果尚未定型，目前饮用没有任何作用。");
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
