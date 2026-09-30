/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import com.badlogic.gdx.Gdx;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SwampGas;
import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.actors.blobs.damageblobs.IceEffectDamage;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.CountDown;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Wet;
import pd.effects.Wound;
import pd.items.Gold;
import pd.items.Generator;
import pd.items.RedDewdrop;
import pd.items.StoneOre;
import pd.items.food.vegetable.NutVegetable;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.potions.PotionOfToxicGas;
import pd.items.quest.DarkGold;
import pd.items.weapon.melee.Spork;
import pd.items.weapon.melee.special.TekkoKagi;
import pd.items.wands.Wand;
import pd.items.weapon.missiles.throwing.Boomerang;
import pd.items.quest.AdventureJournal;
import pd.levels.ThiefCatchLevel;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.SpsPrisonSprites;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;

public final class SpsPrisonMobs {
	private SpsPrisonMobs() { }

	public static class GhostPhoto extends Mob {
		{ spriteClass = SpsPrisonSprites.GhostPhoto.class; HP = HT = 100 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5); defenseSkill = 5 + legacyDepthAdjustment(0); flying = true; EXP = 3; maxLvl = 20; loot = RedDewdrop.class; lootChance = 0.1f; }
		@Override public int damageRoll() { return 0; }
		@Override public float attackDelay() { return 0.5f; }
		@Override public int attackSkill(Char target) { return 25; }
		@Override public int drRoll() { return 0; }
		@Override public int attackProc(Char enemy, int damage) { Buff.affect(enemy, Wet.class, Wet.DURATION); return damage; }
		@Override public void die(Object cause) {
			super.die(cause);
			for (int offset : PathFinder.NEIGHBOURS9) { int cell = pos + offset; if (Dungeon.level.insideMap(cell) && !Dungeon.level.solid[cell]) GameScene.add(Blob.seed(cell, 8, IceEffectDamage.class)); }
		}
	}

	public static class Assassin extends Mob {
		{ spriteClass = SpsPrisonSprites.Assassin.class; baseSpeed = 2f; HP = HT = 80 + 5 * Random.NormalIntRange(2, 5); EXP = 10; maxLvl = 18; defenseSkill = 15; loot = StoneOre.class; lootChance = 0.2f; properties.add(Property.HUMAN); resistances.add(ToxicGas.class); resistances.add(Poison.class); }
		@Override public int damageRoll() { return Random.NormalIntRange(10, 23); }
		@Override public int attackSkill(Char target) { return 25; }
		@Override public int drRoll() { return Random.NormalIntRange(0, 5); }
		@Override public float attackDelay() { return 0.75f; }
		@Override protected boolean canAttack(Char enemy) { return buff(Locked.class) != null ? Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy) : new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos; }
		@Override public pd.items.Item SupercreateLoot() { return new TekkoKagi(); }
		@Override public void die(Object cause) {
			int cell = pos;
			super.die(cause);
			recordKill(cell);
		}
		static void recordKill(int cell) {
			Statistics.assassinsKilled++;
			if (Gdx.app != null) GLog.w(Messages.get(Mob.class, "killcount", Statistics.assassinsKilled));
			if (Statistics.assassinsKilled == 100 && Dungeon.level != null && Dungeon.level.insideMap(cell)) {
				pd.items.Heap heap = Dungeon.level.drop(new TekkoKagi(), cell);
				if (heap.sprite != null) heap.sprite.drop();
			}
		}
		public static Assassin spawnAt(int cell) { if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null; Assassin mob = new Assassin(); mob.pos = cell; mob.state = mob.HUNTING; GameScene.add(mob, 2f); return mob; }
	}

	public static class TrollWarrior extends Mob {
		private boolean enraged;
		{ spriteClass = SpsPrisonSprites.TrollWarrior.class; baseSpeed = 1.2f; HP = HT = 80 + 5 * Random.NormalIntRange(2, 5); EXP = 10; maxLvl = 20; defenseSkill = 15; loot = StoneOre.class; lootChance = 0.2f; resistances.add(ToxicGas.class); resistances.add(Poison.class); }
		@Override protected boolean act() { if (HP < HT && !enraged) { Buff.affect(this, AttackUp.class, 8f).level(20); Buff.affect(this, DefenceUp.class, 8f).level(80); enraged = true; yell(Messages.get(this, "angry")); } return super.act(); }
		@Override public int damageRoll() { return Random.NormalIntRange(15, 30); }
		@Override public int attackSkill(Char target) { return 30; }
		@Override public int drRoll() { return Random.NormalIntRange(5, 7); }
		@Override public float attackDelay() { return 1.2f; }
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put("enraged", enraged); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); enraged = b.getBoolean("enraged"); }
	}

	public static class FireRabbit extends Mob {
		private Ballistica beam;
		{ spriteClass = SpsPrisonSprites.FireRabbit.class; HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5); defenseSkill = 8 + legacyDepthAdjustment(0); EXP = 5; maxLvl = 20; loot = PotionOfLiquidFlame.class; lootChance = 0.1f; properties.add(Property.FIERY); immunities.add(Locked.class); immunities.add(Burning.class); immunities.add(FireEffectDamage.class); }
		@Override protected boolean doAttack(Char enemy) { if (beam != null) for (int cell : beam.subPath(1, beam.dist)) GameScene.add(Blob.seed(cell, 5, FireEffectDamage.class)); return super.doAttack(enemy); }
		@Override public int damageRoll() { return Random.NormalIntRange(6, 8 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 12; }
		@Override public int drRoll() { return Random.NormalIntRange(5, 10); }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(3) == 0) { Buff.affect(enemy, Burning.class).reignite(enemy, 4f); yell(Messages.get(this, "yell")); } if (Dungeon.level.distance(pos, enemy.pos) == 3) { Buff.affect(enemy, ArmorBreak.class, 5f).level(20); return 0; } return damage; }
		@Override protected boolean canAttack(Char enemy) { beam = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE); return beam.collisionPos == enemy.pos && Dungeon.level.distance(pos, enemy.pos) <= 3; }
	}

	public static class BambooMob extends Mob {
		{
			spriteClass = SpsPrisonSprites.Bamboo.class; HP = HT = 40; defenseSkill = 0;
			EXP = 1; loot = NutVegetable.class; lootChance = 0.4f;
			properties.add(Property.PLANT); resistances.add(Roots.class);
			weaknesses.add(ToxicGas.class); weaknesses.add(Ooze.class);
			weaknesses.add(SwampGas.class); weaknesses.add(Wand.class);
			weaknesses.add(Poison.class);
		}
		@Override public pd.items.Item SupercreateLoot() { return Generator.random(Generator.Category.ARMOR); }
		@Override public int damageRoll() { return Random.NormalIntRange(4, 12); }
		@Override public int attackSkill(Char target) { return 15; }
		@Override public int drRoll() { return Random.NormalIntRange(1, 3); }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(3) == 0) Buff.affect(this, DefenceUp.class, 3f).level(20); return super.attackProc(enemy, damage); }
		@Override public int defenseProc(Char enemy, int damage) { int reflected = Random.IntRange(0, Math.max(0, damage)) - enemy.drRoll(); if (reflected > 0) { enemy.damage(reflected, this); Wound.hit(enemy); } return super.defenseProc(enemy, damage); }
		@Override protected boolean getCloser(int target) { return true; }
		@Override protected boolean getFurther(int target) { return true; }
	}

	public static class GoldCollector extends Mob {
		private boolean stoleReserve;
		{ spriteClass = SpsPrisonSprites.GoldCollector.class; HP = HT = 75 + Math.min(425, Dungeon.gold / 10); defenseSkill = 5; baseSpeed = 2f; }
		@Override public int damageRoll() { return Random.IntRange(5, 19); }
		@Override public int attackSkill(Char target) { return Dungeon.gold / 100; }
		@Override public int drRoll() { return 0; }
		@Override public int attackProc(Char enemy, int damage) { if (enemy == Dungeon.hero) { if (!stoleReserve) { int amount = Math.max(0, Dungeon.gold / 10); stoleReserve = true; Buff.affect(this, ShieldArmor.class).level(Dungeon.gold / 20); Dungeon.gold = Math.max(0, Dungeon.gold - amount); if (enemy.sprite != null) enemy.sprite.showStatus(pd.sprites.CharSprite.NEUTRAL, "-" + amount); } else { int amount = Math.min(10, Math.max(0, Dungeon.gold)); Dungeon.gold -= amount; if (amount > 0) Dungeon.level.drop(new Gold(amount), enemy.pos).sprite.drop(); } } return damage; }
		@Override public void die(Object cause) { for (int i = 0, n = Random.Int(5); i < n; i++) Dungeon.level.drop(new DarkGold(), pos).sprite.drop(); super.die(cause); }
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put("stole_reserve", stoleReserve); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); stoleReserve = b.getBoolean("stole_reserve"); }
	}

	public static class Zombie extends Mob {
		{ spriteClass = SpsPrisonSprites.Zombie.class; HP = HT = 70 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 7); defenseSkill = 9 + legacyDepthAdjustment(1); baseSpeed = 2f; EXP = 7; maxLvl = 18; state = WANDERING; loot = PotionOfToxicGas.class; lootChance = 0.1f; properties.add(Property.UNDEAD); resistances.add(ToxicGas.class); }
		@Override public float attackDelay() { return 2f; }
		@Override public int damageRoll() { return Random.NormalIntRange(10 + legacyDepthAdjustment(0), 20 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 15 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(3, 8); }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(3) == 0) Buff.affect(enemy, BeOld.class).set(20f); return damage; }
		@Override public pd.items.Item SupercreateLoot() { return new pd.items.UnBlessAnkh(); }
	}

	public static class BanditKing extends Mob {
		{ spriteClass = SpsPrisonSprites.BanditKing.class; HP = HT = 300; EXP = 10; maxLvl = 25; flying = true; defenseSkill = 20; lootChance = 0.2f; properties.add(Property.ELF); properties.add(Property.MINIBOSS); if (grantsSpork()) Dungeon.sporkAvailable = false; }
		public static boolean grantsSpork() { return Dungeon.legacyDepth() < 25; }
		@Override public pd.items.Item createLoot() { return new Boomerang(); }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return Random.NormalIntRange(10, 20); }
		@Override public float speed() { return 2f; }
		@Override public int attackProc(Char enemy, int damage) { if (enemy.buff(CountDown.class) == null) { Buff.affect(enemy, CountDown.class); state = FLEEING; } return damage; }
		@Override public void die(Object cause) { int cell = pos; boolean pursuit = Dungeon.level instanceof ThiefCatchLevel; super.die(cause); if (pursuit) { AdventureJournal.complete(18); Dungeon.level.unseal(); GameScene.bossSlain(); } if (grantsSpork()) { yell(Messages.get(this, "die")); GLog.n(Messages.get(this, "dis")); if (!Dungeon.LimitedDrops.SPS_SPORK.dropped()) { pd.items.Heap heap = Dungeon.level.drop(new Spork(), cell); if (heap != null && heap.sprite != null) heap.sprite.drop(); Dungeon.LimitedDrops.SPS_SPORK.drop(); Dungeon.sporkAvailable = false; yell(Messages.get(this, "spork")); } } }
	}
}
