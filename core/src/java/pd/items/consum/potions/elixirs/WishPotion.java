/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.elixirs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.items.consum.potions.wish.WishEngine;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.windows.WndTextInput;

/**
 * SPSEXPD: 许愿魔药——使用后弹出愿望输入窗，许下一个愿望。
 *
 * 许愿按名称做词匹配，再按幸运值与描述准确度决定奖励：
 * 幸运值决定能拿到的奖励等级，描述准确度决定能否命中想要的物品。
 */
public class WishPotion extends Elixir {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WishPotion.class)
			.t("name", "许愿魔药")
			.t("desc", "瓶中晃动着被搅碎的彩虹色梦境。饮下它，你可以用一个名字许下愿望——你想要得越准确、你的运气越好，它就越可能成真，强度也越高。")
			.t("wish_title", "许愿")
			.t("wish_body", "写下你的愿望：物品名、类型（武器、神器、秘药……）、怪物，甚至死亡。\n幸运越高，愿望能换到的东西越好；描述越准确越容易命中；特质必须写得分毫不差。")
			.t("wish_confirm", "许愿")
			.t("wish_cancel", "放弃")
			.t("result_kind", "你的愿望化作了一件：%s")
			.t("result_mob", "你召唤出了：%s")
			.t("result_mob_failed", "你身旁没有能让它现身的位置。")
			.t("result_trait", "你获得了特质：%s")
			.t("result_trait_unavailable", "你的愿望指向了「%s」，但它此刻无法获得。");
	}

	/** 愿望最长字符数。 */
	public static final int MAX_WISH_LENGTH = 40;

	{
		//SPSEXPD: 贴图待指认，暂用秘药占位图
		image = SpecificPlaceHolderDict.ELIXIR_HOLDER_0;
		talentFactor = 2f;
	}

	@Override
	public void apply(Hero hero) {
		identify();
		promptWish(hero);
	}

	/** UI 层入口：弹出文本输入窗许愿。 */
	public static void promptWish(final Hero hero) {
		if (hero == null) return;
		GameScene.show(new WndTextInput(
				Messages.get(WishPotion.class, "wish_title"),
				Messages.get(WishPotion.class, "wish_body"),
				"",
				MAX_WISH_LENGTH,
				false,
				Messages.get(WishPotion.class, "wish_confirm"),
				Messages.get(WishPotion.class, "wish_cancel")) {
			@Override
			public void onSelect(boolean positive, String text) {
				if (positive) WishEngine.wish(hero, text);
			}
		});
	}
}
