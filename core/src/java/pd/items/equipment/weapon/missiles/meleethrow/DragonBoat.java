/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.meleethrow;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import com.badlogic.gdx.Gdx;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.items.KindOfWeapon;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DragonBoat extends MeleeThrowWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.DRAGON_BOAT;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DragonBoat.class)
			.t("name", "龙舟模型")
			.t("desc", "木制的龙舟模型，说实在的应该放在玻璃瓶里。\n钝器，飞掷，易碎-尖锐");
	}



	public DragonBoat() { super(1, 5, 10, EquipmentEquipWeaponBasicWeaponDict.DRAGON_BOAT); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.prolong(defender, Paralysis.class, 3f);
		if (Random.Int(100) == 1) {
			destroy(attacker);
			Buff.affect(defender, Bleeding.class).set(50);
			if (Gdx.app != null) GLog.n(Messages.get(KindOfWeapon.class, "destory"));
		}
		return super.proc(attacker, defender, damage);
	}
}
