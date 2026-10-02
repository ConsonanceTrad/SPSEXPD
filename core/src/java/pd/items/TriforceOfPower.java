package pd.items;
import pd.Dungeon;
import pd.messages.InlineText;
public class TriforceOfPower extends TriforcePiece {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TriforceOfPower.class)
			.t("name", "力量三角")
			.t("desc", "起源三角的一部分，代表着力量。");
	}

	@Override protected void collected() { Dungeon.triforceOfPower = true; }
}
