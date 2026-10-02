/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.InlineText;

/** Save-compatible alias used by earlier SPS-SPD migration builds. */
@Deprecated
public class SpsAcidOoze extends AcidOoze {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpsAcidOoze.class)
			.t("name", "酸蚀")
			.t("desc", "翠绿强酸正在持续侵蚀目标，进入水中可以将其洗去。");
	}



}
