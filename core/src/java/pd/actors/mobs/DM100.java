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
import pd.actors.Char;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.Invisibility;
import pd.effects.particles.SparkParticle;
import pd.items.Generator;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.sprites.DM100Sprite;
import pd.utils.GLog;
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DM100 extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DM100.class)
			.t("name", "DM-100")
			.t("zap_kill", "你死于闪电...")
			.t("desc", "DM-100是一种矮人“防卫机械”的早期机型，用来保护下层矿洞中的矮人矿工。然而由于电击威力不足，它们之后被送给上层的人类城市。尽管典狱长起初仍然认为DM-100太过残忍，但在闲置数年后，它们最终还是被用于监狱。然而随着时间推移，囚犯变得越来越难以控制，这使得DM-100成为了监狱的必备品。");
	}


	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = DM100Sprite.class;
		
		HP = HT = 20;
		defenseSkill = 8;
		
		EXP = 6;
		maxLvl = 13;
		
		loot = Generator.Category.SCROLL;
		lootChance = 0.25f;
		
		properties.add(Property.ELECTRIC);
		properties.add(Property.INORGANIC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 8 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 11;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 4);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy)
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class LightningBolt{}
	
	@Override
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {
			
			return super.doAttack( enemy );
			
		} else {
			
			spend( TIME_TO_ZAP );

			Invisibility.dispel(this);
			if (hit( this, enemy, true )) {
				int dmg = Random.NormalIntRange(3, 10);
				dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
				enemy.damage( dmg, new LightningBolt() );

				if (enemy.sprite.visible) {
					enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
					enemy.sprite.flash();
				}
				
				if (enemy == Dungeon.hero) {
					
					PixelScene.shake( 2, 0.3f );
					
					if (!enemy.isAlive()) {
						Badges.validateDeathFromEnemyMagic();
						Dungeon.fail( this );
						GLog.n( Messages.get(this, "zap_kill") );
					}
				}
			} else {
				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}
			
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				return true;
			}
		}
	}
	
}
