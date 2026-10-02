package pd.items;
import pd.Dungeon;
import pd.messages.InlineText;
public class TriforceOfWisdom extends TriforcePiece {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TriforceOfWisdom.class)
			.t("name", "智慧三角")
			.t("desc", "起源三角的一部分，代表着智慧。");
	}

	@Override protected void collected() { Dungeon.triforceOfWisdom = true; }
}
