/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.DarkGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ShockWeb;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SlowGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.TarGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DungeonBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark;
import com.shatteredpixel.shatteredpixeldungeon.levels.BossRushLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SeekingBombSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.UDM300Sprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/** The SPS killing machine with status phases and four homing bombs per break. */
public class UDM300 extends BossRushBoss {
	{
		spriteClass = UDM300Sprite.class;
		baseSpeed = 0.75f;
		properties.add(Property.INORGANIC);
		properties.add(Property.MECH);
		resistances.add(EnchantmentDark.class);
		immunities.add(EnchantmentDark.class);
		immunities.add(SlowGas.class);
		immunities.add(TarGas.class);
		immunities.add(Tar.class);
	}

	@Override
	protected void onPhaseChanged(int phase) {
		for (int i = 0; i < 4; i++) {
			SeekBomb bomb = new SeekBomb();
			bomb.pos = Dungeon.level instanceof BossRushLevel
					? ((BossRushLevel) Dungeon.level).safeSpawnCell(pos) : pos;
			bomb.state = bomb.HUNTING;
			GameScene.add(bomb);
		}
	}

	@Override
	protected boolean act() {
		Class<? extends Blob> blob = activePhaseBlob();
		if (blob != null) GameScene.add(Blob.seed(pos, activePhaseVolume(), blob));
		if (state == FLEEING && buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror.class) == null
				&& enemy != null && enemySeen && enemy.buff(Poison.class) == null
				&& enemy.buff(Burning.class) == null && enemy.buff(Tar.class) == null) {
			state = HUNTING;
		}
		return super.act();
	}

	protected Class<? extends Blob> activePhaseBlob() {
		if (breaks == 1) return SlowGas.class;
		if (breaks == 2) return TarGas.class;
		if (breaks == 3) return DarkGas.class;
		return null;
	}

	protected int activePhaseVolume() {
		return breaks == 1 ? 30 : breaks == 2 ? 60 : breaks == 3 ? 100 : 0;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) {
			if (breaks == 0) Buff.affect(enemy, Poison.class).set(Random.Int(7, 9));
			else if (breaks == 1) Buff.affect(enemy, Tar.class);
			else if (breaks == 2) Buff.affect(enemy, Burning.class).reignite(enemy, 3f);
			state = FLEEING;
		}
		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		int reflected = Random.IntRange(0, Math.max(0, damage / 3)) - enemy.drRoll();
		if (reflected > 0 || Random.Int(3) == 0) enemy.damage(Math.max(0, reflected), this);
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void move(int step, boolean travelling) {
		if (state == FLEEING) GameScene.add(Blob.seed(pos, Random.IntRange(5, 6), ShockWeb.class));
		super.move(step, travelling);
	}

	@Override protected Class<? extends BossRushBoss> nextBoss() { return UKing.class; }

	public static class SeekBomb extends Mob {
		private static final int BOMB_DELAY = 10;
		private int timeToBomb = BOMB_DELAY;
		private boolean exploding;

		{
			spriteClass = SeekingBombSprite.class;
			HP = HT = 1;
			EXP = 0;
			defenseSkill = 0;
			baseSpeed = 1f;
			state = HUNTING;
			properties.add(Property.BOSS_MINION);
			properties.add(Property.INORGANIC);
			properties.add(Property.MECH);
			properties.add(Property.MINIBOSS);
		}

		@Override protected boolean act() {
			timeToBomb--;
			if (timeToBomb <= 0) {
				explode();
				return true;
			}
			return super.act();
		}

		@Override public int attackProc(Char enemy, int damage) {
			explode();
			return damage;
		}

		private void explode() {
			if (exploding) return;
			exploding = true;
			createBomb().explode(pos);
			destroy();
			if (sprite != null) sprite.die();
		}

		@Override public void die(Object cause) {
			if (!exploding) createBomb().explode(pos);
			super.die(cause);
		}
		protected DungeonBomb createBomb() { return new DungeonBomb(); }
		@Override public int attackSkill(Char target) { return 10; }
		@Override public int damageRoll() { return Random.NormalIntRange(0, 1); }
		@Override public int drRoll() { return 0; }

		private static final String TIMER = "timer";
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(TIMER, timeToBomb); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); timeToBomb = bundle.contains(TIMER) ? bundle.getInt(TIMER) : BOMB_DELAY; }
	}
}
