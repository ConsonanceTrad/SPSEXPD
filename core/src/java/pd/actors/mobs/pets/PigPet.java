/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.food.SmallMeat;
import pd.sprites.PigPetSprite;
import render.utils.math.Random;

public class PigPet extends PET {
	{
		spriteClass = PigPetSprite.class;
		cooldown = 50;
		flying = false;
		properties.add(Property.BEAST);
		updateStats(true);
	}
	@Override protected Kind kind() { return Kind.PIG; }
	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 150 + petLevel() * 2;
		defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() {
		return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2);
	}
	@Override public int drRoll() { return Random.IntRange(petLevel(), Math.max(petLevel(), petLevel() * 3)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }
	@Override public int attackProc(Char enemy, int damage) {
		if (cooldown > 0) cooldown--;
		if (cooldown == 0 && Dungeon.level != null && enemy != null) {
			Dungeon.level.drop(Generator.random(Generator.Category.MUSHROOM), enemy.pos).sprite.drop();
			cooldown = Math.max(25, 45 - petLevel());
		}
		return super.attackProc(enemy, damage);
	}
	@Override public int defenseProc(Char enemy, int damage) {
		if (Random.Int(15) == 0 && Dungeon.level != null) Dungeon.level.drop(new SmallMeat(), pos).sprite.drop();
		return super.defenseProc(enemy, damage);
	}
}
