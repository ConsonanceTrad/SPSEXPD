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

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Silent;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.weapon.Weapon;
import pd.items.weapon.Weapon.Enchantment;
import pd.items.weapon.melee.MeleeWeapon;
import pd.journal.Notes;
import pd.messages.Messages;
import pd.sprites.StatueSprite;
import pd.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Statue extends Mob {
	
	{
		spriteClass = StatueSprite.class;

		EXP = 50 + legacyDepthAdjustment(0) * 2;
		state = PASSIVE;
		
		properties.add(Property.ELEMENT);
	}
	
	protected Weapon weapon;

	public boolean levelGenStatue = true;
	
	public Statue() {
		super();
		
		HP = HT = 15 + legacyDepthAdjustment(0) * 5;
		defenseSkill = 4 + legacyDepthAdjustment(0) * 2;
	}

	public void createWeapon( boolean useDecks ){
		for (int attempts = 0; attempts < 100; attempts++) {
			Weapon candidate = (Weapon)(useDecks
					? Generator.random(Generator.Category.OLDWEAPON)
					: Generator.randomUsingDefaults(Generator.Category.OLDWEAPON));
			if (candidate instanceof MeleeWeapon && candidate.trueLevel() >= 0) {
				weapon = candidate;
				break;
			}
		}
		if (weapon == null) {
			weapon = (Weapon)Generator.randomUsingDefaults(Generator.Category.OLDWEAPON);
			if (weapon.trueLevel() < 0) weapon.level(0);
		}
		levelGenStatue = useDecks;
		weapon.cursed = false;
		weapon.identify();
		weapon.enchant( Enchantment.random() );
	}

	public Weapon weapon(){
		return weapon;
	}
	
	private static final String WEAPON	= "weapon";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( WEAPON, weapon );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		weapon = (Weapon)bundle.get( WEAPON );
	}
	
	@Override
	public int damageRoll() {
		return weapon.damageRoll(this);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return (int)((9 + legacyDepthAdjustment(0)) * weapon.accuracyFactor( this, target ));
	}
	
	@Override
	public float attackDelay() {
		return super.attackDelay()*weapon.delayFactor( this );
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level != null && enemy != null
				&& Dungeon.level.distance(pos, enemy.pos) <= weapon.reachFactor(this);
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, legacyDepthAdjustment(0));
	}
	
	@Override
	public boolean add(Buff buff) {
		if (buff instanceof Locked || buff instanceof Silent) {
			damage(Random.NormalIntRange(1, HT * 2 / 3), buff);
			return false;
		}
		return super.add(buff);
	}

	@Override
	public void damage( int dmg, Object src ) {

		if (state == PASSIVE) {
			state = HUNTING;
		}
		
		super.damage( dmg, src );
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		damage = weapon.proc( this, enemy, damage );
		if (!enemy.isAlive() && enemy == Dungeon.hero){
			Dungeon.fail(this);
			GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
		}
		return damage;
	}
	
	@Override
	public void beckon( int cell ) {
		// Source statues ignore beckoning even after they are awakened.
	}
	
	@Override
	public void die( Object cause ) {
		Heap heap = Dungeon.level == null ? null : Dungeon.level.drop(weapon, pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
		super.die( cause );
	}

	@Override
	public Notes.Landmark landmark() {
		return levelGenStatue ? Notes.Landmark.STATUE : null;
	}

	@Override
	public void destroy() {
		if (landmark() != null) {
			Notes.remove( landmark() );
		}
		super.destroy();
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public boolean reset() {
		state = PASSIVE;
		return true;
	}

	@Override
	public String description() {
		return Messages.get(this, "desc", weapon.name());
	}
	
	{
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
	}

	public static Statue random(){
		return random( true );
	}

	public static Statue random( boolean useDecks ){
		Statue statue = new Statue();
		statue.createWeapon(useDecks);
		return statue;
	}
	
}
