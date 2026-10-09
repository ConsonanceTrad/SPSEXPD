package pd.windows;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Tinkerer2;
import pd.items.quest.Mushroom;
import pd.messages.Messages;
import pd.messages.InlineText;
import pd.utils.GLog;

/**
 * SPSEXPD: 二次强化入口。原先是三选一奖励窗口（无人机/仙女卡牌/遥控卫星），
 * 现已取消选择——带任务蘑菇对话即直接完成，只解锁露珠瓶二阶能力（清洗/加速）。
 * 类名与文件名为兼容既有测试而保留。
 */
public final class WndTinkerer2 {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(WndTinkerer2.class)
			.t("info", "哦，你找到那个啦。万分感谢，我再改进一下你的露珠瓶，它现在能种植了。")
			.t("farewell", "小镇见，%s！");
	}

	private WndTinkerer2() {
	}

	//SPSEXPD: 二次强化直接完成，不再提供奖励选择
	public static void performUpgrade(Tinkerer2 tinkerer, Hero hero, Mushroom mushroom) {
		if (tinkerer == null || hero == null || mushroom == null) return;

		mushroom.detach(hero.belongings.backpack);
		//SPSEXPD: 二阶解锁「种植」；只加不清零
		Dungeon.dewDraw = true;
		tinkerer.yell(Messages.get(WndTinkerer2.class, "farewell", hero.name()));
		GLog.p(Messages.get(WndTinkerer2.class, "info"));
		tinkerer.destroy();
		tinkerer.sprite.die();
	}
}
