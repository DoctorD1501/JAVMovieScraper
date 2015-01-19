package moviescraper.doctord.preferences;

public class GuiSettings extends Settings {

	enum Key implements Settings.Key {
		showToolbar,
		showOutputPanel,
		;

		@Override
		public String getKey() {
			// prefix setting key to avoid clashing
			return "Gui:" + toString();
		}	
	}
	
	public boolean getShowToolbar(){
		return getBooleanValue(Key.showToolbar, true);
	}
	
	public void setShowToolbar(boolean preferenceValue){
		setBooleanValue(Key.showToolbar, preferenceValue);
	}
	
	public boolean getShowOutputPanel(){
		return getBooleanValue(Key.showOutputPanel, false);
	}
	
	public void setShowOutputPanel(boolean preferenceValue){
		setBooleanValue(Key.showOutputPanel, preferenceValue);
	}
}
