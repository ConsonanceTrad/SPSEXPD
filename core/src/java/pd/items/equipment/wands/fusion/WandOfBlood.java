package pd.items.equipment.wands.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.equipment.wands.DamageWand;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentWandBasicWandDict;

/** The original SPS-PD blood wand, kept in the fusion package for save compatibility. */
public class WandOfBlood extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfBlood.class)
			.t("name", "鲜血法杖")
			.t("staff_name", "血契魔杖")
			.t("ondeath", "你在血契中耗尽了自己的生命...")
			.t("charged", "敌人的生命能量流入血契魔杖！")
			.t("desc", "这根_暗属性_法杖能发射黑暗能量，如果法杖头上装饰用的小骷髅还不够直白地揭示这一点的话。")
			.t("stats_desc", "该法杖会释放腐坏能量，造成_%1$d~%2$d点伤害_并用于治愈自身。")
			.t("upgrade_stat_name_1", "盟友治疗")
			.t("upgrade_stat_name_2", "自身护盾")
			.t("upgrade_stat_name_3", "亡灵伤害")
			.t("bmage_desc", "战斗法师攻击被魅惑的目标时会获得护盾，并免除下一次血契的生命消耗。")
			.t("eleblast_desc", "血契魔杖的元素风暴会魅惑敌人、治疗盟友并伤害敌对亡灵。");
	}




	{
		image = EquipmentWandBasicWandDict.WAND_CORRUPTION_0;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override public int min(int level) { return level; }
	@Override public int max(int level) { return 6 + 2 * level; }

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			target.damage((int)(damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
			if (curUser.HP < curUser.HT) {
				int healing = Random.Int(0, damageRoll());
				curUser.HP += Math.min(healing, curUser.HT - curUser.HP);
			}
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.darkhit();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.SHADOW,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
