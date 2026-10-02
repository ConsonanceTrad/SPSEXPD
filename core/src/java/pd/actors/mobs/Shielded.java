/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.messages.InlineText;

/** The legacy shielded brute. The complete behavior lives in the save-compatible guard base. */
public class Shielded extends SpsExitMobs.GuardShielded {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Shielded.class)
			.t("name", "持盾豺狼")
			.t("desc", "这个豺狼人带着一块盾牌。说真的它还是带把大剑强点。")
			.t("def_verb", "格挡");
	}



}
