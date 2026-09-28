/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.GlassTotem;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DungeonBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.Harp;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.ReedPipe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.WarDrum;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatteryTombSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LichDancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SeekingBombSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD's city boss which dances between monuments and leaves protective tombs. */
public class LichDancer extends Mob {
	private int breaks;

	{
		spriteClass = LichDancerSprite.class;
		HP = HT = 1000;
		EXP = 60;
		defenseSkill = 25;
		baseSpeed = 1f;
		properties.add(Property.UNDEAD);
		properties.add(Property.MAGICER);
		properties.add(Property.BOSS);
		resistances.add(ToxicGas.class);
		resistances.add(EnchantmentDark.class);
		resistances.add(WandOfDisintegration.class);
		immunities.add(Paralysis.class);
		immunities.add(Vertigo.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(20, 38); }
	@Override public int attackSkill(Char target) { return 65; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 20); }

	@Override
	protected boolean act() {
		if (3 - breaks > 4 * HP / HT) {
			breaks++;
			jumpAndSpawnTomb();
			return true;
		}
		if (breaks > 0 && enemy != null && Random.Int(5) == 0) spawnLinkBomb(enemy.pos);
		if (HP < HT) HP += 3;
		return super.act();
	}

	private void jumpAndSpawnTomb() {
		if (Dungeon.level == null) return;
		ArrayList<Integer> destinations = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			int terrain = Dungeon.level.map[cell];
			if ((terrain == Terrain.WELL || terrain == Terrain.STATUE_SP)
					&& (Actor.findChar(cell) == null || cell == pos)) destinations.add(cell);
		}
		if (!destinations.isEmpty()) {
			int oldPos = pos;
			int newPos = Random.element(destinations);
			if (sprite != null) sprite.move(oldPos, newPos);
			move(newPos, false);
			if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[newPos]) {
				CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
				Sample.INSTANCE.play(Assets.Sounds.PUFF);
			}
		}
		spawnTomb();
		spend(1f / speed());
	}

	private void spawnTomb() {
		if (Dungeon.level == null) return;
		ArrayList<Integer> pedestals = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.map[cell] == Terrain.PEDESTAL && Actor.findChar(cell) == null) pedestals.add(cell);
		}
		if (pedestals.isEmpty()) return;
		BatteryTomb tomb = new BatteryTomb();
		tomb.pos = Random.element(pedestals);
		GameScene.add(tomb);
	}

	private void spawnLinkBomb(int center) {
		if (Dungeon.level == null) return;
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.distance(center, cell) <= 1
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])
					&& Actor.findChar(cell) == null) cells.add(cell);
		}
		if (cells.isEmpty()) return;
		LinkBomb bomb = new LinkBomb();
		bomb.pos = Random.element(cells);
		GameScene.add(bomb);
		ScrollOfTeleportation.appear(bomb, bomb.pos);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0) Buff.prolong(enemy, Vertigo.class, 3f);
		enemy.damage(damageRoll() / 2, new EnergyDamage());
		return damage / 2;
	}

	private boolean hasBattery() {
		if (Dungeon.level == null) return false;
		for (Mob mob : Dungeon.level.mobs) if (mob instanceof BatteryTomb && mob.isAlive()) return true;
		return false;
	}

	@Override public void damage(int damage, Object src) {
		if (hasBattery()) damage = Random.Int(10);
		super.damage(damage, src);
	}

	@Override public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice"));
	}

	@Override public void die(Object cause) {
		super.die(cause);
		com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.RockCode.dropForPerformer(
				new com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.Lbox());
		SpsCityBossRewards.grant(pos, 1000, 2000, rareLoot(), commonLoot());
		yell(Messages.get(this, "die"));
	}

	public static Item rareLoot() { return new GlassTotem().identify(); }
	static Item commonLoot() { return Generator.random(Generator.Category.MUSICWEAPON); }

	private static final String BREAKS = "breaks";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(BREAKS, breaks); }
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		breaks = bundle.getInt(BREAKS);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	public static final class EnergyDamage { }

	public static class BatteryTomb extends Mob {
		{
			spriteClass = BatteryTombSprite.class;
			HP = HT = 200;
			defenseSkill = 0;
			EXP = 10;
			state = PASSIVE;
			alignment = Alignment.NEUTRAL;
			loot = StoneOre.class;
			lootChance = 0.05f;
			properties.add(Property.MECH);
			properties.add(Property.MINIBOSS);
		}

		@Override public void beckon(int cell) { }
		@Override public boolean add(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff buff) { return false; }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
		@Override public void damage(int damage, Object src) { super.damage(Math.min(50, damage), src); }

		@Override protected boolean act() {
			if (Random.Int(20) == 0 && Dungeon.level != null && Dungeon.level.mobs.size() < 6) {
				for (int offset : PathFinder.NEIGHBOURS4) {
					int cell = pos + offset;
					if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
					ManySkeleton.spawnAt(cell);
				}
			}
			spend(TICK);
			return true;
		}
	}

	public static class LinkBomb extends Mob {
		private int bombTime = 3;
		{
			spriteClass = SeekingBombSprite.class;
			HP = HT = 1;
			defenseSkill = 0;
			EXP = 0;
			state = PASSIVE;
			properties.add(Property.MECH);
			properties.add(Property.MINIBOSS);
		}
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
		@Override protected boolean act() {
			yell(Integer.toString(bombTime) + "!");
			if (bombTime-- <= 0) {
				new DungeonBomb().explode(pos);
				destroy();
				if (sprite != null) sprite.die();
				return true;
			}
			return super.act();
		}
		private static final String BOMB_TIME = "bomb_time";
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(BOMB_TIME, bombTime); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); bombTime = bundle.getInt(BOMB_TIME); }
	}
}
