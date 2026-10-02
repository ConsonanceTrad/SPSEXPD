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
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.AllyBuff;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemIconSheet;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class ScrollOfSirensSong extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfSirensSong.class)
			.t("name", "魅音秘卷")
			.t("prompt", "选择一个目标")
			.t("no_target", "这张秘卷在没有目标的情况下激活了。")
			.t("cancel", "你必须选择一个目标。")
			.t("desc", "阅读此卷轴将播放出摄人心魄的音律，令目标敌人沉沦于你，永久地变成盟友！听到这歌声的其它敌人则是暂时被魅惑。\n\n特别强大的敌人可以抵抗沉沦效果，但同样会被魅惑。")
			.t("$enthralled.name", "沉沦")
			.t("$enthralled.desc", "这个生物已因魅音秘卷的效果而沉沦。\n\n沉沦的单位将永远忠心于你，并主动攻击其遇到的所有敌人。");
	}



	
	{
		icon = ItemIconSheet.SCROLL_SIREN;
	}

	protected static boolean identifiedByUse = false;
	
	@Override
	public void doRead() {
		if (!isKnown()) {
			identify();
			curItem = detach(curUser.belongings.backpack);
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}
		GameScene.selectCell(targeter);
	}

	private CellSelector.Listener targeter = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null && isKnown()){
				return;
			}

			Mob target = null;
			if (cell != null){
				Char ch = Actor.findChar(cell);
				if (ch != null && ch.alignment != Char.Alignment.ALLY && ch instanceof Mob){
					target = (Mob)ch;
				}
			}

			if (target == null && !anonymous && !identifiedByUse){
				GLog.w(Messages.get(ScrollOfSirensSong.class, "cancel"));
				return;

			} else {

				curUser.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
				Sample.INSTANCE.play( Assets.Sounds.CHARMS );
				Sample.INSTANCE.playDelayed( Assets.Sounds.LULLABY, 0.1f );

				for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
					if (Dungeon.level.heroFOV[mob.pos] && mob != target && mob.alignment != Char.Alignment.ALLY) {
						Buff.affect( mob, Charm.class, Charm.DURATION ).object = curUser.id();
						mob.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
					}
				}

				if (target != null){
					if (!target.isImmune(Enthralled.class)){
						AllyBuff.affectAndLoot(target, curUser, Enthralled.class);

					} else {
						Buff.affect( target, Charm.class, Charm.DURATION ).object = curUser.id();

					}
					target.sprite.centerEmitter().burst( Speck.factory( Speck.HEART ), 10 );
				} else {
					GLog.w(Messages.get(ScrollOfSirensSong.class, "no_target"));
				}

				if (!identifiedByUse) {
					curItem.detach(curUser.belongings.backpack);
				}
				identifiedByUse = false;

				readAnimation();

			}
		}

		@Override
		public String prompt() {
			return Messages.get(ScrollOfSirensSong.class, "prompt");
		}

	};

	public static class Enthralled extends AllyBuff {

		{
			type = buffType.NEGATIVE;
			announced = true;
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.HEARTS);
			else    target.sprite.remove(CharSprite.State.HEARTS);
		}

		@Override
		public int icon() {
			return BuffIndicator.HEART;
		}
	}
	
}
