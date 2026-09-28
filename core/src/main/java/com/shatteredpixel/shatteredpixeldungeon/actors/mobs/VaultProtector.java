/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.items.VioletDewdrop;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VaultProtectorSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class VaultProtector extends Mob {

	private boolean skillUsed;

	{
		spriteClass = VaultProtectorSprite.class;
		HP = HT = 400;
		defenseSkill = 10;
		EXP = 1;
		state = HUNTING;
		loot = VioletDewdrop.class;
		lootChance = 1f;
		properties.add(Property.HUMAN);
	}

	@Override public int damageRoll() {
		return Random.NormalIntRange(8 + Statistics.goldThievesKilled / 10,
				10 + Statistics.goldThievesKilled / 5);
	}

	@Override public int attackSkill(Char target) { return 40; }
	@Override public int drRoll() { return Random.NormalIntRange(5, 20); }

	@Override protected boolean canAttack(Char enemy) {
		if (buff(Silent.class) != null || !skillUsed) return super.canAttack(enemy);
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (!skillUsed) {
			skillUsed = true;
			Dungeon.gold++;
			return damage;
		}
		enemy.damage(damageRoll(), DamageType.ENERGY_DAMAGE);
		Dungeon.gold = Math.max(0, Dungeon.gold - Math.max(1, Dungeon.gold / 100));
		return 0;
	}

	private static final String SKILL_USED = "skill_used";

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SKILL_USED, skillUsed);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		skillUsed = bundle.getBoolean(SKILL_USED);
	}
}
