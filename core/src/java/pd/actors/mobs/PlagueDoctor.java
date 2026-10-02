/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Poison;
import pd.actors.buffs.SpeedUp;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.npcs.RatKing;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.equipment.artifacts.AlchemistsToolkit;
import pd.items.specific.keys.SpsSkeletonKey;
import pd.items.misc.PotionOfMage;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.wands.WandOfLight;
import pd.items.equipment.weapon.rockcode.Dpotion;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.PlagueDoctorSprite;
import pd.sprites.ShadowRatSprite;
import pd.ui.BossHealthBar;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class PlagueDoctor extends LegacyDualLootMob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(PlagueDoctor.class)
			.t("name", "瘟疫医生")
			.t("desc", "这是一个不被认可的研究者。在被带到这个世界后，他彻底疯了，并会随着战斗进行变得愈发危险。")
			.t("notice", "所以……你是来帮我做研究的吗？")
			.t("yell", "放心，这不会痛的……我会证明给你看……")
			.t("yell2", "老鼠……它们到处都是……我们完了……")
			.t("crazy", "瘟疫医生的信念正在经受考验……狂乱！")
			.t("$shadowrat.name", "瘟疫之影")
			.t("$shadowrat.desc", "一种奇怪的影子，外形和老鼠差不多。");
	}



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
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) if (mob instanceof ShadowRat) mob.die(null);
	}

	@Override public void die(Object cause) {
		super.die(cause);
		if (Dungeon.hero != null) {
			Buff.detach(Dungeon.hero, ShadowRatSummon.class);
		}
		pd.items.specific.journalpages.JournalPage.dropAt(
				new pd.items.specific.journalpages.Sokoban1(), pos);
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
			for (Mob mob : Dungeon.level.mobs()) if (mob instanceof ShadowRat) rats++;
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
