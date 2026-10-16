/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.items.equipment.weapon.guns.GunArchetype;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 殉道者 —— 信徒二档（王冠 B 路线）的枪械。
 *
 * <p>霰弹枪构型：一次开火打出多颗弹丸，沿用_通用弹匣_装填；对_恶魔类_生物的伤害提升更多。</p>
 */
public class Martyr extends GunWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.FOLLOWER_CUSHION_T2_2_MARTYR;

	/** 每次开火打出的弹丸数。 */
	public static final int PELLETS = 4;
	/** 对恶魔类生物的额外伤害。 */
	public static final int DEMON_BONUS = 12;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Martyr.class)
			.t("name", "殉道者")
			.t("desc", "一把霰弹枪构型的发射器，枪托上刻着一行祷文。\n\n它用_通用弹匣_装填，一次开火打出 _%1$d 颗弹丸_。\n\n对_恶魔类_生物，每颗弹丸的伤害都会额外增加 %2$d。");
	}

	{
		//SPSEXPD: 暂借用现成枪械图标
		image = EquipmentEquipWeaponGunDict.GUN_A;
	}

	public Martyr() {
		super(GunArchetype.PISTOL);
		//SPSEXPD: 霰弹枪构型——一次开火多颗弹丸
		shotsPerFire = PELLETS;
	}

	@Override
	protected void onBulletHit(Char attacker, Char defender, int damage) {
		if (defender != null && defender.isAlive() && defender.properties().contains(Char.Property.DEMONIC)) {
			defender.damage(DEMON_BONUS, this);
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
