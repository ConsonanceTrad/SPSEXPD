/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.ui;

import pd.Statistics;
import pd.scenes.PixelScene;
import render.noosa.BitmapText;
import render.noosa.Image;
import render.noosa.ui.Component;

/**
 * SPSEXPD: 右上角按钮组里的游戏内时间显示，专责组件（与 MenuPane 那排按钮同一行，位于其最左）。
 *
 * <p>左侧是日/夜/午夜图标（12x11 自绘，见 {@link Icons#TIME_DAY} 等），右侧是 {@code HH:MM} 文本。
 * 数据源是 {@link Statistics#spsTime}（一天 1440 分钟，开局 360 = 6:00，英雄每回合推进）；
 * 日期不在这里显示，改由「挂历」物品承担（{@code Statistics.calendarYear/Month/Day}）。</p>
 *
 * <p>相位划分：白天 07:00–18:59 用太阳；夜晚 19:00–21:59 与 02:00–06:59 用淡蓝弯月；
 * 午夜 22:00–01:59 用深蓝月加星。夜晚文本转淡蓝，便于直接看到昼夜推进。</p>
 *
 * <p>⚠️ 关键坑：{@link Component} 的 {@code x/y/width/height} 只是<b>逻辑坐标</b>——
 * {@code render.noosa.Group} 绘制子元素时<b>不会</b>自动加上这个偏移，子元素必须由
 * {@link #layout()} 自己摆到绝对坐标 {@code x/y} 处。少这一步，内容会停在 (0,0)，
 * 也就是看起来"跑到屏幕左上角"。</p>
 */
public class TimeLabel extends Component {

	private static final int DAY_TEXT   = 0xFFFFFF;
	private static final int NIGHT_TEXT = 0x8FB8FF;

	/** 图标与文本之间的间隔。 */
	private static final float GAP = 1f;

	private Image iconDay;
	private Image iconNight;
	private Image iconMidnight;
	private BitmapText label;

	private int   lastMinutes = Integer.MIN_VALUE;
	private Phase lastPhase;

	/** 昼夜相位，决定用哪张图标与什么文本颜色。 */
	private enum Phase { DAY, NIGHT, MIDNIGHT }

	@Override
	protected void createChildren(){
		iconDay = Icons.get( Icons.TIME_DAY );
		add( iconDay );
		iconNight = Icons.get( Icons.TIME_NIGHT );
		add( iconNight );
		iconMidnight = Icons.get( Icons.TIME_MIDNIGHT );
		add( iconMidnight );
		//首次刷新前先只显示白天那一张，避免三张叠在一起
		iconNight.visible = false;
		iconMidnight.visible = false;

		label = new BitmapText( PixelScene.pixelFont );
		label.hardlight( DAY_TEXT );
		add( label );
	}

	/** SPSEXPD: 按当前游戏内时间刷新图标、文本与尺寸；时间没变就什么都不做。 */
	public void refresh(){
		int minutes = (int)Statistics.spsTime % 1440;
		if (minutes == lastMinutes){
			return;
		}
		lastMinutes = minutes;

		int h = minutes / 60;
		int m = minutes % 60;
		label.text( (h < 10 ? "0" + h : Integer.toString(h)) + ":"
				+ (m < 10 ? "0" + m : Integer.toString(m)) );
		label.measure();

		Phase phase = phaseOf( h );
		if (phase != lastPhase){
			lastPhase = phase;
			iconDay.visible      = phase == Phase.DAY;
			iconNight.visible    = phase == Phase.NIGHT;
			iconMidnight.visible = phase == Phase.MIDNIGHT;
			label.hardlight( phase == Phase.DAY ? DAY_TEXT : NIGHT_TEXT );
		}

		//setSize() 内部会调用 layout()
		setSize( iconDay.width() + GAP + label.width(),
				Math.max( iconDay.height(), label.height() ) );
	}

	/** 白天 07:00–18:59；午夜 22:00–01:59；其余为夜晚。 */
	private static Phase phaseOf( int hour ){
		if (hour >= 7 && hour < 19){
			return Phase.DAY;
		}
		if (hour >= 22 || hour < 2){
			return Phase.MIDNIGHT;
		}
		return Phase.NIGHT;
	}

	@Override
	protected void layout(){
		if (label == null){
			return;
		}
		//SPSEXPD: 子元素用绝对坐标——图标与文本都摆到本组件的 x/y 处
		iconDay.x = iconNight.x = iconMidnight.x = x;
		iconDay.y = iconNight.y = iconMidnight.y = y;

		label.x = x + iconDay.width() + GAP;
		label.y = y + (height - label.height()) / 2f;
		PixelScene.align( label );
	}
}
