/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Silent;
import pd.items.RedDewdrop;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.ShellSprite;
import pd.utils.GLog;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;

public class Shell extends Mob {

	private static final float TIME_TO_ZAP = 2f;
	private int shellCharge;

	{
		spriteClass = ShellSprite.class;
		HP = HT = 500;
		defenseSkill = 0;
		EXP = 50;
		state = PASSIVE;
		alignment = Alignment.NEUTRAL;
		loot = RedDewdrop.class;
		lootChance = 1f;
		properties.add(Property.BOSS);
		properties.add(Property.BOSS_MINION);
		properties.add(Property.INORGANIC);
		properties.add(Property.MECH);
		resistances.add(pd.actors.blobs.Electricity.class);
	}

	@Override public int damageRoll() { return 0; }
	@Override public int attackSkill(Char target) { return 100; }
	@Override public int drRoll() { return 0; }
	@Override public void beckon(int cell) { }

	public void addCharge(int amount) { shellCharge = Math.max(0, shellCharge + amount); }
	public int charge() { return shellCharge; }

	@Override
	public void damage(int damage, Object source) {
		if (shellCharge > 0) zapAround();
		super.damage(damage, source);
	}

	@Override
	protected boolean act() {
		if (shellCharge > 20 && Random.Int(shellCharge) > 20
				&& Dungeon.hero != null && Dungeon.hero.isAlive()) {
			zapAll();
		}
		return super.act();
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (buff(Silent.class) != null) return Dungeon.level.adjacent(pos, enemy.pos);
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)) return super.doAttack(enemy);
		if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
			sprite.zap(enemy.pos);
			return false;
		}
		zapEnemy();
		return true;
	}

	private void zapEnemy() {
		spend(TIME_TO_ZAP);
		if (enemy == null || !enemy.isAlive() || !hit(this, enemy, true) || shellCharge <= 0) return;
		int damage = quarterChargeDamage();
		shellCharge = Math.max(0, shellCharge - damage);
		damage(enemy, damage);
	}

	private void zapAll() {
		GLog.n(Messages.get(this, "zap"));
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob != this && mob.isAlive() && Dungeon.level.distance(pos, mob.pos) > 1) {
				mob.damage(1, this);
				if (sprite != null && mob.sprite != null && (sprite.visible || mob.sprite.visible)) sprite.zap(mob.pos);
			}
		}
		if (Dungeon.hero != null && Dungeon.hero.isAlive()
				&& Dungeon.level.distance(pos, Dungeon.hero.pos) > 1 && shellCharge > 0) {
			int damage = quarterChargeDamage();
			shellCharge = Math.max(0, shellCharge - damage);
			damage(Dungeon.hero, damage);
		}
	}

	private void zapAround() {
		GLog.n(Messages.get(this, "zap"));
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			Char ch = Actor.findChar(cell);
			if (ch != null && ch == Dungeon.hero && ch.isAlive() && shellCharge > 0) {
				int damage = halfChargeDamage();
				shellCharge = Math.max(0, shellCharge - 1);
				damage(ch, damage);
			} else if (ch != null && ch != this && ch.isAlive()) {
				ch.damage(1, this);
				if (sprite != null && ch.sprite != null && (sprite.visible || ch.sprite.visible)) sprite.zap(ch.pos);
			}
		}
	}

	private int quarterChargeDamage() {
		int low = Math.round(shellCharge / 4f);
		int high = Math.round(shellCharge / 2f);
		return high > low ? Random.Int(low, high) : low;
	}

	private int halfChargeDamage() {
		int low = shellCharge / 2;
		return shellCharge > low ? Random.Int(low, shellCharge) : low;
	}

	private void damage(Char target, int damage) {
		if (Dungeon.level.water[target.pos] && !target.flying) damage = Math.round(damage * 1.5f);
		target.damage(damage, this);
		if (sprite != null && target.sprite != null) sprite.zap(target.pos);
	}

	public void onZapComplete() {
		zapEnemy();
		next();
	}

	// The legacy shell rejects every status effect, not only hostile ones.
	@Override public synchronized boolean add(Buff buff) { return false; }

	private static final String CHARGE = "shell_charge";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, shellCharge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); shellCharge = Math.max(0, bundle.getInt(CHARGE)); }
}
