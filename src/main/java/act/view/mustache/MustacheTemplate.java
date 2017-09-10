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
