package moviescraper.doctord.scraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.logging.Logger;
import org.jsoup.select.Elements;

public class CloudflareHandler {

	private static final Logger LOGGER = Logger.getLogger(CloudflareHandler.class.getName());

	private final URL url;
	private final ScriptEngine engine;

	public CloudflareHandler(URL url) {
		this.url = url;
		ScriptEngineManager engineManager = new ScriptEngineManager();
		engine = engineManager.getEngineByName("nashorn");
	}

	public URL handle(Document challenge) throws UnexpectedWebsiteData {

		Element form = challenge.select("#challenge-form").first();
		String jschl_vc = form.select("[name=jschl_vc]").first().val();
		String pass = form.select("[name=pass]").first().val();
		Elements scripts = challenge.select("script");
		if (scripts.size() != 1) {
			throw new UnexpectedWebsiteData("Javascript challenge has changed");
		}
		LOGGER.log(Level.FINE, "Body: {0}", challenge.html());
		String jschl_answer = this.getAnswer(form, this.url, scripts.get(0).html());
		LOGGER.log(Level.FINE, "JS Challenge response: {0}", jschl_answer);

		try {
			Thread.sleep(4000);
		} catch (InterruptedException ex) {
			LOGGER.log(Level.WARNING, "Wait for cloudflare timeout have been interrupted");
		}

		URIBuilder builder = new URIBuilder();
		builder.setScheme(url.getProtocol());
		builder.setHost(url.getHost());
		builder.setPath(form.attributes().get("action"));
		builder.addParameter("jschl_vc", jschl_vc);
		builder.addParameter("pass", pass);
		builder.addParameter("jschl_answer", jschl_answer);
		LOGGER.log(Level.FINE, "Cloudflare answer: {0}", builder.toString());
		try {
			return new URL(builder.toString());
		} catch (MalformedURLException ex) {
			throw new UnexpectedWebsiteData("Cannot generated valid URL: " + builder.toString(), ex);
		}
	}

	public static URL handleCloudflare(URL url, Document challenge) throws UnexpectedWebsiteData {

		CloudflareHandler cloudflare = new CloudflareHandler(url);
		return cloudflare.handle(challenge);
	}

	private String getAnswer(Element form, URL url, String script) throws UnexpectedWebsiteData {

		Bindings bindings = engine.createBindings();

		String a = "0";
		StringBuilder sb = new StringBuilder();
		int challenge_start_index = script.indexOf("var s,t,o,p,b,r,e,a,k,i,n,g,f");
		int challenge_end_index = script.indexOf("f.submit");
		if (challenge_start_index == -1 || challenge_end_index == -1) {
			throw new UnexpectedWebsiteData("Javascript challenge has changed.");
		}
		String[] lines = script.substring(challenge_start_index, challenge_end_index).split("\\r\\n|\\r|\\n");
		if (lines.length != 10) {
			throw new UnexpectedWebsiteData("Javascript challenge has changed");
		}

		sb.append(lines[0].replace("var s,t,o,p,b,r,e,a,k,i,n,g,f, ", "var "));
		sb.append("t=\"");
		sb.append(url.getHost());
		sb.append("\";\n");
		bindings.put("a", a);
		bindings.put("f", form);
		sb.append(lines[7].replace("a.value", "a"));
		sb.append(";a;");

		LOGGER.log(Level.FINE, "Rebuilt JS: {0}", sb.toString());
		try {
			a = engine.eval(sb.toString(), bindings).toString();
			return a;
		} catch (ScriptException e) {
			LOGGER.log(Level.WARNING, "Cannot resolv JS challenge", e);
			throw new UnexpectedWebsiteData("Cannot resolv JS challenge", e);
		}
	}
}
