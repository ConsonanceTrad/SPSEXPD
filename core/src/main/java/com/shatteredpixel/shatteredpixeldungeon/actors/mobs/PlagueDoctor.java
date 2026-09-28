/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.DarkGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpeedUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.PotionOfMage;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SpsSkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.Dpotion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PlagueDoctorSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShadowRatSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PlagueDoctor extends LegacyDualLootMob {
	private static final String BREAKS = "breaks";
	private static final String SPAWNED_SHADOW = "spawned_shadow";
	private int breaks;
	private boolean spawnedShadow;

	{
		spriteClass = PlagueDoctorSprite.class;
		HP = HT = 500;
		defenseSkill = 5;
		baseSpeed = 0.75f;
		EXP = 30;
		maxLvl = 35;
		setupLegacyDualLoot(AlchemistsToolkit.class, 0.2f, Generator.Category.POTION, 1f);
		FLEEING = new Fleeing();
		properties.add(Property.HUMAN);
		properties.add(Property.BOSS);
		immunities.add(ToxicGas.class);
		immunities.add(ParalyticGas.class);
		immunities.add(DarkGas.class);
		immunities.add(ConfusionGas.class);
	}

	@Override public Item SupercreateLoot() { return new PotionOfMage(); }

	@Override public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice"));
		Dungeon.level.seal();
		if (!spawnedShadow && Dungeon.hero != null) {
			Buff.affect(Dungeon.hero, ShadowRatSummon.class);
			spawnedShadow = true;
		}
	}

	@Override protected boolean act() {
		if (3 - breaks > 4 * HP / HT) {
			breaks++;
			if (breaks > 1) {
				GLog.i(Messages.get(this, "crazy"));
				yell(Messages.get(this, "yell2"));
			}
			return true;
		}
		if (breaks == 1) {
			state = FLEEING;
			GameScene.add(Blob.seed(pos, 20, ToxicGas.class));
		} else if (breaks == 2) {
			state = HUNTING;
		}
		return super.act();
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (breaks == 0) {
			if (Random.Int(2) == 0) {
				switch (Random.Int(4)) {
					case 0:
						enemy.HP += enemy.HT / 10;
						if (enemy.sprite != null) enemy.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
						break;
					case 1: Buff.affect(enemy, AttackUp.class, 5f).level(20); break;
					case 2: Buff.affect(enemy, DefenceUp.class, 5f).level(20); break;
					default: Buff.affect(enemy, SpeedUp.class, 5f); break;
				}
			}
			damage = 0;
			if (Random.Int(3) == 0) yell(Messages.get(this, "yell"));
		} else if (breaks == 2 && Random.Int(2) == 0) {
			Buff.affect(enemy, Bleeding.class).set(5);
		} else if (breaks == 3) {
			Buff.affect(this, AttackUp.class, 5f).level(50);
			Buff.affect(this, DefenceUp.class, 5f).level(25);
		}
		return damage;
	}

	@Override public int defenseProc(Char enemy, int damage) {
		if (breaks == 1 && Random.Int(2) == 0) {
			switch (Random.Int(4)) {
				case 0: GameScene.add(Blob.seed(pos, 25, ToxicGas.class)); break;
				case 1: GameScene.add(Blob.seed(pos, 25, ConfusionGas.class)); break;
				case 2: GameScene.add(Blob.seed(pos, 25, ParalyticGas.class)); break;
				default: GameScene.add(Blob.seed(pos, 25, DarkGas.class)); break;
			}
		}
		return super.defenseProc(enemy, damage);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(6, 19); }
	@Override public int attackSkill(Char target) { return 30; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 5); }

	@Override public void destroy() {
		super.destroy();
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) if (mob instanceof ShadowRat) mob.die(null);
	}

	@Override public void die(Object cause) {
		super.die(cause);
		if (Dungeon.hero != null) {
			Buff.detach(Dungeon.hero, ShadowRatSummon.class);
		}
		com.shatteredpixel.shatteredpixeldungeon.items.journalpages.JournalPage.dropAt(
				new com.shatteredpixel.shatteredpixeldungeon.items.journalpages.Sokoban1(), pos);
		Dungeon.level.drop(new Gold(1500), pos).sprite.drop();
		Dungeon.level.drop(new SpsSkeletonKey(Dungeon.depth), pos).sprite.drop();
		RatKing king = new RatKing();
		king.state = king.WANDERING;
		king.pos = pos;
		GameScene.add(king, 1f);
		ScrollOfTeleportation.appear(king, king.pos);
		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.PERFORMER && Dungeon.hero.skin == 7) {
			Dungeon.level.drop(new Dpotion(), Dungeon.hero.pos).sprite.drop();
		}
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Badges.validateBossSlain();
	}

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BREAKS, breaks);
		bundle.put(SPAWNED_SHADOW, spawnedShadow);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		breaks = bundle.getInt(BREAKS);
		spawnedShadow = bundle.getBoolean(SPAWNED_SHADOW);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	public static class ShadowRatSummon extends Buff {
		private static final String CHARGE = "charge";
		private int charge;

		@Override public boolean act() {
			charge++;
			int rats = 1;
			for (Mob mob : Dungeon.level.mobs) if (mob instanceof ShadowRat) rats++;
			int needed = Math.min(10, rats);
			if (charge >= needed) {
				charge -= needed;
				int cell = randomFreeCell();
				if (cell >= 0) {
					ShadowRat.spawnAt(cell);
					Sample.INSTANCE.play(Assets.Sounds.BURNING);
				}
			}
			spend(TICK);
			return true;
		}

		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = bundle.getInt(CHARGE); }
	}

	private static int randomFreeCell() {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null) cells.add(cell);
		}
		return cells.isEmpty() ? -1 : Random.element(cells);
	}

	public static class ShadowRat extends Mob {
		{
			spriteClass = ShadowRatSprite.class;
			HP = HT = 60;
			defenseSkill = 3;
			EXP = 1;
			loot = StoneOre.class;
			lootChance = 0.2f;
			properties.add(Property.UNKNOW);
			properties.add(Property.MINIBOSS);
			immunities.add(ToxicGas.class);
			immunities.add(Poison.class);
			immunities.add(Burning.class);
		}

		@Override public void damage(int damage, Object source) {
			if (source instanceof WandOfLight) { destroy(); if (sprite != null) sprite.die(); }
			else super.damage(damage, source);
		}
		@Override public int attackProc(Char enemy, int damage) {
			damage = super.attackProc(enemy, damage);
			if (Random.Int(3) == 0) Buff.prolong(enemy, AttackDown.class, 10f).level(25);
			else if (Random.Int(3) == 0) Buff.prolong(enemy, Vertigo.class, 5f);
			return super.attackProc(enemy, damage);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(6, 9); }
		@Override public int attackSkill(Char target) { return 25; }
		@Override public int drRoll() { return 0; }

		public static void spawnAroundChance(int center) {
			for (int offset : PathFinder.NEIGHBOURS4) {
				int cell = center + offset;
				if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
						&& Actor.findChar(cell) == null && Random.Float() < 0.75f) spawnAt(cell);
			}
		}
		public static ShadowRat spawnAt(int cell) {
			if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null;
			ShadowRat rat = new ShadowRat();
			rat.pos = cell;
			rat.state = rat.HUNTING;
			GameScene.add(rat, 2f);
			return rat;
		}
	}
}
