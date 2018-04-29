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

package org.apache.jena.arq.riot.langsuite;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.jena.core.rdf.model.Model ;
import org.apache.jena.core.rdf.model.ModelFactory ;
import org.apache.jena.arq.riot.Lang ;
import org.apache.jena.arq.riot.RDFDataMgr ;
import org.apache.jena.arq.riot.RDFLanguages ;
import org.apache.jena.arq.riot.RiotException ;
import org.apache.jena.arq.sparql.core.DatasetGraph ;
import org.apache.jena.arq.sparql.core.DatasetGraphFactory ;
import org.apache.jena.arq.sparql.junit.EarlReport ;
import org.apache.jena.arq.sparql.util.IsoMatcher ;
import org.junit.jupiter.api.TestInfo;

import junit.framework.Test;
import junit.framework.TestResult;

public class UnitTestEval extends LangTestCase implements Test {
    String input ;
    String output ;
    String baseIRI ;
    Lang lang ;
    
    public UnitTestEval(String name, String testURI, String input, String output, String baseIRI, Lang lang, EarlReport earl)
    {
        super(name, testURI, earl) ;
        this.input = input ;
        this.output = output ;
        this.baseIRI = baseIRI ;
        this.lang = lang ;
    }
    
    @Override
    public void _setUp()
    {}

    @Override
    public void _tearDown()
    {}

    @Override
    public void runTestForReal(TestInfo testInfo)
    {
        // Could generalise run4() to cover both cases.
        // run3() predates dataset reading and is more tested. 
        if ( RDFLanguages.isTriples(lang) )
            run3() ;
        else
            run4() ;
    }
    
    private void run4() { 
        DatasetGraph dsg = DatasetGraphFactory.create() ;
        try {
            if ( baseIRI != null )
                RDFDataMgr.read(dsg, input, baseIRI, lang) ;
            else
                RDFDataMgr.read(dsg, input, lang) ;
            
            Lang outLang = RDFLanguages.filenameToLang(output, Lang.NQUADS) ;
            
            DatasetGraph results = DatasetGraphFactory.create() ;
            try {
                RDFDataMgr.read(results, output, outLang) ;
            } catch (RiotException ex) {
                fail("Failed to read results: "+ex.getMessage()) ;
            }

            boolean b = isomorphic(dsg, results) ;

            if ( !b )
            {
                System.out.println("**** Test: ") ;
                System.out.println("---- Parsed");
                RDFDataMgr.write(System.out, dsg, Lang.TRIG) ;
                System.out.println("---- Expected");
                RDFDataMgr.write(System.out, results, Lang.TRIG) ;
                System.out.println("--------");
            }
            
            assertTrue("Datasets not isomorphic", b) ;
        } catch (RiotException ex)
        {
            // Catch and rethrow - debugging.
            throw ex ;    
        }
        catch (RuntimeException ex) 
        { 
            ex.printStackTrace(System.err) ;
            throw ex ; }
    }

    private boolean isomorphic(DatasetGraph dsg1, DatasetGraph dsg2) {
        return IsoMatcher.isomorphic(dsg1, dsg2) ;
    }

    // Triples test.
    private void run3() {     
        Model model = ModelFactory.createDefaultModel() ;
        try {
            if ( baseIRI != null )
                RDFDataMgr.read(model, input, baseIRI, lang) ;
            else
                RDFDataMgr.read(model, input, lang) ;
            
            Lang outLang = RDFLanguages.filenameToLang(output, Lang.NQUADS) ;
            
            Model results = ModelFactory.createDefaultModel() ;
            try {
                RDFDataMgr.read(results, output, outLang) ;
            } catch (RiotException ex) {
                fail("Failed to read results: "+ex.getMessage()) ;
            }

            boolean b = model.isIsomorphicWith(results) ;

            if ( !b )
            {
                //model.isIsomorphicWith(results) ;
                System.out.println("---- Parsed");
                model.write(System.out, "TTL") ;
                System.out.println("---- Expected");
                results.write(System.out, "TTL") ;
                System.out.println("--------");
            }
            
            assertTrue("Models not isomorphic", b) ;
        } catch (RiotException ex)
        {
            // Catch and rethrow - debugging.
            throw ex ;    
        }
        catch (RuntimeException ex) 
        { 
            ex.printStackTrace(System.err) ;
            throw ex ; }
    }

    @Override
    public int countTestCases() {
        return 0;
    }

    @Override
    public void run(TestResult result) {

    }
}
