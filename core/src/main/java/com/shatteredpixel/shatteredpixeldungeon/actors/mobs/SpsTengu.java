/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow.HugeShuriken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD's Tengu implementation, independent of Shattered's two-room arena. */
public class SpsTengu extends Mob {

	private static final int JUMP_DELAY = 5;
	private int timeToJump = JUMP_DELAY;

	{
		spriteClass = TenguSprite.class;
		HP = HT = 600;
		EXP = 40;
		defenseSkill = 30;
		viewDistance = 5;
		properties.add(Property.HUMAN);
		properties.add(Property.BOSS);
		resistances.add(Burning.class);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
		resistances.add(EnchantmentDark.class);
		weaknesses.add(WandOfLight.class);
		weaknesses.add(EnchantmentLight.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(23, 35); }
	@Override public int attackSkill(Char target) { return 40; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 5); }

	@Override
	protected boolean canAttack(Char enemy) {
		if (buff(Locked.class) != null) return super.canAttack(enemy);
		return new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		timeToJump--;
		if (timeToJump <= 0 && Dungeon.level.adjacent(pos, enemy.pos)) {
			if (jump(enemy, true)) return true;
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

	@Override
	public int attackProc(Char enemy, int damage) {
		int distance = Dungeon.level.distance(pos, enemy.pos);
		if (distance == 1) {
			Buff.affect(enemy, Silent.class, 0.5f);
			timeToJump--;
		}
		if (distance > 1 && Random.Int(10) > 7) {
			Buff.affect(enemy, Locked.class, 5f);
			timeToJump++;
		}
		if (distance > 1 && Random.Int(10) > 9) {
			Buff.affect(enemy, Burning.class).reignite(enemy, 4f);
			timeToJump++;
		}
		return super.attackProc(enemy, damage);
	}

	private boolean jump(Char target, boolean animateImmediately) {
		timeToJump = JUMP_DELAY;
		ArrayList<Integer> visibleCells = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
			if (target != null && Dungeon.level.adjacent(cell, target.pos)) continue;
			if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) visibleCells.add(cell);
		}
		if (visibleCells.isEmpty()) return false;

		for (int i = 0; i < 3; i++) {
			int trapCell = Random.element(visibleCells);
			if (Dungeon.level.map[trapCell] == Terrain.INACTIVE_TRAP) {
				Dungeon.level.setTrap(new PoisonDartTrap().reveal(), trapCell);
				Dungeon.level.set(trapCell, Terrain.TRAP);
				GameScene.updateMap(trapCell);
			}
		}

		int oldPos = pos;
		int newPos = Random.element(visibleCells);
		move(newPos);
		if (animateImmediately && sprite != null) sprite.move(oldPos, newPos);
		if (sprite != null && Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[newPos]) {
			CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
			Sample.INSTANCE.play(Assets.Sounds.PUFF);
		}
		spend(1f / speed());
		return true;
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
		com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.RockCode.dropForPerformer(
				new com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.Nshuriken());
		SpsPrisonBossRewards.grant(pos, new MasterThievesArmband().identify(), new HugeShuriken());
		yell(Messages.get(this, "die"));
	}

	private static final String TIME_TO_JUMP = "time_to_jump";
	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TIME_TO_JUMP, timeToJump);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		timeToJump = bundle.contains(TIME_TO_JUMP) ? bundle.getInt(TIME_TO_JUMP) : JUMP_DELAY;
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}
}
