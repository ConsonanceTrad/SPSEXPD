/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.BurningFistSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.InfectingFistSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PinningFistSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RottingFistSprite;

/** The active SPS-PD 0.9.8 final boss identity. */
public class Yog extends SpsYog {

	@Override
	protected Fist[] createFists() {
		return new Fist[]{new RottingFist(), new BurningFist(), new PinningFist(), new InfectingFist()};
	}

	@Override
	protected SpsYog.Larva createLarva() {
		return new Larva();
	}

	public static class RottingFist extends SpsYog.RottingFist {
		{ spriteClass = RottingFistSprite.class; }
	}
	public static class BurningFist extends SpsYog.BurningFist {
		{ spriteClass = BurningFistSprite.class; }
	}
	public static class InfectingFist extends SpsYog.InfectingFist {
		{ spriteClass = InfectingFistSprite.class; }
	}
	public static class PinningFist extends SpsYog.PinningFist {
		{ spriteClass = PinningFistSprite.class; }
	}
	public static class Larva extends SpsYog.Larva { }
}
