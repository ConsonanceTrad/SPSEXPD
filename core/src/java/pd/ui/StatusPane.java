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

package pd.ui;

import pd.Assets;
import pd.Dungeon;
import pd.SPDAction;
import pd.Statistics;
import pd.actors.Actor;
import pd.effects.CircleArc;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.HeroSprite;
import pd.windows.WndHero;
import pd.windows.WndKeyBindings;
import render.input.GameAction;
import render.noosa.BitmapText;
import render.noosa.Camera;
import render.noosa.ColorBlock;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.NinePatch;
import render.noosa.particles.Emitter;
import render.noosa.ui.Component;
import render.utils.math.ColorMath;
import render.utils.math.GameMath;

public class StatusPane extends Component {

	private NinePatch bg;
	private Image avatar;
	private Button heroInfo;
	private float warning;

	//SPSXPD: 闪烁速率仍供炼金场景/菜单的闪烁动画使用（头像的「有天赋点闪金光」已移除）
	public static final float FLASH_RATE = (float)(Math.PI*1.5f); //1.5 blinks per second

	private int lastTier = 0;

	private Image shieldHP;
	private Image hp;
	private Image Dot; //a visual darkening over HP and shield that shows total incoming DOT
	private BitmapText hpText;
	private Button heroInfoOnBar;

	//SPSEXPD: 局内 SP 点数（默认 Hero.spp；英雄有专属来源时改显示它的数值）。
	//位置与边框格子由背板素材提供（用户绘制），有上限时另画一条黄色进度条
	private BitmapText spText;
	private BitmapText spLabel;
	private ColorBlock spBar;
	private int lastSP = Integer.MIN_VALUE;
	private int lastSPMax = Integer.MIN_VALUE;
	/** SP 数字的缩放（与血条数字同规格；背板已提供底色，不需要再放大字号） */
	private static final float SP_TEXT_SCALE = 0.5f;
	/** 长数值自动缩字号的下限（再小就读不清了） */
	private static final float SP_TEXT_SCALE_MIN = 0.35f;
	//SPSEXPD: 背板素材 status_pane.png 小模式帧里为 SP 预留的格子（帧内坐标，用户绘制）
	private static final float SP_SLOT_X = 30f;
	private static final float SP_SLOT_Y = 7f;
	private static final float SP_SLOT_W = 22f;
	private static final float SP_SLOT_H = 7f;
	/** 黄色上限条的高度（贴格子内底部） */
	private static final float SP_BAR_H = 2f;

	private Image exp;
	private BitmapText expText;

	private int lastLvl = -1;

	private BitmapText level;

	private BuffIndicator buffs;
	private Compass compass;

	private BusyIndicator busy;
	private CircleArc counter;

	private boolean large;

	//potentially extends the hero portrait space to avoid some cutouts
	public static float heroPaneExtraWidth = 0;
	private NinePatch heroPaneCutout;
	//potentially shrinks and/or repositions the hp bar to avoid some cutouts
	public static int hpBarMaxWidth = 50;
	private Image hpCutout;
	//potentially adjusts the row(s) of the the buff indicator to avoid some cutouts
	public static float[] buffBarRowMaxWidths;
	public static float[] buffBarRowAdjusts;

	public StatusPane( boolean large ){
		super();

		String asset = Assets.Interfaces.STATUS;

		this.large = large;

		if (large)  bg = new NinePatch( asset, 0, 64, 41, 39, 33, 0, 4, 0 );
		else        bg = new NinePatch( asset, 0,  0, 82, 38, 32, 0, 5, 0 );
		add( bg );

		heroPaneCutout = new NinePatch(asset, 0, 0, 5, 36, 4, 0, 0, 0);
		heroPaneCutout.visible = false;
		add(heroPaneCutout);

		hpCutout = new Image(asset, 90, 0, 12, 9);
		hpCutout.visible = false;
		add(hpCutout);

		heroInfo = new Button(){
			@Override
			protected void onClick () {
				Camera.main.panTo( Dungeon.hero.sprite.center(), 5f );
				GameScene.show( new WndHero() );
			}
			
			@Override
			public GameAction keyAction() {
				return SPDAction.HERO_INFO;
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info"));
			}
		};
		add(heroInfo);

		avatar = HeroSprite.avatar( Dungeon.hero );
		add( avatar );

		compass = new Compass( Statistics.amuletObtained ? Dungeon.level.entrance() : Dungeon.level.exit() );
		add( compass );

		if (large)  shieldHP = new Image(asset, 0, 112, 128, 9);
		else        shieldHP = new Image(asset, 0, 44, 50, 4);
		add(shieldHP);

		if (large)  hp = new Image(asset, 0, 103, 128, 9);
		else        hp = new Image(asset, 0, 40, 50, 4);
		add( hp );

		if (large)  Dot = new Image(asset, 0, 103, 128, 9);
		else        Dot = new Image(asset, 0, 40, 50, 4);
		Dot.hardlight(0, 0, 0);
		Dot.alpha(0.25f);
		add( Dot );

		hpText = new BitmapText(PixelScene.pixelFont);
		hpText.alpha(0.6f);
		add(hpText);

		//SPSEXPD: 局内 SP 点数——白色数字 + 有上限时的黄色进度条（底板/边框由背板素材提供）
		spBar = new ColorBlock(1, 1, 0xFFFF66);
		spBar.visible = false;
		add(spBar);

		//SPSEXPD: 格内最左的「SP」小标签（半透明红），与数字一起说明这是 SP 点数
		spLabel = new BitmapText(PixelScene.pixelFont);
		spLabel.text("SP");
		spLabel.measure();
		spLabel.hardlight(0xC03838);
		spLabel.alpha(0.7f);
		add(spLabel);

		//SPSEXPD: 数字与血条/经验数字同观感（白字 + 0.6 透明度），不做纯白
		spText = new BitmapText(PixelScene.pixelFont);
		spText.alpha(0.6f);
		add(spText);

		heroInfoOnBar = new Button(){
			@Override
			protected void onClick () {
				Camera.main.panTo( Dungeon.hero.sprite.center(), 5f );
				GameScene.show( new WndHero() );
			}
		};
		add(heroInfoOnBar);

		if (large)  exp = new Image(asset, 0, 121, 128, 7);
		else        exp = new Image(asset, 0, 48, 17, 4);
		add( exp );

		expText = new BitmapText(PixelScene.pixelFont);
		expText.hardlight( 0xFFFFAA );
		expText.alpha(0.6f);
		add(expText);

		level = new BitmapText( PixelScene.pixelFont);
		level.hardlight( 0xFFFFAA );
		add( level );

		buffs = new BuffIndicator( Dungeon.hero, large );
		add( buffs );

		busy = new BusyIndicator();
		add( busy );

		counter = new CircleArc(18, 4.25f);
		counter.color( 0x808080, true );
		counter.show(this, busy.center(), 0f);
	}

	@Override
	protected void layout() {

		height = large ? 39 : 38;

		float heroPaneWidth = 30 + heroPaneExtraWidth;

		bg.x = x + heroPaneExtraWidth;
		bg.y = y;
		if (large)  bg.size( 160, bg.height ); //HP bars must be 128px wide atm
		else        bg.size(hpBarMaxWidth+32, bg.height ); //default max right is 50px health bar + 32

		avatar.x = bg.x - avatar.width / 2f + 15;
		avatar.y = bg.y - avatar.height / 2f + 16;
		PixelScene.align(avatar);

		heroInfo.setRect( x, y, heroPaneWidth, large ? 40 : 36 );

		compass.x = avatar.x + avatar.width / 2f - compass.origin.x;
		compass.y = avatar.y + avatar.height / 2f - compass.origin.y;
		PixelScene.align(compass);

		if (large) {
			exp.x = x + 30;
			exp.y = y + 30;

			hp.x = shieldHP.x = Dot.x = x + 30;
			hp.y = shieldHP.y = Dot.y = y + 19;

			hpText.x = hp.x + (128 - hpText.width())/2f;
			hpText.y = hp.y + 1;
			PixelScene.align(hpText);

			expText.x = exp.x + (128 - expText.width())/2f;
			expText.y = exp.y;
			PixelScene.align(expText);

			//SPSEXPD: large 模式血条下方只剩 2px，SP 底板叠在血条内左端（hpText 居中，左侧是空的）
			layoutSpPoint();

			heroInfoOnBar.setRect(heroInfo.right(), y + 19, 130, 20);

			//little extra for 14th buff
			buffs.setRect(x + 31, y, 142, 16);

			busy.x = x + bg.width + 1;
			busy.y = y + bg.height - 9;
		} else {
			exp.x = x+2;
			exp.y = y+30;

			if (heroPaneExtraWidth > 0){
				heroPaneCutout.visible = true;
				heroPaneCutout.x = x;
				heroPaneCutout.y = y;
				heroPaneCutout.size(heroPaneExtraWidth+4, heroPaneCutout.height);
			}

			float hpleft = x + heroPaneWidth;
			if (hpBarMaxWidth < 82){
				//the class variable assumes the left of the bar can't move, but we can inset it 9px
				int hpWidth = (int)hpBarMaxWidth;
				if (hpWidth <= 41){
					hpleft -= 9;
					hpWidth += 9;
					hpCutout.visible = true;
					hpCutout.x = hpleft - 2;
					hpCutout.y = y;
				}
				hp.frame(50-hpWidth, 40, 50, 4);
				shieldHP.frame(50-hpWidth, 44, 50, 4);
			}

			hp.x = shieldHP.x = Dot.x = hpleft;
			hp.y = shieldHP.y = Dot.y = y + 2;

			hpText.scale.set(PixelScene.align(0.5f));
			hpText.x = hp.x + 1;
			hpText.y = hp.y + (hp.height - (hpText.baseLine()+hpText.scale.y))/2f;
			hpText.y -= 0.001f; //prefer to be slightly higher
			PixelScene.align(hpText);

			expText.scale.set(PixelScene.align(0.5f));
			expText.x = exp.x + 1;
			expText.y = exp.y + (exp.height - (expText.baseLine()+expText.scale.y))/2f;
			expText.y -= 0.001f; //prefer to be slightly higher
			PixelScene.align(expText);

			heroInfoOnBar.setRect(heroInfo.right(), y, 50, 9);

			//SPSEXPD: SP 数字落在背板素材预留的格子里；buff 区顺次下移到该格下方
			layoutSpPoint();

			if (buffBarRowMaxWidths != null){
				buffs.rowWidthLimits = buffBarRowMaxWidths;
			}
			if (buffBarRowAdjusts != null){
				buffs.rowHeightAdjusts = buffBarRowAdjusts;
			}
			buffs.setRect( x + heroPaneWidth + 1, y + SP_SLOT_Y + SP_SLOT_H + 1, 55, 16 );

			busy.x = x + 1;
			busy.y = y + 37;
		}

		counter.point(busy.center());
	}
	
	private static final int[] warningColors = new int[]{0x660000, 0xCC0000, 0x660000};

	private int oldHP = 0;
	private int oldShield = 0;
	private int oldMax = 0;

	@Override
	public void update() {
		super.update();
		
		int health = Dungeon.hero.HP;
		int shield = Dungeon.hero.shielding();
		int incomingDOT = Dungeon.hero.incomingDOT();
		int max = Dungeon.hero.HT;

		if (!Dungeon.hero.isAlive()) {
			avatar.tint(0x000000, 0.5f);
		} else if ((health/(float)max) < 0.334f) {
			warning += Game.elapsed * 5f *(0.4f - (health/(float)max));
			warning %= 1f;
			avatar.tint(ColorMath.interpolate(warning, warningColors), 0.5f );
		} else {
			avatar.resetColor();
		}

		float healthPercent = health/(float)max;
		float shieldPercent = shield/(float)max;
		float DOTPercent    = incomingDOT/(float)max;

		if (healthPercent + shieldPercent > 1f){
			float excess = healthPercent + shieldPercent;
			healthPercent /= excess;
			shieldPercent /= excess;
			DOTPercent    /= excess;
		}

		hp.scale.x = healthPercent;
		shieldHP.scale.x = healthPercent + shieldPercent;
		Dot.scale.x = Math.min(DOTPercent, shieldHP.scale.x);
		Dot.x = shieldHP.x + shieldHP.width() - Dot.width();

		if (oldHP != health || oldShield != shield || oldMax != max){
			if (shield <= 0) {
				hpText.text(health + "/" + max);
			} else {
				hpText.text(health + "+" + shield + "/" + max);
			}
			oldHP = health;
			oldShield = shield;
			oldMax = max;
		}

		//SPSEXPD: 局内 SP 点数 —— 默认显示 Hero.spp；英雄身上挂着专属来源（如武僧内力）时显示它
		int sp = Dungeon.hero.spp;
		int spMax = -1;
		pd.actors.hero.SpPointSource spSource = Dungeon.hero.activeSpSource();
		if (spSource != null) {
			sp = spSource.value( Dungeon.hero );
			spMax = spSource.max( Dungeon.hero );
		}
		if (sp != lastSP || spMax != lastSPMax) {
			lastSP = sp;
			lastSPMax = spMax;
			spText.text( spMax > 0 ? sp + "/" + spMax : Integer.toString( sp ) );
			spText.measure();
			//SPSEXPD: 文本宽度变了，居中位置与上限条都要跟着重算（黄条用上方刚更新的 lastSP/lastSPMax）
			layoutSpPoint();
		}

		if (large) {
			exp.scale.x = (128 / exp.width) * Dungeon.hero.exp / Dungeon.hero.maxExp();

			hpText.measure();
			hpText.x = hp.x + (128 - hpText.width())/2f;

			expText.text(Dungeon.hero.exp + "/" + Dungeon.hero.maxExp());
			expText.measure();
			expText.x = hp.x + (128 - expText.width())/2f;

		} else {
			exp.scale.x = ((17 + heroPaneExtraWidth) / exp.width) * Dungeon.hero.exp / Dungeon.hero.maxExp();
			expText.text(Dungeon.hero.exp + "/" + Dungeon.hero.maxExp());
		}

		if (Dungeon.hero.lvl != lastLvl) {

			if (lastLvl != -1) {
				showStarParticles();
			}

			lastLvl = Dungeon.hero.lvl;

			if (large){
				level.text( "lv. " + lastLvl );
				level.measure();
				level.x = x + (30f - level.width()) / 2f;
				level.y = y + 33f - level.baseLine() / 2f;
			} else {
				level.text( Integer.toString( lastLvl ) );
				level.measure();
				level.x = x + heroPaneExtraWidth + 25.5f - level.width() / 2f;
				level.y = y + 31.0f - level.baseLine() / 2f;
			}
			PixelScene.align(level);
		}

		int tier = Dungeon.hero.tier();
		if (tier != lastTier) {
			lastTier = tier;
			avatar.copy( HeroSprite.avatar( Dungeon.hero ) );
		}

		counter.setSweep((1f - Actor.now()%1f)%1f);
	}

	/** SPSEXPD: 摆放局内 SP 点数 —— 小模式落在背板素材预留的格子里（居中）；
	 *  有上限时在格内底部再画一条按当前值占比的黄色进度条 */
	private void layoutSpPoint(){
		if (spText == null || spBar == null || hp == null) return;

		float slotX, slotY, slotW, slotH;
		if (large){
			//large：血条内左侧（没有背板格子），可用宽度取到居中数字之前
			slotW = 50f;
			slotH = 7f;
			slotX = hp.x + 2;
			slotY = hp.y + (hp.height - slotH)/2f;
		} else {
			//小模式：背板素材预留的格子（用素材内坐标，不随 heroPaneExtraWidth 变化）
			slotX = x + SP_SLOT_X;
			slotY = y + SP_SLOT_Y;
			slotW = SP_SLOT_W;
			slotH = SP_SLOT_H;
		}

		//SPSEXPD: 格内最左是半透明红的「SP」标签（固定缩放），数字在其右侧剩余区域居中、再右偏 2px；
		//长数值按可用宽度自动缩字号（下限 SP_TEXT_SCALE_MIN），保证不压到标签也不溢出格子
		spLabel.scale.set(SP_TEXT_SCALE, SP_TEXT_SCALE);
		float labelW = spLabel.width() + 1f;

		float scale = SP_TEXT_SCALE;
		float avail = Math.max(4f, slotW - labelW - 2f);
		spText.scale.set(scale, scale);
		while (scale > SP_TEXT_SCALE_MIN && spText.width() > avail){
			scale -= 0.05f;
			spText.scale.set(scale, scale);
		}

		spLabel.x = slotX + 1f;
		spLabel.y = slotY + (slotH - spLabel.height())/2f;
		PixelScene.align(spLabel);

		//数字在标签右侧的剩余区域居中，并整体再向右偏 1px
		spText.x = slotX + labelW + (slotW - labelW - spText.width())/2f + 1f;
		spText.y = slotY + (slotH - spText.height())/2f;
		PixelScene.align(spText);

		//SPSEXPD: 上限黄条——只有存在上限（max > 0）时才显示，宽度 = 格内宽 × 当前占比
		boolean hasMax = lastSPMax > 0;
		spBar.visible = hasMax;
		if (hasMax){
			float frac = Math.max(0f, Math.min(1f, lastSP / (float) lastSPMax));
			spBar.size( Math.max(1f, (slotW - 2f) * frac), SP_BAR_H );
			spBar.x = slotX + 1f;
			spBar.y = slotY + slotH - SP_BAR_H - 1f;
			PixelScene.align(spBar);
		}
	}

	public void updateAvatar(){
		avatar.copy( HeroSprite.avatar( Dungeon.hero ) );
	}

	public void alpha( float value ){
		value = GameMath.gate(0, value, 1f);
		bg.alpha(value);
		heroPaneCutout.alpha(value);
		hpCutout.alpha(value);
		avatar.alpha(value);
		shieldHP.alpha(value);
		hp.alpha(value);
		hpText.alpha(0.6f*value);
		exp.alpha(value);
		if (expText != null) expText.alpha(0.6f*value);
		level.alpha(value);
		compass.alpha(value);
		busy.alpha(value);
		counter.alpha(value);
	}

	public void showStarParticles(){
		Emitter emitter = (Emitter)recycle( Emitter.class );
		emitter.revive();
		emitter.pos( avatar.center() );
		emitter.burst( Speck.factory( Speck.STAR ), 12 );
	}

}
