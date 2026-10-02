/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.Amok;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.StoneIce;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.Wet;
import pd.actors.damagetype.DamageType;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.EnergyParticle;
import pd.items.Dewdrop;
import pd.items.Generator;
import pd.items.Item;
import pd.items.SaveYourLife;
import pd.items.StoneOre;
import pd.items.consum.food.fruit.Strawberry;
import pd.items.consum.food.meatfood.Meat;
import pd.items.consum.food.vegetable.NutVegetable;
import pd.items.misc.LuckyBadge;
import pd.items.consum.scrolls.ScrollOfRegrowth;
import pd.items.equipment.wands.WandOfAcid;
import pd.items.equipment.wands.WandOfLightning;
import pd.items.equipment.wands.WandOfSwamp;
import pd.items.equipment.weapon.missiles.ShitBall;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.SpsSewerSprites;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Early-floor SPS-PD monsters, grouped to keep the legacy spawn table explicit. */
public final class SpsSewerMobs {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpsSewerMobs.class)
			.t("$brownbat.name", "小蝙蝠")
			.t("$brownbat.desc", "小蝙蝠呈棕色，是一种没什么威胁的生物。不过杀死它时，它有几率发出很大的响声。")
			.t("$brownbat.die", "凄惨的叫声惊醒了附近的敌人！")
			.t("$dustelement.name", "尘埃元素")
			.t("$dustelement.desc", "年久失修的下水道中的尘埃与向外扩散的黑暗力量融合，生成了这种羸弱的元素。")
			.t("$dustelement.blind", "灰尘阻碍了你的视线。")
			.t("$ratboss.name", "领头鼠")
			.t("$ratboss.desc", "领头鼠是鼠群的头领。虽然它不像鼠王一样有威信，但依然可以叫来鼠群。")
			.t("$ratboss.spawn", "这里出现了一群老鼠！")
			.t("$shit.name", "马桶精灵")
			.t("$shit.desc", "住在下水道的精灵，与腐坏和垃圾一起生活。")
			.t("$livemoss.name", "寄生苔藓")
			.t("$livemoss.desc", "扭曲的植物占据了死去老鼠的躯体，并向外抛洒寄生孢子。")
			.t("$patroluav.name", "巡逻无人机")
			.t("$patroluav.desc", "一种高科技无人机，曾被用于清理下水道垃圾。")
			.t("$vagrant.name", "流浪者")
			.t("$vagrant.desc", "住在下水道的流浪者，有着极强的恢复能力。")
			.t("$exvagrant.name", "感染流浪者")
			.t("$exvagrant.desc", "被源石感染的流浪者，有着极强的恢复能力和生命偷取能力，并会在死亡时污染周围环境。");
	}



	private SpsSewerMobs() { }

	private abstract static class DualLootMob extends LegacyDualLootMob {
		protected final void setupDualLoot(Object primary, float firstChance, Object secondary, float secondChance) {
			setupLegacyDualLoot(primary, firstChance, secondary, secondChance);
		}
	}

	static float combinedLegacyLootChance(float firstChance, float secondChance, int luckBonus) {
		return LegacyDualLootMob.combinedChance(firstChance, secondChance, luckBonus);
	}

	static float primaryLegacyLootShare(float firstChance, float secondChance, int luckBonus) {
		return LegacyDualLootMob.primaryShare(firstChance, secondChance, luckBonus);
	}

	public static class BrownBat extends DualLootMob {
		{ spriteClass = SpsSewerSprites.BrownBat.class; HP = HT = 20; defenseSkill = 1; baseSpeed = 2f; EXP = 1; maxLvl = 6; flying = true; setupDualLoot(Meat.class, 0.5f, Generator.Category.SEED, 0.05f); }
		@Override public int damageRoll() { return Random.NormalIntRange(1, 4); }
		@Override public int attackSkill(Char target) { return 5 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return 1; }
		@Override public void die(Object cause) {
			super.die(cause);
			if (Random.Int(5) == 0 && enemy != null && Dungeon.level != null) {
				for (Mob mob : Dungeon.level.mobs()) if (Random.Int(2) == 0) mob.beckon(enemy.pos);
				yell(Messages.get(this, "die"));
			}
		}
	}

	public static class DustElement extends Mob {
		{ spriteClass = SpsSewerSprites.DustElement.class; HP = HT = 35 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3); defenseSkill = 4 + legacyDepthAdjustment(1); EXP = 2; maxLvl = 8; loot = Generator.Category.SEED; lootChance = 0.5f; properties.add(Property.ELEMENT); resistances.add(DamageType.Earth.class); resistances.add(WandOfAcid.class); resistances.add(Ooze.class); resistances.add(WandOfSwamp.class); }
		@Override public int damageRoll() { return Random.NormalIntRange(2, 5 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 11 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 2); }
		@Override public int attackProc(Char enemy, int damage) {
			if (Random.Int(10) == 0) Buff.prolong(enemy, Blindness.class, Random.IntRange(3, 9));
			enemy.damage(damageRoll(), DamageType.EARTH_DAMAGE);
			return 0;
		}
		@Override public boolean add(Buff buff) {
			if (buff instanceof Roots) { if (isAlive() && HP < HT) HP = Math.min(HT, HP + HT / 10); return false; }
			if (buff instanceof Wet) { boolean inWater = Dungeon.level != null && Dungeon.level.insideMap(pos) && Dungeon.level.water[pos]; damage(Random.NormalIntRange(inWater ? HT / 2 : 1, inWater ? HT : HT * 2 / 3), buff); return false; }
			return super.add(buff);
		}
	}

	public static class RatBoss extends DualLootMob {
		private boolean spawnedRats;
		{ spriteClass = SpsSewerSprites.RatBoss.class; HP = HT = 50 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5); defenseSkill = 5 + legacyDepthAdjustment(2); EXP = 10; setupDualLoot(Generator.Category.BERRY, 0.5f, ScrollOfRegrowth.class, 0.1f); properties.add(Property.BEAST); properties.add(Property.BOSS); }
		@Override public Item SupercreateLoot() { return new SaveYourLife(); }
		@Override public int damageRoll() { return Random.NormalIntRange(2 + legacyDepthAdjustment(1), 8 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 11 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return legacyDepthAdjustment(1); }
		@Override public void notice() { super.notice(); if (!spawnedRats) { spawnRats(); spawnedRats = true; yell(Messages.get(this, "spawn")); } }
		private void spawnRats() {
			for (int offset : PathFinder.NEIGHBOURS8) { int cell = pos + offset; if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) { Rat rat = new Rat(); rat.pos = cell; rat.state = rat.HUNTING; GameScene.add(rat, 1f); } }
		}
		private static final String SPAWNED_RATS = "spawned_rats";
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(SPAWNED_RATS, spawnedRats); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); spawnedRats = b.getBoolean(SPAWNED_RATS); }
	}

	public static class Shit extends Mob {
		{ spriteClass = SpsSewerSprites.Shit.class; HP = HT = 30; defenseSkill = 0; EXP = 3; maxLvl = 18; loot = ShitBall.class; lootChance = 1f; }
		@Override public int damageRoll() { return 1; }
		@Override public float attackDelay() { return 2.5f; }
		@Override protected boolean canAttack(Char enemy) { return buff(Locked.class) != null ? Dungeon.level.adjacent(pos, enemy.pos) : Dungeon.level.distance(pos, enemy.pos) <= 2; }
		@Override public int attackProc(Char enemy, int damage) { Buff.affect(enemy, BeOld.class).set(4f); return super.attackProc(enemy, damage); }
		@Override public int attackSkill(Char target) { return 10; }
		@Override public int drRoll() { return 0; }
	}

	public static class LiveMoss extends Mob {
		private boolean usedSpores;
		{ spriteClass = SpsSewerSprites.LiveMoss.class; HP = HT = 50 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3); defenseSkill = 5 + legacyDepthAdjustment(1); EXP = 5; maxLvl = 9; loot = Generator.Category.MEDICINE; lootChance = 0.1f; }
		@Override public int damageRoll() { return Random.NormalIntRange(3, 6 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 12 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 4); }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(3) == 0 && !usedSpores) { Buff.affect(enemy, GrowSeed.class).set(5f); usedSpores = true; } return super.attackProc(enemy, damage); }
		private static final String USED_SPORES = "used_spores";
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(USED_SPORES, usedSpores); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); usedSpores = b.getBoolean(USED_SPORES); }
	}

	public static class PatrolUAV extends Mob {
		{ spriteClass = SpsSewerSprites.PatrolUAV.class; HP = HT = 50 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3); defenseSkill = legacyDepthAdjustment(1); EXP = 5; maxLvl = 10; flying = true; loot = StoneOre.class; lootChance = 0.4f; state = WANDERING; properties.add(Property.MECH); immunities.add(Amok.class); immunities.add(Sleep.class); immunities.add(Terror.class); immunities.add(Burning.class); immunities.add(Vertigo.class); immunities.add(ElectriShock.class); immunities.add(WandOfLightning.class); }
		@Override public int damageRoll() { return Random.NormalIntRange(4, 7); }
		@Override public int attackSkill(Char target) { return 5; }
		@Override public int drRoll() { return Random.NormalIntRange(5, 10); }
		@Override public void die(Object cause) {
			super.die(cause);
			if (Dungeon.level == null) return;
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell) && !Dungeon.level.solid[cell]) {
					GameScene.add(Blob.seed(cell, 3, ElectriShock.class));
					if (sprite != null && Dungeon.level.heroFOV[cell]) CellEmitter.get(cell).burst(EnergyParticle.FACTORY, 5);
				}
			}
		}
	}

	public static class Vagrant extends DualLootMob {
		{ spriteClass = SpsSewerSprites.Vagrant.class; HP = HT = 80; defenseSkill = 8 + legacyDepthAdjustment(0); EXP = 5; maxLvl = 20; setupDualLoot(NutVegetable.class, 0.1f, Generator.Category.BERRY, 0.05f); state = PASSIVE; }
		@Override public int damageRoll() { return Random.NormalIntRange(1, 7 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 12; }
		@Override public int drRoll() { return Random.NormalIntRange(0, 3); }
		protected void regenerate(int amount) { if (HP < HT && buff(BeOld.class) == null) { HP += amount; if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1); } }
		protected boolean actAfterRegeneration() { return super.act(); }
		@Override protected boolean act() { regenerate(10); return actAfterRegeneration(); }
		@Override public void damage(int damage, Object src) { if (state == PASSIVE) state = HUNTING; super.damage(damage, src); }
		@Override public int defenseProc(Char enemy, int damage) { if (state == FLEEING) Dungeon.level.drop(new Dewdrop(), pos).sprite.drop(); return super.defenseProc(enemy, damage); }
	}

	public static class ExVagrant extends Vagrant {
		{ spriteClass = SpsSewerSprites.ExVagrant.class; HP = HT = 100; defenseSkill = 10 + legacyDepthAdjustment(0); EXP = 6; }
		@Override public int damageRoll() { return Random.NormalIntRange(5, 10 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 15; }
		@Override public int drRoll() { return Random.NormalIntRange(3, 6); }
		@Override protected boolean act() { regenerate(20); return super.act(); }
		@Override public int attackProc(Char enemy, int damage) { if (buff(BeOld.class) == null) HP = Math.min(HT, HP + damage); return super.attackProc(enemy, damage); }
		@Override public void die(Object cause) {
			super.die(cause);
			if (Dungeon.level == null) return;
			for (int offset : PathFinder.NEIGHBOURS8) { int cell = pos + offset; if (!Dungeon.level.insideMap(cell)) continue; Char ch = Actor.findChar(cell); if (ch != null && ch.isAlive()) Buff.affect(ch, StoneIce.class).level(5); }
			if (Dungeon.level.insideMap(pos) && Dungeon.level.heroFOV[pos]) {
				Sample.INSTANCE.play(pd.Assets.Sounds.BLAST);
			}
		}
	}
}
