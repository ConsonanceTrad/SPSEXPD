/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

public class SacrificeBook extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SacrificeBook.class)
			.t("name", "献祭之书")
			.t("desc", "附带一种宇宙香料的书籍。配合这种香料进行仪式能极大提升使用者的能力，但过多使用会带来不可预知的副作用。")
			.t("ac_use", "使用")
			.t("use_1", "+5生命上限")
			.t("use_2", "+1力量")
			.t("use_lot", "你感觉不太舒服。");
	}



	public static final String AC_USE = "USE";
	{
		image = EquipmentNonEquipDict.DEMON_BOOK;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (!AC_USE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (Dungeon.sacrifice == 0) {
			hero.HTBoost += 5;
			hero.updateHT(true);
			if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "use_1"));
		} else {
			hero.STR++;
			if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "use_2"));
		}
		if (Dungeon.sacrifice > 1) {
			int minimum = 5 * Dungeon.sacrifice;
			int maximum = hero.permanentHT() / 5;
			int loss = maximum > minimum ? Random.Int(minimum, maximum) : minimum;
			hero.spendPermanentHT(Math.min(loss, hero.permanentHT() - 1));
			GLog.w(Messages.get(this, "use_lot"));
		}
		Dungeon.sacrifice++;
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
		hero.busy();
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
