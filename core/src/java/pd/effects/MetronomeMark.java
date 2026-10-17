/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.effects;

import pd.Dungeon;
import pd.items.unique.items.Metronome;
import pd.tiles.DungeonTilemap;
import render.noosa.ColorBlock;
import render.noosa.ui.Component;

/**
 * SPSEXPD: 节拍器标出的「下一拍」格子高亮。
 *
 * <p>用四个描边条画一个方框（不透明的暖金色）。相比整格半透明块，描边在任何地板色上都读得出来。</p>
 */
public class MetronomeMark extends Component {

	private static final int COLOR = 0xFF8A00;   // 橙金色，不透明
	private static final float EDGE = 2f;

	private final ColorBlock top = new ColorBlock( 1, 1, COLOR );
	private final ColorBlock bottom = new ColorBlock( 1, 1, COLOR );
	private final ColorBlock left = new ColorBlock( 1, 1, COLOR );
	private final ColorBlock right = new ColorBlock( 1, 1, COLOR );

	private int lastCell = Integer.MIN_VALUE;
	private boolean loggedOnce = false;

	public MetronomeMark() {
		super();
		add( top );
		add( bottom );
		add( left );
		add( right );
	}

	@Override
	protected void layout() {
		float w = width();
		float h = height();
		if (w <= 0) w = DungeonTilemap.SIZE;
		if (h <= 0) h = DungeonTilemap.SIZE;

		top.scale.set( w, EDGE );
		top.x = left();
		top.y = top();

		bottom.scale.set( w, EDGE );
		bottom.x = left();
		bottom.y = top() + h - EDGE;

		left.scale.set( EDGE, h );
		left.x = left();
		left.y = top();

		right.scale.set( EDGE, h );
		right.x = left() + w - EDGE;
		right.y = top();
	}

	@Override
	public void update() {
		super.update();

		int cell = Metronome.currentBeatCell();
		if (cell != lastCell) {
			System.err.println( "[SPSEXPD] MetronomeMark: cell=" + cell
					+ " x=" + (int)x + " y=" + (int)y
					+ " w=" + (int)width() + " h=" + (int)height()
					+ " visible=" + visible + " active=" + active
					+ " camera=" + (camera == null ? "null" : "ok")
					+ " members=" + (parent == null ? "noparent" : "ok") );
			lastCell = cell;
			if (!loggedOnce && cell >= 0) {
				loggedOnce = true;
				System.err.println( "[SPSEXPD] MetronomeMark top.x=" + (int)top.x + " top.y=" + (int)top.y
						+ " top.w=" + (int)top.width() + " top.h=" + (int)top.height() );
			}
		}

		if (cell < 0 || Dungeon.level == null || Dungeon.hero == null
				|| !Metronome.equipped( Dungeon.hero ) ) {
			visible = false;
			return;
		}
		visible = true;

		render.utils.geom.PointF p = DungeonTilemap.tileToWorld( cell );
		setPos( p.x, p.y );
		setSize( DungeonTilemap.SIZE, DungeonTilemap.SIZE );
	}
}
