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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Skull;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DwarfKingTombSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpsKingSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpsUndeadSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Comparator;

/** SPS-PD's original city king encounter, kept separate from Shattered's DwarfKing. */
public class King extends Mob {
	private static final int MAX_ARMY_SIZE = 5;
	private boolean nextPedestal = true;
	private int tombId = -1;

	{
		spriteClass = SpsKingSprite.class;
		HP = HT = 1500;
		EXP = 60;
		defenseSkill = 25;
		baseSpeed = 0.75f;
		properties.add(Property.DWARF);
		properties.add(Property.BOSS);
		resistances.add(ToxicGas.class);
		resistances.add(WandOfDisintegration.class);
		immunities.add(Paralysis.class);
		immunities.add(Vertigo.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(20, 38); }
	@Override public int attackSkill(Char target) { return 62; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 14); }

	private int pedestal() { return com.shatteredpixel.shatteredpixeldungeon.levels.SpsCityBossLevel.pedestal(nextPedestal); }
	private int undeadCount() {
		int count = 0;
		if (Dungeon.level != null) for (Mob mob : Dungeon.level.mobs) if (mob instanceof Undead && ((Undead) mob).ownerId == id() && mob.isAlive()) count++;
		return count;
	}
	private int maxArmySize() { return 1 + MAX_ARMY_SIZE * (HT - HP) / HT; }
	private boolean canTryToSummon() {
		if (Dungeon.level == null || undeadCount() >= maxArmySize()) return false;
		Char occupant = Actor.findChar(pedestal());
		return occupant == null || occupant == this;
	}

	@Override protected boolean getCloser(int target) { return super.getCloser(canTryToSummon() ? pedestal() : target); }
	@Override protected boolean canAttack(Char enemy) { return canTryToSummon() ? pos == pedestal() : Dungeon.level.adjacent(pos, enemy.pos); }
	@Override protected boolean doAttack(Char enemy) {
		if (canTryToSummon() && pos == pedestal()) {
			summon();
			spend(attackDelay());
			return true;
		}
		if (Actor.findChar(pedestal()) == enemy) nextPedestal = !nextPedestal;
		return super.doAttack(enemy);
	}

	@Override protected boolean act() {
		if (findTomb() == null) spawnTomb();
		if (HP < HT) {
			HP += 3;
			if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
		}
		return super.act();
	}

	private void spawnTomb() {
		if (Dungeon.level == null) return;
		int cell = 3 * 48 + 23;
		if (!Dungeon.level.insideMap(cell) || Actor.findChar(cell) != null) return;
		DwarfKingTomb tomb = new DwarfKingTomb();
		tomb.pos = cell; tomb.ownerId = id(); tombId = tomb.id(); GameScene.add(tomb);
	}
	private DwarfKingTomb findTomb() {
		Actor actor = Actor.findById(tombId);
		if (actor instanceof DwarfKingTomb && ((DwarfKingTomb) actor).isAlive()) return (DwarfKingTomb) actor;
		if (Dungeon.level != null) for (Mob mob : Dungeon.level.mobs) if (mob instanceof DwarfKingTomb && ((DwarfKingTomb) mob).ownerId == id() && mob.isAlive()) {
			tombId = mob.id(); return (DwarfKingTomb) mob;
		}
		return null;
	}

	private void summon() {
		nextPedestal = !nextPedestal;
		if (sprite != null) sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
		Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
		boolean[] passable = Dungeon.level.passable.clone();
		for (Actor actor : Actor.all()) if (actor instanceof Char && Dungeon.level.insideMap(((Char) actor).pos)) passable[((Char) actor).pos] = false;
		PathFinder.buildDistanceMap(pos, passable, maxArmySize());
		ArrayList<Integer> cells = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) if (cell != pos && PathFinder.distance[cell] < Integer.MAX_VALUE && Actor.findChar(cell) == null) cells.add(cell);
		cells.sort(Comparator.comparingInt(cell -> PathFinder.distance[cell]));
		int amount = Math.min(maxArmySize() - undeadCount(), cells.size());
		for (int i = 0; i < amount; i++) {
			Undead undead = new Undead(); undead.ownerId = id(); undead.pos = cells.get(i); undead.state = undead.HUNTING; GameScene.add(undead);
			com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation.appear(undead, undead.pos);
		}
		yell(Messages.get(this, "arise"));
		if (HP < HT) HP += Random.Int(1, HT - HP);
	}

	@Override public void notice() { super.notice(); BossHealthBar.assignBoss(this); yell(Messages.get(this, "meeting")); }
	@Override public void die(Object cause) {
		DwarfKingTomb tomb = findTomb();
		int center = tomb == null ? pos : tomb.pos;
		int targetTombId = tomb == null ? -1 : tomb.id();
		yell(Messages.get(this, "cannot"));
		super.die(cause);
		com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.RockCode.dropForPerformer(
				new com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.Zshield());
		if (tomb != null) { BossHealthBar.assignBoss(tomb); DwarfLich.spawnAround(center, targetTombId); }
		else SpsCityBossRewards.grant(center, 1000, 2000, rareLoot(), commonLoot());
	}

	private static final String PEDESTAL = "pedestal", TOMB_ID = "tomb_id";
	@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(PEDESTAL, nextPedestal); b.put(TOMB_ID, tombId); }
	@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); nextPedestal = b.getBoolean(PEDESTAL); tombId = b.getInt(TOMB_ID); if (state != SLEEPING) BossHealthBar.assignBoss(this); }

	public static class Undead extends Mob {
		int ownerId = -1;
		{ spriteClass = SpsUndeadSprite.class; HP = HT = 100; defenseSkill = 15; EXP = 0; state = WANDERING; properties.add(Property.UNDEAD); properties.add(Property.BOSS); immunities.add(Paralysis.class); }
		@Override public int damageRoll() { return Random.NormalIntRange(12, 16); }
		@Override public int attackSkill(Char target) { return 49; }
		@Override public int drRoll() { return 5; }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(MAX_ARMY_SIZE) == 0) Buff.prolong(enemy, Paralysis.class, 1f); return damage; }
		@Override public void damage(int damage, Object source) {
			super.damage(damage, source);
			if (source instanceof ToxicGas) ((ToxicGas) source).clear(pos);
		}
		@Override public void die(Object cause) {
			super.die(cause);
			if (Dungeon.level != null && Dungeon.level.heroFOV != null
					&& Dungeon.level.insideMap(pos) && Dungeon.level.heroFOV[pos]) {
				Sample.INSTANCE.play(Assets.Sounds.BONES);
			}
		}
		private static final String OWNER_ID = "owner_id";
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(OWNER_ID, ownerId); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); ownerId = b.getInt(OWNER_ID); }
	}

	public static class DwarfKingTomb extends Mob {
		int ownerId = -1;
		{ spriteClass = DwarfKingTombSprite.class; HP = HT = 1000; defenseSkill = 5; EXP = 10; state = PASSIVE; alignment = Alignment.NEUTRAL; loot = StoneOre.class; lootChance = 0.05f; properties.add(Property.UNKNOW); properties.add(Property.BOSS); }
		@Override public void beckon(int cell) { }
		@Override public boolean add(Buff buff) { return false; }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
		private King owner() { Actor actor = Actor.findById(ownerId); return actor instanceof King ? (King) actor : null; }
		@Override public void damage(int damage, Object src) { if (owner() != null && owner().isAlive()) yell(Messages.get(this, "impossible")); else super.damage(damage, src); }
		@Override public void die(Object cause) {
			super.die(cause);
			TombCleanup cleanup = new TombCleanup();
			for (Mob mob : new ArrayList<>(Dungeon.level.mobs)) {
				if ((mob instanceof Undead && ((Undead) mob).ownerId == ownerId) || (mob instanceof DwarfLich && ((DwarfLich) mob).tombId == id())) mob.die(cleanup);
			}
			SpsCityBossRewards.grant(pos, 1000, 2000, rareLoot(), commonLoot());
		}
		private static final String OWNER_ID = "owner_id";
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(OWNER_ID, ownerId); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); ownerId = b.getInt(OWNER_ID); }
	}

	static final class TombCleanup { }

	static Item rareLoot() { return new ChaliceOfBlood().identify(); }
	static Item commonLoot() { return new Skull(5); }
}
