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

package pd.items.consum.scrolls.exotic;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.PrismaticGuard;
import pd.actors.hero.spells.Stasis;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.PrismaticImage;
import pd.effects.FloatingText;
import pd.sprites.CharSprite;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class ScrollOfPrismaticImage extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfPrismaticImage.class)
			.t("name", "虹卫秘卷")
			.t("desc", "这张秘卷上的咒文会创造使用者的一个虹光守卫。这个像使用者的弱化版克隆体的幻像有着相同的防御，但生命值和造成的伤害更低。\n\n虹光守卫将吸引敌人的火力从而保护使用者。\n\n当虹光守卫存在时阅读这张秘卷将会为其恢复所有生命。");
	}

	
	{
		icon = ItemIconSheet.SCROLL_PRISIMG;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		boolean found = false;
		for (Mob m : Dungeon.level.mobs().toArray(new Mob[0])){
			if (m instanceof PrismaticImage){
				found = true;
				m.HP = m.HT;
				m.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(m.HT), FloatingText.HEALING );
			}
		}

		if (!found){
			if (Stasis.getStasisAlly() instanceof PrismaticImage){
				found = true;
				Stasis.getStasisAlly().HP = Stasis.getStasisAlly().HT;
			}
		}
		
		if (!found) {
			Buff.affect(curUser, PrismaticGuard.class).set( PrismaticGuard.maxHP( curUser ) );
		}

		identify();
		
		Sample.INSTANCE.play( Assets.Sounds.READ );
	
		readAnimation();
	}
}
