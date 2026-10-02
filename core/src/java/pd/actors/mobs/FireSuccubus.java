/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.messages.InlineText;

/** The legacy fire succubus. The complete behavior lives in the save-compatible guard base. */
public class FireSuccubus extends SpsExitMobs.GuardFireSuccubus {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FireSuccubus.class)
			.t("name", "魅魔")
			.t("desc", "魅魔是外表诱人(稍有某种哥特风格)的女性恶魔。通过使用魔法，魅魔可以魅惑英雄，使英雄在魅惑消退前无法攻击它。");
	}



}
