/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.ui;

import pd.Dungeon;
import pd.SPDSettings;
import pd.Statistics;
import pd.actors.buffs.Hunger;
import pd.actors.hero.perks.PerkImageSheet;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.Torch;
import pd.items.equipment.bags.ShoppingCart;
import pd.items.Waterskin;
import pd.items.consum.food.Food;
import pd.items.consum.potions.PotionOfStrength;
import pd.items.consum.food.Pasty;
import pd.items.consum.food.SmallRation;
import pd.items.consum.food.SupplyRation;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndHero;
import render.gltextures.TextureCache;
import render.noosa.Image;
import render.noosa.ui.Component;

/**
 * SPSEXPD: 左下角的快捷操作按钮（按需出现，各自可在「游戏辅助」设置里开关）——
 * 照明（优先火把，其次露珠瓶）、天赋加点、饥饿进食。
 */
public class QuickActionBar extends Component {

	//SPSEXPD: 指示器尺寸 = 底图 flag.png 的原始尺寸（用户重绘为 24x16，不缩放；图标用正常尺寸）
	public static final int BTN_W = 24;
	public static final int BTN_H = 16;
	//SPSEXPD: 按钮之间的间距
	private static final int BTN_GAP = 5;
	private static final int STEP = BTN_H + BTN_GAP;

	//SPSEXPD: 按钮位于屏幕上方时改为自上而下生长（默认在下方，自下而上堆叠）
	private boolean growDown = false;

	private Button btnLight;
	private Button btnTalent;
	private Button btnEat;

	@Override
	protected void createChildren() {
		//照明
		btnLight = new ActionButton( new ItemSprite( new Torch() ) ) {
			@Override
			protected void onClick() {
				doLight();
			}
		};
		add( btnLight );

		//特质加点（图标与英雄窗特质页签一致）
		btnTalent = new ActionButton( new Image( pd.Assets.Interfaces.SPECIFIC_POINT ) ) {
			@Override
			protected void onClick() {
				doPerk();
			}
		};
		add( btnTalent );

		//饥饿进食
		btnEat = new ActionButton( new ItemSprite( new SupplyRation() ) ) {
			@Override
			protected void onClick() {
				doEat();
			}
		};
		add( btnEat );
	}

	@Override
	public void update() {
		super.update();
		refresh();
	}

	//SPSEXPD: 刷新三个按钮的可见性与位置
	public void refresh() {
		Hero hero = Dungeon.hero;
		//SPSEXPD: 总开关关闭时三个按钮一律不显示（各自开关的状态保留，重新打开即恢复）
		//注意：这里刻意不看 hero.ready —— 它在回合处理期间会短暂为 false，会让按钮每回合闪一下
		boolean alive = hero != null && hero.isAlive() && SPDSettings.quickAll();

		setShown( btnLight, alive && SPDSettings.quickLight() && needsLight() );
		setShown( btnTalent, alive && SPDSettings.quickTalent() && hasUnspentPerks() );
		setShown( btnEat, alive && SPDSettings.quickEat() && isStarving() );
	}

	//SPSEXPD: 只在可见性真正变化时才改动并重排，避免每帧/每回合重复赋值造成闪烁
	private void setShown( Button btn, boolean value ) {
		if (btn.visible != value) {
			btn.visible = btn.active = value;
			layoutButtons();
		}
	}

	@Override
	protected void layout() {
		super.layout();
		//SPSEXPD: setPos() 只改坐标，必须在这里重排，否则「反转左侧按钮布局」要等场景重建才生效
		if (btnLight != null) {
			layoutButtons();
		}
	}

	//SPSEXPD: 自下而上排列——底部固定，按钮按可见性往上堆；位于屏幕上方时改为自上而下生长
	private void layoutButtons() {
		if (growDown) {
			float t = y;
			if (btnLight.visible) {
				btnLight.setPos( x, t );
				t += STEP;
			}
			if (btnTalent.visible) {
				btnTalent.setPos( x, t );
				t += STEP;
			}
			if (btnEat.visible) {
				btnEat.setPos( x, t );
			}
			return;
		}

		float b = y + BTN_H;
		if (btnEat.visible) {
			btnEat.setPos( x, b - BTN_H );
			b -= STEP;
		}
		if (btnTalent.visible) {
			btnTalent.setPos( x, b - BTN_H );
			b -= STEP;
		}
		if (btnLight.visible) {
			btnLight.setPos( x, b - BTN_H );
		}
	}

	//SPSEXPD: 设置「交换位置」后由 GameScene 调用——位于屏幕上方时向下生长
	public void setGrowDown( boolean value ) {
		if (growDown != value) {
			growDown = value;
			layoutButtons();
		}
	}

	//SPSEXPD: 按钮占据的高度（供左侧快捷栏避让）
	public static int visibleHeight() {
		//最多三个按钮，具体高度由 SideQuickBar 的反向预留在布局时决定
		return 3 * STEP;
	}

	//SPSEXPD: 需要照明——夜晚且身上有露珠瓶或火把
	private boolean needsLight() {
		Hero hero = Dungeon.hero;
		if (hero == null) return false;
		if (!Statistics.spsNight()) return false;
		return hero.belongings.getItem( Waterskin.class ) != null
				|| hero.belongings.getItem( Torch.class ) != null;
	}

	//SPSEXPD: 有未分配的天赋点
	private boolean hasUnspentPerks() {
		Hero hero = Dungeon.hero;
		return hero != null && hero.reservedPerks > 0;
	}

	//SPSEXPD: 正在挨饿
	private boolean isStarving() {
		Hero hero = Dungeon.hero;
		if (hero == null) return false;
		Hunger hunger = hero.buff( Hunger.class );
		return hunger != null && hunger.isStarving();
	}

	//SPSEXPD: 照明——优先用火把，没有才用露珠瓶的「照明」
	private void doLight() {
		Hero hero = Dungeon.hero;
		if (hero == null) return;

		Torch torch = hero.belongings.getItem( Torch.class );
		if (torch != null) {
			torch.execute( hero, Torch.AC_LIGHT );
			return;
		}

		Waterskin vial = hero.belongings.getItem( Waterskin.class );
		if (vial != null) {
			vial.execute( hero, Waterskin.AC_LIGHT );
			return;
		}

		GLog.w( Messages.get( this, "no_light" ) );
	}

	//SPSEXPD: 天赋加点——直接打开属性页的天赋标签
	private void doPerk() {
		//SPSEXPD: 特质加点——与状态栏指示器共用同一入口
		pd.windows.WndGainNewPerk.Show( Dungeon.hero );
	}

	//SPSEXPD: 饥饿进食——有干粮/小包干粮就直接吃；否则打开购物车，没有购物车就打开主背包
	private void doEat() {
		Hero hero = Dungeon.hero;
		if (hero == null) return;

		//1) 干粮 > 小干粮 > 小包干粮，直接进食
		Food food = findFood( hero, SupplyRation.class );
		if (food == null) food = findFood( hero, SmallRation.class );
		if (food == null) food = findFood( hero, Pasty.class );
		if (food != null) {
			food.execute( hero, Food.AC_EAT );
			return;
		}

		//2) 打开购物车（自动跳到购物车标签），方便在食物堆里挑
		ShoppingCart cart = hero.belongings.getItem( ShoppingCart.class );
		if (cart != null) {
			GameScene.show( new WndBag( cart ) );
			return;
		}

		//3) 没有购物车就打开主背包
		GameScene.show( new WndBag( hero.belongings.backpack ) );
	}

	private Food findFood( Hero hero, Class<? extends Food> type ) {
		for (Item item : hero.belongings) {
			if (type.isInstance( item ) && item.actions( hero ).contains( Food.AC_EAT )) {
				return (Food) item;
			}
		}
		return null;
	}

	//SPSEXPD: 指示器风格的操作位——半透明灰底 + 居中图标（外观与状态指示器一致，而非按钮）
	private class ActionButton extends Button {

		private final Image bg;
		private final Image icon;

		ActionButton( Image icon ) {
			super();
			//SPSEXPD: 构造期回调的坑——Component 的构造会调用 createChildren()，
			//而 setSize() 会立刻调用 layout()；因此字段与子元素必须全部先就位，setSize() 放最后
			this.icon = icon;

			//SPSEXPD: 底图按原始尺寸使用，不做缩放
			bg = new Image( "interfaces/flag.png" );
			add( bg );      //底
			add( icon );    //图标（位于底之上）

			setSize( BTN_W, BTN_H );
		}

		@Override
		protected void layout() {
			super.layout();
			if (bg != null) {
				bg.x = x;
				bg.y = y;
			}
			if (icon != null) {
			icon.x = x + (width - icon.width()) / 2f;
			icon.y = y + (height - icon.height()) / 2f;
			}
		}
	}
}
