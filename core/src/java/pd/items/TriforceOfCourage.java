package pd.items;
import pd.Dungeon;
import pd.messages.InlineText;
public class TriforceOfCourage extends TriforcePiece {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TriforceOfCourage.class)
			.t("name", "勇气三角")
			.t("desc", "起源三角的一部分，代表着勇气。");
	}



	@Override protected void collected() { Dungeon.triforceOfCourage = true; }
}
