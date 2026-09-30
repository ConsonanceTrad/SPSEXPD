package pd.items;
import pd.Dungeon;
public class TriforceOfPower extends TriforcePiece {
	@Override protected void collected() { Dungeon.triforceOfPower = true; }
}
