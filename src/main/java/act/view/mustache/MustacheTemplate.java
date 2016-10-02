package act.view.mustache;

import act.view.TemplateBase;
import com.github.mustachejava.Mustache;
import org.osgl.$;

import java.io.StringWriter;
import java.util.Map;

public class MustacheTemplate extends TemplateBase {

    private Mustache mustache;

    MustacheTemplate(Mustache mustache) {
        this.mustache = $.notNull(mustache);
    }

    @Override
    protected String render(Map<String, Object> renderArgs) {
        StringWriter sw = new StringWriter();
        mustache.execute(sw, renderArgs);
        return sw.toString();
    }
}
