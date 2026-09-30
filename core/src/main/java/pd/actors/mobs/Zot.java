/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.effects.CellEmitter;
import pd.effects.Pushing;
import pd.effects.Speck;
import pd.items.SoulCollect;
import pd.items.Heap;
import pd.items.misc.AutoPotion;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.weapon.enchantments.EnchantmentDark;
import pd.levels.ZotBossLevel;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ZotSprite;
import pd.ui.BossHealthBar;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;

import java.util.ArrayList;

/** Zot, the optional prison boss reached through the Palantir. */
public class Zot extends Mob {

	public static final int LEGACY_DEPTH = 99;
	private static final int JUMP_DELAY = 10;
	private int timeToJump = JUMP_DELAY;

	{
		spriteClass = ZotSprite.class;
		baseSpeed = 0.5f;
		HP = HT = 25000;
		EXP = 20;
		defenseSkill = 40;
		properties.add(Property.UNKNOW);
		properties.add(Property.BOSS);

		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
		immunities.add(EnchantmentDark.class);
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Burning.class);
		immunities.add(ToxicGas.class);
		immunities.add(ScrollOfPsionicBlast.class);
		immunities.add(Vertigo.class);
		immunities.add(Paralysis.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(75, 125); }
	@Override public int attackSkill(Char target) { return 90; }
	@Override public int drRoll() { return Random.NormalIntRange(25, 45); }

	@Override
	protected boolean act() {
		if (paralysed > 0) {
			yell(Messages.get(this, "pain"));
			spawnParalysedEye();
			if (HP < HT) {
				if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				HP += 5;
			}
		}
		boolean result = super.act();
		int regeneration = Dungeon.hero != null
				&& Dungeon.hero.buff(AutoPotion.AutoHealPotion.class) != null
				? 1 : Random.Int(2, 5);
		if (HP < HT) {
			HP += regeneration;
			if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
		}
		return result;
	}

	private void spawnParalysedEye() {
		if (Dungeon.hero == null || countMobs(MagicEye.class) >= 11) return;
		ArrayList<Integer> cells = new ArrayList<>();
		for (int index = 0; index < 2; index++) {
			int cell = Dungeon.hero.pos + PathFinder.NEIGHBOURS4[index];
			if (Dungeon.level.insideMap(cell)
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])
					&& Actor.findChar(cell) == null) cells.add(cell);
		}
		if (!cells.isEmpty() && Random.Int(10) == 0) spawnMagicEye(Random.element(cells));
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (buff(Silent.class) != null) return super.canAttack(enemy);
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		timeToJump--;
		if (timeToJump <= 0 && Dungeon.level.adjacent(pos, enemy.pos)) {
			if (jump(enemy, true)) {
				spend(1f / speed());
				return true;
			}
		}
		return super.doAttack(enemy);
	}

	@Override
	protected boolean getCloser(int target) {
		if (fieldOfView != null && target >= 0 && target < fieldOfView.length && fieldOfView[target]) {
			Char targetChar = Actor.findChar(target);
			if (targetChar == null) targetChar = Dungeon.hero;
			if (jump(targetChar, false)) return true;
		}
		return super.getCloser(target);
	}

	private boolean jump(Char targetChar, boolean animateImmediately) {
		timeToJump = JUMP_DELAY;
		spawnPhase();

		ArrayList<Integer> destinations = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
			if (targetChar != null && Dungeon.level.adjacent(cell, targetChar.pos)) continue;
			if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) destinations.add(cell);
		}
		if (destinations.isEmpty()) return false;
		int newPos = Random.element(destinations);
		int oldPos = pos;
		move(newPos);
		if (animateImmediately && sprite != null) sprite.move(oldPos, newPos);
		if (Dungeon.level.heroFOV[newPos]) {
			CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
			Sample.INSTANCE.play(Assets.Sounds.PUFF);
		}
		return true;
	}

	private void spawnPhase() {
		if (countMobs(ZotPhase.class) >= 7) return;
		ArrayList<Integer> cells = freeNeighbours(pos);
		if (cells.isEmpty()) return;
		ZotPhase phase = new ZotPhase();
		phase.pos = Random.element(cells);
		phase.state = phase.HUNTING;
		GameScene.add(phase);
		Actor.add(new Pushing(phase, pos, phase.pos));
	}

	private void spawnMagicEye() {
		if (Dungeon.hero == null || countMobs(MagicEye.class) >= 11) return;
		ArrayList<Integer> cells = freeNeighbours(Dungeon.hero.pos);
		if (cells.isEmpty()) return;
		spawnMagicEye(Random.element(cells));
	}

	private void spawnMagicEye(int cell) {
		MagicEye eye = new MagicEye();
		eye.pos = cell;
		eye.state = eye.HUNTING;
		GameScene.add(eye);
		Actor.add(new Pushing(eye, pos, eye.pos));
	}

	private ArrayList<Integer> freeNeighbours(int center) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell)
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])
					&& Actor.findChar(cell) == null) result.add(cell);
		}
		return result;
	}

	private int countMobs(Class<? extends Mob> type) {
		int count = 0;
		for (Mob mob : Dungeon.level.mobs) if (type.isInstance(mob)) count++;
		return count;
	}

	@Override
	public void damage(int damage, Object source) {
		spawnMagicEye();
		super.damage(damage, source);
	}

	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice"));
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		if (Dungeon.level instanceof ZotBossLevel) {
			((ZotBossLevel) Dungeon.level).zotDefeated();
		} else {
			Dungeon.zotKilled = true;
		}
		Heap soul = Dungeon.level.drop(new SoulCollect(), pos);
		if (soul != null && soul.sprite != null) soul.sprite.drop();
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob instanceof ZotPhase || mob instanceof MagicEye) mob.die(null);
		}
		yell(Messages.get(this, "die"));
	}

	private static final String TIME_TO_JUMP = "time_to_jump";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TIME_TO_JUMP, timeToJump);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		timeToJump = bundle.contains(TIME_TO_JUMP) ? bundle.getInt(TIME_TO_JUMP) : JUMP_DELAY;
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}
}
