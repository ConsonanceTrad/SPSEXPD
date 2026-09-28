/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BoxStar;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Mushroom;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MKbox extends Item {
	public static final String AC_USE = "USE";
	{ image=ItemSpriteSheet.LEGACY_MK_BOX; defaultAction=AC_USE; unique=true; }
	@Override public ArrayList<String> actions(Hero hero) { ArrayList<String>a=super.actions(hero); a.add(AC_USE); return a; }
	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) { if (!use(hero)) GLog.i(Messages.get(this, "need_gold")); }
		else super.execute(hero, action);
	}
	public boolean use(Hero hero) {
		if (hero == null || Dungeon.level == null || Dungeon.gold < 100) return false;
		Dungeon.gold -= 100; hero.spendAndNext(1f);
		if (Random.Int(50) == 0) Buff.affect(hero, BoxStar.class, BoxStar.DURATION);
		else if (Random.Int(49) == 0) Dungeon.gold += 500;
		else if (Random.Int(48) < 3) Dungeon.level.drop(new Ankh(), hero.pos).sprite.drop(hero.pos);
		else if (Random.Int(45) < 20) Dungeon.level.drop(new WarHammer(), hero.pos).sprite.drop(hero.pos);
		else if (Random.Int(25) < 20) Dungeon.level.drop(new Mushroom(), hero.pos).sprite.drop(hero.pos);
		else if (Random.Int(2) == 1) Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		else Buff.affect(hero, FrostImbue.class, FrostImbue.DURATION);
		return true;
	}
	@Override public boolean isUpgradable(){return false;}
	@Override public boolean isIdentified(){return true;}
	@Override public int value(){return 30*quantity;}
}
