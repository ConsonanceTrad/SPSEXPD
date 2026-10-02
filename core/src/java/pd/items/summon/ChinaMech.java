/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.summon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MindVision;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.equipment.weapon.missiles.fusion.RocketMissile;
import pd.sprites.PatrolUAVSprite;
import pd.messages.InlineText;

/**
 * SPS 0.9.9 壁垒支援用无人机：使用后投掷到目标点生成 HW大疆号支援无人机（对照 0.9.9 ChinaMech）。
 * 无人机不可被施加 buff、不会攻击，只会跟随英雄；死亡掉落高级食物与火箭，并给英雄短暂心灵视域。
 * 礼物商店 DEF_ROBOT 解锁的开局奖励。
 */
public class ChinaMech extends SpsSummonItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChinaMech.class)
			.t("name", "壁垒支援用无人机")
			.t("ac_active", "使用")
			.t("desc", "呼叫支援，呼叫支援。")
			.t("$huaweidajiang.name", "HW大疆号")
			.t("$huaweidajiang.desc", "壁垒用于支援开拓者的无人机，内含高级食物及若干火箭。");
	}




	private static boolean activate;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public void execute(Hero hero, String action) {
		activate = AC_ACTIVE.equals(action);
		if (activate) beginActivation(hero);
		else super.execute(hero, action);
	}

	@Override
	protected void onThrow(int cell) {
		if (!activate || Dungeon.level.pit[cell]) {
			super.onThrow(cell);
		} else {
			summonOrRecover(cell, new HuaweiDajiang());
		}
		activate = false;
	}

	/** HW大疆号：跟随英雄的支援无人机（对照 0.9.9 ChinaMech.HuaweiDajiang）。 */
	public static class HuaweiDajiang extends Mob {

		{
			spriteClass = PatrolUAVSprite.class;
			HP = HT = 10;
			defenseSkill = 1;
			EXP = 1;
			flying = true;
			alignment = Alignment.ALLY;
			state = WANDERING;
			properties.add(Property.MECH);
		}

		@Override
		public synchronized boolean add(Buff buff) {
			//0.9.9: 无人机不受任何 buff 影响
			return false;
		}

		@Override
		protected boolean canAttack(Char enemy) {
			return false;
		}

		@Override
		public int damageRoll() {
			return 1;
		}

		@Override
		public int attackSkill(Char target) {
			return 1;
		}

		@Override
		protected boolean getCloser(int target) {
			//0.9.9: 无视目标，始终走向英雄
			return super.getCloser(Dungeon.hero.pos);
		}

		@Override
		public void die(Object cause) {
			super.die(cause);
			Dungeon.level.drop(Generator.random(Generator.Category.HIGHFOOD), pos);
			Dungeon.level.drop(new RocketMissile().quantity(2), pos);
			if (Dungeon.hero != null) Buff.affect(Dungeon.hero, MindVision.class, 10f);
		}
	}
}
