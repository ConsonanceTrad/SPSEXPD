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

package pd.actors.mobs.npcs;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.AllyBuff;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.PrismaticGuard;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.rings.RingOfAccuracy;
import pd.items.equipment.rings.RingOfEvasion;
import pd.levels.features.Chasm;
import pd.sprites.CharSprite;
import pd.sprites.PrismaticSprite;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class PrismaticImage extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(PrismaticImage.class)
			.t("name", "虹光幻像")
			.t("desc", "这个闪烁着虹光的幻像和你非常相似，甚至还装备着你当前的武器和护甲。\n\n虹光守卫会追击敌人并吸引仇恨，进而用其强化的防御力和生命值分担火力。虽然它不似镜像那般有攻击性，但它生命值更高，还具有基于你护甲的防御增益。\n\n当幻像生命降至0时，它将保持一段时间的存在，在此期间仍能被治愈。当周围没有敌人时，幻像会消散并依附其主人身上。")
			.t("discover_hint", "你可通过某张卷轴遇到该单位。");
	}

	
	{
		spriteClass = PrismaticSprite.class;
		
		HP = HT = 10;
		defenseSkill = 1;
		
		alignment = Alignment.ALLY;
		intelligentAlly = true;
		state = HUNTING;
		
		WANDERING = new Wandering();
		
		//before other mobs
		actPriority = MOB_PRIO + 1;
	}
	
	private Hero hero;
	private int heroID;
	public int armTier;
	
	private int deathTimer = -1;
	
	@Override
	protected boolean act() {
		
		if (!isAlive()){
			deathTimer--;
			
			if (deathTimer > 0) {
				sprite.alpha((deathTimer + 3) / 8f);
				spend(TICK);
			} else {
				destroy();
				sprite.die();
			}
			return true;
		}
		
		if (deathTimer != -1){
			if (paralysed == 0) sprite.remove(CharSprite.State.PARALYSED);
			deathTimer = -1;
			sprite.resetColor();
		}
		
		if ( hero == null ){
			hero = (Hero) Actor.findById(heroID);
			if ( hero == null ){
				destroy();
				sprite.die();
				return true;
			}
		}
		
		if (hero.tier() != armTier){
			armTier = hero.tier();
			((PrismaticSprite)sprite).updateArmor( armTier );
		}
		
		return super.act();
	}
	
	@Override
	public void die(Object cause) {
		if (deathTimer == -1) {
			if (cause == Chasm.class){
				super.die( cause );
			} else {
				deathTimer = 5;
				sprite.add(CharSprite.State.PARALYSED);
			}
		}
	}

	@Override
	public boolean isActive() {
		return isAlive() || deathTimer > 0;
	}

	private static final String HEROID	= "hero_id";
	private static final String TIMER	= "timer";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( HEROID, heroID );
		bundle.put( TIMER, deathTimer );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		heroID = bundle.getInt( HEROID );
		deathTimer = bundle.getInt( TIMER );
	}
	
	public void duplicate( Hero hero, int HP ) {
		this.hero = hero;
		heroID = this.hero.id();
		this.HP = HP;
		HT = PrismaticGuard.maxHP( hero );
	}
	
	@Override
	public int damageRoll() {
		if (hero != null) {
			return Random.NormalIntRange( 2 + hero.lvl/4, 4 + hero.lvl/2 );
		} else {
			return Random.NormalIntRange( 2, 4 );
		}
	}
	
	@Override
	public int attackSkill( Char target ) {
		if (hero != null) {
			//same base attack skill as hero, benefits from accuracy ring
			return (int)((9 + hero.lvl) * RingOfAccuracy.accuracyMultiplier(hero));
		} else {
			return 0;
		}
	}
	
	@Override
	public int defenseSkill(Char enemy) {
		if (hero != null) {
			int baseEvasion = 4 + hero.lvl;
			int heroEvasion = (int)((4 + hero.lvl) * RingOfEvasion.evasionMultiplier( hero ));
			if (hero.belongings.armor() != null){
				heroEvasion = (int)hero.belongings.armor().evasionFactor(this, heroEvasion);
			}

			//if the hero has more/less evasion, 50% of it is applied
			//includes ring of evasion and armor boosts
			return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
		} else {
			return 0;
		}
	}
	
	@Override
	public int drRoll() {
		int dr = super.drRoll();
		if (hero != null){
			return dr + hero.drRoll();
		} else {
			return dr;
		}
	}
	
	@Override
	public int defenseProc(Char enemy, int damage) {
		if (hero != null && hero.belongings.armor() != null){
			damage = hero.belongings.armor().proc( enemy, this, damage );
		}
		return super.defenseProc(enemy, damage);
	}

	@Override
	public int glyphLevel(Class<? extends Armor.Glyph> cls) {
		if (hero != null){
			return Math.max(super.glyphLevel(cls), hero.glyphLevel(cls));
		} else {
			return super.glyphLevel(cls);
		}
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		
		if (enemy instanceof Mob) {
			((Mob)enemy).aggro( this );
		}
		
		return super.attackProc( enemy, damage );
	}
	
	@Override
	public CharSprite sprite() {
		CharSprite s = super.sprite();
		
		hero = (Hero)Actor.findById(heroID);
		if (hero != null) {
			armTier = hero.tier();
		} else {
			armTier = 1;
		}
		((PrismaticSprite)s).updateArmor( armTier );
		return s;
	}
	
	{
		immunities.add( ToxicGas.class );
		immunities.add( CorrosiveGas.class );
		immunities.add( Burning.class );
		immunities.add( AllyBuff.class );
	}
	
	private class Wandering extends Mob.Wandering{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (!enemyInFOV){
				Buff.affect(hero, PrismaticGuard.class).set( PrismaticImage.this );
				destroy();
				CellEmitter.get(pos).start( Speck.factory(Speck.LIGHT), 0.2f, 3 );
				sprite.die();
				Sample.INSTANCE.play( Assets.Sounds.TELEPORT );
				return true;
			} else {
				return super.act(enemyInFOV, justAlerted);
			}
		}
		
	}
	
}
