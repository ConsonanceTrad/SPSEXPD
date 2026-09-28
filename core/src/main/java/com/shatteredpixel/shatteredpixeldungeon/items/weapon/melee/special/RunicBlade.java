/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Boomerang;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD's consumable weapon-refining runic blade. */
public class RunicBlade extends MeleeWeapon {

	public static final String AC_REFORGE = "REFORGE";

	{
		image = ItemSpriteSheet.SPS_RUNIC_BLADE;
		tier = 5;
		ACC = 1f;
		DLY = 1f;
		RCH = 1;
	}

	@Override public int min(int lvl) { return 0; }
	@Override public int max(int lvl) { return 35 + 13 * Math.max(0, lvl); }
	@Override public int STRReq(int lvl) { return Math.max(1, 18 - Math.max(0, lvl)); }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (level() > 0) actions.add(AC_REFORGE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_REFORGE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		curUser = hero;
		GameScene.selectItem(itemSelector);
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(RunicBlade.class, "choose"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return item instanceof Weapon && item != RunicBlade.this && !(item instanceof Boomerang);
		}
		@Override public void onSelect(Item item) {
			if (item == null || curUser == null) return;
			consume(curUser);
			reforge(item);
			Item.evoke(curUser);
			curUser.spendAndNext(2 * Actor.TICK);
			GLog.w(Messages.get(RunicBlade.class, "reforged"));
			Badges.validateItemLevelAquired(item);
		}
	};

	private void consume(Hero hero) {
		if (hero.belongings.weapon == this) hero.belongings.weapon = null;
		else if (hero.belongings.secondWep == this) hero.belongings.secondWep = null;
		else detach(hero.belongings.backpack);
		Item.updateQuickslot();
	}

	public int reforge(Item item) {
		if (item == null || item instanceof Boomerang) return 0;
		int upgrades = 0;
		float chance = 0.9f;
		for (int i = 0; i < level(); i++) {
			if (i < 2 || Random.Float() < chance) {
				item.upgrade();
				upgrades++;
				if (i >= 2) chance = Math.max(0.5f, chance - 0.1f);
			}
		}
		return upgrades;
	}
}
