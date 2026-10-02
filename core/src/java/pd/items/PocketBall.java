package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.pets.LegacyPet;
import pd.effects.particles.ShadowParticle;
import pd.messages.Messages;
import pd.utils.GLog;

/** The original empty pocket ball, thrown directly at a companion to recover its soul. */
public class PocketBall extends Item {
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
	}
	public PocketBall() { this(1); }
	public PocketBall(int quantity) { this.quantity = quantity; }
	@Override protected void onThrow(int cell) {
		Char target = Actor.findChar(cell);
		if (!(target instanceof LegacyPet)) {
			super.onThrow(cell);
			return;
		}
		LegacyPet pet = (LegacyPet) target;
		if (pet.sprite != null) pet.sprite.emitter().burst(ShadowParticle.CURSE, 6);
		Item egg = PocketBallFull.petEgg(pet.legacyType());
		if (egg == null) egg = new CapturedPetEgg(pet.legacyType());
		Heap heap = Dungeon.level.drop(egg, cell);
		if (heap.sprite != null) heap.sprite.drop();
		pet.destroy();
		if (pet.sprite != null) pet.sprite.killAndErase();
		GLog.n(Messages.get(this, "get_pet"));
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
