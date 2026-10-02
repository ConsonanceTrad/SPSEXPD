/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

public class Sokoban4 extends JournalPage {
	{
		image = SpecificTaskDict.TELEPORT_COORDINATE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sokoban4.class)
			.t("name", "Otiluck的旅行日志之终极挑战")
			.t("desc", "终极挑战的地址。\n\n似乎这些生物十分害怕我。我该说这里民风淳朴呢还是我长得太危险了呢？当我尝试靠近它们时，它们就会把我传送到另一个位置。好在这里还有一些可食用的食物，否则我肯定会饿死在这里。\n\n当我第61次被传送走时，我看到了一块巨大的石碑。令我吃惊的是，它似乎是在讲述一段历史。只要我能读懂这段历史，我就能知晓它们的语言...");
	}



	public Sokoban4() { super(4); }
}
