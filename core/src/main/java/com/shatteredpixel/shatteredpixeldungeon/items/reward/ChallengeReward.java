/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.reward;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

abstract class ChallengeReward extends Item {
	static final String AC_USE = "USE";
	private final int glow;

	ChallengeReward(int glow) {
		this.glow = glow;
		image = ItemSpriteSheet.CHALLENGE_REWARD_BAG;
		stackable = false;
		defaultAction = AC_USE;
	}

	protected abstract Item[] contents();

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
		hero.spend(1f);
		hero.busy();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		for (Item item : contents()) {
			Heap heap = Dungeon.level.drop(item, hero.pos);
			if (heap.sprite != null) heap.sprite.drop(hero.pos);
		}
		detach(hero.belongings.backpack);
	}

	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(glow); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 10 * quantity; }
}
