/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.faithbuff.BalanceFaith;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.faithbuff.DemonFaith;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.faithbuff.FaithBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.faithbuff.HumanFaith;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.faithbuff.LifeFaith;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.faithbuff.MechFaith;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;

import java.util.ArrayList;

public class FaithSign extends Item {

	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_DEMON = "DEMON";
	public static final String AC_HUMAN = "HUMAN";
	public static final String AC_MECH = "MECH";
	public static final String AC_LIFE = "LIFE";
	public static final String AC_BALANCE = "BALANCE";

	{
		image = ItemSpriteSheet.LEGACY_FAITH_SIGN;
		unique = true;
		defaultAction = AC_CHOOSE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_DEMON);
		actions.add(AC_HUMAN);
		actions.add(AC_MECH);
		actions.add(AC_LIFE);
		actions.add(AC_BALANCE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) {
			GameScene.show(new WndUseItem(null, this));
			return;
		}
		Class<? extends FaithBuff> faith = null;
		if (AC_DEMON.equals(action)) faith = DemonFaith.class;
		else if (AC_HUMAN.equals(action)) faith = HumanFaith.class;
		else if (AC_MECH.equals(action)) faith = MechFaith.class;
		else if (AC_LIFE.equals(action)) faith = LifeFaith.class;
		else if (AC_BALANCE.equals(action)) faith = BalanceFaith.class;
		if (faith == null) {
			super.execute(hero, action);
			return;
		}
		Buff.detach(hero, DemonFaith.class);
		Buff.detach(hero, HumanFaith.class);
		Buff.detach(hero, MechFaith.class);
		Buff.detach(hero, LifeFaith.class);
		Buff.detach(hero, BalanceFaith.class);
		Buff.affect(hero, faith);
		hero.spendAndNext(1f);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
