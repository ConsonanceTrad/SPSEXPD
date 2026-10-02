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

import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Light;
import pd.actors.buffs.Terror;
import pd.actors.damagetype.DamageType;
import pd.effects.CellEmitter;
import pd.effects.particles.PurpleParticle;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.equipment.wands.WandOfDisintegration;
import pd.items.equipment.weapon.enchantments.EnchantmentDark;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.EyeSprite;
import pd.utils.GLog;
import render.noosa.tweeners.AlphaTweener;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public class Eye extends Mob {
	
	{
		spriteClass = EyeSprite.class;
		
		HP = HT = 200 + legacyDepthAdjustment(0) * Random.NormalIntRange(4, 7);
		defenseSkill = 20 + legacyDepthAdjustment(1);
		viewDistance = Light.DISTANCE;
		
		EXP = 15;
		maxLvl = 35;
		
		flying = true;

		loot = PotionOfHealing.class;
		lootChance = 0.1f;

		properties.add(Property.DEMONIC);
		properties.add(Property.MAGICER);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(5, 25);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		enemy.damage(damageRoll() * 3 / 4, DamageType.LIGHT_DAMAGE);
		return damage / 4;
	}

	@Override
	public int attackSkill( Char target ) {
		return 30 + legacyDepthAdjustment(0);
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(20, 30);
	}
	
	protected Ballistica beam;
	private int beamTarget = -1; // retained for save compatibility
	private int beamCooldown;    // retained for save compatibility
	public boolean beamCharged;

	@Override
	protected boolean canAttack( Char enemy ) {

		beam = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
		beamTarget = enemy.pos;
		return beam.subPath(1, beam.dist).contains(enemy.pos);
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		beam = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
		beamTarget = enemy.pos;
		spend(attackDelay());
		boolean rayVisible = false;
		for (int cell : beam.subPath(0, beam.dist)) {
			if (Dungeon.level.heroFOV[cell]) {
				rayVisible = true;
				break;
			}
		}
		if (rayVisible && sprite != null) {
			sprite.zap(beam.collisionPos);
			return false;
		} else {
			deathGaze();
			return true;
		}
	}

	@Override
	public float attackDelay() {
		return 1.6f;
	}

	@Override
	public void die(Object cause) {
		flying = false;
		super.die(cause);
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class DeathGaze{}

	public void deathGaze(){
		if (beam == null) return;
		beamCharged = false;
		beamCooldown = 0;
		for (int cell : beam.subPath(1, beam.dist)) {
			Char ch = Actor.findChar(cell);
			if (ch == null) {
				continue;
			}

			if (hit( this, ch, true )) {
				ch.damage(Random.NormalIntRange(14, 20 + legacyDepthAdjustment(0)), DamageType.LIGHT_DAMAGE);

				if (Dungeon.level.heroFOV[cell] && ch.sprite != null) {
					ch.sprite.flash();
					CellEmitter.center(cell).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
				}

				if (!ch.isAlive() && ch == Dungeon.hero) {
					Badges.validateDeathFromEnemyMagic();
					Dungeon.fail( this );
					GLog.n( Messages.get(this, "deathgaze_kill") );
				}
			} else if (ch.sprite != null) {
				ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
			}
		}

		beam = null;
		beamTarget = -1;
	}

	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();
		if (dropsLegacyMysteryMeat() && Dungeon.hero != null && Dungeon.level != null && legacyLootLevelEligible()
				&& Random.Float() < legacySecondaryLootChance(0.5f)) {
			pd.items.Heap heap = Dungeon.level.drop(new MysteryMeat(), pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
	}

	protected boolean dropsLegacyMysteryMeat() {
		return true;
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(Generator.random(Generator.Category.POTION),
				Generator.random(Generator.Category.WAND));
	}

	public static void spawnAroundChance(int pos) {
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null && Random.Float() < 0.5f) spawnAt(cell);
		}
	}

	public static Eye spawnAt(int pos) {
		if (!Dungeon.level.insideMap(pos) || !Dungeon.level.passable[pos] || Actor.findChar(pos) != null) return null;
		Eye eye = new Eye();
		eye.pos = pos;
		eye.state = eye.HUNTING;
		GameScene.add(eye, 2f);
		if (eye.sprite != null) {
			eye.sprite.alpha(0);
			if (eye.sprite.parent != null) eye.sprite.parent.add(new AlphaTweener(eye.sprite, 1, 0.5f));
		}
		return eye;
	}

	private static final String BEAM_TARGET     = "beamTarget";
	private static final String BEAM_COOLDOWN   = "beamCooldown";
	private static final String BEAM_CHARGED    = "beamCharged";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( BEAM_TARGET, beamTarget);
		bundle.put( BEAM_COOLDOWN, beamCooldown );
		bundle.put( BEAM_CHARGED, beamCharged );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		beamTarget = -1;
		beamCooldown = 0;
		beamCharged = false;
	}

	{
		resistances.add( WandOfDisintegration.class );
		resistances.add( EnchantmentDark.class );
		immunities.add( Terror.class );
	}
}
