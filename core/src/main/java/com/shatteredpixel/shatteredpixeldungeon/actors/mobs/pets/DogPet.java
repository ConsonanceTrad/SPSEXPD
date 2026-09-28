/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.MoonCake;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.PetFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood.MeatFood;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DogPetSprite;
import com.watabou.utils.Random;

public class DogPet extends PET {
	{
		spriteClass = DogPetSprite.class;
		cooldown = 50;
		properties.add(Property.BEAST);
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.DOG; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof MeatFood; }
	@Override public Item SupercreateLoot() { return new MoonCake(); }

	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 150 + petLevel() * 2;
		defenseSkill = petLevel() * 3 / 2;
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2); }
	@Override public int drRoll() { return Random.IntRange(petLevel() * 2, Math.max(petLevel() * 2, petLevel() * 5)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }

	@Override protected boolean act() {
		supportHero();
		return super.act();
	}

	void supportHero() {
		if (Dungeon.hero != null && Dungeon.level != null && Dungeon.level.adjacent(pos, Dungeon.hero.pos)
				&& cooldown <= 0) {
			Buff.affect(Dungeon.hero, ShieldArmor.class).level(petLevel() * 2);
			Buff.affect(this, ShieldArmor.class).level(petLevel() * 2);
			cooldown = Math.max(20, 40 - petLevel());
		}
	}
	@Override public int attackProc(Char enemy, int damage) { cooldown--; return super.attackProc(enemy, damage); }
	@Override public int defenseProc(Char enemy, int damage) { cooldown--; return super.defenseProc(enemy, damage); }
}
