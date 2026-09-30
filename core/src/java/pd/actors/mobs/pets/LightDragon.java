package pd.actors.mobs.pets;

import pd.sprites.LightDragonSprite;

public class LightDragon extends PET {
	{ spriteClass = LightDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.LIGHT_DRAGON; }
}
