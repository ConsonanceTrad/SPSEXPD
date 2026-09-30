/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Degrade;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.quest.AdventureJournal;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.OtilukeSprite;
import pd.utils.GLog;
import watabou.utils.Random;

/** Corrupted Otiluke mirror which powers the energy-core defenses. */
public class Otiluke extends Mob {

	private static final int LEGACY_DEPTH = 67;
	private static final float TIME_TO_ZAP = 1f;

	public static class EnergyDamage {
	}

	{
		spriteClass = OtilukeSprite.class;
		HP = HT = 10000;
		defenseSkill = 40;
		EXP = 101;
		maxLvl = -1;
		state = PASSIVE;
		properties.add(Property.BOSS);
		properties.add(Property.ELEMENT);
		properties.add(Property.MAGICER);
		properties.add(Property.INORGANIC);
		resistances.add(Poison.class);
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Burning.class);
		immunities.add(Vertigo.class);
		immunities.add(Paralysis.class);
	}

	@Override
	public int damageRoll() {
		return 150;
	}

	@Override
	public int attackSkill(Char target) {
		return 150 + LEGACY_DEPTH * 2;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(20, 40);
	}

	@Override
	public void damage(int damage, Object source) {
		if (state == PASSIVE) state = HUNTING;
		if (state == HUNTING) {
			for (Mob mob : Dungeon.level.mobs) {
				if (mob instanceof MineSentinel && mob.state == PASSIVE && Random.Int(20) < 2) {
					mob.damage(1, this);
					break;
				}
			}
		}
		super.damage(damage, source);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		enemy.damage(damageRoll() / 2, new EnergyDamage());
		return damage / 2;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy)
				|| new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)
				|| new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {
			return super.doAttack(enemy);
		}
		if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
			sprite.zap(enemy.pos);
			return false;
		}
		zap();
		return true;
	}

	private void zap() {
		spend(TIME_TO_ZAP);
		Char target = enemy;
		if (target == null || !target.isAlive()) return;
		if (hit(this, target, true)) {
			if (target == Dungeon.hero && Random.Int(2) == 0) {
				pd.actors.buffs.Buff.prolong(
						target, Degrade.class, 6f);
			}
			target.damage(Random.IntRange(100, 160 + LEGACY_DEPTH * 2), new EnergyDamage());
			if (target == Dungeon.hero && !target.isAlive()) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail(this);
				GLog.n(Messages.get(this, "bolt_kill"));
			}
		} else if (target.sprite != null) {
			target.sprite.showStatus(CharSprite.NEUTRAL, target.defenseVerb());
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Dungeon.level.locked = false;
		AdventureJournal.complete(completionDestination());
	}

	public static int completionDestination() {
		return AdventureJournal.destinationForBranch(Dungeon.branch) == 19 ? 19 : 7;
	}
}
