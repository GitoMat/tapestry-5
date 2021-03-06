// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.webresources;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.TapestryConstants;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.assets.AssetChecksumGenerator;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.apache.tapestry5.webresources.WebResourcesSymbols;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * A wrapper around the Google Closure {@link Compiler} used to minimize
 * a JavaScript resource.
 */
public class GoogleClosureMinimizer extends AbstractMinimizer
{

    private final static String OUTPUT_CHARSET = "utf-8";

    private final List<SourceFile> EXTERNS = Collections.emptyList();

    private final Request request;
    private final CompilationLevel compilationLevel;

    static
    {
        Compiler.setLoggingLevel(Level.SEVERE);
    }

    public GoogleClosureMinimizer(Logger logger, OperationTracker tracker, AssetChecksumGenerator checksumGenerator, Request request,
                                  @Symbol(WebResourcesSymbols.COMPILATION_LEVEL)
                                  CompilationLevel compilationLevel)
    {
        super(logger, tracker, checksumGenerator, "text/javascript");
        this.request = request;
        this.compilationLevel = compilationLevel;
    }

    @Override
    protected boolean isEnabled(StreamableResource resource)
    {
        return request.getAttribute(TapestryConstants.DISABLE_JAVASCRIPT_MINIMIZATION) == null;
    }

    @Override
    protected InputStream doMinimize(StreamableResource resource) throws IOException
    {
        // Don't bother to pool the Compiler

        CompilerOptions options = new CompilerOptions();

        compilationLevel.setOptionsForCompilationLevel(options);

        options.setCodingConvention(new ClosureCodingConvention());
        options.setOutputCharset(OUTPUT_CHARSET);
        options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.WARNING);

        Compiler compiler = new Compiler();

        compiler.disableThreads();

        SourceFile input = SourceFile.fromInputStream(resource.toString(), resource.openStream());

        List<SourceFile> inputs = Collections.singletonList(input);

        Result result = compiler.compile(EXTERNS, inputs, options);

        if (result.success)
        {
            return IOUtils.toInputStream(compiler.toSource(), OUTPUT_CHARSET);
        }

        throw new RuntimeException(String.format("Compilation failed: %s.",
                InternalUtils.join(CollectionFactory.newList(result.errors), ";")));
    }
}
