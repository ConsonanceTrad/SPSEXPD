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

package pd.items;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.MagicImmune;
import pd.actors.hero.Hero;
import pd.effects.particles.ShadowParticle;
import pd.journal.Document;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.Bundle;

import java.util.ArrayList;

public abstract class EquipableItem extends Item {

	public static final String AC_EQUIP		= "EQUIP";
	public static final String AC_UNEQUIP	= "UNEQUIP";

	{
		bones = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( isEquipped( hero ) ? AC_UNEQUIP : AC_EQUIP );
		return actions;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (super.doPickUp(hero, pos)){
			if (!isIdentified() && !Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_IDING)){
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_IDING);
			}
			return true;
		} else {
			return false;
		}
	}

	protected static int slotOfUnequipped = -1;
	private boolean forceUnequipWithoutTime;

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_EQUIP )) {
			//In addition to equipping itself, item reassigns itself to the quickslot
			//This is a special case as the item is being removed from inventory, but is staying with the hero.
			int slot = Dungeon.quickslot.getSlot( this );
			slotOfUnequipped = -1;
			doEquip(hero);
			if (slot != -1) {
				Dungeon.quickslot.setSlot( slot, this );
				updateQuickslot();
			//if this item wasn't quickslotted, but the item it is replacing as equipped was
			//then also have the item occupy the unequipped item's quickslot
			} else if (slotOfUnequipped != -1 && defaultAction() != null) {
				Dungeon.quickslot.setSlot( slotOfUnequipped, this );
				updateQuickslot();
			}
		} else if (action.equals( AC_UNEQUIP )) {
			doUnequip( hero, true );
		}
	}

	@Override
	public void doDrop( Hero hero ) {
		if (!isEquipped( hero ) || doUnequip( hero, false, false )) {
			super.doDrop( hero );
		}
	}

	@Override
	public void cast( final Hero user, int dst ) {

		if (isEquipped( user )) {
			if (quantity == 1 && !this.doUnequip( user, false, false )) {
				return;
			}
		}

		super.cast( user, dst );
	}

	public static void equipCursed( Hero hero ) {
		hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
		Sample.INSTANCE.play( Assets.Sounds.CURSED );
	}

	protected float timeToEquip( Hero hero ) {
		return 1f;
	}

	public abstract boolean doEquip( Hero hero );

	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {

		if (!forceUnequipWithoutTime && cursed
				&& hero.buff(MagicImmune.class) == null
				&& (!hero.belongings.lostInventory() || keptThroughLostInventory())) {
			GLog.w(Messages.get(EquipableItem.class, "unequip_cursed"));
			return false;
		}

		if (!forceUnequipWithoutTime) {
			if (single) {
				hero.spendAndNext( timeToEquip( hero ) );
			} else {
				hero.spend( timeToEquip( hero ) );
			}
		}

		slotOfUnequipped = Dungeon.quickslot.getSlot(this);

		//temporarily keep this item so it can be collected
		boolean wasKept = keptThoughLostInvent;
		keptThoughLostInvent = true;
		if (!collect || !collect( hero.belongings.backpack )) {
			onDetach();
			Dungeon.quickslot.clearItem(this);
			updateQuickslot();
			if (collect) Dungeon.level.drop( this, hero.pos ).sprite.drop();
		}
		keptThoughLostInvent = wasKept;

		return true;
	}

	/** Removes equipment destroyed by an external effect while still running subclass cleanup. */
	public final boolean forceUnequipWithoutTime(Hero hero) {
		forceUnequipWithoutTime = true;
		try {
			return doUnequip(hero, false, false);
		} finally {
			forceUnequipWithoutTime = false;
		}
	}

	final public boolean doUnequip( Hero hero, boolean collect ) {
		return doUnequip( hero, collect, true );
	}

	public void activate( Char ch ){}

}
