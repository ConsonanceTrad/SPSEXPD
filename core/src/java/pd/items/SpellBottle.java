/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.CharSprite;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

/**
 * SPSEXPD: 法术之瓶 —— 永久法强的唯一道具来源。
 *
 * 与力量之瓶（{@code StrBottle}）同源设计：饮用 +1 永久法强并回满生命。
 * 获取走力量之瓶的限量掉落通道（每层与力量之瓶随机二选一），并可经炼金釜
 * 用 2 瓶炼金能量与力量之瓶互换。
 */
public class SpellBottle extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SpellBottle.class)
			.t("name", "法术之瓶")
			.t("ac_use", "使用")
			.t("msg_1", "+1法强")
			.t("skillup", "你的法术强度提升了。")
			.t("desc", "饮用后永久获得一点法术强度，并完全恢复生命。\n\n法术强度会增幅所有法杖的伤害。");
	}



	public static final String AC_USE = "USE";

	{
		//SPSEXPD: 暂用编号图集里紫色斜放试管作占位图标，后续再画专属贴图
		image = ConsumPotionSeedBasicPotionDict.BASIC_POTION_EXTRA_3;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			use(hero);
		} else {
			super.execute(hero, action);
		}
	}

	public boolean use(Hero hero) {
		if (hero == null || hero.belongings == null || !hero.belongings.contains(this)) return false;
		hero.improveMagicSkill(1);
		hero.HP = hero.HT;
		if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "msg_1"));
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	//SPSEXPD: 与力量之瓶一致，法术之瓶不会被爆炸炸毁
	@Override public boolean survivesExplosion() { return true; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
