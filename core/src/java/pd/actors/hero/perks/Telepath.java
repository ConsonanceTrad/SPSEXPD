/*
 * 心灵感知 —— 隔墙感知附近的敌人。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class Telepath extends Perk {

	static {
		InlineText.of(Telepath.class)
				.t("title", "心灵感知")
				.t("desc", "感知周围 %s 格内的敌人，即使隔着墙。");
	}

	public Telepath() {
		super(2);
	}

	@Override
	public int image() {
		return PerkImageSheet.TELEPATH;
	}

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero.heroClass == HeroClass.HUNTRESS || !hero.heroPerk.has(Telepath.class);
	}

	public int range() {
		return level() * 2;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(range()));
	}
}
