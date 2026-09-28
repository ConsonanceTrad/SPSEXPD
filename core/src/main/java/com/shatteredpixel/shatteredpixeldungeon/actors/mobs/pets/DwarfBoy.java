/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.CompleteFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.PetFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.NormalRation;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DwarfBoySprite;
import com.watabou.utils.Random;

public class DwarfBoy extends PET {
	{
		spriteClass = DwarfBoySprite.class;
		cooldown = 50;
		properties.add(Property.DWARF);
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.DWARF_BOY; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof CompleteFood; }
	@Override public Item SupercreateLoot() { return new NormalRation(); }

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
		if (Random.Int(5) == 0) Buff.affect(enemy, Vertigo.class, 6f);
		return super.attackProc(enemy, damage);
	}
	@Override public int defenseProc(Char enemy, int damage) {
		if (enemy != null) enemy.damage(petLevel(), this);
		cooldown--;
		if (cooldown <= 0) {
			if (enemy != null) enemy.damage(petLevel() * 2, this);
			cooldown = Math.max(10, 30 - petLevel());
		}
		return super.defenseProc(enemy, damage);
	}
}
