/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.blobs.ToxicGas;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.quest.AdventureJournal;
import pd.items.Heap;
import pd.items.OrbOfZot;
import pd.levels.Terrain;
import pd.levels.traps.SummoningTrap;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ShadowYogSprite;
import pd.utils.GLog;
import render.utils.Bundle;
import render.utils.Random;
import render.noosa.particles.Emitter;

import java.util.ArrayList;

public class ShadowYog extends Mob {

	private int breaks;

	{
		spriteClass = ShadowYogSprite.class;
		int heroLevel = Dungeon.hero == null ? 1 : Math.max(1, Dungeon.hero.lvl);
		HP = HT = 50 * heroLevel;
		baseSpeed = 2f;
		defenseSkill = 32;
		EXP = 100;
		state = PASSIVE;
		properties.add(Property.UNKNOW);
		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Burning.class);
		immunities.add(ToxicGas.class);
		immunities.add(Paralysis.class);
		immunities.add(Vertigo.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(43, 83); }
	@Override public int attackSkill(Char target) { return 50; }
	@Override public int drRoll() { return 25; }
	@Override public void beckon(int cell) { }

	@Override
	protected boolean act() {
		if (5 - breaks > 6 * HP / HT) {
			breaks++;
			ArrayList<Integer> cells = new ArrayList<>();
			for (int i = 0; i < Dungeon.level.length(); i++) {
				if (Dungeon.level.passable[i] && Actor.findChar(i) == null) cells.add(i);
			}
			if (!cells.isEmpty()) {
				int oldPos = pos;
				move(Random.element(cells));
				CellEmitter.get(oldPos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
				if (sprite != null) sprite.place(pos);
				GLog.n(Messages.get(this, "blink"));
			}
			spend(TICK);
			return true;
		}
		return super.act();
	}

	@Override
	public void damage(int damage, Object source) {
		if (Dungeon.level != null) {
			ArrayList<Integer> inactive = new ArrayList<>();
			for (int cell = 0; cell < Dungeon.level.length(); cell++) {
				if (Dungeon.level.heroFOV[cell] && Dungeon.level.passable[cell]
						&& Dungeon.level.map[cell] == Terrain.INACTIVE_TRAP) inactive.add(cell);
			}
			Random.shuffle(inactive);
			for (int i = 0; i < Math.min(4, inactive.size()); i++) {
				int cell = inactive.get(i);
				Dungeon.level.setTrap(new SummoningTrap().reveal(), cell);
				Dungeon.level.map[cell] = Terrain.TRAP;
				GameScene.updateMap(cell);
			}
			int heroLevel = Dungeon.hero == null ? 1 : Math.max(1, Dungeon.hero.lvl);
			if (Dungeon.level.mobs.size() < heroLevel * 2) Fiend.spawnAroundChance(pos);
		}
		super.damage(damage, source);
	}

	@Override
	public void die(Object cause) {
		int deathPos = pos;
		if (sprite == null || Dungeon.hero == null) {
			HP = 0;
			Actor.remove(this);
			if (Dungeon.level != null) Dungeon.level.mobs.remove(this);
		} else super.die(cause);
		if (Dungeon.level == null) return;
		boolean anotherAlive = false;
		for (Mob mob : Dungeon.level.mobs) {
			if (mob != this && mob instanceof ShadowYog && mob.isAlive()) {
				anotherAlive = true;
				break;
			}
		}
		if (!anotherAlive) {
			AdventureJournal.complete(9);
			Heap heap = Dungeon.level.drop(new OrbOfZot(), deathPos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
			Dungeon.level.unseal();
			if (sprite != null) GameScene.bossSlain();
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mob instanceof Fiend || mob instanceof GoldOrc) mob.die(cause);
			}
			if (sprite != null) {
				Emitter emitter = CellEmitter.get(deathPos);
				if (emitter != null) emitter.burst(Speck.factory(Speck.LIGHT), 12);
			}
			if (sprite != null) yell(Messages.get(this, "die"));
		}
	}

	@Override
	public void notice() {
		super.notice();
		yell(Messages.get(this, "illusion"));
	}

	private static final String BREAKS = "breaks";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(BREAKS, breaks); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); breaks = bundle.getInt(BREAKS); }
}
