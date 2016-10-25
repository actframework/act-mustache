package act.view.mustache;

import act.Act;
import act.app.App;
import act.util.ActContext;
import act.view.Template;
import act.view.View;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheNotFoundException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MustacheView extends View {

    public static final String ID = "mustache";

    DefaultMustacheFactory mf;
    private boolean cacheEnabled = Act.isProd();

    private ConcurrentMap<String, Template> templateCache = new ConcurrentHashMap<String, Template>();

    @Override
    public String name() {
        return ID;
    }

    @Override
    protected Template loadTemplate(String resourcePath, ActContext context) {
        Template template = loadTemplateFromCache(resourcePath);
        if (null == template) {
            Thread t0 = Thread.currentThread();
            ClassLoader cl0 = t0.getContextClassLoader();
            try {
                t0.setContextClassLoader(context.app().classLoader());
                if (!cacheEnabled) {
                    // ensure new MustacheFactory instance to allow
                    // template reload when running in dev mode
                    init(context.app());
                }
                Mustache mustache = mf.compile(resourcePath);
                template = new MustacheTemplate(mustache);
            } catch (MustacheNotFoundException e) {
                return null;
            } finally {
                t0.setContextClassLoader(cl0);
            }
            cacheTemplate(resourcePath, template);
        }
        return template;
    }

    @Override
    protected void init(App app) {
        // Mustache class path resource resolver cannot resolve path start with "/"
        mf = new DefaultMustacheFactory(templateHome().substring(1));
    }


    private Template loadTemplateFromCache(String path) {
        if (!cacheEnabled) {
            return null;
        }
        return templateCache.get(path);
    }

    private void cacheTemplate(String path, Template template) {
        if (!cacheEnabled || null == template) {
            return;
        }
        templateCache.put(path, template);
    }
}
