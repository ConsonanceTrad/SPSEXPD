/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Mushroom;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BunnySprite;
import com.watabou.utils.Random;

public class Bunny extends PET {
	{
		spriteClass = BunnySprite.class;
		properties.add(Property.BEAST);
		cooldown = 40;
		flying = false;
		updateStats(true);
	}
	@Override protected Kind kind() { return Kind.BUNNY; }
	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 150 + petLevel() * 3;
		defenseSkill = 8 + petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public void move(int step, boolean travelling) {
		super.move(step, travelling);
		int terrain = Dungeon.level.map[step];
		if (cooldown > 0 && (terrain == Terrain.HIGH_GRASS || terrain == Terrain.OLD_HIGH_GRASS
				|| terrain == Terrain.GRASS)) cooldown--;
	}
	@Override public int drRoll() { return Random.IntRange(1, Math.max(1, petLevel() * 2)); }
	@Override public int attackSkill(Char target) { return petLevel() + 8; }
	@Override public int damageRoll() {
		int a = (5 + petLevel()) * 5;
		int b = (5 + petLevel() * 3) * 4;
		return Random.NormalIntRange(Math.min(a, b), Math.max(a, b));
	}
	@Override public int attackProc(Char enemy, int damage) {
		if (cooldown == 0) {
			int kind = Random.Int(3);
			Item reward = kind == 0 ? Generator.random(Generator.Category.SEED)
					: kind == 1 ? Generator.random(Generator.Category.BERRY) : new Mushroom();
			Dungeon.level.drop(reward, pos).sprite.drop();
			cooldown = Math.max(4, 40 - petLevel());
		}
		return damage;
	}
}
