/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.quest;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Imp;
import pd.actors.mobs.npcs.VaultTokenDoor;
import pd.actors.mobs.quest.vault.VaultBossElemental;
import pd.items.BrokenSeal;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.levels.Level;
import pd.levels.Transitions;
import pd.levels.VaultLevel;
import pd.levels.features.LevelTransition;
import pd.levels.rooms.quest.vault.VaultFinalRoom;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.sprites.ImpSprite;
import pd.sprites.ItemSprite;
import pd.ui.QuickSlotButton;
import pd.windows.WndBag;
import pd.windows.WndError;
import pd.windows.WndOptions;
import pd.windows.WndTitledMessage;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

public class EscapeCrystal extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EscapeCrystal.class)
			.t("name", "逃脱棱晶")
			.t("ac_use", "使用")
			.t("prompt", "选择一件物品")
			.t("desc", "一块似乎内含着传送魔法的细长棱晶。你随时都可以使用这块棱晶离开宝库，但若是提早离开的话可别想拿到最好的报酬！")
			.t("discover_hint", "你可在某个任务中找到该物品。")
			.t("injure_warning_1", "时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“天哪，看起来你伤得有点重，你确定你那边一切还好吗？我是很想要那座雕像啦，但我更希望你不要把命丢掉！”")
			.t("injure_warning_2", "“如果你现在处境不妙的话，可以先用_初始房间里的传送道具_想办法回到那边去，休整好了再重新出去探索。”")
			.t("injure_warning_3", "“假如情况实在严峻到你根本没法解决了，那你还是_用我给你的棱晶逃出宝库_吧。不过你可别想着这样还能从我这里拿到多少报酬，我能让你保住小命就不错了！”")
			.t("leaving_start", "时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“听着，我完全理解你有可能会紧张，不过能不能麻烦您起码走出第一个房间出去看看？”\n\n“你大可不必担心我把你的装备偷走，我这个恶魔一向守信用，我保证你回来的时候它们都会齐齐整整物归原主。万一你真的遇到危险了，我也肯定会让你传送回来的，我向你发誓。”")
			.t("leaving_early", "时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“真的假的，这就打算跑路了吗！？想从宝库里绝大多数敌人的眼前溜走很容易，这你明明是知道的吧？”\n\n“天哪，假如你真的觉得自己现在就有生命危险了，我当然会把你传送出来，可你也别想带什么别的宝物回来了。我还以为真的能指望你帮点忙呢 =(”")
			.t("leaving_partly_explored", "时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“喔，你打算回来了吗？也行，起码你也帮我勘探了一下宝库的布局，聊胜于无了。”\n\n“唉，我当然是期待你还能多做点什么了 =( 。你需要的话我当然会把你传送回来，或许能让你一块带上个什么药剂或者卷轴回来吧。”")
			.t("leaving_fully_explored", "时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“要我把你带回来？行，至少你算是把这片宝库摸索了个大概。托你的福，我现在对宝库里的机关与地形多了不少了解，不过我还是希望你能再深入探索一下，顺带也替你自己好好搜刮点物资嘛。”\n\n“我会让你带一样东西出来的，不过升级过的物品可不行！”")
			.t("leaving_partial_victory", "时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“真不错啊，你已经替我把这座宝库的绝大部分都探索、清理过一遍了！虽然我真的还是很希望你能把那座雕像带出来就是了。”\n\n“我就直说吧，你帮了我许多，只要不是升级超过+1的物品你都可以挑出来带走，我也会按约定在楼下把店面准备好的。反正我还得花上些时间安排接下来的行动，布置一下店面也费不了多久。”")
			.t("leaving_victory", "时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“干得漂亮！！你准备回来了吗？我就在原地，记得来我这把雕像给我。”\n\n“按咱们说好的，我会把店面设在楼下不远处，你也可以任选一件东西带出宝库！你慢慢挑吧，毕竟宝库里剩下的东西你可拿不出来。”")
			.t("leaving_item", "你确定这就是你想带走的物品吗？")
			.t("leaving_seal", "_贴附在这件护甲上的破损纹章与宝库中的黑镜紧密联系，你无法将它带出宝库。_离开时，纹章会自动从护甲上剥落并使其等级降低一级，但不会带走其上原有的刻印。")
			.t("leaving_staff", "_这把法师魔杖与宝库中的黑镜紧密联系，其本身无法被带出宝库，但你可以带走其中原本灌注的法杖。_离开时，法杖会以比魔杖低一级的属性被自动剥离出来。")
			.t("leaving_yes", "离开宝库")
			.t("leaving_no", "留在这里")
			.t("error_start", "似乎你并不处于宝库层中！可能是bug或崩溃导致的。")
			.t("error_no_items", "你还没有把物品存放好，棱晶已经从你的背包中移除。")
			.t("error_with_items", "你背包里的物品已经存放好了，现在可以再度进入宝库以开始委托任务。存起来的物品会在委托结束时归还。");
	}




	{
		image = ConsumGoodsMaterialsMaterialsDict.ESCAPE_0;

		unique = true;

		defaultAction = AC_USE;
	}

	public static final String AC_USE = "USE";

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = new ArrayList<>(); //no drop or throw
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute( final Hero hero, String action ) {

		super.execute(hero, action);

		if (action.equals( AC_USE )) {

			if (Dungeon.level instanceof VaultLevel){

				//pre-v4.0.0 saves still in the vault tester area
				if (Imp.Quest.isOld()){
					leaveVault(null, 0);
					return;
				}

				int score = 0;

				//firstly, score is always a full 4k if the hero had the statue
				if (hero.belongings.getItem(ImpStatue.class) != null){
					score = 4000;
				} else {
					//otherwise there is partial score, to a max of 3k:

					//1,000 for exploring up to 80% of the level
					score += (int) (1000 * Dungeon.level.levelExplorePercent(Dungeon.depth));

					//1,000 for collecting tokens (100 each), plus a 250 bonus for opening the door
					boolean doorOpened = true;
					for (Char ch : Dungeon.level.mobs()){
						if (ch instanceof VaultTokenDoor){
							doorOpened = false;
							break;
						}
					}
					if (doorOpened){
						score += 1250; //1000 for tokens, 250 for door
					} else {
						Item tokens = hero.belongings.getItem(DwarfToken.class);
						if (tokens != null){
							score += Math.min(1000, 100*tokens.quantity());
						}
					}

					//up to 750 for damaging/killing the boss elemental
					VaultFinalRoom r = (VaultFinalRoom) ((VaultLevel) Dungeon.level).room(VaultFinalRoom.class);
					if (r.elementalWasSummoned()){
						boolean elementalFound = false;
						for (Char ch : Dungeon.level.mobs()){
							if (ch instanceof VaultBossElemental){
								elementalFound = true;
								score += (int) (750 * (ch.HP/(float)ch.HT));
								break;
							}
						}
						if (!elementalFound){
							//some poor sucker is absolutely going to kill the boss, not take the statue,
							// and then be forced to leave by a golem or something
							score += 750;
						}
					}

					//finally, score is rounded down to the nearest 50 points
					score = (score/50)*50;

				}

				if (score < 50){
					GameScene.show(new WndTitledMessage(new ImpSprite(),
							Messages.titleCase(Messages.get(Imp.class, "name")),
							Messages.get(EscapeCrystal.class, "leaving_start")));
				} else {
					String message;
					if (score < 500)        message = Messages.get(EscapeCrystal.class, "leaving_early");
					else if (score <= 1000) message = Messages.get(EscapeCrystal.class, "leaving_partly_explored");
					else if (score <= 2000) message = Messages.get(EscapeCrystal.class, "leaving_fully_explored");
					else if (score < 4000)  message = Messages.get(EscapeCrystal.class, "leaving_partial_victory");
					else                    message = Messages.get(EscapeCrystal.class, "leaving_victory");

					int finalScore = score;
					GameScene.show(new WndOptions(new ImpSprite(),
							Messages.titleCase(Messages.get(Imp.class, "name")),
							message,
							Messages.get(EscapeCrystal.class, "leaving_yes"),
							Messages.get(EscapeCrystal.class, "leaving_no")) {
						@Override
						protected void onSelect(int index) {
							if (index == 0) {
								if (finalScore >= 500) {
									GameScene.selectItem(new WndBag.ItemSelector(){

										@Override
										public String textPrompt() {
											return Messages.get(EscapeCrystal.class, "prompt");
										}

										@Override
										public boolean itemSelectable(Item item) {
											if (item instanceof EscapeCrystal){
												return false;
											}
											//lowest reward, just a consumable
											if (finalScore <= 1000){
												return !item.unique && !(item instanceof EquipableItem || item instanceof Wand);
											//mid rewards, item at a max of +0 or +1
											} else if (finalScore < 4000){
												int maxLevel = finalScore > 2000 ? 1 : 0;
												if (item instanceof MagesStaff){
													return ((MagesStaff) item).wandClass() != null
															&& item.level() <= maxLevel+1; //+1 to account for staff's level
												} else if (item instanceof Armor && ((Armor) item).checkSeal() != null){
													return item.level() <= maxLevel+1; //+1 to account for seal's level
												} else {
													return item.level() <= maxLevel && !item.unique;
												}
											} else {
												if (item instanceof MagesStaff){
													return ((MagesStaff) item).wandClass() != null;
												}
												return !item.unique;
											}
										}

										@Override
										public void onSelect(Item item) {
											if (item != null){

												String desc = Messages.get(EscapeCrystal.class, "leaving_item");

												if (item instanceof Armor && ((Armor) item).checkSeal() != null){
													desc += "\n\n" + Messages.get(EscapeCrystal.class, "leaving_seal");
												} else if (item instanceof MagesStaff){
													desc += "\n\n" + Messages.get(EscapeCrystal.class, "leaving_staff");
												//can only take 1 of a consumable item
												} if (item.quantity() > 0 && !(item instanceof EquipableItem)){
													item = item.duplicate().quantity(1);
												}

												Item finalItem = item;
												GameScene.show(new WndOptions(
														new ItemSprite(finalItem),
														Messages.titleCase(finalItem.title()),
														desc,
														Messages.get(EscapeCrystal.class, "leaving_yes"),
														Messages.get(EscapeCrystal.class, "leaving_no")){
													@Override
													protected void onSelect(int index) {
														if (index == 0){
															leaveVault(finalItem, finalScore);
														}
														super.onSelect(index);
													}
												});

											}
										}
									});
								} else {
									leaveVault(null, finalScore);
								}
							}
						}
					});
				}

			} else {
				if (storedItems == null || !storedItems.contains(BELONGINGS)){
					GameScene.show(new WndError(Messages.get(EscapeCrystal.class, "error_start") + "\n\n" +
							Messages.get(EscapeCrystal.class, "error_no_items")));
					detachAll(hero.belongings.backpack);
				} else {
					GameScene.show(new WndError(Messages.get(EscapeCrystal.class, "error_start") + "\n\n" +
							Messages.get(EscapeCrystal.class, "error_with_items")));
				}

			}

		}

	}

	private void leaveVault( Item preserve, int score ){
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

		Dungeon.hero.live(); //clears all non-persist buffs, resets hunger/regen
		Dungeon.hero.HP = Dungeon.hero.HT; //full heal

		//logic for removing Warrior's Seal or Mage's staff
		if (preserve instanceof Armor && ((Armor) preserve).checkSeal() != null){
			BrokenSeal seal = ((Armor) preserve).checkSeal();
			Armor.Glyph attached = seal.getGlyph();
			((Armor) preserve).detachSeal();
			//glyph always gets preserved
			if (((Armor) preserve).glyph == null && attached != null){
				((Armor) preserve).inscribe(attached);
			}
		} else if (preserve instanceof MagesStaff){
			Wand w = Reflection.newInstance(((MagesStaff) preserve).wandClass());
			w.identify(false);
			w.upgrade(preserve.level()-1);
			preserve = w;
		}

		restoreHeroBelongings(Dungeon.hero, preserve);
		Dungeon.hero.updateHT(false);
		detachAll(Dungeon.hero.belongings.backpack);
		if (!Imp.Quest.isOld()) Imp.Quest.complete(score);

		Transitions.beforeTransition();
		InterlevelScene.curTransition = new LevelTransition(Dungeon.level,
				Dungeon.hero.pos,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth,
				0,
				LevelTransition.Type.BRANCH_EXIT);
		InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
		Game.switchScene(InterlevelScene.class);
	}

	public static String BELONGINGS = "belongings";
	public static String QUICKSLOTS = "quickslots";
	public static String GOLD       = "gold";
	public static String ENERGY     = "energy";

	public void storeHeroBelongings( Hero hero ){
		storedItems = new Bundle();

		Bundle belongings = new Bundle();
		hero.belongings.storeInBundle(belongings);
		storedItems.put(BELONGINGS, belongings);

		Bundle quickslots = new Bundle();
		Dungeon.quickslot.storePlaceholders(quickslots);
		storedItems.put(QUICKSLOTS, quickslots);

		storedItems.put(GOLD, Dungeon.gold);
		storedItems.put(ENERGY, Dungeon.energy);

		Dungeon.quickslot.reset();
		QuickSlotButton.reset();
		Dungeon.gold = Dungeon.energy = 0;
		hero.belongings.clear();
	}

	public void restoreHeroBelongings( Hero hero, Item preserve ){
		//we detach the item being preserved first, to cancel any equip-based buffs (e.g. wand charging)
		if (preserve != null) {
			preserve.detachAll(hero.belongings.backpack);
		}

		hero.belongings.clear();

		Dungeon.quickslot.reset();
		Dungeon.quickslot.restorePlaceholders(storedItems.getBundle(QUICKSLOTS));
		QuickSlotButton.reset();

		Dungeon.hero.belongings.restoreFromBundle(storedItems.getBundle(BELONGINGS));

		Dungeon.gold = storedItems.getInt(GOLD);
		Dungeon.energy = storedItems.getInt(ENERGY);

		if (preserve != null){
			if (!preserve.collect()) {
				//if hero's inventory is full, the Imp drops the reward
				Imp.Quest.reward = preserve;
			}
		}

		storedItems = null;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	public Bundle storedItems;

	public static String STORED_ITEMS = "stored_items";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STORED_ITEMS, storedItems);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		storedItems = bundle.getBundle(STORED_ITEMS);
	}
}
