/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.effects.CellEmitter;
import pd.effects.particles.PurpleParticle;
import pd.items.Generator;
import pd.items.Item;
import pd.items.StoneOre;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.MagicEyeSprite;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.utils.Random;

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
