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
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.quest.DwarfToken;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.VaultTokenDoorSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import pd.windows.WndTitledMessage;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import pd.messages.InlineText;

public class VaultTokenDoor extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VaultTokenDoor.class)
			.t("name", "奇怪的门")
			.t("def_verb", "格挡")
			.t("desc", "这扇门看似没有上锁，凭你的力量却难以移动分毫。门的表面有一道不同寻常的机关，其上的十个凹槽看起来可以用某种扁平的菱形物体填补上去。")
			.t("no_tokens", "_也许你能在宝库里找到什么可以契合进凹槽的东西？_")
			.t("too_few_tokens", "_你的矮人徽记与凹槽完美契合_，但徽记的数量还不够。")
			.t("enough_tokens", "_你有足够的矮人徽记以填入所有凹槽_，要把它们放进去吗？")
			.t("open", "开门")
			.t("not_yet", "算了")
			.t("unlocked", "徽记渐渐与门融为一体，与此同时你听到了远处门锁打开的声音。");
	}


	{
		spriteClass = VaultTokenDoorSprite.class;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.OBJECT);
	}

	@Override
	protected void throwItems() {
		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null) {
			Dungeon.level.drop( heap.pickUp(), pos+Dungeon.level.width() ).sprite.drop( pos );
		}
	}

	@Override
	public boolean interact(Char c) {
		if (c instanceof Hero){
			Hero h = (Hero) c;

			Item tokens = h.belongings.getItem(DwarfToken.class);

			String descText = description();
			if (tokens == null){
				descText += "\n\n" + Messages.get(this, "no_tokens");
			} else if (tokens.quantity() < 10){
				descText += "\n\n" + Messages.get(this, "too_few_tokens");
			} else {
				descText += "\n\n" + Messages.get(this, "enough_tokens");
			}

			String finalDescText = descText;

			ShatteredPixelDungeon.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					if (tokens != null && tokens.quantity() >= 10) {
						GameScene.show(new WndOptions(sprite(),
								Messages.titleCase(name()),
								finalDescText,
								Messages.get(VaultTokenDoor.class, "open"),
								Messages.get(VaultTokenDoor.class, "not_yet")) {
							@Override
							protected void onSelect(int index) {
								super.onSelect(index);
								if (index == 0){
									c.sprite.operate(pos);
									Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
									Sample.INSTANCE.playDelayed(Assets.Sounds.UNLOCK, 0.25f);
									GLog.p(Messages.get(VaultTokenDoor.class, "unlocked"));
									VaultTokenDoor.this.destroy();
									Level.set(pos, Terrain.DOOR);
									GameScene.updateMap(pos);
									ScrollOfMagicMapping.discover(pos);
									sprite.killAndErase();
									tokens.detachAll(h.belongings.backpack);
								}
							}
						});
					} else {
						GameScene.show(new WndTitledMessage(sprite(),
								Messages.titleCase(name()),
								finalDescText));
					}
				}

			});
		}

		return false;
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage( int dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}
}
