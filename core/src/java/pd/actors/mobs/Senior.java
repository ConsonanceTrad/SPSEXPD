/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.sprites.SeniorSprite;
import pd.messages.InlineText;

/** Original SPS runtime/save identity for the fully migrated senior monk. */
public class Senior extends SpsExitMobs.GuardSenior {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Senior.class)
			.t("name", "武僧大师")
			.t("desc", "相较普通武僧而言，武僧大师变秃了，也变强了!");
	}



	{
		spriteClass = SeniorSprite.class;
	}
}
