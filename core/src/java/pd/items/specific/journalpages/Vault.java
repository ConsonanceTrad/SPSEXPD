/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.messages.InlineText;

public class Vault extends JournalPage {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Vault.class)
			.t("name", "宝地坐标")
			.t("desc", "一个字迹潦草的地点坐标，旁边有2020.1.25的字样。");
	}



	public Vault() { super(6); }
}
