/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;

import java.util.ArrayList;
import pd.messages.InlineText;

public class MitBottle extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MitBottle.class)
			.t("name", "根骨之瓶")
			.t("ac_use", "使用")
			.t("msg_1", "+1力量")
			.t("msg_2", "+10生命上限")
			.t("desc", "使用后永久获得一点力量和十点生命上限。");
	}

	public static final String AC_USE = "USE";
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (!AC_USE.equals(action)) { super.execute(hero, action); return; }
		use(hero);
	}
	public boolean use(Hero hero) {
		if (hero == null || hero.belongings == null || !hero.belongings.contains(this)) return false;
		hero.STR++;
		hero.HTBoost += 10;
		hero.updateHT(true);
		hero.HP = hero.HT;
		if (hero.sprite != null) {
			hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "msg_1"));
			hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "msg_2"));
		}
		Badges.validateStrengthAttained();
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
		return true;
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
