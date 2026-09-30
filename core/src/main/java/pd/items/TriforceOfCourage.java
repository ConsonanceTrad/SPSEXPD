package pd.items;
import pd.Dungeon;
public class TriforceOfCourage extends TriforcePiece {
	@Override protected void collected() { Dungeon.triforceOfCourage = true; }
}
