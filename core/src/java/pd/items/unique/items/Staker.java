/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.items.consum.materials.DryTwig;
import pd.items.equipment.weapon.guns.GunArchetype;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 尖钉发射器 —— 信徒一档（面具 B 路线）的枪械。
 *
 * <p>用_枯枝_装填，一次一发；对_恶魔类_生物伤害额外提升。</p>
 */
public class Staker extends GunWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.FOLLOWER_CUSHION_T1_2_STAKER;

	/** 对恶魔类生物的额外伤害。 */
	public static final int DEMON_BONUS = 8;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Staker.class)
			.t("name", "尖钉发射器")
			.t("no_twig", "你没有可以装填的枯枝。")
			.t("desc", "一台把尖钉钉进东西里的装置。它吃的是_枯枝_，一次一发。\n\n对_恶魔类_生物，它打出的伤害会额外增加 %1$d。");
	}

	{
		//SPSEXPD: 暂借用现成枪械图标
		image = EquipmentEquipWeaponGunDict.GUN_A;
	}

	public Staker() {
		super(GunArchetype.PISTOL);
		//SPSEXPD: 木装填——弹匣一次只装一发
		fullCharge = 1;
	}

	@Override
	protected boolean consumeReload(Hero hero) {
		if (hero == null) return false;
		DryTwig twig = hero.belongings.getItem(DryTwig.class);
		if (twig == null) return false;
		if (twig.quantity() > 1) {
			twig.quantity(twig.quantity() - 1);
		} else {
			twig.detach(hero.belongings.backpack);
		}
		return true;
	}

	@Override
	protected String reloadHintKey() {
		return "no_twig";
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
