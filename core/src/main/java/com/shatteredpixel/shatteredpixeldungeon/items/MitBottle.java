/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class MitBottle extends Item {
	public static final String AC_USE = "USE";
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	{
		image = ItemSpriteSheet.SPS_MIT_BOTTLE;
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
