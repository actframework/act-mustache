package act.view.mustache;

/*-
 * #%L
 * ACT Mustache
 * %%
 * Copyright (C) 2016 - 2018 ActFramework
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
import act.app.SourceInfo;
import act.view.TemplateException;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.TemplateContext;
import org.osgl.$;
import org.osgl.util.S;

import java.util.List;

public class MustacheTemplateException extends TemplateException {
    public MustacheTemplateException(RuntimeException t) {
        super(t);
    }

    @Override
    protected void populateSourceInfo(Throwable t) {
        if (null == t) {
            return;
        }
        if (t instanceof MustacheException) {
            populateSourceInfo((MustacheException) t);
        }
        if (null == sourceInfo) {
            Throwable cause = rootCauseOf(t);
            if (cause instanceof MustacheException) {
                MustacheException me = $.cast(cause);
                populateSourceInfo(me);
            }
            if (null == sourceInfo) {
                super.populateSourceInfo(t);
            }
        }
    }

    @Override
    public String errorMessage() {
        Throwable t = rootCauseOf(this);
        String msg = t.getLocalizedMessage();
        return S.blank(msg) ? t.toString() : msg;
    }

    @Override
    protected boolean isTemplateEngineInvokeLine(String line) {
        return false;
    }

    private void populateSourceInfo(MustacheException e) {
        TemplateContext context = e.getContext();
        if (null != context) {
            sourceInfo = new MustacheSourceInfo(context);
        }
    }

    private static class MustacheSourceInfo extends SourceInfo.Base {

        MustacheSourceInfo(TemplateContext context) {
            lineNumber = context.line();
            fileName = context.file();
            lines = readTemplateSource(fileName);
        }


        private static List<String> readTemplateSource(String template) {
            MustacheView view = (MustacheView) Act.viewManager().view(MustacheView.ID);
            return view.loadContent(template);
        }
    }
}
