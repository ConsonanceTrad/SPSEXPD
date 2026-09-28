package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/** The original empty pocket ball, thrown directly at a companion to recover its soul. */
public class PocketBall extends Item {
	{
		image = ItemSpriteSheet.POCKET_BALL;
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
