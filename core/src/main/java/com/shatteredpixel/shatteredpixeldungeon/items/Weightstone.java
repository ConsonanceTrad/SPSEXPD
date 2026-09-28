/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Weightstone extends Item {

	public static final String AC_APPLY = "APPLY";
	public static final float TIME_TO_APPLY = 2f;

	{
		image = ItemSpriteSheet.SPS_WEIGHTSTONE;
		stackable = true;
		defaultAction = AC_APPLY;
	}

	@Override public ArrayList<String> actions(Hero hero) { ArrayList<String> actions = super.actions(hero); actions.add(AC_APPLY); return actions; }
	@Override public void execute(Hero hero, String action) {
		if (AC_APPLY.equals(action)) { curUser = hero; GameScene.selectItem(itemSelector); }
		else super.execute(hero, action);
	}

	public boolean apply(Hero hero, Weapon weapon) {
		if (hero == null || weapon == null || !hero.belongings.contains(weapon)) return false;
		Item used = detach(hero.belongings.backpack);
		if (used == null) return false;
		weapon.enchant();
		ScrollOfRemoveCurse.uncurse(hero, weapon);
		weapon.identify();
		GLog.w(Messages.get(this, "apply"));
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			hero.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
			Enchanting.show(hero, weapon);
		}
		Sample.INSTANCE.play(Assets.Sounds.MISS);
		hero.spendAndNext(TIME_TO_APPLY);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 40 * quantity; }

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(Weightstone.class, "select"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item instanceof Weapon; }
		@Override public void onSelect(Item item) { if (item instanceof Weapon) Weightstone.this.apply(curUser, (Weapon)item); }
	};
}
