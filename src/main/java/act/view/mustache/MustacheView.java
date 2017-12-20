package act.view.mustache;

/*-
 * #%L
 * ACT Mustache
 * %%
 * Copyright (C) 2016 - 2017 ActFramework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import act.Act;
import act.app.App;
import act.view.Template;
import act.view.View;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheNotFoundException;
import org.osgl.util.IO;
import org.osgl.util.S;
import osgl.version.Version;
import osgl.version.Versioned;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Versioned
public class MustacheView extends View {

    public static final Version VERSION = Version.of(MustacheView.class);

    public static final String ID = "mustache";

    DefaultMustacheFactory mf;
    private boolean cacheEnabled = Act.isProd();
    private String suffix;

    private ConcurrentMap<String, Template> templateCache = new ConcurrentHashMap<String, Template>();

    @Override
    public String name() {
        return ID;
    }

    @Override
    protected Template loadTemplate(String resourcePath) {
        Template template = loadTemplateFromCache(resourcePath);
        if (null == template) {
            Thread t0 = Thread.currentThread();
            ClassLoader cl0 = t0.getContextClassLoader();
            try {
                App app = Act.app();
                t0.setContextClassLoader(app.classLoader());
                if (!cacheEnabled) {
                    // ensure new MustacheFactory instance to allow
                    // template reload when running in dev mode
                    init(app);
                }
                Mustache mustache = mf.compile(resourcePath);
                template = new MustacheTemplate(mustache);
            } catch (MustacheNotFoundException e) {
                if (resourcePath.endsWith(suffix)) {
                    return null;
                }
                return loadTemplate(S.concat(resourcePath, suffix));
            } finally {
                t0.setContextClassLoader(cl0);
            }
            cacheTemplate(resourcePath, template);
        }
        return template;
    }

    @Override
    protected Template loadInlineTemplate(String content) {
        Mustache mustache = mf.compile(IO.reader(content), content);
        return new MustacheTemplate(mustache);
    }

    @Override
    protected void init(App app) {
        // Mustache class path resource resolver cannot resolve path start with "/"
        mf = new DefaultMustacheFactory(templateHome().substring(1));
        suffix = app.config().get("view.mustache.suffix");
        if (null == suffix) {
            suffix = ".mustache";
        } else {
            suffix = suffix.startsWith(".") ? suffix : S.concat(".", suffix);
        }
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
