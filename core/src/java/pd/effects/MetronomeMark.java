/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.effects;

import pd.Dungeon;
import pd.actors.buffs.MetronomeBeat;
import pd.items.unique.items.Metronome;
import pd.tiles.DungeonTilemap;
import render.noosa.ColorBlock;
import render.noosa.ui.Component;

/**
 * SPSEXPD: 节拍器标出的「下一拍」格子高亮。
 *
 * <p>节拍由系统每回合自动指定（见 {@link MetronomeBeat#autoBeat}），玩家必须看得见是哪一格，
 * 所以在世界层画一个跟随它的方框（四个描边条 + 四角点，比整格半透明块显眼得多）。</p>
 */
public class MetronomeMark extends Component {

	private static final int COLOR = 0xFFE066;   // 暖金色，压在浅色地板上也清楚
	private static final float EDGE = 2f;        // 描边粗细
	private static final float PERIOD = 0.9f;

	private final ColorBlock top = new ColorBlock( DungeonTilemap.SIZE, EDGE, COLOR );
	private final ColorBlock bottom = new ColorBlock( DungeonTilemap.SIZE, EDGE, COLOR );
	private final ColorBlock left = new ColorBlock( EDGE, DungeonTilemap.SIZE, COLOR );
	private final ColorBlock right = new ColorBlock( EDGE, DungeonTilemap.SIZE, COLOR );
	private final ColorBlock center = new ColorBlock( 4f, 4f, COLOR );

	private float phase = 0f;
	private int lastCell = Integer.MIN_VALUE;
	private boolean loggedOnce = false;

	public MetronomeMark() {
		super();
		add( top );
		add( bottom );
		add( left );
		add( right );
		add( center );
		visible = false;
	}

	@Override
	protected void layout() {
		top.x = left();
		top.y = top();
		top.scale.set( width(), EDGE );

		bottom.x = left();
		bottom.y = top() + height() - EDGE;
		bottom.scale.set( width(), EDGE );

		left.x = left();
		left.y = top();
		left.scale.set( EDGE, height() );

		right.x = left() + width() - EDGE;
		right.y = top();
		right.scale.set( EDGE, height() );

		center.x = left() + width() * 0.5f - 2f;
		center.y = top() + height() * 0.5f - 2f;
	}

	@Override
	public void update() {
		super.update();

		int cell = Metronome.currentBeatCell();

		//SPSEXPD: 诊断——换格时打一行
		if (cell != lastCell) {
			if (!loggedOnce || cell >= 0) {
				System.err.println( "[SPSEXPD] MetronomeMark: cell=" + cell
						+ " x=" + (int)x + " y=" + (int)y
						+ " hero=" + (Dungeon.hero == null ? "null" : "ok")
						+ " equipped=" + (Dungeon.hero != null && Metronome.equipped( Dungeon.hero )) );
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

		render.utils.geom.PointF centerPoint = DungeonTilemap.tileCenterToWorld( cell );
		setPos( centerPoint.x - DungeonTilemap.SIZE * 0.5f,
				centerPoint.y - DungeonTilemap.SIZE * 0.5f );
		setSize( DungeonTilemap.SIZE, DungeonTilemap.SIZE );

		phase += render.noosa.Game.elapsed;
		if (phase > PERIOD) phase -= PERIOD;
		float a = 0.55f + 0.45f * ( 0.5f + 0.5f * (float)Math.sin( phase / PERIOD * Math.PI * 2 ) );
		top.alpha( a );
		bottom.alpha( a );
		left.alpha( a );
		right.alpha( a );
		center.alpha( a );
	}
}
