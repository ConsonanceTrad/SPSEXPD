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
import pd.actors.Char;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.Silent;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.hero.spells.HolyWard;
import pd.actors.hero.spells.ShieldOfLight;
import pd.items.Generator;
import pd.items.Item;
import pd.items.weapon.melee.StoneCross;
import pd.items.wands.WandOfLivingEarth;
import pd.levels.features.Chasm;
import pd.messages.Messages;
import pd.plants.Earthroot;
import pd.sprites.SkeletonSprite;
import pd.ui.TargetHealthIndicator;
import pd.utils.GLog;
import watabou.noosa.audio.Sample;
import watabou.utils.PathFinder;
import watabou.utils.Random;

public class Skeleton extends Mob {
	@Override public Item SupercreateLoot() { return new StoneCross(); }
	
	{
		spriteClass = SkeletonSprite.class;
		
		HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5);
		defenseSkill = 15 + legacyDepthAdjustment(0);
		baseSpeed = 0.8f;
		
		EXP = 5;
		maxLvl = 25;

		loot = Generator.Category.MELEEWEAPON;
		lootChance = 0.15f;

		properties.add(Property.UNDEAD);
		properties.add(Property.INORGANIC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 22 + legacyDepthAdjustment(0));
	}
	
	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		boolean heroKilled = legacyDeathBurst();

		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.Sounds.BONES );
		}
		
		if (heroKilled) {
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "explo_kill") );
		}
	}

	boolean legacyDeathBurst() {
		boolean heroKilled = false;
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
			if (ch != null && ch.isAlive()) {
				int damage = Math.max(0, Random.NormalIntRange(3, 8)
						- Random.IntRange(0, Math.max(0, ch.drRoll()) / 2));
				ch.damage( damage, this );
				Buff.affect(ch, Silent.class, 10f);
				if (ch == Dungeon.hero && !ch.isAlive()) {
					heroKilled = true;
				}
			}
		}
		return heroKilled;
	}

	@Override
	public int attackSkill( Char target ) {
		return 16 + legacyDepthAdjustment(0);
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 5);
	}

}
