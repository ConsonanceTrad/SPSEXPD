/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.items.Item;
import pd.items.food.completefood.PetFood;
import pd.items.food.vegetable.Vegetable;
import pd.items.potions.PotionOfShield;
import pd.sprites.GentleCrabSprite;
import render.utils.math.Random;

public class GentleCrab extends PET {
	{
		spriteClass = GentleCrabSprite.class; cooldown = 50; baseSpeed = 1.5f; properties.add(Property.FISHER); updateStats(true);
	}
	@Override protected Kind kind() { return Kind.GENTLE_CRAB; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof Vegetable; }
	@Override public Item SupercreateLoot() { return new PotionOfShield(); }
	@Override public void updateStats(boolean refill) {
		int old = HT; HT = 150 + petLevel() * 2; defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel(), 5 + petLevel() * 2); }
	@Override public int drRoll() { return Random.IntRange(0, petLevel() * 2); }
	@Override public int attackSkill(Char target) { return petLevel() + 10; }
	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(10) == 0) damage = damage * 3 / 2;
		if (cooldown <= 0 && enemy != null && enemy.isAlive()) {
			Buff.prolong(enemy, ArmorBreak.class, 5f).level(10 + petLevel()); cooldown = Math.max(5, 25 - petLevel());
		}
		if (cooldown > 0) cooldown--;
		return super.attackProc(enemy, damage);
	}
}
