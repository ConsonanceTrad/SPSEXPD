/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.CharSprite;

import java.util.ArrayList;
import pd.messages.InlineText;

public class StrBottle extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StrBottle.class)
			.t("name", "力量之瓶")
			.t("ac_use", "使用")
			.t("msg_1", "+1力量")
			.t("desc", "饮用后永久获得一点力量，并完全恢复生命。");
	}

	public static final String AC_USE = "USE";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
		hero.STR++;
		hero.HP = hero.HT;
		if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "msg_1"));
		Badges.validateStrengthAttained();
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
