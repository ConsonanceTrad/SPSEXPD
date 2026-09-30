package pd.actors.mobs.pets;

import pd.sprites.LerySprite;

public class LeryFire extends PET {
	{ spriteClass = LerySprite.class; properties.add(Property.ELEMENT); updateStats(true); }
	@Override protected Kind kind() { return Kind.LERY_FIRE; }
}
