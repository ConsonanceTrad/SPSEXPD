/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.Generator;
import pd.sprites.BlueWraithSprite;
import com.watabou.utils.Random;

public class BlueWraith extends Wraith {

	{
		spriteClass = BlueWraithSprite.class;
		HP = HT = 250;
		defenseSkill = 24;
		baseSpeed = 2f;
		EXP = 20;
		maxLvl = 100;
		setupLegacyDualLoot(Generator.random(Generator.Category.SEED), 1f, null, 0f);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(20, 90); }
	@Override public int attackSkill(Char target) { return 46; }
	@Override public int drRoll() { return Random.NormalIntRange(10, 25); }
	@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 2; }

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(10) == 0) {
			Buff.affect(enemy, Vertigo.class, 5f);
			Buff.affect(enemy, Terror.class, Terror.DURATION).object = id();
		}
		return damage;
	}

	@Override
	public void adjustStats(int level) {
		this.level = level;
		defenseSkill = 24;
		enemySeen = true;
	}
}
