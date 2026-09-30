/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicWeak;
import pd.items.Item;
import pd.items.food.completefood.PetFood;
import pd.items.food.meatfood.MeatFood;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.wands.WandOfMagicMissile;
import pd.sprites.KodoraSprite;
import render.utils.Random;

public class Kodora extends PET {
	{
		spriteClass = KodoraSprite.class; cooldown = 50; properties.add(Property.DRAGON); updateStats(true);
	}
	@Override protected Kind kind() { return Kind.KODORA; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof MeatFood; }
	@Override public Item SupercreateLoot() { return new PotionOfLiquidFlame(); }
	@Override public void updateStats(boolean refill) {
		int old = HT; HT = 150 + petLevel() * 2; defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel(), 5 + petLevel() * 2); }
	@Override public int drRoll() { return Random.IntRange(0, petLevel() * 2); }
	@Override public int attackSkill(Char target) { return petLevel() + 10; }
	@Override public int attackProc(Char enemy, int damage) {
		if (enemy == null) return 0;
		if (cooldown <= 0 && enemy.isAlive()) { Buff.affect(enemy, MagicWeak.class, petLevel() * 2f); cooldown = Math.max(5, 25 - petLevel()); }
		if (cooldown > 0) cooldown--;
		enemy.damage(damageRoll(), WandOfMagicMissile.class);
		return super.attackProc(enemy, 0);
	}
}
