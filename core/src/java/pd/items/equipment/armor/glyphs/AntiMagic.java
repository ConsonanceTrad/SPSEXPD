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

package pd.items.equipment.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Degrade;
import pd.actors.buffs.Hex;
import pd.actors.buffs.MagicalSleep;
import pd.actors.buffs.Vulnerable;
import pd.actors.buffs.Weakness;
import pd.actors.hero.abilities.duelist.ElementalStrike;
import pd.actors.hero.abilities.mage.ElementalBlast;
import pd.actors.hero.abilities.mage.WarpBeacon;
import pd.actors.hero.spells.GuidingLight;
import pd.actors.hero.spells.HolyLance;
import pd.actors.hero.spells.HolyWeapon;
import pd.actors.hero.spells.Judgement;
import pd.actors.hero.spells.Smite;
import pd.actors.hero.spells.Sunray;
import pd.actors.mobs.CrystalWisp;
import pd.actors.mobs.DM100;
import pd.actors.mobs.Eye;
import pd.actors.mobs.Shaman;
import pd.actors.mobs.Warlock;
import pd.actors.mobs.YogFist;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.artifacts.ChaliceOfBlood;
import pd.items.equipment.bombs.ArcaneBomb;
import pd.items.equipment.bombs.HolyBomb;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.consum.scrolls.ScrollOfRetribution;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.wands.CursedWand;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.items.equipment.wands.WandOfDisintegration;
import pd.items.equipment.wands.WandOfFireblast;
import pd.items.equipment.wands.WandOfFrost;
import pd.items.equipment.wands.WandOfLightning;
import pd.items.equipment.wands.WandOfLivingEarth;
import pd.items.equipment.wands.WandOfMagicMissile;
import pd.items.equipment.wands.WandOfPrismaticLight;
import pd.items.equipment.wands.WandOfTransfusion;
import pd.items.equipment.wands.WandOfWarding;
import pd.items.equipment.weapon.enchantments.Blazing;
import pd.items.equipment.weapon.enchantments.Crystal;
import pd.items.equipment.weapon.enchantments.Grim;
import pd.items.equipment.weapon.enchantments.Shocking;
import pd.items.equipment.weapon.missiles.darts.HolyDart;
import pd.levels.traps.DisintegrationTrap;
import pd.levels.traps.GrimTrap;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import java.util.HashSet;

public class AntiMagic extends Armor.Glyph {

	private static ItemSprite.Glowing TEAL = new ItemSprite.Glowing( 0x88EEFF );
	
	public static final HashSet<Class> RESISTS = new HashSet<>();
	static {
		RESISTS.add( MagicalSleep.class );
		RESISTS.add( Charm.class );
		RESISTS.add( Weakness.class );
		RESISTS.add( Vulnerable.class );
		RESISTS.add( Hex.class );
		RESISTS.add( Degrade.class );
		
		RESISTS.add( DisintegrationTrap.class );
		RESISTS.add( GrimTrap.class );

		RESISTS.add( ArcaneBomb.class );
		RESISTS.add( HolyBomb.HolyDamage.class );
		RESISTS.add( ScrollOfRetribution.class );
		RESISTS.add( ScrollOfPsionicBlast.class );
		RESISTS.add( ScrollOfTeleportation.class );
		RESISTS.add( HolyDart.class );

		RESISTS.add( GuidingLight.class );
		RESISTS.add( HolyWeapon.class );
		RESISTS.add( Sunray.class );
		RESISTS.add( HolyLance.class );
		RESISTS.add( Smite.class );
		RESISTS.add( Judgement.class );

		RESISTS.add( ElementalBlast.class );
		RESISTS.add( CursedWand.class );
		RESISTS.add( WandOfBlastWave.class );
		RESISTS.add( WandOfDisintegration.class );
		RESISTS.add( WandOfFireblast.class );
		RESISTS.add( WandOfFrost.class );
		RESISTS.add( WandOfLightning.class );
		RESISTS.add( WandOfLivingEarth.class );
		RESISTS.add( WandOfMagicMissile.class );
		RESISTS.add( WandOfPrismaticLight.class );
		RESISTS.add( WandOfTransfusion.class );
		RESISTS.add( WandOfWarding.Ward.class );

		RESISTS.add( ChaliceOfBlood.class );

		RESISTS.add( ElementalStrike.class );
		RESISTS.add( Blazing.class );
		RESISTS.add( Shocking.class );
		RESISTS.add( Grim.class );
		RESISTS.add( Crystal.class );

		RESISTS.add( WarpBeacon.class );
		
		RESISTS.add( DM100.LightningBolt.class );
		RESISTS.add( Shaman.EarthenBolt.class );
		RESISTS.add( CrystalWisp.LightBeam.class );
		RESISTS.add( Warlock.DarkBolt.class );
		RESISTS.add( Eye.DeathGaze.class );
		RESISTS.add( YogFist.BrightFist.LightBeam.class );
		RESISTS.add( YogFist.DarkFist.DarkBolt.class );
	}
	
	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		//no proc effect, triggers in Char.damage
		return damage;
	}
	
	public static int drRoll( Char owner, int level ){
		if (level == -1){
			return 0;
		} else {
			return Random.NormalIntRange(
					Math.round(level * genericProcChanceMultiplier(owner)),
					Math.round((3 + (level * 1.5f)) * genericProcChanceMultiplier(owner)));
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return TEAL;
	}

}
