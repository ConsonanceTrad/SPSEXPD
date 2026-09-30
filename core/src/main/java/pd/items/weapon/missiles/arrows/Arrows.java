/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.weapon.missiles.arrows;

import pd.Assets;
import pd.items.weapon.missiles.MissileWeapon;
import pd.sprites.ItemSprite;

/** Base for SPS-PD's consumable special arrows. */
public class Arrows extends MissileWeapon {

	private final int baseMin;
	private final int baseMax;

	public Arrows(int min, int max) {
		baseMin = min;
		baseMax = max;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 1;
		levelKnown = true;
		DLY = 0.2f;
		quantity(1);
	}

	@Override public int min(int lvl) { return baseMin; }
	@Override public int max(int lvl) { return baseMax; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public int value() { return quantity() * 2; }
	@Override public ItemSprite.Glowing glowing() { return GRAY; }

	private static final ItemSprite.Glowing GRAY = new ItemSprite.Glowing(0x888888);
}
