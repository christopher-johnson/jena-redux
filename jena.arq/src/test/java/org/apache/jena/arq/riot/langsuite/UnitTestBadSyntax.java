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

import static org.apache.jena.arq.riot.SysRIOT.fmtMessage ;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.jena.arq.atlas.legacy.BaseTest2 ;
import org.apache.jena.arq.query.Dataset ;
import org.apache.jena.arq.query.DatasetFactory ;
import org.apache.jena.core.rdf.model.Model ;
import org.apache.jena.core.rdf.model.ModelFactory ;
import org.apache.jena.arq.riot.Lang ;
import org.apache.jena.arq.riot.RDFDataMgr ;
import org.apache.jena.arq.riot.RDFLanguages ;
import org.apache.jena.arq.riot.RiotException ;
import org.apache.jena.arq.riot.system.ErrorHandler ;
import org.apache.jena.arq.riot.system.ErrorHandlerFactory ;
import org.apache.jena.arq.sparql.junit.EarlReport ;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;

import junit.framework.Test;
import junit.framework.TestResult;

public class UnitTestBadSyntax extends LangTestCase implements Test {
    private final String uri ;
    private final Lang lang ;

    public UnitTestBadSyntax(String name, String testURI, String uri, Lang lang, EarlReport earl)
    {
        this.uri = uri ;
        this.lang = lang ;
    }
    
    /** An error handler that throw exceptions on warnings and errors */ 
    private static ErrorHandler errorHandlerTestStrict = new ErrorHandler()
    {
        /** report a warning  - do not carry on */
        @Override
        public void warning(String message, long line, long col)
        { 
            throw new RiotException(fmtMessage(message, line, col)) ;
        }
        
        /** report an error - do not carry on */
        @Override
        public void error(String message, long line, long col)
        { 
            throw new RiotException(fmtMessage(message, line, col)) ;
        }

        @Override
        public void fatal(String message, long line, long col)
        {
            throw new RiotException(fmtMessage(message, line, col)) ;
        }
    } ;

    @Before
    public void _setUp()         { BaseTest2.setTestLogging(ErrorHandlerFactory.errorHandlerStrictNoLogging) ; }

    @After
    public void _tearDown()      { BaseTest2.unsetTestLogging() ; }

    @DisplayName("UnitTestBadSyntax")
    @org.junit.jupiter.api.Test
    public void runTestForReal(TestInfo testInfo, TestReporter testReporter)
    {
        if ( RDFLanguages.isTriples(lang) )
            run3() ;
        else
            run4() ;
    }
    
    private void run3() {
        Model model = ModelFactory.createDefaultModel() ;
        try {
            RDFDataMgr.read(model, uri, uri, lang) ;
        } catch (RiotException ex) { return ; }
        catch (RuntimeException ex) {
            ex.printStackTrace(System.err) ;
            fail("Unexpected exception") ;
        }
        fail("Bad syntax test succeed in parsing the file") ;
    }
    
    private void run4() {
        Dataset ds = DatasetFactory.createGeneral() ;
        try {
            RDFDataMgr.read(ds, uri, uri, lang) ;
        } catch (RiotException ex) { return ; }
        catch (RuntimeException ex) {
            ex.printStackTrace(System.err) ;
            fail("Unexpected exception") ;
        }
        fail("Bad syntax test succeed in parsing the file") ;
    }

    @Override
    public int countTestCases() {
        return 0;
    }

    @Override
    public void run(TestResult result) {

    }
}
