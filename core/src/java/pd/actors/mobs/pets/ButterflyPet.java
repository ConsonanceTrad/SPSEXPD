/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.items.Garbage;
import pd.items.Item;
import pd.items.food.completefood.PetFood;
import pd.items.food.fruit.Fruit;
import pd.sprites.ButterflyPetSprite;
import pd.sprites.CharSprite;
import render.utils.math.Random;

public class ButterflyPet extends PET {
	{
		spriteClass = ButterflyPetSprite.class;
		cooldown = 50;
		properties.add(Property.BEAST);
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.BUTTERFLY; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof Fruit; }
	@Override public Item SupercreateLoot() { return new Garbage(); }

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

	@Override protected boolean act() {
		supportHero();
		return super.act();
	}

	void supportHero() {
		if (Dungeon.hero != null && Dungeon.level != null && Dungeon.level.adjacent(pos, Dungeon.hero.pos)) {
			cooldown--;
			if (cooldown <= 0) {
				if (Dungeon.hero.sprite != null) {
					Dungeon.hero.sprite.emitter().start(pd.effects.Speck.factory(
							pd.effects.Speck.HEALING), 0.4f, 1);
					Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, "5");
				}
				Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP + 5);
				cooldown = Math.max(15, 35 - petLevel());
			}
		}
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (cooldown > 0) cooldown--;
		if (Random.Int(5) == 0) Buff.affect(enemy, Blindness.class, petLevel() * 2f);
		return super.attackProc(enemy, damage);
	}
}
