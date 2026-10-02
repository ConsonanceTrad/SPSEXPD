/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.levels.features;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.effects.CellEmitter;
import pd.effects.particles.ElmoParticle;
import pd.levels.ChaosLevel;
import pd.levels.DeadEndLevel;
import pd.levels.Level;
import pd.levels.NewRoomLevel;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.tiles.CustomTilemap;
import pd.tiles.custom.SpsFeatureVisual;
import pd.utils.GLog;
import pd.windows.WndMessage;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.data.Callback;

import java.util.Iterator;
import pd.messages.InlineText;

/** Handles the readable, flammable signs used by SPS fixed maps. */
public final class Sign {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(Sign.class)
			.t("dead_end", "你在这里干嘛？！")
			.t("pit_message", "这块地方没有出口，所以你需要传送道具。那个遗骸下面可能会有多个。")
			.t("chaos", "无尽模式")
			.t("new_room_0", "标准的特别惊喜地牢样板房。")
			.t("new_room_1", "阿萨男爵设计的森林小屋，据说有个小女巫曾在这里生活过。")
			.t("new_room_2", "坚果设计的荒废草场，面积较大，但是由于土地贫瘠，原先的居住者放弃了这里。")
			.t("new_room_3", "坚果设计的沙滩群岛，大片水域，毫无遮拦，而且没有鲨鱼威胁。")
			.t("new_room_4", "坚果设计的雪山冰原，寒冷而温馨。")
			.t("tip_1", "风景不错吧，这里是壁垒边缘的一个小镇。被封锁的下水道就在前方，调查其被封锁的原因吧。")
			.t("tip_2", "欢迎来到这个地牢。如果你是一名新手的话，请打开背包，使用随机灵魂。那样你就能获得你的第一只宠物。宠物的实力是有限的。如果你并不想提升你自己的实力，那么你需要一只很强的宠物。")
			.t("tip_3", "时间总是在不经意之间流逝。在状态栏里面确认当前时间。不要忘了在夜晚准备足够的光源。")
			.t("tip_4", "肉是肉，蘑菇是蔬菜，口粮是主食，梅果是水果。再加上一些由露珠合成的水，你应该能烧出各种各样的美食。")
			.t("tip_5", "下水道最深处有什么，是被黑暗吞噬的元素，还是探索未知的学者，还是一株异国的植物。去看看吧。")
			.t("tip_6", "解决了下水道事件后，你来到了邪角监狱门口。“像素商店”——为成功的探险人士提供一切所需。同时，出售一间小屋！")
			.t("tip_7", "高塔已将枪械投入测试。如果你想的话，你可以把原石和原石锻造成重铅弹。当然你也可以试试其他配方，比如原石和种子什么的。")
			.t("tip_8", "不同种族都能在这种地方被发现，他们有着各自的抗性和弱点，利用这些有助于你的舒适冒险。")
			.t("tip_9", "极度饥饿会让你受伤，但是只要是饥饿就会使你的攻击变得无力。")
			.t("tip_10", "监狱的中心地区，中心控制室，典狱长在这里守望，天狗藏于阴影，而亡灵随时准备突破。做好准备。")
			.t("tip_11", "从监狱离开后，你前往位于宫殿的一处洞穴。据说时间在这个洞穴中是乱的。“像素商店”——花更多的钱，走更远的路。出售观光游票。")
			.t("tip_12", "地牢里面隐藏着大量的陷阱和暗门，以及一些极端的气候环境。不要冲得太快。")
			.t("tip_13", "一般来说，武器会自带一个特殊效果。这种特殊效果在低阶武器上表现得更加明显。而防具分成轻、常规、重三种。不同防具提供的额外效果不同。")
			.t("tip_14", "炸弹和飞镖都是一次性的消耗品。明智地使用它们。远程武器可以提升箭头的伤害。")
			.t("tip_15", "时间在这里交汇，事件在这里发生，到底是机械能够重新生产，还是野兽侵占完全，抑或是由完全不知所以的东西控制……未来在你们手中。")
			.t("tip_16", "洞穴终点将你传送到部落的一处废墟前。“像素集市”——让你在地牢中更加安全。")
			.t("tip_17", "有些状态会互相抵消，而有些状态会互相加强。确保你处于最强状态以便战胜其他敌人。")
			.t("tip_18", "小镇一直在变化，和你一样。")
			.t("tip_19", "很多东西都没被完成，他们会在未来被解决。")
			.t("tip_20", "混乱之战的高潮，一位国王，一位领袖，一个亡灵，胜利花落谁家，由你来揭晓。")
			.t("tip_21", "好了，最后一个任务，调查深渊。“像素集市”——为恶魔猎手提供的特价优惠！展示特殊的非卖品！")
			.t("tip_22", "现在看告示牌貌似没啥意义了，你确定要继续看下去吗。")
			.t("tip_23", "深渊，位于我们能观测到的最边缘地区。没人知道深渊对面是什么。")
			.t("tip_24", "目前并没有探险队能够深入到最底部。这里大部分的生物都附带了恶魔的特性。他们更加强大。")
			.t("tip_25", "邪神就在前方，学习如何驱逐它，并使用它们的力量……")
			.t("burn", "就在你试图阅读它的那一刻，它爆燃成了一团绿色的火焰。");
	}


	private static final int LAST_TIP_DEPTH = 25;

	public static void read(int pos) {
		if (pos == Dungeon.level.pitSign) {
			//SPS: UI 构造必须切回渲染线程（Sign.read 由 Hero 的 actor 流程调用，直接 new 会崩）
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndMessage(Messages.get(Sign.class, "pit_message")));
				}
			});
			return;
		}

		String key = messageKey(Dungeon.level, Dungeon.depth, Statistics.roomType);
		if (key == null) return;
		final String fkey = key;
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show(new WndMessage(Messages.get(Sign.class, fkey)));
			}
		});
		if (key.startsWith("tip_") && Dungeon.depth >= 22) burn(pos);
	}

	static String messageKey(Level level, int depth, int roomType) {
		if (level instanceof DeadEndLevel) return "dead_end";
		if (level instanceof ChaosLevel) return "chaos";
		if (level instanceof NewRoomLevel) {
			return "new_room_" + (roomType >= 0 && roomType <= 4 ? roomType : 0);
		}
		return depth >= 1 && depth <= LAST_TIP_DEPTH ? "tip_" + depth : null;
	}

	private static void burn(int pos) {
		Level.set(pos, Terrain.EMBERS);
		Iterator<CustomTilemap> iterator = Dungeon.level.customTiles.iterator();
		while (iterator.hasNext()) {
			CustomTilemap tile = iterator.next();
			if (tile instanceof SpsFeatureVisual
					&& ((SpsFeatureVisual) tile).feature() == SpsFeatureVisual.SIGN
					&& tile.tileX == pos % Dungeon.level.width()
					&& tile.tileY == pos / Dungeon.level.width()) {
				((SpsFeatureVisual) tile).destroy();
				iterator.remove();
			}
		}
		GameScene.updateMap(pos);
		GLog.w(Messages.get(Sign.class, "burn"));
		CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
	}

	private Sign() {
	}
}
