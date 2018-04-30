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

package org.apache.jena.arq.sparql.junit;


import org.apache.jena.base.atlas.io.IndentedLineBuffer ;
import org.apache.jena.arq.query.Query ;
import org.apache.jena.arq.query.Syntax ;
import org.apache.jena.arq.sparql.sse.SSEParseException ;
import org.apache.jena.arq.sparql.util.QueryUtils ;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;

import junit.framework.TestResult;

@DisplayName("TestSerialization")
@ExtendWith(TestItemParameterResolver.class)
public class TestSerialization extends EarlTestCase implements junit.framework.Test{
    static int count = 0 ;
    String queryString ;
    TestItem testItem ;
    
    public TestSerialization(TestItem item)
    {
        this.testItem = item;
    }

//    public SerializerTest(String queryString)
//    {
//        this(queryString, queryString) ;
//    }
//    
//    public SerializerTest(String testName, String queryString)
//    {
//        super(testName) ;
//        setTest(testName, queryString) ;
//    }

    private void setTest(String testName, EarlReport earl, String _queryString)
    {
        this.queryString = _queryString ;
    }
    
    // A serialization test is:
    //   Read query in.
    //   Serialize to string.
    //   Parse again.
    //   Are they equal?


    @org.junit.jupiter.api.Test
    public void runTestForReal(TestInfo testInfo, TestReporter testReporter)
    {
        Query query = null ;
        if ( queryString == null )
            query = queryFromTestItem(testItem) ;
        else
            query = queryFromString(queryString) ;
        
        // Whatever was read in.
        runTestWorker(query, query.getSyntax(), testInfo) ;
    }
    
    protected void runTestWorker(Query query, Syntax syntax, TestInfo testInfo)
    {
        IndentedLineBuffer buff = new IndentedLineBuffer() ;
        query.serialize(buff, syntax) ;
        String baseURI = null ;
        
        if ( ! query.explicitlySetBaseURI() )
            // Not in query - use the same one (e.g. file read from) .  
            baseURI = query.getBaseURI() ;
        
        // Query syntax and algebra tests. 
        
        try {
            QueryUtils.checkParse(query) ;
        } 
        catch (RuntimeException ex)
        {
            System.err.println("**** Test: " + testInfo.getDisplayName()) ;
            System.err.println("** "+ex.getMessage()) ;
            System.err.println(query) ;
            throw ex ; 
        }

        try {
            QueryUtils.checkOp(query, true) ;
        } catch (SSEParseException ex)
        {
            System.err.println("**** Test: ") ;
            System.err.println("** Algebra error: "+ex.getMessage()) ;
        }
    }

    @Override
    public int countTestCases() {
        return 0;
    }

    @Override
    public void run(TestResult result) {

    }
}
