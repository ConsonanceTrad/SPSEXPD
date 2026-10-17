/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.effects;

import pd.Dungeon;
import pd.actors.buffs.MetronomeBeat;
import pd.items.unique.items.Metronome;
import pd.scenes.PixelScene;
import pd.tiles.DungeonTilemap;
import render.noosa.ColorBlock;

/**
 * SPSEXPD: 节拍器标出的「下一拍」格子高亮。
 *
 * <p>节拍由系统每回合自动指定（见 {@link MetronomeBeat#autoBeat}），玩家必须看得见是哪一格，
 * 所以在世界层画一个跟随它的半透明方块，并做轻微呼吸闪烁。</p>
 */
public class MetronomeMark extends ColorBlock {

	private static final int COLOR = 0xFFFFFF;
	private static final float ALPHA_MIN = 0.22f;
	private static final float ALPHA_MAX = 0.60f;
	private static final float PERIOD = 0.9f;

	private float phase = 0f;
	private int lastCell = Integer.MIN_VALUE;
	private boolean loggedOnce = false;

	public MetronomeMark() {
		super( DungeonTilemap.SIZE, DungeonTilemap.SIZE, COLOR );
		alpha( 0f );
	}

	@Override
	public void update() {
		super.update();

		int cell = Metronome.currentBeatCell();

		//SPSEXPD: 诊断——第一次拿到有效格 / 每次换格时打一行，方便确认高亮走到了哪一步
		if (cell != lastCell) {
			if (!loggedOnce || cell >= 0) {
				System.err.println("[SPSEXPD] MetronomeMark: cell=" + cell
						+ " camera=" + (camera == null ? "null" : "ok")
						+ " level=" + (Dungeon.level == null ? "null" : "ok")
						+ " hero=" + (Dungeon.hero == null ? "null" : "ok")
						+ " equipped=" + (Dungeon.hero != null && Metronome.equipped( Dungeon.hero )));
				loggedOnce = true;
			}
			lastCell = cell;
		}

		if (cell < 0 || Dungeon.level == null || Dungeon.hero == null
				|| !Metronome.equipped( Dungeon.hero ) ) {
			visible = false;
			return;
		}
		visible = true;

		render.utils.geom.PointF center = DungeonTilemap.tileCenterToWorld( cell );
		//SPSEXPD: ColorBlock 的尺寸存在 scale 里（width()/height() 读的就是 scale），
		//不能用 width/height 字段——那两个是 1
		x = center.x - width() * 0.5f;
		y = center.y - height() * 0.5f;

		phase += render.noosa.Game.elapsed;
		if (phase > PERIOD) phase -= PERIOD;
		alpha( ALPHA_MIN + ( ALPHA_MAX - ALPHA_MIN )
				* ( 0.5f + 0.5f * (float)Math.sin( phase / PERIOD * Math.PI * 2 ) ) );
	}
}
