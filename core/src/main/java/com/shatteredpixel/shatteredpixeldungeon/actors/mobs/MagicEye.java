/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MagicEyeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

/** Zot's original eye minion. Its beam behavior comes from the modern evil eye. */
public class MagicEye extends Eye {
	{
		spriteClass = MagicEyeSprite.class;
		properties.add(Property.ELEMENT);
		HP = HT = 100 + Zot.LEGACY_DEPTH * Random.NormalIntRange(4, 7);
		defenseSkill = 40 + Zot.LEGACY_DEPTH / 2;
		EXP = 16;
		loot = Generator.Category.SEED;
		lootChance = 0.1f;
	}

	@Override public int damageRoll() { return 1; }
	@Override public int attackSkill(Char target) { return 30 + Zot.LEGACY_DEPTH; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 20); }
	@Override public float attackDelay() { return 2f; }
	@Override public Item SupercreateLoot() { return new StoneOre(); }
	@Override protected boolean dropsLegacyMysteryMeat() { return false; }

	@Override
	protected boolean canAttack(Char enemy) {
		beam = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
		return beam.subPath(1, beam.dist).contains(enemy.pos);
	}

	@Override
	public void deathGaze() {
		if (beam == null) return;
		beamCharged = false;
		for (int cell : beam.subPath(1, beam.dist)) {
			Char target = Actor.findChar(cell);
			if (target == null) continue;
			if (hit(this, target, true)) {
				target.damage(Random.NormalIntRange(20, 50), DamageType.LIGHT_DAMAGE);
				if (Dungeon.level.heroFOV[cell] && target.sprite != null) {
					target.sprite.flash();
					CellEmitter.center(cell).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
				}
				if (!target.isAlive() && target == Dungeon.hero) {
					Badges.validateDeathFromEnemyMagic();
					Dungeon.fail(this);
					GLog.n(Messages.get(Eye.class, "deathgaze_kill"));
				}
			} else if (target.sprite != null) {
				target.sprite.showStatus(CharSprite.NEUTRAL, target.defenseVerb());
			}
		}
		beam = null;
	}
}
