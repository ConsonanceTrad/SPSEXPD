/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

/** The energy-core page, which unlocks adventure destination 7. */
public class EnergyCore extends JournalPage {
	{
		image = SpecificTaskDict.TELEPORT_COORDINATE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnergyCore.class)
			.t("name", "Otiluck的旅行日志之能源核心")
			.t("desc", "能源核心的地址。\n\n说明：这是一个自动攻击的生物。它由zot创造，在我打败zot后我控制了这个生物。");
	}



	public EnergyCore() {
		super(7);
	}
}
