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

import static org.junit.jupiter.api.Assertions.fail;

import org.apache.jena.arq.query.QueryException ;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import junit.framework.TestResult;

public class SyntaxUpdateTest extends EarlTestCase implements junit.framework.Test {
    static int count = 0 ;
    String updateString ;
    boolean expectLegalSyntax ;
    TestItem testItem ;
    
    public SyntaxUpdateTest(String testName, EarlReport earl, TestItem t)
    {
        this(testName, earl, t, true) ;
    }

    public SyntaxUpdateTest(String testName, EarlReport earl, TestItem t, boolean positiveTest)
    {
        super(testName, t.getURI(), earl) ;
        testItem = t ;
        expectLegalSyntax = positiveTest ; 
    }

    public SyntaxUpdateTest(String testName, EarlReport earl, String queryString,  boolean positiveTest)
    {
        super(testName, TestItem.fakeURI(), earl) ;
        setTest(testName, queryString, positiveTest) ;
    }

    private void setTest(String testName, String _queryString, boolean positiveTest)
    {
        //super.setName(testName) ;
        this.updateString = _queryString ;
        expectLegalSyntax = positiveTest ; 
    }
    
    
    @Test
    public void runTestForReal(TestInfo testInfo)
    {
        try {
            if ( updateString == null )
                updateFromTestItem(testItem) ;
            else
                updateFromString(updateString) ;
            
            if ( ! expectLegalSyntax )
                fail("Expected parse failure") ;
        }
        catch (QueryException qEx)
        {
            if ( expectLegalSyntax )
                throw qEx ;
        }

        catch (Exception ex)
        {
            fail( "Exception: "+ex.getClass().getName()+": "+ex.getMessage()) ;
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
