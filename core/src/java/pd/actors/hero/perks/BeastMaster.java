/*
 * 驯兽大师 —— 与魂石共鸣：献祭魂石以永久获得该生物的能力特质，或炸环以下一次攻击换取它的力量。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class BeastMaster extends Perk {

	static {
		InlineText.of(BeastMaster.class)
			.t("title", "驯兽大师")
			.t("desc", "你能与魂石共鸣：\n\n1 级：你的伙伴投影可以与你站在同一格，不再挡住窄道。\n2 级：解锁「献祭」—— 消耗魂石，永久获得该生物的能力特质。\n3 级：解锁「炸环」—— 消耗魂石，为你的下一次攻击换取增幅（幅度随生物种类与培养程度提升）。");
	}

	public BeastMaster() {
		super(3);
	}
}