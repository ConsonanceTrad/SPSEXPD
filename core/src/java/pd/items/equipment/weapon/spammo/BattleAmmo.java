package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class BattleAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BattleAmmo.class)
			.t("name", "战斗弹")
			.t("desc", "将原石和吞星种锻造而成的特殊子弹，能增强使用者的战斗能力。");
	}



	private static final ItemSprite.Glowing DEEP_GREEN = new ItemSprite.Glowing(0x006633);
	@Override public ItemSprite.Glowing glowing() { return DEEP_GREEN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.5f * damage), attacker);
		Buff.prolong(attacker, AttackUp.class, 5f).level(35);
		Buff.prolong(attacker, DefenceUp.class, 5f).level(35);
	}
}
