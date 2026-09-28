/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.damagetype;

/** Distinct SPS elemental damage sources used by resistances and immunities. */
public abstract class DamageType {
	public static final Energy ENERGY_DAMAGE = new Energy();
	public static final Fire FIRE_DAMAGE = new Fire();
	public static final Ice ICE_DAMAGE = new Ice();
	public static final Shock SHOCK_DAMAGE = new Shock();
	public static final Earth EARTH_DAMAGE = new Earth();
	public static final Light LIGHT_DAMAGE = new Light();
	public static final Dark DARK_DAMAGE = new Dark();

	public static final class Energy extends DamageType { }
	public static final class Fire extends DamageType { }
	public static final class Ice extends DamageType { }
	public static final class Shock extends DamageType { }
	public static final class Earth extends DamageType { }
	public static final class Light extends DamageType { }
	public static final class Dark extends DamageType { }
}
