package ch.puzzle.marinabackend;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;

@Component("overridingLocaleResolver")
public class LocaleResolver extends AcceptHeaderLocaleResolver {

    private static final List<Locale> LOCALES = asList(new Locale("en"), new Locale("de"));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String localeString = request.getParameter("lang");
        if (localeString == null || localeString.trim().isEmpty()) {
            String header = request.getHeader("Accept-Language");
            if (header == null || header.trim().isEmpty()) {
                return Locale.getDefault();
            } else {
                localeString = header;
            }
        }
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(localeString);
        return Locale.lookup(list, LOCALES);
    }
}
