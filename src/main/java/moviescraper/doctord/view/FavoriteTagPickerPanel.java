package moviescraper.doctord.view;

import java.util.ArrayList;
import moviescraper.doctord.model.dataitem.Tag;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class FavoriteTagPickerPanel extends FavoriteItemPickerPanel {

	private static final long serialVersionUID = 1L;
	public final static String listSeperator = ",,";

	public FavoriteTagPickerPanel() {
		super();
	}

	@Override
	public String[] getSettingValues() {
		ArrayList<Tag> favoriteTagArrayList = getFavoriteTagsFromPreferences();
		String[] tagArray = new String[favoriteTagArrayList.size()];
		for(int i = 0; i < favoriteTagArrayList.size(); i++)
		{
			tagArray[i] = favoriteTagArrayList.get(i).getTag();
		}
		return tagArray;
	}
	
	public static ArrayList<Tag> getFavoriteTagsFromPreferences() {
		String[] existingFavoriteTagsArray = MoviescraperPreferences.getInstance().getfrequentlyUsedTags().split(listSeperator);
		ArrayList<Tag> favoriteTagsToReturn = new ArrayList<>(existingFavoriteTagsArray.length);
		for(String existingFavoriteTag : existingFavoriteTagsArray)
		{
			favoriteTagsToReturn.add(new Tag(existingFavoriteTag));
		}
		//alphabetical order list
		favoriteTagsToReturn.sort((p1, p2) -> p1.getTag().compareTo(p2.getTag()));
		return favoriteTagsToReturn;
	}

	@Override
	public void storeSettingValues() {
		String preferenceValue = "";
		for(int i = 0; i < favoritesListModel.size(); i++)
		{
			preferenceValue += favoritesListModel.get(i);
			if(i != favoritesListModel.size() -1)
				preferenceValue += listSeperator ;
		}
		MoviescraperPreferences.getInstance().setFrequentlyUsedTags(preferenceValue);

	}

}
