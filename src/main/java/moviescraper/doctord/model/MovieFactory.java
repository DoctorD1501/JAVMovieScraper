package moviescraper.doctord.model;

import java.util.ArrayList;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.MPAARating;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Outline;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tag;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;

public class MovieFactory {
	
	/**
	 * @return A movie object with every field initialized to either blank (for things like string values) or having zero values (array/list objects)
	 */
	public static Movie createEmptyMovie() {
			
			
			ArrayList<Actor> actors = new ArrayList<>();
			
			ArrayList<Director> directors = new ArrayList<>();
			
			Thumb[] fanart = new Thumb[0]; 
			Thumb[] extraFanart = new Thumb[0]; 

			ArrayList<Genre> genres = new ArrayList<>();
			
			ArrayList<Tag> tags = new ArrayList<>();
			
			
			ID id = ID.BLANK_ID;
			MPAARating mpaa = MPAARating.BLANK_RATING;
			OriginalTitle originalTitle = OriginalTitle.BLANK_ORIGINALTITLE;
			Outline outline = Outline.BLANK_OUTLINE;
			Plot plot = Plot.BLANK_PLOT;
			Thumb[] posters = new Thumb[0]; 

			Rating rating = Rating.BLANK_RATING;
			ReleaseDate releaseDate = ReleaseDate.BLANK_RELEASEDATE;
			Runtime runtime = Runtime.BLANK_RUNTIME;
			Set set = Set.BLANK_SET;
			SortTitle sortTitle= SortTitle.BLANK_SORTTITLE;
			Studio studio = Studio.BLANK_STUDIO;
			Tagline tagline = Tagline.BLANK_TAGLINE;
			Title title = new Title("");
			Top250 top250 = Top250.BLANK_TOP250;
			Trailer trailer = Trailer.BLANK_TRAILER;
			Votes votes = Votes.BLANK_VOTES;
			Year year = Year.BLANK_YEAR;
			
			return new Movie(actors, directors, fanart, extraFanart, genres, tags, id, mpaa, originalTitle, outline, plot, posters, rating, releaseDate, runtime, set, sortTitle, studio, tagline, title, top250, trailer, votes, year);
	}

}
