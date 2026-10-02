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

package pd.actors.buffs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.equipment.wands.WandOfCorrosion;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.Image;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Corrosion extends Buff implements Hero.Doom, Buff.DOTbuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Corrosion.class)
			.t("name", "酸蚀")
			.t("heromsg", "你正在被强酸溶解！")
			.t("ondeath", "你被彻底溶解掉了...")
			.t("rankings_desc", "被溶解")
			.t("desc", "强酸能以惊人的速度腐蚀掉血肉、金属和骨头。\n\n目标被腐蚀的时间越长，酸蚀伤害越高。\n\n酸蚀效果剩余时长：%1$s回合\n当前酸蚀伤害：%2$d");
	}




	private float damage = 1;
	protected float left;

	//used in specific cases where the source of the corrosion is important for death logic
	private Class source;

	private static final String DAMAGE	= "damage";
	private static final String LEFT	= "left";
	private static final String SOURCE	= "source";

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DAMAGE, damage );
		bundle.put( LEFT, left );
		bundle.put( SOURCE, source);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		damage = bundle.getFloat( DAMAGE );
		left = bundle.getFloat( LEFT );
		source = bundle.getClass( SOURCE );
	}

	public void set(float duration, int damage){
		set(duration, damage, null);
	}

	public void set(float duration, int damage, Class source) {
		this.left = Math.max(duration, left);
		if (this.damage < damage) this.damage = damage;
		this.source = source;
		if (target != null) target.needsIncomingDOTUpdate = true;
	}

	public void extend( float duration ) {
		left += duration;
		if (target != null) target.needsIncomingDOTUpdate = true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.POISON;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 0.5f, 0f);
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString((int)damage);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(left), (int)damage);
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			target.damage((int)damage, this);
			if (damage < (Dungeon.scalingDepth()/2)+2) {
				damage++;
			} else {
				damage += 0.5f;
			}
			
			spend( TICK );
			if ((left -= TICK) <= 0) {
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
	public void onDeath() {
		if (source == WandOfCorrosion.class){
			Badges.validateDeathFromFriendlyMagic();
		}

		Dungeon.fail( this );
		GLog.n(Messages.get(this, "ondeath"));
	}

	@Override
	public int totalIncomingDMG() {
		int total = 0;
		float curDMG = damage;
		for (int i = (int)Math.ceil(left); i > 0; i--){
			total += (int)curDMG;
			if (curDMG < (Dungeon.scalingDepth()/2)+2) {
				curDMG++;
			} else {
				curDMG += 0.5f;
			}
		}
		return total;
	}
}
