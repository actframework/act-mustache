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
import act.view.TemplateBase;
import com.github.mustachejava.Mustache;
import org.osgl.$;
import org.osgl.bootstrap.Version;
import org.osgl.http.H;
import org.osgl.util.IO;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class MustacheTemplate extends TemplateBase {

    public static final Version VERSION = MustacheView.VERSION;

    private final Mustache mustache;
    private final boolean directWrite;

    MustacheTemplate(Mustache mustache) {
        this.mustache = $.notNull(mustache);
        this.directWrite = Act.isProd();
    }

    @Override
    protected void merge(Map<String, Object> renderArgs, H.Response response) {
        if (directWrite) {
            Writer writer = response.writer();
            try {
                mustache.execute(writer, renderArgs);
            } finally {
                IO.close(writer);
            }
        } else {
            super.merge(renderArgs, response);
        }
    }

    @Override
    protected String render(Map<String, Object> renderArgs) {
        StringWriter sw = new StringWriter();
        mustache.execute(sw, renderArgs);
        return sw.toString();
    }
}
