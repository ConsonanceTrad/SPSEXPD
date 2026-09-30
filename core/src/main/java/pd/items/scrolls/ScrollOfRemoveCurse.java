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

package pd.items.scrolls;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Degrade;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.LightShootAttack;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.Flare;
import pd.effects.particles.ShadowParticle;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.armor.Armor;
import pd.items.bags.Bag;
import pd.items.wands.Wand;
import pd.items.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ScrollOfRemoveCurse extends InventoryScroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_REMCURSE;
		preferredBag = Belongings.Backpack.class;
	}

	@Override
	public void doRead() {
		detach(curUser.belongings.backpack);
		if (curUser.sprite != null) new Flare(6, 32).show(curUser.sprite, 2f);
		Sample.INSTANCE.play(Assets.Sounds.READ);
		Invisibility.dispel();

		ArrayList<Item> belongings = new ArrayList<>();
		for (Item item : curUser.belongings) belongings.add(item);
		boolean procced = uncurse(curUser, belongings.toArray(new Item[0]));
		Buff.detach(curUser, STRDown.class);

		GLog.i(Messages.get(this, procced ? "cleansed" : "not_cleansed"));
		identify();
		readAnimation();
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return uncursable(item);
	}

	public static boolean uncursable( Item item ){
		if (item.isEquipped(Dungeon.hero) && Dungeon.hero.buff(Degrade.class) != null) {
			return true;
		} if ((item instanceof EquipableItem || item instanceof Wand) && ((!item.isIdentified() && !item.cursedKnown) || item.cursed)){
			return true;
		} else if (item instanceof Weapon){
			return ((Weapon)item).hasCurseEnchant();
		} else if (item instanceof Armor){
			return ((Armor)item).hasCurseGlyph();
		} else {
			return false;
		}
	}

	@Override
	protected void onItemSelected(Item item) {
		new Flare( 6, 32 ).show( curUser.sprite, 2f );

		boolean procced = uncurse( curUser, item );

		if (curUser.buff(Degrade.class) != null) {
			Degrade.detach(curUser, Degrade.class);
			procced = true;
		}

		if (procced) {
			GLog.p( Messages.get(this, "cleansed") );
		} else {
			GLog.i( Messages.get(this, "not_cleansed") );
		}
	}

	public static boolean uncurse( Hero hero, Item... items ) {
		boolean procced = uncurseItems(items);
		if (Dungeon.level != null) {
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (Dungeon.level.heroFOV[mob.pos]) Buff.affect(mob, LightShootAttack.class).level(6);
			}
		}

		if (procced) {
			if (hero != null) {
				if (hero.sprite != null) hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
				hero.updateHT(false); //for ring of might
				updateQuickslot();
			}
			Badges.validateClericUnlock();
		}
		return procced;
	}

	private static boolean uncurseItems(Item... items) {
		boolean procced = false;
		for (Item item : items) {
			if (item != null) {
				item.cursedKnown = true;
				if (item.cursed) {
					procced = true;
					item.uncurse();
					if (item.level() < 0) item.upgrade(-item.level() * 2);
				}
			}
			if (item instanceof Bag) {
				procced = uncurseItems(((Bag) item).items.toArray(new Item[0])) || procced;
			}
			if (item instanceof Weapon){
				Weapon w = (Weapon) item;
				if (w.hasCurseEnchant()){
					w.enchant(null);
					procced = true;
				}
			}
			if (item instanceof Armor){
				Armor a = (Armor) item;
				if (a.hasCurseGlyph()){
					a.inscribe(null);
					procced = true;
				}
			}
			if (item instanceof Wand){
				((Wand) item).updateLevel();
			}
		}
		return procced;
	}

	@Override
	public void empoweredRead() {
		for (Item item : curUser.belongings) if (item.cursed) item.cursedKnown = true;
		Sample.INSTANCE.play(Assets.Sounds.READ);
		Invisibility.dispel();
		doRead();
	}
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
