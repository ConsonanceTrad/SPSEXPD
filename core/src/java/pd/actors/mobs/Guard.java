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

package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Silent;
import pd.effects.Chains;
import pd.effects.Effects;
import pd.effects.Pushing;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.weapon.enchantments.EnchantmentDark2;
import pd.items.equipment.weapon.enchantments.EnchantmentDark;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.GuardSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public class Guard extends Mob {

	//they can only use their chains once
	private boolean chainsUsed = false;

	{
		spriteClass = GuardSprite.class;

		HP = HT = 75 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 7);
		defenseSkill = 9 + legacyDepthAdjustment(1);

		EXP = 10;
		maxLvl = 20;

		loot = Generator.Category.ARMOR;
		lootChance = 0.2f;

		properties.add(Property.HUMAN);

		immunities.add(EnchantmentDark.class);
		immunities.add(EnchantmentDark2.class);

		HUNTING = new Hunting();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(12 + legacyDepthAdjustment(0), 20 + legacyDepthAdjustment(3));
	}

	@Override
	public float attackDelay() {
		return 1.2f;
	}

	private boolean chain(int target){
		if (chainsUsed || enemy.properties().contains(Property.IMMOVABLE))
			return false;

		Ballistica chain = new Ballistica(pos, target, Ballistica.PROJECTILE);

		if (chain.collisionPos != enemy.pos
				|| chain.path.size() < 2
				|| Dungeon.level.pit[chain.path.get(1)])
			return false;
		else {
			int newPos = -1;
			for (int i : chain.subPath(1, chain.dist)){
				//find the closest position to the guard that's open for the target
				if (!Dungeon.level.solid[i] && Actor.findChar(i) == null
						&& (Dungeon.level.openSpace[i] || !Char.hasProp(enemy, Property.LARGE))){
					newPos = i;
					break;
				}
			}

			if (newPos == -1){
				return false;
			} else {
				final int newPosFinal = newPos;
				this.target = newPos;

				boolean visible = sprite != null && sprite.parent != null && enemy.sprite != null
						&& (sprite.visible || enemy.sprite.visible);
				if (visible) {
					yell(Messages.get(this, "scorpion"));
					new Item().throwSound();
					Sample.INSTANCE.play(Assets.Sounds.CHAINS);
					sprite.parent.add(new Chains(sprite.center(),
							enemy.sprite.destinationCenter(),
							Effects.Type.CHAIN,
							new Callback() {
						public void call() {
							Actor.add(new Pushing(enemy, enemy.pos, newPosFinal, new Callback() {
								public void call() {
									pullEnemy(enemy, newPosFinal);
								}
							}));
							next();
						}
					}));
				} else {
					pullEnemy(enemy, newPos);
				}
			}
		}
		chainsUsed = true;
		return true;
	}

	private void pullEnemy( Char enemy, int pullPos ){
		enemy.pos = pullPos;
		if (enemy.sprite != null) enemy.sprite.place(pullPos);
		Dungeon.level.occupyCell(enemy);
		Cripple.prolong(enemy, Cripple.class, 4f);
		if (enemy == Dungeon.hero) {
			Dungeon.hero.interrupt();
			Dungeon.observe();
			GameScene.updateFog();
		} else if (enemy.sprite != null) {
			enemy.sprite.visible = Dungeon.level.heroFOV[pullPos];
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		boolean heroKilled = legacyDeathBurst();
		if (Dungeon.level.heroFOV[pos]) Sample.INSTANCE.play(Assets.Sounds.BONES);
		if (heroKilled) {
			Dungeon.fail(this);
			GLog.n(Messages.get(this, "explo_kill"));
		}
	}

	boolean legacyDeathBurst() {
		boolean heroKilled = false;
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char ch = findChar(pos + offset);
			if (ch != null && ch.isAlive()) {
				int damage = Math.max(0, Random.NormalIntRange(3, 8)
						- Random.IntRange(0, Math.max(0, ch.drRoll()) / 2));
				ch.damage(damage, this);
				if (ch == Dungeon.hero && !ch.isAlive()) heroKilled = true;
			}
		}
		return heroKilled;
	}

	@Override
	public int attackSkill( Char target ) {
		return 12 + legacyDepthAdjustment(0);
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(5, 10);
	}

	@Override
	public Item createLoot() {
		Item result = Generator.random(Generator.Category.ARMOR);
		for (int i = 0; i < 2; i++) {
			Item candidate = Generator.random(Generator.Category.ARMOR);
			if (candidate.level() < result.level()) result = candidate;
		}
		return result;
	}

	private final String CHAINSUSED = "chainsused";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHAINSUSED, chainsUsed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		chainsUsed = bundle.getBoolean(CHAINSUSED);
	}

	private boolean chainVisible() {
		return sprite != null && sprite.parent != null && enemy != null && enemy.sprite != null
				&& (sprite.visible || enemy.sprite.visible);
	}

	boolean legacyChainAllowed(boolean enemyInFOV) {
		return !chainsUsed && enemyInFOV && enemy != null
				&& Dungeon.level.distance(pos, enemy.pos) < 5
				&& !Dungeon.level.adjacent(pos, enemy.pos)
				&& buff(Silent.class) == null;
	}

	private class Hunting extends Mob.Hunting {
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			enemySeen = enemyInFOV;
			if (legacyChainAllowed(enemyInFOV) && Random.Int(3) == 0) {
				boolean visible = chainVisible();
				if (chain(enemy.pos)) return !visible;
			}
			return super.act(enemyInFOV, justAlerted);
		}
	}
}
