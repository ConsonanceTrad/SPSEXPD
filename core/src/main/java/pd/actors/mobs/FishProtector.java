/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Frost;
import pd.actors.buffs.FrostIce;
import pd.actors.buffs.Tar;
import pd.actors.damagetype.DamageType;
import pd.effects.Speck;
import pd.items.VioletDewdrop;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.sprites.FishProtectorSprite;
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
