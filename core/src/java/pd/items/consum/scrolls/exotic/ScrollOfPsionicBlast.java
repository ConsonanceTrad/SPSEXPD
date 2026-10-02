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
import pd.Badges;
import pd.Dungeon;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Weakness;
import pd.actors.mobs.Mob;
import pd.items.consum.scrolls.ScrollOfRetribution;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ScrollOfPsionicBlast extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfPsionicBlast.class)
			.t("name", "灵爆秘卷")
			.t("ondeath", "灵能震爆撕碎了你的意识...")
			.t("desc", "这张秘卷封存着惊人的毁灭性能量，一旦被释放出来可摧毁视野内所有生物的心智。\n\n然而，使用者也会遭受灵爆的严重反噬，使其身受重创，双目失明，力量虚弱。灵爆秘卷击中的目标越多，其对使用者造成的伤害越低。");
	}

	
	{
		icon = ItemIconSheet.SCROLL_PSIBLAST;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		GameScene.flash( 0x80FFFFFF );
		
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
		GLog.i(Messages.get(ScrollOfRetribution.class, "blast"));

		ArrayList<Mob> targets = new ArrayList<>();

		//calculate targets first, in case damaging/blinding a target affects hero vision
		for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
			if (Dungeon.level.heroFOV[mob.pos]) {
				targets.add(mob);
			}
		}

		for (Mob mob : targets){
			//always kills non-resistant enemies
			//resistant enemies take 50% current HP at full health, scaling to 75% at 1/2 HP, and 100% at 1/3 hp
			mob.damage(Math.round(mob.HT/2f + mob.HP/2f), this);
			if (mob.isAlive()) {
				Buff.prolong(mob, Blindness.class, Blindness.DURATION);
			}
		}
		
		curUser.damage(Math.max(0, Math.round(curUser.HT*(0.5f * (float)Math.pow(0.9, targets.size())))), this);
		if (curUser.isAlive()) {
			Buff.prolong(curUser, Blindness.class, Blindness.DURATION);
			Buff.prolong(curUser, Weakness.class, Weakness.DURATION*5f);
			Dungeon.observe();
			readAnimation();
		} else {
			Badges.validateDeathFromFriendlyMagic();
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "ondeath") );
		}

		identify();
		
	
	}
}
