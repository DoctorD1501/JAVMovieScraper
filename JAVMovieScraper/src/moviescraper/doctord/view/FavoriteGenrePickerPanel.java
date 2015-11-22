package moviescraper.doctord.view;

import java.util.ArrayList;
import java.util.List;

import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class FavoriteGenrePickerPanel extends FavoriteItemPickerPanel {

	private static final long serialVersionUID = 1L;
	public final static String listSeperator = ",,";

	public FavoriteGenrePickerPanel() {
		super();
	}

	@Override
	public String[] getSettingValues() {
		ArrayList<Genre> favoriteGenreArrayList = getFavoriteGenresFromPreferences();
		String[] genreArray = new String[favoriteGenreArrayList.size()];
		for(int i = 0; i < favoriteGenreArrayList.size(); i++)
		{
			genreArray[i] = favoriteGenreArrayList.get(i).getGenre();
		}
		return genreArray;
	}
	
	public static ArrayList<Genre> getFavoriteGenresFromPreferences() {
		String[] existingFavoriteGenresArray = MoviescraperPreferences.getInstance().getfrequentlyUsedGenres().split(listSeperator);
		ArrayList<Genre> favoriteGenresToReturn = new ArrayList<Genre>(existingFavoriteGenresArray.length);
		for(String existingFavoriteGenre : existingFavoriteGenresArray)
		{
			favoriteGenresToReturn.add(new Genre(existingFavoriteGenre));
		}
		//alphabetical order list
		favoriteGenresToReturn.sort((p1, p2) -> p1.getGenre().compareTo(p2.getGenre()));
		return favoriteGenresToReturn;
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
		MoviescraperPreferences.getInstance().setFrequentlyUsedGenres(preferenceValue);

	}

}
