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

import org.apache.jena.arq.query.Dataset ;
import org.apache.jena.arq.query.DatasetFactory ;
import org.apache.jena.arq.sparql.junit.TestItemParameterResolver;
import org.apache.jena.core.rdf.model.Model ;
import org.apache.jena.core.rdf.model.ModelFactory ;
import org.apache.jena.arq.riot.Lang ;
import org.apache.jena.arq.riot.RDFDataMgr ;
import org.apache.jena.arq.riot.RDFLanguages ;
import org.apache.jena.arq.sparql.junit.EarlReport ;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;

import junit.framework.Test;
import junit.framework.TestResult;

@DisplayName("UnitTestSyntax")
@ExtendWith(TestItemParameterResolver.class)
public class UnitTestSyntax extends LangTestCase implements Test {
    private final String uri ;
    private final Lang lang ;

    public UnitTestSyntax(String name, String testURI, String uri, Lang lang, EarlReport earl)
    {
        this.uri = uri ;
        this.lang = lang ;
    }

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
        RDFDataMgr.read(model, uri, uri, lang) ;
    }
    
    private void run4() {
        Dataset ds = DatasetFactory.createGeneral() ;
        RDFDataMgr.read(ds, uri, uri, lang) ;
    }
    
    @Override
    public void _setUp()
    {}

    @Override
    public void _tearDown()
    {}

    @Override
    public int countTestCases() {
        return 0;
    }

    @Override
    public void run(TestResult result) {

    }
}
