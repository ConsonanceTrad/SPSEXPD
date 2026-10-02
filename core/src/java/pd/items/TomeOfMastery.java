/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.actors.buffs.Blindness;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The SPS leather tome which offers the reader's two original subclasses. */
public class TomeOfMastery extends TengusMask {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TomeOfMastery.class)
			.t("name", "精通之书")
			.t("ac_read", "阅读")
			.t("blind", "在失明的时候你没法阅读它。")
			.t("way", "你选择了走上%s的道路！")
			.t("desc", "这本皮封典籍不算厚，但你隐约感觉能从中学到不少东西。阅读这本典籍需要一些时间。");
	}



	public static final String AC_READ = "READ";
	public static final float TIME_TO_READ = 10f;
	private Hero reader;
	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
		defaultAction = AC_READ;
	}
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove("WEAR");
		if (!actions.contains(AC_READ)) actions.add(AC_READ);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (!AC_READ.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (hero.buff(Blindness.class) != null) {
			GLog.w(Messages.get(this, "blind"));
			return;
		}
		reader = hero;
		super.execute(hero, "WEAR");
	}
	@Override public void choose(HeroSubClass way) {
		if (reader != null) reader.spend(TIME_TO_READ - 1f);
		super.choose(way);
	}
}
