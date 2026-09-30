/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Silent;
import pd.actors.damagetype.DamageType;
import pd.items.VioletDewdrop;
import pd.mechanics.Ballistica;
import pd.sprites.CharSprite;
import pd.sprites.ForestProtectorSprite;
import com.watabou.utils.Random;

public class ForestProtector extends Mob {

	private static final float TIME_TO_ZAP = 1f;

	{
		spriteClass = ForestProtectorSprite.class;
		HP = HT = 250;
		defenseSkill = 10;
		EXP = 1;
		state = HUNTING;
		loot = VioletDewdrop.class;
		lootChance = 1f;
		properties.add(Property.PLANT);
		resistances.add(DamageType.Earth.class);
	}

	@Override public int damageRoll() {
		return Random.NormalIntRange(5 + Statistics.gnollArchersKilled / 10,
				10 + Statistics.gnollArchersKilled / 5);
	}

	@Override public int attackSkill(Char target) { return 26; }
	@Override public int drRoll() { return Random.NormalIntRange(5, 10); }

	@Override public int attackProc(Char enemy, int damage) {
		enemy.damage(damageRoll() / 2, DamageType.EARTH_DAMAGE);
		return damage * 3;
	}

	@Override protected boolean canAttack(Char enemy) {
		if (buff(Silent.class) != null) return super.canAttack(enemy);
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override protected boolean doAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)) return super.doAttack(enemy);
		if (sprite != null && (sprite.visible || enemy.sprite != null && enemy.sprite.visible)) {
			sprite.zap(enemy.pos);
			return false;
		}
		zap();
		return true;
	}

	private void zap() {
		spend(TIME_TO_ZAP);
		if (enemy == null || !enemy.isAlive()) return;
		if (hit(this, enemy, true)) {
			int damage = Random.Int(5 + Statistics.gnollArchersKilled / 10,
					10 + Statistics.gnollArchersKilled / 5);
			enemy.damage(damage, DamageType.EARTH_DAMAGE);
		} else if (enemy.sprite != null) {
			enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}
}
