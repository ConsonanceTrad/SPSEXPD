/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.ui;

import pd.Statistics;
import pd.scenes.PixelScene;
import render.gltextures.TextureCache;
import render.noosa.BitmapText;
import render.noosa.SkinnedBlock;
import render.noosa.ui.Component;

/**
 * SPSEXPD: HUD 右上角的游戏内时间显示。
 *
 * <p>数据源是 {@link Statistics#spsTime} / {@link Statistics#spsDays}（一天 1440 分钟，开局 360 = 6:00，
 * 英雄每回合推进），夜晚（{@link Statistics#spsNight()}）文字转为淡蓝，方便直接看到昼夜推进
 * ——配合暂停菜单「测试时间」下的「时间 +6 小时」调试按钮使用。</p>
 *
 * <p>显示格式：{@code D<第几天> HH:MM}。位置由 {@link #setRightInset(float)} 决定（避让右上角按钮条）。</p>
 */
public class TimeIndicator extends Component {

	private static final int   DAY_TEXT   = 0xFFFFFF;
	private static final int   NIGHT_TEXT = 0x8FB8FF;
	private static final int   BG_COLOR   = 0x88000000;
	private static final float PAD        = 1f;

	private BitmapText label;
	private SkinnedBlock bg;

	/** 右边缘对齐位置（由 MenuPane 布局时给出；<0 表示没有锚点，退回屏幕右上角）。 */
	private float anchorRight = -1f;
	private float anchorY     = 2f;

	private int     lastMinutes = Integer.MIN_VALUE;
	private int     lastDays    = Integer.MIN_VALUE;
	private boolean lastNight;

	public TimeIndicator(){
		super();
	}

	@Override
	protected void createChildren(){
		bg = new SkinnedBlock( 1, 1, TextureCache.createSolid( BG_COLOR ) );
		add( bg );

		label = new BitmapText( PixelScene.pixelFont );
		label.hardlight( DAY_TEXT );
		add( label );
	}

	/** SPSEXPD: 右边缘对齐位置——由 MenuPane 在 layout() 里传入（贴在事件记录/深度图标左侧）。 */
	public void setAnchorRight( float value ){
		anchorRight = value;
		reposition();
	}

	public void setAnchorY( float value ){
		anchorY = value;
		reposition();
	}

	/** SPSEXPD: 按当前尺寸贴到锚点右边缘；没有锚点时退回屏幕右上角。 */
	private void reposition(){
		float right = anchorRight >= 0f ? anchorRight
				: (PixelScene.uiCamera != null ? PixelScene.uiCamera.width - 2f : 158f);
		x = PixelScene.align( right - width );
		y = PixelScene.align( anchorY );
	}

	@Override
	public void update(){
		super.update();

		int minutes = (int)Statistics.spsTime;
		boolean night = Statistics.spsNight();
		if (minutes == lastMinutes && Statistics.spsDays == lastDays && night == lastNight){
			return;
		}
		lastMinutes = minutes;
		lastDays    = Statistics.spsDays;
		lastNight   = night;

		int h = minutes / 60;
		int m = minutes % 60;
		//SPSEXPD: UI 只显示时间——日期改由「挂历」物品显示（Statistics.calendarYear/Month/Day）
		label.text( (h < 10 ? "0" + h : Integer.toString(h)) + ":"
				+ (m < 10 ? "0" + m : Integer.toString(m)) );
		label.hardlight( night ? NIGHT_TEXT : DAY_TEXT );
		label.measure();

		float w = label.width() + PAD * 2f;
		float hgt = label.height() + PAD * 2f;
		bg.size( w, hgt );
		bg.x = 0;
		bg.y = 0;
		label.x = PAD;
		label.y = PAD;

		width  = w;
		height = hgt;

		//SPSEXPD: 尺寸算好后按锚点重新定位（位置由 MenuPane 给的右边缘决定）
		reposition();
	}
}
