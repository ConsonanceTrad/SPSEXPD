/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.UpgradeBlobRed;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.PetFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Vegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FoxHelperSprite;
import com.watabou.utils.Random;

public class FoxHelper extends PET {
	{
		spriteClass = FoxHelperSprite.class; cooldown = 50; properties.add(Property.ORC); updateStats(true);
	}
	@Override protected Kind kind() { return Kind.FOX_HELPER; }
	@Override public boolean lovefood(Item item) { return item instanceof PetFood || item instanceof Plant.Seed || item instanceof Vegetable || item instanceof Fruit; }
	@Override public Item SupercreateLoot() { return new UpgradeBlobRed(); }
	@Override public void updateStats(boolean refill) {
		int old = HT; HT = 150 + petLevel() * 2; defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(5 + petLevel() / 2, 5 + petLevel() * 3 / 2); }
	@Override public int drRoll() { return Random.IntRange(petLevel(), Math.max(petLevel(), petLevel() * 3)); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }
	@Override protected boolean act() { supportHero(); return super.act(); }
	void supportHero() {
		if (Dungeon.hero != null && Dungeon.level != null && Dungeon.level.adjacent(pos, Dungeon.hero.pos) && cooldown <= 0) {
			if (sprite != null) sprite.emitter().start(com.shatteredpixel.shatteredpixeldungeon.effects.Speck.factory(
					com.shatteredpixel.shatteredpixeldungeon.effects.Speck.UP), 0.4f, 1);
			Heap heap = Dungeon.level.drop(supportReward(), pos);
			if (heap.sprite != null) heap.sprite.drop();
			cooldown = Math.max(45, 65 - petLevel());
		}
	}
	protected Item supportReward() { return new ScrollOfUpgrade(); }
	@Override public int attackProc(Char enemy, int damage) { cooldown--; return super.attackProc(enemy, damage); }
	@Override public int defenseProc(Char enemy, int damage) { cooldown--; return super.defenseProc(enemy, damage); }
}
