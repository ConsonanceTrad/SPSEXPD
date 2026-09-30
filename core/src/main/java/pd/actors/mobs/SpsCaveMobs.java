/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dry;
import pd.actors.buffs.FrostIce;
import pd.actors.buffs.HiddenShadow;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.WatchOut;
import pd.actors.damagetype.DamageType;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.SparkParticle;
import pd.items.Generator;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.food.WaterItem;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.SpsCaveSprites;
import render.noosa.Camera;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;

import java.util.ArrayList;

public final class SpsCaveMobs {
	private SpsCaveMobs() { }

	private abstract static class DualLootMob extends LegacyDualLootMob {
		protected final void setupDualLoot(Object first, float firstChance, Object second, float secondChance) {
			setupLegacyDualLoot(first, firstChance, second, secondChance);
		}
	}

	public static class GnollShaman extends DualLootMob {
		private static final float ZAP_TIME = 2f;
		{ spriteClass = SpsCaveSprites.GnollShaman.class; HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5); defenseSkill = 15 + legacyDepthAdjustment(0); EXP = 10; maxLvl = 25; setupDualLoot(Generator.Category.SCROLL, 0.15f, pd.items.wands.WandOfLightning.class, 0.02f); }
		@Override public int damageRoll() { return Random.NormalIntRange(14, 20 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 16 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 4); }
		@Override public int attackProc(Char enemy, int damage) { enemy.damage(damageRoll() / 2, DamageType.SHOCK_DAMAGE); return damage / 2; }
		@Override protected boolean canAttack(Char enemy) { return buff(Silent.class) != null ? Dungeon.level.adjacent(pos, enemy.pos) : new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos; }
		@Override protected boolean doAttack(Char enemy) {
			if (Dungeon.level.distance(pos, enemy.pos) <= 1) return super.doAttack(enemy);
			boolean visible = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[enemy.pos];
			if (visible) sprite.zap(enemy.pos);
			spend(ZAP_TIME);
			if (hit(this, enemy, true)) {
				int damage = Random.IntRange(2 + legacyDepthAdjustment(0), 11 + legacyDepthAdjustment(3));
				if (Dungeon.level.water[enemy.pos] && !enemy.flying) damage = Math.round(damage * 1.5f);
				enemy.damage(damage, DamageType.SHOCK_DAMAGE);
				if (enemy.sprite != null) { enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3); enemy.sprite.flash(); }
				if (enemy == Dungeon.hero && !enemy.isAlive()) { Dungeon.fail(this); Camera.main.shake(2, 0.3f); }
			} else if (enemy.sprite != null) enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
			return !visible;
		}
		public void onZapComplete() { next(); }
	}

	public static class SandMob extends Mob {
		private boolean hidden;
		{ spriteClass = SpsCaveSprites.SandMob.class; HP = HT = 90 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5); defenseSkill = 5 + legacyDepthAdjustment(0); baseSpeed = 0.5f; EXP = 10; maxLvl = 25; loot = WaterItem.class; lootChance = 0.5f; }
		@Override public int damageRoll() { return Random.NormalIntRange(17, 25 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 10 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 10); }
		@Override public int attackProc(Char enemy, int damage) { if (!hidden && enemy == Dungeon.hero) { hidden = true; Buff.affect(this, HiddenShadow.class, 6f); } if (Random.Int(5) == 0) Buff.prolong(enemy, Dry.class, 10f); if (Random.Int(5) == 0) Buff.prolong(enemy, Slow.class, 10f); return damage; }
		@Override public boolean add(Buff buff) { if (buff instanceof Dry) { if (HP < HT) HP = Math.min(HT, HP + HT / 10); return false; } if (buff instanceof Vertigo) { damage(Random.NormalIntRange(Dungeon.level.water[pos] ? HT/2 : 1, Dungeon.level.water[pos] ? HT : HT*2/3), buff); return false; } return super.add(buff); }
		@Override public void die(Object cause) { spawnMinions(pos); super.die(cause); }
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put("hidden", hidden); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); hidden = b.getBoolean("hidden"); }
		protected MiniSand newMiniSand() { return new MiniSand(); }
		private void spawnMinions(int center) { for (int offset : PathFinder.NEIGHBOURS4) { int cell = center + offset; if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) { MiniSand mob = newMiniSand(); mob.pos = cell; mob.state = mob.HUNTING; GameScene.add(mob, 1f); } } }
		public static class MiniSand extends Mob {
			{ spriteClass = SpsCaveSprites.SandMob.class; HP = HT = 45 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5); defenseSkill = 25; EXP = 0; state = WANDERING; }
			@Override public int damageRoll() { return Random.NormalIntRange(15, 20 + legacyDepthAdjustment(0)); }
			@Override public int attackSkill(Char target) { return 30 + legacyDepthAdjustment(0); }
			@Override public int drRoll() { return 0; }
			@Override public int attackProc(Char enemy, int damage) { if (Random.Int(5) == 0) Buff.prolong(enemy, Dry.class, 10f); if (Random.Int(5) == 0) Buff.prolong(enemy, Slow.class, 10f); return damage; }
			@Override public boolean add(Buff buff) { if (buff instanceof Vertigo) { damage(Random.NormalIntRange(Dungeon.level.water[pos] ? HT/2 : 1, Dungeon.level.water[pos] ? HT : HT*2/3), buff); return false; } return super.add(buff); }
			@Override protected boolean spawnsNightmareVirusOnDeath() { return false; }
		}
	}

	public static class IceBug extends Mob {
		{ spriteClass = SpsCaveSprites.IceBug.class; HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5); defenseSkill = 15 + legacyDepthAdjustment(0); baseSpeed = 1.5f; EXP = 9; maxLvl = 25; loot = StoneOre.class; lootChance = 0.3f; properties.add(Property.ICY); immunities.add(FrostIce.class); }
		@Override public int damageRoll() { return Random.NormalIntRange(15, 20 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 16 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(2, 5); }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(5) == 0) Buff.affect(enemy, FrostIce.class).level(5); return damage; }
		public static IceBug spawnAt(int cell) { if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null; IceBug mob = new IceBug(); mob.pos = cell; mob.state = mob.HUNTING; GameScene.add(mob, 1f); return mob; }
	}

	public static class TimeKeeper extends Mob {
		private boolean escaped;
		{ spriteClass = SpsCaveSprites.TimeKeeper.class; HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5); defenseSkill = 15 + legacyDepthAdjustment(0); EXP = 10; maxLvl = 25; loot = Generator.Category.SCROLL; lootChance = 0.15f; }
		@Override public int damageRoll() { return Random.NormalIntRange(14, 20 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 16 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 4); }
		@Override public int attackProc(Char enemy, int damage) { enemy.damage(damageRoll(), DamageType.ENERGY_DAMAGE); if (Dungeon.level != null && enemy.isAlive() && Random.Int(5) == 1) ScrollOfTeleportation.teleportChar(enemy); return 0; }
		@Override public void damage(int damage, Object src) {
			if (!escaped && HP - damage < HT / 2) {
				escaped = true; damage = 0; Buff.affect(this, WatchOut.class, 200f);
				ArrayList<Integer> cells = new ArrayList<>();
				for (int cell = 0; cell < Dungeon.level.length(); cell++) if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null && (Dungeon.hero == null || !Dungeon.level.adjacent(cell, Dungeon.hero.pos))) cells.add(cell);
				if (!cells.isEmpty()) ScrollOfTeleportation.teleportToLocation(this, Random.element(cells));
				yell(Messages.get(this, "yell"));
				if (sprite != null && Dungeon.level.heroFOV[pos]) { CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6); Sample.INSTANCE.play(Assets.Sounds.PUFF); }
			}
			super.damage(damage, src);
		}
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put("escaped", escaped); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); escaped = b.getBoolean("escaped"); }
	}
}
