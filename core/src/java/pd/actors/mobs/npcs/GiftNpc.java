/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.GiftNpcSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import pd.windows.WndQuest;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Original SPS tent resident gift and friendship loop. */
public abstract class GiftNpc extends NPC {

	public enum Visual {
		REN("sprites/npcs/sps_town_ren.png", 16, 16,
				15, new int[]{0,0,0,1,1,1,2,2,2,3,3,3}, 20, new int[]{0}, 12, new int[]{0,2,3}, 20, new int[]{0}),
		ASH_WOLF("sprites/npcs/sps_town_ashwolf.png", 16, 16,
				10, new int[]{0,0,0,1,1,1,2,2,2,3,3,3}, 20, new int[]{0}, 12, new int[]{0,2,3}, 20, new int[]{0}),
		COCONUT("sprites/npcs/sps_town_coconut.png", 16, 16,
				10, new int[]{0,0,1,1,0,0,2,2,3,3,2,2}, 20, new int[]{4,5,7,5,7,5},
				12, new int[]{0,8,9,10,11}, 20, new int[]{12,12,12,13,13,13,14,14,14,15}),
		BEGGER("sprites/mobs/sps_vagrant.png", 12, 16,
				2, new int[]{0,0,0,1,0,0,1,1}, 15, new int[]{2,3,4,5,6,7},
				12, new int[]{8,9,10}, 8, new int[]{11,12,13,14}),
		A_FLY("sprites/npcs/sps_town_afly.png", 16, 16,
				15, new int[]{0,0,0,1,1,1,2,2,2,3,3,3}, 20, new int[]{0}, 12, new int[]{0,2,3}, 20, new int[]{0}),
		BA_MECH("sprites/mobs/sps_mrdestructo.png", 16, 16,
				2, new int[]{8,9,10,11}, 12, new int[]{9,10,11}, 15, new int[]{8,12}, 8, new int[]{8,7,13}),
		FRUIT_WORKER("sprites/mobs/sps_scarecrow.png", 16, 16,
				3, new int[]{0,0,1,1,2,2,3,3,3,3,3,3}, 3, new int[]{0,0,1,1,2,2,3,3,3,3,3,3},
				12, new int[]{0,2,3}, 20, new int[]{0}),
		MEAT_SELLER("sprites/mobs/sps_gift_meat_seller.png", 14, 14,
				10, new int[]{1,1,1,1,1,0,0,0,0}, 10, new int[]{1,1,1,1,1,0,0,0,0},
				10, new int[]{1,1,1,1,1,0,0,0,0}, 10, new int[]{1,1,1,1,1,0,0,0,0}),
		TORCH("sprites/mobs/sps_gift_torch.png", 12, 14,
				10, new int[]{0,1,2}, 12, new int[]{0,1,3}, 15, new int[]{4,5,6}, 15, new int[]{7,8,9,10,11,12,13,12}),
		FLY_LING("sprites/npcs/sps_town_whiteling.png", 16, 16,
				3, new int[]{0,11,11,12,12,12,13,13,13}, 20, new int[]{0}, 12, new int[]{0,2,3}, 20, new int[]{0}),
		BUNNY_KEEPER("sprites/mobs/sps_demonrabbit.png", 12, 15,
				6, new int[]{1,0,1,0}, 15, new int[]{2,3,2,3}, 12, new int[]{4,5}, 15, new int[]{1,6,7});

		public final String asset;
		public final int frameWidth, frameHeight;
		public final int idleFps, runFps, attackFps, dieFps;
		public final int[] idleFrames, runFrames, attackFrames, dieFrames;
		Visual(String asset, int frameWidth, int frameHeight,
				int idleFps, int[] idle, int runFps, int[] run,
				int attackFps, int[] attack, int dieFps, int[] die) {
			this.asset = asset;
			this.frameWidth = frameWidth;
			this.frameHeight = frameHeight;
			this.idleFps = idleFps;
			this.runFps = runFps;
			this.attackFps = attackFps;
			this.dieFps = dieFps;
			idleFrames = idle;
			runFrames = run;
			attackFrames = attack;
			dieFrames = die;
		}
	}

	public static final class GiftResult {
		public final String messageKey;
		public final List<Item> items;
		GiftResult(String messageKey, Item... items) {
			this.messageKey = messageKey;
			this.items = Collections.unmodifiableList(Arrays.asList(items));
		}
	}

	private int friendship;

	{
		spriteClass = GiftNpcSprite.class;
		flying = true;
		properties.add(Property.MINIBOSS);
		properties.add(Property.IMMOVABLE);
	}

	public abstract Visual visual();
	public abstract boolean acceptsGift(Item item);
	protected abstract GiftResult reward(Hero hero);

	public int friendship() { return friendship; }
	public void friendship(int value) { friendship = value; }

	protected int friendshipAdjustment() { return 0; }

	public GiftResult receiveGift(Item item, Hero hero) {
		if (item == null || hero == null || !acceptsGift(item)) return null;
		friendship += 10 + friendshipAdjustment();
		return reward(hero);
	}

	protected GiftResult result(String key, Item... items) { return new GiftResult(key, items); }

	//按类名匹配：子类传入的 names 即物品类名（改名需同步调用点）
	protected boolean named(Item item, String... names) {
		if (item == null) return false;
		String name = item.getClass().getSimpleName();
		for (String accepted : names) if (accepted.equals(name)) return true;
		return false;
	}

	@Override public void damage(int damage, Object source) { }

	@Override
	public boolean interact(Char c) {
		if (!(c instanceof Hero)) return true;
		Hero hero = (Hero)c;
		if (sprite != null) sprite.turnTo(pos, hero.pos);
		GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()), Messages.get(this, "normal"),
				Messages.get(GiftNpc.class, "talk"), Messages.get(GiftNpc.class, "gift")) {
			@Override protected void onSelect(int index) {
				if (index == 0) showDialogue(talkKey());
				else if (index == 1) GameScene.selectItem(giftSelector);
			}
		});
		return true;
	}

	private String talkKey() {
		if (friendship <= 20) return "yell1";
		if (friendship <= 50) return "yell2";
		if (friendship <= 80) return "yell3";
		return "yell4";
	}

	private void showDialogue(String key) {
		GameScene.show(new WndQuest(this, Messages.get(this, key)));
	}

	private final WndBag.ItemSelector giftSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(GiftNpc.class, "gift_prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return null; }
		@Override public boolean itemSelectable(Item item) { return true; }
		@Override public void onSelect(Item item) {
			if (item == null) return;
			GiftResult result = receiveGift(item, Dungeon.hero);
			if (result == null) {
				GLog.n(Messages.get(GiftNpc.class, "npc_not_item"));
				return;
			}
			item.detach(Dungeon.hero.belongings.backpack);
			Dungeon.hero.spendAndNext(1f);
			GLog.p(Messages.get(GiftNpc.class, "npc_item", item.name()));
			for (Item reward : result.items) {
				pd.items.Heap heap = Dungeon.level.drop(reward, Dungeon.hero.pos);
				if (heap.sprite != null) heap.sprite.drop();
			}
			showDialogue(result.messageKey);
		}
	};

	public static GiftNpc randomResident() {
		switch (Random.Int(11)) {
			case 0: return new GiftRen();
			case 1: return new GiftAshWolf();
			case 2: return new GiftCoconut();
			case 3: return new GiftBegger();
			case 4: return new GiftAFly();
			case 5: return new GiftBaMech();
			case 6: return new GiftFruitWorker();
			case 7: return new GiftMeatSeller();
			case 8: return new GiftTorch();
			case 9: return new GiftFlyLing();
			default: return new GiftBunnyKeeper();
		}
	}

	private static final String FRIENDSHIP = "friend";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(FRIENDSHIP, friendship); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); friendship = bundle.getInt(FRIENDSHIP); }
}
