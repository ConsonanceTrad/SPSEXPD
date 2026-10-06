package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.ChocoboEgg;
import pd.items.consum.eggs.DaturaEgg;
import pd.items.consum.eggs.RibbonRatEgg;
import pd.messages.InlineText;
public class RandomEgg9 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg9.class)
			.t("name", "随机九月灵魂")
			.t("desc", "获得一颗随机的九月魂石，包括缎带鼠、陆行鸟、曼陀罗。");
	}


 public RandomEgg9() { super(RibbonRatEgg.class, ChocoboEgg.class, DaturaEgg.class); } }
