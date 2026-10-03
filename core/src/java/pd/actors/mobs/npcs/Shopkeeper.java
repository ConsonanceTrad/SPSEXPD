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

package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.BlobImmunity;
import pd.actors.buffs.Buff;
import pd.actors.hero.HeroClass;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.ElmoParticle;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.specific.sellitem.SellPermit;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.journal.Notes;
import pd.levels.rooms.special.SpsShopRoom;
import pd.levels.traps.GuardianTrap;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.sprites.ShopkeeperSprite;
import pd.ui.CurrencyIndicator;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import pd.windows.WndTitledMessage;
import pd.windows.WndTradeItem;
import render.noosa.Game;
import render.noosa.Image;
import render.utils.data.BArray;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Shopkeeper extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Shopkeeper.class)
			.t("name", "店主")
			.t("thief", "小偷，小偷！")
			.t("warn", "小心！我不会警告你第二次了。")
			.t("flee", "店主关店跑路了！")
			//SPS: 商人被打不再跑路，改为召唤守卫并涨价
			.t("guards", "守卫！守卫！")
			.t("free_now", "守卫已被尽数击败，本店今日免费！")
			//SPS: 守卫被清光、以及免费拿货时商人的喊话
			.t("guards_down", "我的守卫……我的金币……")
			.t("free_buy", "金币……我的金币…")
			.t("sell", "出售")
			.t("talk", "交谈")
			.t("buyback", "店主不情不愿地退还了你的物品。")
			.t("talk_prison_intro", "我这有你成功冒险所需的一切东西！")
			.t("talk_prison_warrior", "你的那个纹章挺有意思的嘛，它们通常来讲都是一整个的。难不成你是一位蒙受了耻辱的英雄，还是别的什么人物？好啦，不管你做了些什么，都与我无关。我只对你的金币感兴趣。")
			.t("talk_prison_mage", "嘻嘻，我想我在城里的什么地方有见过你的通缉令。在逃亡对吧？哦？你说你是无辜的，真的吗？行行行...放轻松，比起举报你，我还有更值得做的事情。但也许你应该消费消费以确保这一点？")
			.t("talk_prison_rogue", "你是盗贼公会指派来清理这片区域的吗？不是？只是来购物的？你能来到这说明你确实杀了几个怪物，但别指望我会因此给你打折。我已经交足了保护费，但也没怎么见你们在这护我周全。")
			.t("talk_prison_huntress", "小姐，这似乎不是你该待的地方。狭窄的地牢走廊想来不如城镇大厅或者市郊森林那样宽敞，不是吗？嘿嘿，哦，请别在意我这碎嘴巴，为什么不买点东西呢？")
			.t("talk_prison_duelist", "哇偶，这不是闯荡天下的大英雄嘛！如果你在尝试着拯救又一个小镇，那我只能说祝你好运了。毕竟这里的危机可比土匪帮派什么的糟糕多了。当然，我相信你会比任何人做得更棒，只是千万别死在我的店门口。")
			.t("talk_prison_cleric", "哦您好陛下，或许现在该尊称您为“圣上”了？呵呵，无论如何这可不是您这种大人物该来的地方，要是您也殡天了恐怕您的臣民们会心碎的吧。想必您手头肯定不缺世俗钱财，为何不买点东西以防万一呢？")
			.t("talk_caves", "花钱，你才能活得更久。\n\n哦，如果你看到那个巨魔铁匠，代我向他问好。顺便提醒这个笨蛋，他上周从我这买锤子的钱还没结清呢！")
			.t("talk_city", "我的货可以保你平安。\n\n...另外，劳烦您别在这瞎晃悠。我花了很长很长时间才让这里的怪物离我远点，可不希望你把麻烦惹回来！")
			.t("talk_halls", "嘿，那边那位！我为恶魔猎人提供特别优惠！\n\n哦还有，在下面可要当心！底下的恶魔比宝库里那些守卫还难缠，而且这一次可没有逃脱棱晶帮你咯。你要是死了就没法回来消费啦！;)\n\n那些恶魔都和我一样吗？嘿嘿，并不。我只是个小角色，算不上能打，况且我还保全了自由意志。下面那些恶魔都比我猛得多，并且它们都受奴役于...呃...总之，你在下面当心点吧。")
			.t("talk_ascent", "无论你打算用那个护符做什么，我都不想掺和。买点东西，然后赶紧离开，咱俩都该闪人了！")
			.t("desc", "这个矮胖的家伙看起来更适合在某些大城市里做买卖而不是在这种地牢。这些商品的价格说明了为什么他会喜欢在这儿做生意。");
	}




	@Override public Item SupercreateLoot() { return new SellPermit(); }

	{
		spriteClass = ShopkeeperSprite.class;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.HUMAN);
	}

	public static int MAX_BUYBACK_HISTORY = 3;
	public ArrayList<Item> buybackItems = new ArrayList<>();

	private int turnsSinceHarmed = -1;

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.SHOP;
	}

	@Override
	protected boolean act() {

		if (turnsSinceHarmed >= 0){
			turnsSinceHarmed ++;
		}

		sprite.turnTo( pos, Dungeon.hero.pos );
		spend( TICK );
		return super.act();
	}
	
	@Override
	public void damage( int dmg, Object src ) {
		processHarm();
	}
	
	@Override
	public boolean add( Buff buff ) {
		if (buff.type == Buff.buffType.NEGATIVE){
			processHarm();
		}
		return false;
	}

	public void processHarm(){

		//do nothing if the shopkeeper is out of the hero's FOV
		if (!Dungeon.level.heroFOV[pos]){
			return;
		}

		if (turnsSinceHarmed == -1){
			turnsSinceHarmed = 0;
			yell(Messages.get(this, "warn"));

			//use a new actor as we can't clear the gas while we're in the middle of processing it
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					//cleanses all harmful blobs in the shop
					ArrayList<Blob> blobs = new ArrayList<>();
					for (Class c : new BlobImmunity().immunities()){
						Blob b = Dungeon.level.blobs.get(c);
						if (b != null && b.volume > 0){
							blobs.add(b);
						}
					}

					PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 4 );

					for (int i=0; i < Dungeon.level.length(); i++) {
						if (PathFinder.distance[i] < Integer.MAX_VALUE) {

							boolean affected = false;
							for (Blob blob : blobs) {
								if (blob.cur[i] > 0) {
									blob.clear(i);
									affected = true;
								}
							}

							if (affected && Dungeon.level.heroFOV[i]) {
								CellEmitter.get( i ).burst( Speck.factory( Speck.DISCOVER ), 2 );
							}

						}
					}
					Actor.remove(this);
					return true;
				}
			});

		//There is a 1 turn buffer before more damage/debuffs make the shopkeeper flee
		//This is mainly to prevent stacked effects from causing an instant flee
		} else if (turnsSinceHarmed >= 1) {
			//SPS: 店主不再跑路，改为召唤守卫（见 summonGuards）
			summonGuards();
		}
	}

	//SPS: 商人被打时召唤的石像守卫：每次触发 2 只，固定按第 30 层强度（不随当前层浮动）。
	private static final int GUARD_COUNT = 2;
	private static final int GUARD_DEPTH = 30;
	//SPS: 每触发一次涨价 50%（叠乘）；击败 10 只守卫后本店免费并停止补货。
	private static final float PRICE_STEP = 1.5f;
	private static final int GUARDS_TO_FREE = 10;

	/** SPS: 本层商人的涨价倍率（免费后恒为 0）。换层时由商店重置。 */
	public static float priceMultiplier = 1f;
	/** SPS: 击败足够守卫后，本层商店免费且停止补货。换层时由商店重置。 */
	public static boolean freeAndNoRestock = false;

	/** SPS: 本层商店房间，用于卖光后补货。不参与序列化。 */
	public SpsShopRoom shopRoom = null;

	/** SPS: 已被击败的、本商人召唤的守卫数。 */
	private int guardsKilled = 0;

	/**
	 * SPS: 在店主附近召唤石像守卫（{@link ShopGuard}），并把本店价格永久提高 50%。
	 * 可重复触发，倍率叠乘。
	 */
	public void summonGuards() {
		priceMultiplier *= PRICE_STEP;

		GLog.newLine();
		GLog.n( Messages.get(this, "guards") );
		if (sprite != null) {
			CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6 );
		}

		for (int i = 0; i < GUARD_COUNT; i++) {
			int cell = guardCell();
			if (cell == -1) break;

			ShopGuard guard = new ShopGuard();
			guard.owner = this;
			guard.pos = cell;

			GameScene.add( guard );
			guard.beckon( Dungeon.hero.pos );
			if (Dungeon.level.heroFOV[cell]) {
				CellEmitter.get( cell ).burst( ElmoParticle.FACTORY, 8 );
			}
		}

		if (shopRoom != null) shopRoom.checkRestock();
	}

	/** SPS: 本店的一只守卫被击败。累计到阈值后本店免费并停止补货，商人也会喊一句。 */
	public void onGuardKilled() {
		guardsKilled++;
		if (!freeAndNoRestock && guardsKilled >= GUARDS_TO_FREE) {
			freeAndNoRestock = true;
			GLog.p( Messages.get(this, "free_now") );
			yell( Messages.get(this, "guards_down") );
		}
	}

	/**
	 * SPS: 本商人召唤的石像守卫。属性固定按第 {@link #GUARD_DEPTH} 层算，
	 * 伤害覆写为固定成长，不再依赖随机武器。
	 *   HP = 100 + depth*5、defenseSkill = 25 + depth*2、EXP = 50 + depth*2
	 */
	public static class ShopGuard extends GuardianTrap.Guardian {

		private Shopkeeper owner;

		{
			HP = HT = 100 + GUARD_DEPTH * 5;
			defenseSkill = 25 + GUARD_DEPTH * 2;
			EXP = 50 + GUARD_DEPTH * 2;
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 20 + GUARD_DEPTH, 20 + GUARD_DEPTH * 3 );
		}

		@Override
		public void die( Object cause ) {
			super.die( cause );
			if (owner != null) owner.onGuardKilled();
		}

		private static final String OWNER = "owner";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( OWNER, owner );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			owner = (Shopkeeper) bundle.get( OWNER );
		}
	}

	/** SPS: 在店主周围一圈里找一个能站人的空位；没有则返回 -1。 */
	private int guardCell() {
		int w = Dungeon.level.width();
		ArrayList<Integer> free = new ArrayList<>();
		for (int dy = -2; dy <= 2; dy++) {
			for (int dx = -2; dx <= 2; dx++) {
				if (dx == 0 && dy == 0) continue;

				int c = pos + dx + dy * w;
				if (c < 0 || c >= Dungeon.level.length()) continue;
				//绕行时列坐标会串到隔壁行，跳过
				if (Math.abs((c % w) - (pos % w)) > 2) continue;

				if (Dungeon.level.passable[c] && !Dungeon.level.pit[c] && Actor.findChar(c) == null) {
					free.add(c);
				}
			}
		}
		return free.isEmpty() ? -1 : free.get( Random.Int( free.size() ) );
	}
	
	public void flee() {
		destroy();

		Notes.remove( landmark() );
		GLog.newLine();
		GLog.n(Messages.get(this, "flee"));

		if (sprite != null) {
			sprite.killAndErase();
			CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		for (Heap heap: Dungeon.level.heaps.valueList()) {
			if (heap.type == Heap.Type.FOR_SALE) {
				if (ShatteredPixelDungeon.scene() instanceof GameScene) {
					CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
				}
				if (heap.size() == 1) {
					heap.destroy();
				} else {
					heap.items.remove(heap.size()-1);
					heap.type = Heap.Type.HEAP;
				}
			}
		}
	}
	
	@Override
	public boolean reset() {
		return true;
	}

	//shopkeepers are greedy!
	public static int sellPrice(Item item){
		boolean follower = Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.FOLLOWER;
		int multiplier = follower ? 4 : 5;
		int cap = follower ? 20 : 25;
		int base = item.value() * Math.min(multiplier * (Dungeon.legacyDepth() / 5 + 1), cap);
		//SPS: 被袭扰后涨价（每次触发 ×1.5，叠乘）；击败足够守卫后本店免费
		if (freeAndNoRestock) return 0;
		return Math.round( base * priceMultiplier );
	}
	
	public static WndBag sell() {
		return GameScene.selectItem( itemSelector );
	}

	public static boolean canSell(Item item){
		if (item.value() <= 0)                                              return false;
		if (item.unique && !item.stackable)                                 return false;
		if (item instanceof Armor && ((Armor) item).checkSeal() != null)    return false;
		if (item.isEquipped(Dungeon.hero) && item.cursed)                   return false;
		return true;
	}

	private static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(Shopkeeper.class, "sell");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return Shopkeeper.canSell(item);
		}

		@Override
		public void onSelect( Item item ) {
			if (item != null && Dungeon.hero != null && Dungeon.hero.isAlive()) {
				WndBag parentWnd = sell();
				GameScene.show( new WndTradeItem( item, parentWnd ) );
			}
		}
	};

	@Override
	public boolean interact(Char c) {
		if (c != Dungeon.hero) {
			return true;
		}
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				String[] options = new String[2+ buybackItems.size()];
				int maxLen = PixelScene.landscape() ? 30 : 25;
				int i = 0;
				options[i++] = Messages.get(Shopkeeper.this, "sell");
				options[i++] = Messages.get(Shopkeeper.this, "talk");
				for (Item item : buybackItems){
					options[i] = Messages.get(Heap.class, "for_sale", item.value(), Messages.titleCase(item.title()));
					if (options[i].length() > maxLen) options[i] = options[i].substring(0, maxLen-3) + "...";
					i++;
				}
				CurrencyIndicator.showGold = true;
				GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()), description(), options){
					@Override
					protected void onSelect(int index) {
						super.onSelect(index);
						if (index == 0){
							sell();
						} else if (index == 1){
							GameScene.show(new WndTitledMessage(sprite(), Messages.titleCase(name()), chatText()));
						} else if (index > 1){
							GLog.i(Messages.get(Shopkeeper.this, "buyback"));
							Item returned = buybackItems.remove(index-2);
							Dungeon.gold -= returned.value();
							Statistics.goldCollected -= returned.value();
							if (returned instanceof MissileWeapon && returned.isUpgradable()){
								Buff.affect(Dungeon.hero, MissileWeapon.UpgradedSetTracker.class).levelThresholds.put(((MissileWeapon) returned).setID, returned.level());
							}
							if (!returned.doPickUp(Dungeon.hero)){
								Dungeon.level.drop(returned, Dungeon.hero.pos);
							}
						}
					}

					@Override
					protected boolean enabled(int index) {
						if (index > 1){
							return Dungeon.gold >= buybackItems.get(index-2).value();
						} else {
							return super.enabled(index);
						}
					}

					@Override
					protected boolean hasIcon(int index) {
						return index > 1;
					}

					@Override
					protected Image getIcon(int index) {
						if (index > 1){
							return new ItemSprite(buybackItems.get(index-2));
						}
						return null;
					}

					@Override
					public void hide() {
						super.hide();
						CurrencyIndicator.showGold = false;
					}
				});
			}
		});
		return true;
	}

	public String chatText(){
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			return Messages.get(this, "talk_ascent");
		}
		switch (Dungeon.depth){
			case 6: default:
				return Messages.get(this, "talk_prison_intro") + "\n\n" + Messages.get(this, "talk_prison_" + Dungeon.hero.heroClass.name());
			case 11:
				return Messages.get(this, "talk_caves");
			case 16:
				return Messages.get(this, "talk_city");
			case 20:
				return Messages.get(this, "talk_halls");
		}
	}

	public static String BUYBACK_ITEMS = "buyback_items";

	public static String TURNS_SINCE_HARMED = "turns_since_harmed";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BUYBACK_ITEMS, buybackItems);
		bundle.put(TURNS_SINCE_HARMED, turnsSinceHarmed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		buybackItems.clear();
		if (bundle.contains(BUYBACK_ITEMS)){
			for (Bundlable i : bundle.getCollection(BUYBACK_ITEMS)){
				buybackItems.add((Item) i);
			}
		}
		turnsSinceHarmed = bundle.contains(TURNS_SINCE_HARMED) ? bundle.getInt(TURNS_SINCE_HARMED) : -1;
	}
}
