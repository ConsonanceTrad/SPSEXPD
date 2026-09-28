/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BeCorrupt;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MindArrow extends MissileWeapon {

	{
		image = ItemSpriteSheet.SPS_MIND_ARROW;
		tier = 1;
		baseUses = 1;
		DLY = 0.1f;
		levelKnown = true;
		bones = false;
	}

	public MindArrow() { this(1); }
	public MindArrow(int number) { quantity(number); }
	@Override public int min(int lvl) { return 0; }
	@Override public int max(int lvl) { return 10; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public Item random() { return quantity(2); }
	@Override public int value() { return 0; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		int points = Dungeon.hero == null ? 0 : Math.max(0, Dungeon.hero.spp);
		if (points > 0) defender.damage(points, this);
		Buff.affect(defender, BeCorrupt.class).level(points);
		return result;
	}
}
