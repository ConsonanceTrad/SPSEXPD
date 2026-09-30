/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.Generator;
import pd.items.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.SentinelSprite;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;

import java.util.ArrayList;

/** Steel guardian from the SPS energy-core arena. */
public class MineSentinel extends Mob {

	private static final int LEGACY_DEPTH = 67;
	private static final int REGENERATION = 100;
	private Weapon weapon;

	{
		spriteClass = SentinelSprite.class;
		HP = HT = 400 + LEGACY_DEPTH * 10;
		defenseSkill = 15;
		EXP = 25;
		maxLvl = -1;
		state = PASSIVE;
		properties.add(Property.INORGANIC);
		properties.add(Property.MECH);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Burning.class);
		immunities.add(Vertigo.class);
		immunities.add(Paralysis.class);
	}

	public MineSentinel() {
		weapon = (Weapon) Generator.random(Generator.Category.OLDWEAPON);
		weapon.identify();
		weapon.enchant(Weapon.Enchantment.randomCommon());
		weapon.upgrade(10);
	}

	@Override
	protected boolean act() {
		if (state == HUNTING) {
			for (int offset : PathFinder.NEIGHBOURS8) {
				Char ch = Actor.findChar(pos + offset);
				if (ch instanceof MineSentinel && Random.Int(10) < 2) {
					ch.damage(1, this);
					break;
				}
			}
		}

		if (state == HUNTING && !heroNear() && Random.Float() < 0.5f) {
			ArrayList<Integer> cells = new ArrayList<>();
			for (int offset : PathFinder.NEIGHBOURS8) {
				int cell = Dungeon.hero.pos + offset;
				if (cell >= 0 && cell < Dungeon.level.length()
						&& Actor.findChar(cell) == null
						&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) cells.add(cell);
			}
			if (!cells.isEmpty()) teleport(Random.element(cells));
		} else if (state != PASSIVE && HP < HT / 4 && Random.Float() < 0.5f) {
			int cell = Dungeon.level.randomRespawnCell(this);
			if (cell != -1) {
				teleport(cell);
				HP = Math.min(HT, HP + REGENERATION);
			}
		}
		return super.act();
	}

	private void teleport(int cell) {
		int old = pos;
		move(cell, false);
		if (sprite != null) sprite.place(cell);
	}

	private boolean heroNear() {
		return Dungeon.hero != null && Dungeon.level.distance(pos, Dungeon.hero.pos) <= 1;
	}

	private boolean otilukeAlive() {
		for (Mob mob : Dungeon.level.mobs) if (mob instanceof Otiluke && mob.isAlive()) return true;
		return false;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return !otilukeAlive() && Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		int reflected = Random.IntRange(0, damage / 3);
		if (reflected > 0 && otilukeAlive()) enemy.damage(reflected, this);
		return super.defenseProc(enemy, damage);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(weapon.min(weapon.buffedLvl()), weapon.max(weapon.buffedLvl()));
	}

	@Override
	public int attackSkill(Char target) {
		return Math.round((30 + LEGACY_DEPTH * 2) * weapon.accuracyFactor(this, target));
	}

	@Override
	public float attackDelay() {
		return weapon.delayFactor(this);
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(3, 30);
	}

	@Override
	public void damage(int damage, Object source) {
		if (state == PASSIVE) state = HUNTING;
		super.damage(damage, source);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		return weapon.proc(this, enemy, damage);
	}

	@Override
	public void beckon(int cell) {
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Dungeon.level.drop(weapon, pos).sprite.drop();
	}

	@Override
	public String description() {
		return Messages.get(this, "desc", weapon.name());
	}

	private static final String WEAPON = "weapon";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(WEAPON, weapon);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		weapon = (Weapon) bundle.get(WEAPON);
	}
}
