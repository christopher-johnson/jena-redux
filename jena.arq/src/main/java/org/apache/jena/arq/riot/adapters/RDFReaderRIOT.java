/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.arq.riot.adapters ;

import java.io.InputStream ;
import java.io.Reader ;
import java.util.Locale ;

import org.apache.jena.core.graph.GraphEvents ;
import org.apache.jena.core.rdf.model.Model ;
import org.apache.jena.core.rdf.model.RDFErrorHandler ;
import org.apache.jena.core.rdf.model.RDFReader ;
import org.apache.jena.core.rdf.model.impl.RDFDefaultErrorHandler ;
import org.apache.jena.arq.riot.Lang ;
import org.apache.jena.arq.riot.RDFDataMgr ;
import org.apache.jena.arq.riot.RDFLanguages ;
import org.apache.jena.arq.sparql.util.Context ;
import org.apache.jena.arq.sparql.util.Symbol ;

/** Adapter from Jena2 original style adapter to RIOT reader. */
public class RDFReaderRIOT implements RDFReader {
    private final String      basename ;
    protected final Lang      hintlang ;
    protected Context         context      = new Context() ;
    protected RDFErrorHandler errorHandler = new RDFDefaultErrorHandler() ;

    public RDFReaderRIOT() {
        this((Lang)null) ;
    }

    public RDFReaderRIOT(String lang) {
        this(lang != null ? RDFLanguages.nameToLang(lang) : null) ;
    }

    public RDFReaderRIOT(Lang hintlang) {
        this.hintlang = hintlang ;
        this.basename =  (hintlang==null) 
            ? "org.apache.jena.arq.riot.reader.generic"
            : "org.apache.jena.arq.riot.reader." + hintlang.getLabel().toLowerCase(Locale.ROOT) ;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void read(Model model, Reader r, String base) {
        startRead(model) ;
        RDFDataMgr.read(model, r, base, hintlang) ;
        finishRead(model) ;
    }

    @Override
    public void read(Model model, InputStream r, String base) {
        startRead(model) ;
        RDFDataMgr.read(model, r, base, hintlang) ;
        finishRead(model) ;
    }

    @Override
    public void read(Model model, String url) {
        startRead(model) ;
        RDFDataMgr.read(model, url, hintlang) ;
        finishRead(model) ;
    }

    @Override
    public Object setProperty(String propName, Object propValue) {
        Symbol sym = Symbol.create(basename + propName) ;
        Object oldObj = context.get(sym) ;
        return oldObj ;
    }

    protected void startRead(Model model) {
        model.notifyEvent(GraphEvents.startRead) ;
    }

    protected void finishRead(Model model) {
        model.notifyEvent(GraphEvents.finishRead) ;
    }

    @Override
    public RDFErrorHandler setErrorHandler(RDFErrorHandler errHandler) {
        RDFErrorHandler old = errorHandler ;
        errorHandler = errHandler ;
        return old ;
    }
}
