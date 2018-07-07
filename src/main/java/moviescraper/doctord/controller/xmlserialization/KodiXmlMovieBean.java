package moviescraper.doctord.controller.xmlserialization;

import java.io.IOException;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.net.MalformedURLException;

import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

/**
 * Class which handles serializing a Movie object to and from XML
 */
public class KodiXmlMovieBean {

	private String title;
	private String originaltitle;
	private String sorttitle;
	private String set;
	private String year;
	private String top250;
	private String trailer;
	private String votes;
	private String rating;
	private String outline;
	private String plot;
	private String tagline;
	private String runtime;
	private String releasedate;
	private String studio;
	private String[] thumb;
	private KodiXmlFanartBean fanart;
	private String mpaa;
	private String id;
	private String[] genre;
	private String[] tag;
	// private String[] credits; add in later
	private ArrayList<KodiXmlActorBean> actor;
	private String[] director;

	public static KodiXmlMovieBean makeFromXML(String xml) {
		XStream xstream = KodiXmlMovieBean.getXMLSerializer();
		xstream.ignoreUnknownElements();
		try {
			KodiXmlMovieBean beanToReturn = (KodiXmlMovieBean) xstream.fromXML(xml);
			return beanToReturn;
		} catch (Exception e) {
			System.err.println("File read from nfo is not in Kodi XML format. This movie will not be read in.");
			return null;
		}

	}

	/**
	 * Constructor - handles conversion of a Movie object to a KodiXmlMovieBean object.
	 * Program preferences are taken into account when performing the object conversion so that, for example,
	 * certain fields will not be written to the XML
	 * @param movie - Movie to create the KodiXmlMovieBean from
	 */
	public KodiXmlMovieBean(Movie movie) {
		title = movie.getTitle().getTitle();
		originaltitle = movie.getOriginalTitle().getOriginalTitle();
		sorttitle = movie.getSortTitle().getSortTitle();
		set = movie.getSet().getSet();
		year = movie.getYear().getYear();
		top250 = movie.getTop250().getTop250();
		trailer = movie.getTrailer().getTrailer();
		votes = movie.getVotes().getVotes();
		rating = movie.getRating().getRatingOutOfTen();
		outline = movie.getOutline().getOutline();
		plot = movie.getPlot().getPlot();
		tagline = movie.getTagline().getTagline();
		runtime = movie.getRuntime().getRuntime();
		releasedate = movie.getReleaseDate().getReleaseDate();
		studio = movie.getStudio().getStudio();

		// thumb - aka Posters
		// Preference value allows the user to not write out poster values to the file
		if (MoviescraperPreferences.getInstance().getWriteThumbTagsForPosterAndFanartToNfo()) {
			thumb = new String[movie.getPosters().length];
			for (int i = 0; i < movie.getPosters().length; i++) {
				if (movie.getPosters()[i].getThumbURL() != null) {
					thumb[i] = movie.getPosters()[i].getThumbURL().toString();
				} else {
					thumb[i] = "";
				}
			}
		} else {
			thumb = new String[0];
		}

		//fanart
		// Preference value allows the user to not write out fanart values to the file
		if (MoviescraperPreferences.getInstance().getWriteThumbTagsForPosterAndFanartToNfo()) {
			fanart = new KodiXmlFanartBean(movie.getFanart());
		} else {
			fanart = new KodiXmlFanartBean(new Thumb[0]);
		}

		mpaa = movie.getMpaa().getMPAARating();
		id = movie.getId().getId();
		// genre
		genre = new String[movie.getGenres().size()];
		for (int i = 0; i < genre.length; i++) {
			genre[i] = movie.getGenres().get(i).getGenre();
		}
		// tag
		tag = new String[movie.getTags().size()];
		for (int i = 0; i < tag.length; i++) {
			tag[i] = movie.getTags().get(i).getTag();
		}
		// director
		director = new String[movie.getDirectors().size()];
		for (int i = 0; i < director.length; i++) {
			director[i] = movie.getDirectors().get(i).getName();
		}

		// actor
		actor = new ArrayList<>(movie.getActors().size());
		for (Actor currentActor : movie.getActors()) {
			if (currentActor.getThumb() != null && currentActor.getThumb().getThumbURL() != null) {
				actor.add(new KodiXmlActorBean(currentActor.getName(), currentActor.getRole(), currentActor.getThumb().getThumbURL().toString()));
			} else {
				actor.add(new KodiXmlActorBean(currentActor.getName(), currentActor.getRole(), ""));
			}
		}
	}

	public Movie toMovie() throws IOException {

		ArrayList<Actor> actors = new ArrayList<>();
		if (actor != null) {
			actors = new ArrayList<>(actor.size());
			for (KodiXmlActorBean currentActor : actor) {
				actors.add(currentActor.toActor());
			}
		}

		Thumb[] posterThumbs;
		if (thumb != null) {
			posterThumbs = new Thumb[thumb.length];
			for (int i = 0; i < posterThumbs.length; i++) {
				try {
					posterThumbs[i] = new Thumb(thumb[i]);
				} catch (MalformedURLException e) {
					System.out.println("Ignoring Thumb url: " + thumb[i]);
				}
			}
		} else {
			posterThumbs = new Thumb[0];
		}

		Thumb[] fanartThumbs;
		if (fanart != null && fanart.getThumb() != null) {
			fanartThumbs = new Thumb[fanart.getThumb().length];
			for (int i = 0; i < fanartThumbs.length; i++) {
				try {
					fanartThumbs[i] = new Thumb(fanart.getThumb()[i]);
				} catch (MalformedURLException e) {
					System.out.println("Ignoring Fanart url: " + fanart.getThumb()[i]);
				}
			}
		} else {
			fanartThumbs = new Thumb[0];
		}

		ArrayList<Genre> genres = new ArrayList<>();
		if (genre != null) {
			for (int i = 0; i < genre.length; i++) {
				genres.add(new Genre(genre[i]));
			}
		}

		ArrayList<Tag> tags = new ArrayList<>();
		if (tag != null) {
			for (int i = 0; i < tag.length; i++) {
				tags.add(new Tag(tag[i]));
			}
		}

		ArrayList<Director> directors = new ArrayList<>();
		if (director != null) {
			directors = new ArrayList<>(director.length);
			for (int i = 0; i < director.length; i++) {
				directors.add(new Director(director[i], null));
			}
		}
		Thumb[] emptyExtraFanrt = new Thumb[0];
		Movie movie = new Movie(actors, directors, fanartThumbs, emptyExtraFanrt, genres, tags, new ID(id), new MPAARating(mpaa), new OriginalTitle(originaltitle),
				new Outline(outline), new Plot(plot), posterThumbs, new Rating(10, rating), new ReleaseDate(releasedate), new Runtime(runtime), new Set(set),
				new SortTitle(sorttitle), new Studio(studio), new Tagline(tagline), new Title(title), new Top250(top250), new Trailer(trailer), new Votes(votes), new Year(year));
		return movie;
	}

	public String toXML() {
		String xml = getXMLSerializer().toXML(this);
		return xml;
	}

	private static XStream getXMLSerializer() {
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		xstream.omitField(Thumb.class, "thumbImage");
		xstream.alias("movie", KodiXmlMovieBean.class);
		xstream.alias("thumb", Thumb.class);
		xstream.alias("actor", Actor.class);
		xstream.alias("actor", KodiXmlActorBean.class);
		xstream.alias("fanart", KodiXmlFanartBean.class);
		xstream.addImplicitCollection(KodiXmlMovieBean.class, "actor");
		xstream.addImplicitArray(KodiXmlMovieBean.class, "thumb", "thumb");
		xstream.addImplicitArray(KodiXmlMovieBean.class, "director", "director");
		xstream.addImplicitArray(KodiXmlFanartBean.class, "thumb", "thumb");
		xstream.addImplicitArray(KodiXmlMovieBean.class, "genre", "genre");
		xstream.addImplicitArray(KodiXmlMovieBean.class, "tag", "tag");
		return xstream;
	}

}
