/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.DarkGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DangerousBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HybridSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD's three-phase mixed creature boss. */
public class Hybrid extends Mob {

	private int breaks;

	{
		spriteClass = HybridSprite.class;
		HP = HT = 800;
		defenseSkill = 10;
		baseSpeed = 1.5f;
		EXP = 50;
		properties.add(Property.BEAST);
		properties.add(Property.ALIEN);
		properties.add(Property.UNDEAD);
		properties.add(Property.MECH);
		properties.add(Property.INORGANIC);
		properties.add(Property.BOSS);
		immunities.add(ToxicGas.class);
		immunities.add(ParalyticGas.class);
		immunities.add(DarkGas.class);
		immunities.add(ConfusionGas.class);
	}

	@Override public int damageRoll() {
		return Dungeon.isChallenged(Challenges.TEST_TIME)
				? Random.NormalIntRange(0, 1) : Random.NormalIntRange(30, 42);
	}
	@Override public int attackSkill(Char target) { return 50; }
	@Override public int drRoll() { return Random.NormalIntRange(10, 15); }

	@Override
	protected boolean act() {
		if (3 - breaks > 4 * HP / HT) {
			breaks++;
			new DangerousBomb().explode(pos);
			Buff.affect(this, ShieldArmor.class).level(200);
			return true;
		}
		return super.act();
	}

	@Override
	public void move(int step, boolean travelling) {
		super.move(step, travelling);
		if (Dungeon.level != null && Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && state == FLEEING) {
			Buff.affect(this, EnergyArmor.class).level(200);
			yell(Messages.get(this, "shield"));
			state = HUNTING;
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0) {
			Buff.affect(enemy, Poison.class).set(Random.Int(8, 10));
			state = FLEEING;
		}
		enemy.damage(damage / 2, new EnergyDamage());
		return super.attackProc(enemy, damage - damage / 2);
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (enemy != null && Random.Int(3) == 0) {
			Buff.affect(enemy, Tar.class);
			state = HUNTING;
		}
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int damage, Object src) {
		if (breaks > 2 && damage > 5 && Dungeon.level != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int offset : PathFinder.NEIGHBOURS4) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
						&& Actor.findChar(cell) == null) candidates.add(cell);
			}
			if (!candidates.isEmpty()) {
				Mixers clone = new Mixers();
				clone.HT = clone.HP = damage;
				clone.pos = Random.element(candidates);
				clone.state = clone.HUNTING;
				if (Dungeon.level.map[clone.pos] == Terrain.DOOR) Door.enter(clone.pos);
				GameScene.add(clone, 1f);
				Actor.addDelayed(new SourceTimedPushing(clone, pos, clone.pos), 0f);
			}
		}
		super.damage(damage, src);
	}

	@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 2; }

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
				new com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.Mlaser());
		SpsCavesBossRewards.grant(pos, rareLoot(), new PotionOfExperience());
		yell(Messages.get(this, "die"));
	}

	static Item rareLoot() {
		return Generator.random(Generator.Category.EGGS);
	}

	private static final String BREAKS = "breaks";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BREAKS, breaks);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		breaks = bundle.getInt(BREAKS);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	public static final class EnergyDamage { }

	private static final class SourceTimedPushing extends Pushing {
		SourceTimedPushing(Char ch, int from, int to) {
			super(ch, from, to);
			spend(-1f);
		}
	}

	public static class Mixers extends Mob {
		{
			spriteClass = HybridSprite.class;
			HP = HT = 1;
			defenseSkill = 0;
			EXP = 0;
			properties.add(Property.UNKNOW);
			properties.add(Property.BOSS);
			properties.add(Property.BOSS_MINION);
		}

		@Override public int damageRoll() { return Random.NormalIntRange(25, 55); }
		@Override public int attackSkill(Char target) { return 100; }
		@Override public int drRoll() { return 0; }

		@Override
		public int attackProc(Char enemy, int damage) {
			enemy.damage(damage / 4, new ShockDamage());
			return super.attackProc(enemy, damage - damage / 4);
		}
	}

	public static final class ShockDamage { }
}
