/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
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

public class NmHealBag extends Item {

	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_HEAL = "HEAL";
	public static final String AC_COOK = "COOK";
	public static final String AC_ADD = "ADD";
	public static final int COOK_COST = 10;

	{ image = ItemSpriteSheet.SPS_NM_HEAL_BAG; unique = true; defaultAction = AC_CHOOSE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_HEAL);
		actions.add(AC_COOK);
		actions.add(AC_ADD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) GameScene.show(new WndUseItem(null, this));
		else if (AC_HEAL.equals(action)) heal(hero);
		else if (AC_COOK.equals(action)) { if (!rebuild(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else if (AC_ADD.equals(action)) improve(hero);
		else super.execute(hero, action);
	}

	public boolean heal(Hero hero) {
		if (hero == null || hero.spp <= 0) return false;
		hero.HP = Math.min(hero.HT, hero.HP + hero.spp);
		hero.spp = 0;
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, AttackDown.class);
		Buff.detach(hero, ArmorBreak.class);
		hero.spendAndNext(1f);
		return true;
	}

	public boolean rebuild(Hero hero) {
		if (hero == null || Dungeon.level == null || hero.spp < COOK_COST) return false;
		Item result = Generator.random(Random.Int(4) == 0 ? Generator.Category.HIGHFOOD : Generator.Category.SUMMONED);
		if (result == null) return false;
		hero.spp -= COOK_COST;
		Heap heap = Dungeon.level.drop(result, hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
		hero.spendAndNext(1f);
		return true;
	}

	public boolean improve(Hero hero) {
		if (hero == null || hero.HP <= 10) return false;
		hero.spp += hero.HP / 4;
		hero.HP = 1;
		hero.spendAndNext(1f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
