/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.RandomEasterEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.PetFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Vegetable;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChocoboSprite;
import com.watabou.utils.Random;

public class Chocobo extends PET {
	{
		spriteClass = ChocoboSprite.class;
		cooldown = 50;
		properties.add(Property.BEAST);
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.CHOCOBO; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof Vegetable; }
	@Override public Item SupercreateLoot() { return new RandomEasterEgg(); }

	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 150 + petLevel() * 2;
		defenseSkill = petLevel() * 3 / 2;
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2); }
	@Override public int drRoll() { return Random.IntRange(petLevel() * 2, Math.max(petLevel() * 2, petLevel() * 5)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }

	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0) Buff.affect(this, HasteBuff.class, 5f);
		cooldown--;
		return super.attackProc(enemy, damage);
	}

	@Override public int defenseProc(Char enemy, int damage) {
		if (cooldown <= 0 && Dungeon.hero != null) {
			Buff.affect(Dungeon.hero, HasteBuff.class, petLevel());
			cooldown = Math.max(10, 30 - petLevel());
		}
		return super.defenseProc(enemy, damage);
	}
}
