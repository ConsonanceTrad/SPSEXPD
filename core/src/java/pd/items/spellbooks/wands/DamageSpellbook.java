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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pd.items.spellbooks.wands;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.mechanics.Ballistica;

/**
 * SPSEXPD: 伤害型定向法术书基类（法术书化批次1）。
 *
 * <p>效果<b>直接重写</b>（不复用 Wand.onZap、不内嵌法杖）：伤害 roll 按
 * {@link #min(int)}/{@link #max(int)} 曲线（抄本 0 级 / 原典 5 级），经
 * {@code power()} 吃法强加成；弹道碰撞点结算，子类覆写 {@link #onHitCell}
 * 附加点燃/冻伤等效果。碰撞点用 {@code Ballistica.PROJECTILE}（弹道停在指定点，
 * 与常规法杖一致）。</p>
 */
public abstract class DamageSpellbook extends pd.items.spellbooks.Spellbook {

	@Override
	public boolean targetRequired() { return true; }

	/** 伤害下限曲线（抄本传 0、原典传 5 的对应法杖曲线） */
	public abstract int min( int level );

	/** 伤害上限曲线 */
	public abstract int max( int level );

	@Override
	public void onCast( Hero hero ) {
		//定向书正常不会被走到（targetRequired=true），兜底打自己脚下
		castAt( hero, hero.pos );
	}

	@Override
	public void castAt( Hero hero, int cell ) {
		if (pd.Dungeon.level == null) return;
		//弹道碰撞点：与常规法杖同口径（PROJECTILE 停在指定点）
		pd.mechanics.Ballistica shot = new pd.mechanics.Ballistica( hero.pos, cell, Ballistica.PROJECTILE );
		int hit = shot.collisionPos;

		int base = render.utils.math.Random.NormalIntRange( min( castLevel() ), max( castLevel() ) );
		pd.actors.Char target = pd.actors.Actor.findChar( hit );
		if (target != null && target != hero) {
			int dmg = hitDamage( hero, target, power( hero, base ) );
			target.damage( dmg, this );
			if (target.sprite != null) target.sprite.flash();
			onHitTarget( hero, target, dmg );
		}
		onHitCell( hero, hit, base );
	}

	/** 施法等级：抄本 0 / 原典 5 */
	public abstract int castLevel();

	/** SPSEXPD: 子类可覆写修正最终伤害（结算前；如冰霜的冻伤衰减） */
	protected int hitDamage( Hero hero, pd.actors.Char target, int damage ) {
		return damage;
	}

	/** 主目标命中后的附加效果（默认无） */
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) { }

	/** 弹道落点效果（默认无；如点燃/冰冻地面） */
	protected void onHitCell( Hero hero, int cell, int baseDamage ) { }
}
