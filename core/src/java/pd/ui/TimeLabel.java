/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.ui;

import pd.Statistics;
import pd.scenes.PixelScene;
import render.noosa.BitmapText;
import render.noosa.ui.Component;

/**
 * SPSEXPD: 右上角按钮组里的游戏内时间，专责组件（与 MenuPane 那排按钮同一行，位于其最左）。
 *
 * <p>数据源是 {@link Statistics#spsTime}（一天 1440 分钟，开局 360 = 6:00，英雄每回合推进）
 * 与 {@link Statistics#spsNight()}，夜晚文字转淡蓝，便于直接看到昼夜推进；日期不在这里显示，
 * 改由「挂历」物品承担（{@code Statistics.calendarYear/Month/Day}）。</p>
 *
 * <p>⚠️ 关键坑：{@link Component} 的 {@code x/y/width/height} 只是<b>逻辑坐标</b>——
 * {@code render.noosa.Group} 绘制子元素时<b>不会</b>自动加上这个偏移，子元素必须由
 * {@link #layout()} 自己摆到绝对坐标 {@code x/y} 处。少这一步，文本会停在 (0,0)，
 * 也就是看起来"跑到屏幕左上角"。</p>
 */
public class TimeLabel extends Component {

	private static final int DAY_TEXT   = 0xFFFFFF;
	private static final int NIGHT_TEXT = 0x8FB8FF;

	private BitmapText label;

	private int     lastMinutes = Integer.MIN_VALUE;
	private boolean lastNight;

	@Override
	protected void createChildren(){
		label = new BitmapText( PixelScene.pixelFont );
		label.hardlight( DAY_TEXT );
		add( label );
	}

	/** SPSEXPD: 按当前游戏内时间刷新文本与尺寸；时间没变就什么都不做。 */
	public void refresh(){
		int minutes = (int)Statistics.spsTime % 1440;
		boolean night = Statistics.spsNight();
		if (minutes == lastMinutes && night == lastNight){
			return;
		}
		lastMinutes = minutes;
		lastNight   = night;

		int h = minutes / 60;
		int m = minutes % 60;
		label.text( (h < 10 ? "0" + h : Integer.toString(h)) + ":"
				+ (m < 10 ? "0" + m : Integer.toString(m)) );
		label.hardlight( night ? NIGHT_TEXT : DAY_TEXT );
		label.measure();

		//setSize() 内部会调用 layout()
		setSize( label.width(), label.height() );
	}

	@Override
	protected void layout(){
		if (label == null){
			return;
		}
		//SPSEXPD: 子元素用绝对坐标——把文本摆到本组件的 x/y 处
		label.x = x;
		label.y = y;
		PixelScene.align( label );
	}
}
