/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostIce;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.VioletDewdrop;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FishProtectorSprite;
import com.watabou.utils.Random;

public class FishProtector extends Mob {

	{
		spriteClass = FishProtectorSprite.class;
		HP = HT = 300;
		defenseSkill = 25;
		EXP = 1;
		state = HUNTING;
		loot = VioletDewdrop.class;
		lootChance = 1f;
		properties.add(Property.ICY);
		properties.add(Property.ELEMENT);
		immunities.add(DamageType.Ice.class);
		immunities.add(Frost.class);
		immunities.add(Chill.class);
	}

	@Override public int damageRoll() {
		return Random.NormalIntRange(8 + Statistics.albinoPiranhasKilled / 10,
				10 + Statistics.albinoPiranhasKilled / 5);
	}

	@Override public int attackSkill(Char target) { return 25; }
	@Override public int drRoll() { return Random.NormalIntRange(5, 15); }

	@Override public int attackProc(Char enemy, int damage) {
		enemy.damage(damageRoll(), DamageType.ICE_DAMAGE);
		return 0;
	}

	@Override public void die(Object cause) {
		super.die(cause);
		Level.set(pos, Terrain.WATER);
		GameScene.updateMap(pos);
	}

	@Override public boolean add(Buff buff) {
		if (buff instanceof FrostIce) {
			if (HP < HT && isAlive()) {
				HP = Math.min(HT, HP + HT / 10);
				if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
			return false;
		}
		if (buff instanceof Tar) {
			damage(Random.NormalIntRange(Dungeon.level.water[pos] ? 1 : HT / 2,
					Dungeon.level.water[pos] ? HT * 2 / 3 : HT), buff);
			return false;
		}
		return super.add(buff);
	}
}
