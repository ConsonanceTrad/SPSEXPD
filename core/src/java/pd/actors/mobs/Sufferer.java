/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.RedDewdrop;
import pd.items.equipment.artifacts.UnstableSpellbook;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.sprites.SuffererSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the corrupted warlock. */
public class Sufferer extends SpsHallsMobs.Sufferer {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Sufferer.class)
			.t("name", "受难者")
			.t("desc", "这些高估自己的术士尝试控制高等恶魔，却反被腐化和奴役，永远留在了这里。");
	}


	{
		spriteClass = SuffererSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new ScrollOfUpgrade(), new RedDewdrop(), new UnstableSpellbook());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{ScrollOfUpgrade.class, RedDewdrop.class, UnstableSpellbook.class};
	}
}
