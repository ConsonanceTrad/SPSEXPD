/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ForeverShadow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class NeedPaper extends Item {

	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_HELP = "HELP";
	public static final String AC_SHOP = "SHOP";
	public static final int HELP_COST = 500;
	public static final int SHOP_COST = 3000;

	{ image = ItemSpriteSheet.SPS_NEED_PAPER; unique = true; defaultAction = AC_CHOOSE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_HELP);
		actions.add(AC_SHOP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) GameScene.show(new WndUseItem(null, this));
		else if (AC_HELP.equals(action)) { if (!hide(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else if (AC_SHOP.equals(action)) { if (!shop(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else super.execute(hero, action);
	}

	public boolean hide(Hero hero) {
		if (hero == null || hero.spp < HELP_COST) return false;
		hero.spp -= HELP_COST;
		hero.HP = hero.HT;
		Buff.prolong(hero, ForeverShadow.class, 15f);
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, AttackDown.class);
		Buff.detach(hero, ArmorBreak.class);
		hero.spendAndNext(1f);
		return true;
	}

	public boolean shop(Hero hero) {
		if (hero == null || Dungeon.level == null || hero.spp < SHOP_COST) return false;
		Item item = Generator.random(Random.oneOf(Generator.Category.WAND, Generator.Category.RING,
				Generator.Category.ARTIFACT, Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
		if (item == null) return false;
		hero.spp -= SHOP_COST;
		Heap heap = Dungeon.level.drop(item, hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
		hero.spendAndNext(1f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
