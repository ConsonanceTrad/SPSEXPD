package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.watabou.utils.Bundle;

/** Save-compatible soul produced when an empty pocket ball captures a migrated pet. */
public class CapturedPetEgg extends Egg {
	private static final String PET_TYPE = "pet_type";
	private int petType;
	public CapturedPetEgg() { this(501); }
	public CapturedPetEgg(int petType) { this.petType = petType; }
	@Override protected LegacyPet hatchling() {
		return PocketBallFull.createPet(petType);
	}
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(PET_TYPE, petType); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); petType = bundle.getInt(PET_TYPE); }
}
