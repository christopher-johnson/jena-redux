/*
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

package org.apache.jena.arq.riot.resultset.rw;

import java.util.function.Supplier;

import org.apache.jena.arq.atlas.web.ContentType;
import org.apache.jena.arq.atlas.web.TypedInputStream;
import org.apache.jena.arq.query.ARQ;
import org.apache.jena.arq.query.Dataset;
import org.apache.jena.arq.query.DatasetFactory;
import org.apache.jena.core.rdf.model.Model;
import org.apache.jena.core.rdf.model.ModelFactory;
import org.apache.jena.arq.riot.*;
import org.apache.jena.arq.riot.resultset.ResultSetReaderRegistry;
import org.apache.jena.arq.riot.system.StreamRDF;
import org.apache.jena.arq.riot.system.StreamRDFLib;
import org.apache.jena.arq.riot.system.stream.StreamManager;
import org.apache.jena.arq.sparql.resultset.SPARQLResult;
import org.apache.jena.arq.sparql.util.Context;
import org.apache.jena.arq.system.Txn;

/** Read anything (RDF).
 * <li>By MIME type.
 * <li>By format (resultset, boolean, graph)
 */
public class ReadAnything {
    
    /** Read something RDF/SPARQL like */ 
    public static SPARQLResult read(String url) {
        return read(url, ARQ.getContext());
    }

    /** Read something RDF/SPARQL like */ 
    public static SPARQLResult read(String url, Context context) {
        TypedInputStream in = StreamManager.get(context).open(url);
        ContentType ct = WebContent.determineCT(in.getContentType(), null, url);
        Lang lang = RDFLanguages.contentTypeToLang(ct);
        
        if ( RDFLanguages.isTriples(lang) ) {
            Model model = ModelFactory.createDefaultModel();
            Supplier<SPARQLResult> r = ()->{
                StreamRDF sink = StreamRDFLib.graph(model.getGraph());
                RDFParser.source(in).lang(lang).parse(sink);
                return new SPARQLResult(model);
            };
            if ( model.supportsTransactions() )
                return model.calculateInTxn(r);
            else
                return r.get();
        }

        if ( RDFLanguages.isQuads(lang) ) {
            Dataset ds = DatasetFactory.create();
            Supplier<SPARQLResult> r = ()->{
                StreamRDF sink = StreamRDFLib.dataset(ds.asDatasetGraph());
                RDFParser.source(in).lang(lang).parse(sink);
                return new SPARQLResult(ds);
            };
            
            if ( ds.supportsTransactions() ) 
                return Txn.calculateWrite(ds, r);
            else
                return r.get();
        }
        
        if ( ResultSetReaderRegistry.isRegistered(lang) ) {
            return 
                ResultsReader.create()
                    .forceLang(lang)
                    .context(context)
                    .build()
                    .readAny(in.getInputStream());
            // Which would do, if we need to invert the code ...
//            ResultSetReaderFactory factory = ResultSetReaderRegistry.getFactory(lang);
//            if ( factory == null )
//                throw new RiotException("No ResultSetReaderFactory for "+lang);
//            ResultSetReader reader = factory.create(lang);
//            ResultSet rs = reader.read(in.getInputStream(), context);
        }

        throw new RiotException("Failed to determine: lang = "+lang);
    }
}
