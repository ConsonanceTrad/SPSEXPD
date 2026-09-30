/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.quest;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Vulnerable;
import pd.actors.hero.Hero;
import pd.actors.mobs.Bee;
import pd.actors.mobs.Crab;
import pd.actors.mobs.ElderAvatar;
import pd.actors.mobs.King;
import pd.actors.mobs.LichDancer;
import pd.actors.mobs.Scorpio;
import pd.actors.mobs.Spinner;
import pd.actors.mobs.Swarm;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.weapon.melee.MeleeWeapon;
import pd.levels.Level;
import pd.levels.MiningLevel;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.ui.AttackIndicator;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.Callback;
import render.utils.Random;

import java.util.ArrayList;

public class Pickaxe extends MeleeWeapon {

	public static final String AC_MINE = "MINE";
	public static final float TIME_TO_MINE = 2f;
	
	{
		image = ItemSpriteSheet.PICKAXE;

		levelKnown = true;
		
		unique = true;
		bones = false;
		defaultAction = AC_MINE;

		tier = 2;
	}

	@Override
	public int min(int lvl) {
		return 10 + 3 * lvl;
	}

	@Override
	public int max(int lvl) {
		return 22 + 3 * lvl;
	}
	
	@Override
	public int STRReq(int lvl) {
		return 14;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add(AC_MINE);
		if (Dungeon.level instanceof MiningLevel){
			actions.remove(AC_DROP);
			actions.remove(AC_THROW);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_MINE.equals(action)) {
			if (!mine(hero)) GLog.w(Messages.get(this, "no_vein"));
		} else {
			super.execute(hero, action);
		}
	}

	public static boolean legacyMiningDepth(int depth) {
		return depth >= 11 && depth <= 15 || depth == 32;
	}

	public boolean mine(final Hero hero) {
		if (hero == null || Dungeon.level == null || !legacyMiningDepth(Dungeon.legacyDepth())) return false;
		for (int offset : PathFinder.NEIGHBOURS8) {
			final int cell = hero.pos + offset;
			if (!Dungeon.level.insideMap(cell) || Dungeon.level.map[cell] != Terrain.WALL_DECO) continue;

			hero.spend(TIME_TO_MINE);
			hero.busy();
			Callback complete = new Callback() {
				@Override
				public void call() {
					if (hero.sprite != null) {
						CellEmitter.center(cell).burst(Speck.factory(Speck.STAR), 7);
						Sample.INSTANCE.play(Assets.Sounds.EVOKE);
					}
					Level.set(cell, Terrain.WALL, Dungeon.level);
					GameScene.updateMap(cell);

					DarkGold gold = new DarkGold();
					if (gold.doPickUp(hero)) {
						GLog.i(Messages.get(hero, "you_now_have", gold.name()));
					} else {
						Heap heap = Dungeon.level.drop(gold, hero.pos);
						if (heap.sprite != null) heap.sprite.drop();
					}

					Hunger hunger = hero.buff(Hunger.class);
					if (hunger != null && !hunger.isStarving()) {
						hunger.satisfy(-10);
						BuffIndicator.refreshHero();
					}
					if (hero.sprite != null) hero.onOperateComplete();
				}
			};
			if (hero.sprite != null) hero.sprite.attack(cell, complete);
			else complete.call();
			return true;
		}
		return false;
	}

	@Override
	public boolean keptThroughLostInventory() {
		//pickaxe is always kept when it's needed for the mining level
		return super.keptThroughLostInventory() || Dungeon.level instanceof MiningLevel;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender instanceof King.DwarfKingTomb
				|| defender instanceof ElderAvatar.Obelisk
				|| defender instanceof LichDancer.BatteryTomb) {
			defender.damage(Random.Int(100, 200), this);
		}
		switch (Random.Int(10)) {
			case 1:
				Buff.affect(defender, Bleeding.class).set(10f);
				break;
			case 2:
				Buff.affect(defender, Ooze.class).set(10f);
				break;
			case 3:
				Buff.affect(defender, Poison.class).set(Random.Int(7, 10));
				break;
			default:
				break;
		}
		return super.proc(attacker, defender, damage);
	}
	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(this, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				int damageBoost = 0;
				if (Char.hasProp(enemy, Char.Property.INORGANIC)
						|| enemy instanceof Swarm
						|| enemy instanceof Bee
						|| enemy instanceof Crab
						|| enemy instanceof Spinner
						|| enemy instanceof Scorpio) {
					//+(8+2*lvl) damage, equivalent to +100% damage
					damageBoost = augment.damageFactor(8 + 2*buffedLvl());
				}
				beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);
				if (hero.attack(enemy, 1, damageBoost, Char.INFINITE_ACCURACY)) {
					if (enemy.isAlive()) {
						Buff.affect(enemy, Vulnerable.class, 3f);
					} else {
						onAbilityKill(hero, enemy);
					}
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}
				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				afterAbilityUsed(hero);
			}
		});
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = 8 + 2*buffedLvl();
		return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 8 + 2*level;
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

}
