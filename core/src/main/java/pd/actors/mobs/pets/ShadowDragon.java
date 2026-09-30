package pd.actors.mobs.pets;

import pd.sprites.ShadowDragonSprite;

public class ShadowDragon extends PET {
	{ spriteClass = ShadowDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.SHADOW_DRAGON; }
}
