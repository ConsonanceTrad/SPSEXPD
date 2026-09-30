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

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.BrokenSeal;
import pd.items.Heap;
import pd.items.Item;
import pd.items.armor.Armor;
import pd.items.artifacts.CloakOfShadows;
import pd.items.artifacts.HolyTome;
import pd.items.weapon.SpiritBow;
import pd.items.weapon.melee.Greatsword;
import pd.items.weapon.melee.MagesStaff;
import pd.items.weapon.melee.MeleeWeapon;
import pd.items.weapon.melee.Spellblade;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.VaultMirrorSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import pd.windows.WndTitledMessage;
import watabou.utils.Bundle;
import watabou.utils.Callback;
import watabou.utils.Random;

public class VaultMirror extends NPC {

	{
		spriteClass = VaultMirrorSprite.class;

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

	public Item reward = null;

	public void createReward(HeroClass cls){
		//we create a new generator here as some heroes call RNG here and some don't
		Random.pushGenerator(Random.Long());
			switch (cls) {
				case WARRIOR:
					reward = new BrokenSeal().upgrade().identify(false);
					((BrokenSeal)reward).setGlyph(Armor.Glyph.random());
					break;
				case MAGE:
					reward = new MagesStaff().upgrade(3).identify(false);
					((MagesStaff)reward).enchant();
					break;
				case ROGUE:
					reward = new CloakOfShadows().upgrade(8).identify(false);
					((CloakOfShadows) reward).directCharge(8);
					break;
				case HUNTRESS:
					reward = new SpiritBow().identify(false);
					((SpiritBow)reward).enchant();
					break;
				case DUELIST:
					reward = new MirrorSword().upgrade(3).identify(false);
					((MeleeWeapon)reward).enchant();
					break;
				case CLERIC:
					reward = new HolyTome().upgrade(8).identify(false);
					((HolyTome) reward).directCharge(8);
					break;
				case SPELLSWORD:
					reward = new Spellblade().upgrade(3).identify(false);
					((MeleeWeapon)reward).enchant();
					break;
			}
		Random.popGenerator();
	}

	@Override
	public boolean interact(Char c) {
		if (c instanceof Hero) {
			ShatteredPixelDungeon.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					if (reward != null) {

						String sceneText = Messages.get(VaultMirror.class, "approach") + "\n\n";
						switch (((Hero) c).heroClass){
							case WARRIOR:
								sceneText += Messages.get(VaultMirror.class, "scene_warrior");
								break;
							case MAGE:
								sceneText += Messages.get(VaultMirror.class, "scene_mage");
								break;
							case ROGUE:
								sceneText += Messages.get(VaultMirror.class, "scene_rogue");
								break;
							case HUNTRESS:
								sceneText += Messages.get(VaultMirror.class, "scene_huntress");
								break;
							case DUELIST:
								sceneText += Messages.get(VaultMirror.class, "scene_duelist");
								break;
							case CLERIC:
								sceneText += Messages.get(VaultMirror.class, "scene_cleric");
								break;
							case SPELLSWORD:
								sceneText += Messages.get(VaultMirror.class, "scene_spellsword");
								break;
						}
						sceneText += "\n\n" + Messages.get(VaultMirror.class, "scene_final");

						GameScene.show(new WndOptions(sprite(),
								Messages.titleCase(name()),
								sceneText,
								Messages.get(VaultMirror.class, "take")) {
							@Override
							protected void onSelect(int index) {
								super.onSelect(index);
								if (index == 0) {
									GameScene.show(new WndTitledMessage(sprite(), Messages.titleCase(name()), Messages.get(VaultMirror.class, "scene_take")));
									if (reward.doPickUp((Hero) c)) {
										GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())) );
									} else {
										Dungeon.level.drop(reward, c.pos).sprite.drop();
									}
									Imp.Quest.mirrorUsed = true;
									reward = null;
								}
							}
						});
					} else {
						GameScene.show(new WndTitledMessage(sprite(), Messages.titleCase(name()), Messages.get(VaultMirror.class, "scene_nothing")));
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

	private static final String REWARD = "reward";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (reward != null) {
			bundle.put(REWARD, reward);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(REWARD)){
			reward = (Item) bundle.get(REWARD);
		}
	}

	public static class MirrorSword extends Greatsword {

		{
			//cannot be taken out of the vault
			unique = true;
		}

	}

}
