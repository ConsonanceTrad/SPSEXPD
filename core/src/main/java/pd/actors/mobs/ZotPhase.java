/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.Char;
import pd.actors.blobs.Electricity;
import pd.actors.buffs.Silent;
import pd.items.Generator;
import pd.mechanics.Ballistica;
import pd.sprites.ZotPhaseSprite;
import render.utils.Random;

/** Ranged phase split off from Zot. */
public class ZotPhase extends Mob {
	{
		spriteClass = ZotPhaseSprite.class;
		HP = HT = 200;
		defenseSkill = 40;
		baseSpeed = 1f;
		EXP = 30;
		loot = Generator.Category.SCROLL;
		lootChance = 0.33f;
		properties.add(Property.UNKNOW);
		properties.add(Property.BOSS_MINION);
		resistances.add(Electricity.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(115, 160 + Zot.LEGACY_DEPTH / 2); }
	@Override public int attackSkill(Char target) { return 50 + Zot.LEGACY_DEPTH; }
	@Override public int drRoll() { return 0; }
	@Override public float attackDelay() { return 2f; }

	@Override
	protected boolean canAttack(Char enemy) {
		if (buff(Silent.class) != null) return super.canAttack(enemy);
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
}
