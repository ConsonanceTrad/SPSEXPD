package pd.items.potions;

public abstract class SpsPotion extends Potion {
	@Override public boolean isKnown() { return true; }
	@Override public void setKnown() { }
	@Override public boolean isIdentified() { return true; }
}
