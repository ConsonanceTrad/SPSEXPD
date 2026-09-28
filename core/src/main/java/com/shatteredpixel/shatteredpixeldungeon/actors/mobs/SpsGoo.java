/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GooWarn;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hot;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.UpgradeBlobViolet;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.CopyBall;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SpsSkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.ActiveMrDestructo;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFirebolt;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentFire;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentFire2;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpsGooSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PoisonGooSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Original 350 HP SPS sewer Goo and its splitting poison offspring. */
public class SpsGoo extends Mob {

	private int pumpedUp;
	private boolean spawnedMini;

	{
		HP = HT = 350;
		EXP = 20;
		defenseSkill = 12;
		spriteClass = SpsGooSprite.class;
		properties.add(Property.UNKNOW);
		properties.add(Property.BOSS);
		resistances.add(ToxicGas.class);
		resistances.add(EnchantmentDark.class);
		resistances.add(EnchantmentDark2.class);
		immunities.add(Roots.class);
		weaknesses.add(Burning.class);
		weaknesses.add(WandOfFirebolt.class);
		weaknesses.add(EnchantmentFire.class);
		weaknesses.add(EnchantmentFire2.class);
		weaknesses.add(DamageType.Fire.class);
	}

	@Override
	public int damageRoll() {
		if (pumpedUp > 0) {
			pumpedUp = 0;
			PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
			for (int cell = 0; GameScene.hasActiveScene() && cell < PathFinder.distance.length; cell++) {
				if (PathFinder.distance[cell] < Integer.MAX_VALUE && Dungeon.level.insideMap(cell)) {
					CellEmitter.get(cell).burst(ElmoParticle.FACTORY, 10);
				}
			}
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			return Random.NormalIntRange(5, 30);
		}
		return Random.NormalIntRange(2, 12);
	}

	@Override public int attackSkill(Char target) { return pumpedUp > 0 ? 30 : 15; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 2); }

	@Override
	protected boolean act() {
		if (Dungeon.level.water[pos] && HP < HT) {
			HP += 3;
			if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
		}
		return super.act();
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return pumpedUp > 0 ? distance(enemy) <= 2 : super.canAttack(enemy);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(3) == 0) {
			Buff.affect(enemy, Ooze.class).set(7f);
			if (enemy.sprite != null) enemy.sprite.burst(0x000000, 5);
		}
		if (pumpedUp > 0 && Camera.main != null) Camera.main.shake(3, 0.2f);
		return damage;
	}

	@Override
	public boolean attack(Char enemy, float damageMultiplier, float damageBonus, float accuracyMultiplier) {
		boolean result = super.attack(enemy, damageMultiplier, damageBonus, accuracyMultiplier);
		pumpedUp = 0;
		return result;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (pumpedUp == 1) {
			if (sprite != null) ((SpsGooSprite) sprite).pumpUp();
			warnCells(2);
			pumpedUp++;
			spend(attackDelay());
			return true;
		}
		if (pumpedUp >= 2 || Random.Int(HP * 2 <= HT ? 2 : 5) > 0) {
			boolean visible = sprite != null && enemy.sprite != null
					&& (sprite.visible || enemy.sprite.visible);
			if (visible) {
				if (pumpedUp >= 2) ((SpsGooSprite) sprite).pumpAttack();
				else sprite.attack(enemy.pos);
				return false;
			}
			attack(enemy);
			spend(attackDelay());
			return true;
		}

		pumpedUp++;
		if (sprite != null) ((SpsGooSprite) sprite).pumpUp();
		warnCells(1);
		if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[pos] && sprite != null) {
			sprite.showStatus(CharSprite.WARNING, "!!!");
			GLog.n(Messages.get(this, "atk"));
		}
		spend(attackDelay());
		return true;
	}

	private void warnCells(int distance) {
		PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), distance);
		for (int cell = 0; cell < PathFinder.distance.length; cell++) {
			if (PathFinder.distance[cell] < Integer.MAX_VALUE && Dungeon.level.insideMap(cell)) {
				GameScene.add(Blob.seed(cell, 2, GooWarn.class));
			}
		}
	}

	@Override
	protected boolean getCloser(int target) {
		pumpedUp = 0;
		if (sprite != null) sprite.idle();
		return super.getCloser(target);
	}

	@Override
	protected boolean getFurther(int target) {
		pumpedUp = 0;
		if (sprite != null) sprite.idle();
		return super.getFurther(target);
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
		if (pumpedUp > 0 && sprite != null) ((SpsGooSprite) sprite).pumpUp();
	}

	@Override public Item SupercreateLoot() { return new CopyBall(); }

	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice"));
		Dungeon.level.seal();
		if (!spawnedMini) {
			PoisonGoo.spawnAround(pos);
			spawnedMini = true;
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.RockCode.dropForPerformer(
				new com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.Obubble());
		float rareChance = LegacyDualLootMob.primaryShare(0.2f, 1f,
				LuckyBadge.luckBonus(Dungeon.hero));
		Item bossLoot = Random.Float() < rareChance ? new UpgradeBlobViolet() : new ActiveMrDestructo();
		Dungeon.level.drop(bossLoot, pos).sprite.drop();
		Dungeon.level.drop(new Gold(1500), pos).sprite.drop();
		com.shatteredpixel.shatteredpixeldungeon.items.journalpages.JournalPage.dropAt(
				new com.shatteredpixel.shatteredpixeldungeon.items.journalpages.Sokoban1(), pos);

		RatKing king = new RatKing();
		king.state = king.WANDERING;
		king.pos = pos;
		GameScene.add(king, 1f);
		ScrollOfTeleportation.appear(king, king.pos);

		finishFightIfClear(pos);
		yell(Messages.get(this, "die"));
	}

	private static void finishFightIfClear(int pos) {
		for (Mob mob : Dungeon.level.mobs) {
			if (mob instanceof SpsGoo || mob instanceof PoisonGoo) return;
		}
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Dungeon.level.drop(new SpsSkeletonKey(Dungeon.depth), pos).sprite.drop();
		Badges.validateBossSlain();
	}

	private static final String PUMPED_UP = "pumped_up";
	private static final String SPAWNED_MINI = "spawned_mini";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PUMPED_UP, pumpedUp);
		bundle.put(SPAWNED_MINI, spawnedMini);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		pumpedUp = bundle.getInt(PUMPED_UP);
		spawnedMini = bundle.getBoolean(SPAWNED_MINI);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	public static class PoisonGoo extends Mob {
		private static final float SPAWN_DELAY = 2f;
		private static final float SPLIT_DELAY = 1f;
		private static final String GENERATION = "generation";
		private int generation;

		{
			HP = HT = 100;
			EXP = 1;
			defenseSkill = 12;
			spriteClass = PoisonGooSprite.class;
			baseSpeed = 1.5f;
			loot = new StoneOre();
			lootChance = 0.25f;
			properties.add(Property.ELEMENT);
			properties.add(Property.MINIBOSS);
			resistances.add(ToxicGas.class);
			resistances.add(EnchantmentDark.class);
			immunities.add(Roots.class);
			FLEEING = new Fleeing();
		}

		@Override public int damageRoll() { return Random.NormalIntRange(1, 10); }
		@Override public int attackSkill(Char target) { return 5; }
		@Override public int drRoll() { return 0; }

		@Override
		protected boolean act() {
			boolean result = super.act();
			if (state == FLEEING && buff(Terror.class) == null && enemy != null
					&& enemySeen && enemy.buff(Poison.class) == null) state = HUNTING;
			if (Dungeon.level.water[pos] && HP < HT) {
				HP++;
				if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			} else if (Dungeon.level.water[pos] && HP == HT && HT < 100) {
				HT += 5;
				HP = HT;
				if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
			return result;
		}

		@Override
		public int attackProc(Char enemy, int damage) {
			Buff.affect(enemy, Poison.class).set(Random.IntRange(4, 6));
			state = FLEEING;
			return damage;
		}

		@Override
		public void move(int step, boolean travelling) {
			if (state == FLEEING) GameScene.add(Blob.seed(pos, Random.IntRange(7, 9), Web.class));
			super.move(step, travelling);
		}

		@Override
		public int defenseProc(Char enemy, int damage) {
			boolean bossAlive = false;
			for (Mob mob : Dungeon.level.mobs) {
				if (mob instanceof SpsGoo) {
					bossAlive = true;
					break;
				}
			}
			if (bossAlive && HP >= damage + 2) {
				ArrayList<Integer> candidates = new ArrayList<>();
				for (int offset : PathFinder.NEIGHBOURS4) {
					int cell = pos + offset;
					if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
							&& Actor.findChar(cell) == null) candidates.add(cell);
				}
				if (!candidates.isEmpty()) {
					PoisonGoo clone = split();
					clone.HP = (HP - damage) / 2;
					clone.pos = Random.element(candidates);
					clone.state = clone.HUNTING;
					if (Dungeon.level.map[clone.pos] == Terrain.DOOR) {
						com.shatteredpixel.shatteredpixeldungeon.levels.features.Door.enter(clone.pos);
					}
					GameScene.add(clone, SPLIT_DELAY);
					Actor.add(new Pushing(clone, pos, clone.pos));
					HP -= clone.HP;
				}
			}
			return super.defenseProc(enemy, damage);
		}

		private PoisonGoo split() {
			PoisonGoo clone = new PoisonGoo();
			clone.generation = generation + 1;
			if (buff(Burning.class) != null) Buff.affect(clone, Burning.class).reignite(clone, 3f);
			if (buff(Poison.class) != null) Buff.affect(clone, Poison.class).set(2);
			return clone;
		}

		@Override
		public boolean add(Buff buff) {
			if (buff instanceof Roots) {
				if (HP < HT) {
					HP += HT / 10;
					if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				}
				return false;
			}
			if (buff instanceof Hot) {
				damage(Dungeon.level.water[pos]
						? Random.NormalIntRange(1, HT * 2 / 3)
						: Random.NormalIntRange(HT / 2, HT), buff);
				return false;
			}
			return super.add(buff);
		}

		@Override
		public void die(Object cause) {
			if (generation > 0) lootChance = 0;
			super.die(cause);
			finishFightIfClear(pos);
			yell(Messages.get(this, "die"));
		}

		@Override
		public void notice() {
			super.notice();
			yell(Messages.get(this, "notice"));
		}

		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(GENERATION, generation);
		}
		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			generation = bundle.getInt(GENERATION);
		}

		private class Fleeing extends Mob.Fleeing {
			@Override
			protected void nowhereToRun() {
				if (buff(Terror.class) == null) state = HUNTING;
				else super.nowhereToRun();
			}
		}

		public static void spawnAround(int center) {
			for (int offset : PathFinder.NEIGHBOURS4) {
				int cell = center + offset;
				if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
						&& Actor.findChar(cell) == null) spawnAt(cell);
			}
		}

		public static PoisonGoo spawnAt(int pos) {
			PoisonGoo goo = new PoisonGoo();
			goo.pos = pos;
			goo.state = goo.HUNTING;
			GameScene.add(goo, SPAWN_DELAY);
			return goo;
		}
	}
}
