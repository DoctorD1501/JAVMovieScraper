package moviescraper.doctord.GUI.renderer;

import java.awt.Component;
import java.lang.reflect.Field;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileItem;

public class MovieFieldCellRenderer implements ListCellRenderer<Field> {
	
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	
	public Component getListCellRendererComponent(JList<? extends Field> list, Field value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
		        isSelected, cellHasFocus);
		Field entry = (Field) value;
		
		renderer.setText(formatFieldText(entry.getName()));
		return renderer;
	}
	
	/**
	 * Transform a "aStringLikeThis" to "A String Like This" 
	 */
	public static String formatFieldText(String fieldNameInCamelCase)
	{
		String formattedText = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(WordUtils.capitalize(fieldNameInCamelCase)), " ");
		return formattedText;
	}

}
