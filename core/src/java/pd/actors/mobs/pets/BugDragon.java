package pd.actors.mobs.pets;

import pd.sprites.BugDragonSprite;
import pd.messages.InlineText;

public class BugDragon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BugDragon.class)
			.t("name", "BUG龙")
			.t("desc", "??????");
	}

	{ spriteClass = BugDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.BUG_DRAGON; }
}
