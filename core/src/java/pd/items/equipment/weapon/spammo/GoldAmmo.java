package pd.items.equipment.weapon.spammo;

import pd.Dungeon;
import pd.actors.Char;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class GoldAmmo extends SpAmmo {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoldAmmo.class)
			.t("name", "彩票弹")
			.t("desc", "将原石和种子荚锻造而成的特殊子弹，能消耗金币造成额外伤害。");
	}



	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		int cost = Math.max(0, Dungeon.gold / 100);
		defender.damage((int)(cost * Random.Float(0.25f, 2f)), attacker);
		Dungeon.gold = Math.max(0, Dungeon.gold - cost);
	}
}
