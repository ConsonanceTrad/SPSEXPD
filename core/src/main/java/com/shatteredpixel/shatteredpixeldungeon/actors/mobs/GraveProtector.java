/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.items.VioletDewdrop;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GraveProtectorSprite;
import com.watabou.utils.Random;

public class GraveProtector extends Mob {

	{
		spriteClass = GraveProtectorSprite.class;
		HP = HT = 350;
		defenseSkill = 15;
		EXP = 1;
		state = HUNTING;
		loot = VioletDewdrop.class;
		lootChance = 1f;
		properties.add(Property.TROLL);
	}

	@Override public int damageRoll() {
		return Random.NormalIntRange(8 + Statistics.mossySkeletonsKilled / 10,
				15 + Statistics.mossySkeletonsKilled / 5);
	}

	@Override public int attackSkill(Char target) { return 20; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 8); }

	@Override protected boolean canAttack(Char enemy) {
		if (buff(Locked.class) != null) return super.canAttack(enemy);
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (Dungeon.level.adjacent(pos, enemy.pos)) {
			return damage / 2;
		}
		Buff.prolong(enemy, Slow.class, 5f);
		Buff.affect(enemy, ArmorBreak.class, 5f).level(20);
		return damage;
	}

	@Override public float resist(Class effect) {
		float result = super.resist(effect);
		return Blindness.class.isAssignableFrom(effect) ? result * 1.5f : result;
	}
}
