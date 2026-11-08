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

import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Talent;
import pd.items.equipment.armor.Armor.Glyph;
import pd.items.equipment.armor.Armor;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite.Glowing;
import pd.sprites.ItemSprite;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Viscosity extends Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Viscosity.class)
			.t("name", "粘稠%s")
			.t("deferred", "延缓%d点伤害")
			.t("$defereddamage.name", "延缓伤害")
			.t("$defereddamage.ondeath", "你死于延缓伤害...")
			.t("$defereddamage.rankings_desc", "死于延迟伤害")
			.t("$defereddamage.desc", "瞬时的伤害被延缓，随着时间慢慢释放。\n\n剩余的延迟伤害：%d")
			.t("desc", "这个刻印可以储存对使用者造成的伤害，让使用者缓慢受伤而不是一下子受到重击。");
	}



	
	private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing( 0x8844CC );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage ) {

		//we use a tracker so that this glyph can apply after armor
		Buff.affect(defender, ViscosityTracker.class).level = armor.buffedLvl();

		return damage;
		
	}

	@Override
	public Glowing glowing() {
		return PURPLE;
	}

	public static class ViscosityTracker extends Buff {

		{
			actPriority = Actor.VFX_PRIO;
		}

		private int level = 0;

		public int deferDamage(int dmg){
			return deferDamage( dmg, null );
		}

		//SPSEXPD: deferDamage 带来源 —— 延迟伤害保留原始伤害类型（伤害类型体系定稿）
		public int deferDamage(int dmg, Object src){
			//account for icon stomach (just skip the glyph)
			if (target.buff(Talent.WarriorFoodImmunity.class) != null){
				return dmg;
			}

			int level = Math.max( 0, this.level );

			float percent = (level+1)/(float)(level+6);
			percent *= genericProcChanceMultiplier(target);

			int amount;
			if (percent > 1f){
				dmg = Math.round(dmg / percent);
				amount = dmg;
			} else {
				amount = (int)Math.ceil(dmg * percent);
			}

			if (amount > 0){
				DeferedDamage deferred = Buff.affect( target, DeferedDamage.class );
				deferred.extend( amount, src );

				target.sprite.showStatus( CharSprite.WARNING, Messages.get(Viscosity.class, "deferred", amount) );
			}

			return dmg - amount;
		}

		@Override
		public boolean act() {
			detach();
			return true;
		}
	};
	
	public static class DeferedDamage extends Buff implements Buff.DOTbuff {
		
		{
			type = buffType.NEGATIVE;
		}
		
		protected int damage = 0;

		//SPSEXPD: 延迟伤害的原始伤害类型（DamageType 嵌套类 SimpleName；null=未知）
		protected String srcKindName;

		private static final String DAMAGE	= "damage";
		private static final String SRC_KIND	= "src_kind";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( DAMAGE, damage );
			if (srcKindName != null) bundle.put( SRC_KIND, srcKindName );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			damage = bundle.getInt( DAMAGE );
			if (bundle.contains(SRC_KIND)) {
				srcKindName = bundle.getString(SRC_KIND);
			}
		}

		public void extend( float damage ) {
			extend( damage, null );
		}

		/** SPSEXPD: 延迟伤害的原始伤害类型（DamageType 嵌套SimpleName；null=未知） */
		public String srcKindName() {
			return srcKindName;
		}

		//SPSEXPD: extend 带来源 —— 首次记录延迟伤害的原始伤害类型
		public void extend( float damage, Object src ) {
			if (srcKindName == null && src != null){
				pd.actors.damagetype.DamageType tk = pd.actors.damagetype.DamageKind.iconToken(src);
				if (tk != null){
					srcKindName = tk.getClass().getSimpleName();
				}
			}
			if (this.damage == 0){
				//wait 1 turn before damaging if this is freshly applied
				postpone(TICK);
			}
			this.damage += damage;
			if (target != null) target.needsIncomingDOTUpdate = true;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.DEFERRED;
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(damage);
		}
		
		@Override
		public boolean act() {
			if (target.isAlive()) {

				int damageThisTick = Math.max(1, (int)(damage*0.1f));
				target.damage( damageThisTick, this );
				if (target == Dungeon.hero && !target.isAlive()) {

					Badges.validateDeathFromFriendlyMagic();

					Dungeon.fail( this );
					GLog.n( Messages.get(this, "ondeath") );
				}
				spend( TICK );

				damage -= damageThisTick;
				if (damage <= 0) {
					detach();
				}
				
			} else {
				
				detach();
				
			}

			target.needsIncomingDOTUpdate = true;
			return true;
		}

		@Override
		public void detach() {
			target.needsIncomingDOTUpdate = true;
			super.detach();
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", damage);
		}

		@Override
		public int totalIncomingDMG() {
			return damage;
		}
	}
}
