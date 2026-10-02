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

public class VaultTokenDoor extends NPC {

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
