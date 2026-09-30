package pd.actors.mobs.pets;

import pd.sprites.GoldDragonSprite;

public class GoldDragon extends PET {
	{ spriteClass = GoldDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.GOLD_DRAGON; }
}
