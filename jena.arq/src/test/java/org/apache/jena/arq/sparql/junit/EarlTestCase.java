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

import junit.framework.AssertionFailedError ;

import org.apache.jena.arq.query.Query ;
import org.apache.jena.arq.query.QueryFactory ;
import org.apache.jena.arq.query.Syntax ;
import org.apache.jena.arq.sparql.ARQException ;
import org.apache.jena.arq.update.UpdateFactory ;
import org.apache.jena.arq.update.UpdateRequest ;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;


public abstract class EarlTestCase
{
    public EarlReport report;
    public String testURI;
    private boolean resultRecorded = false ;

    public EarlTestCase()
    {
        this.report = null ;
        this.testURI = testURI ;
    }

    public void setEARL(EarlReport earl)
    {
        this.report = earl ;
    }

    public Query queryFromString(String qStr)
    {
        Query query = QueryFactory.create(qStr) ;
        return query ;
    }

    public Query queryFromTestItem(TestItem testItem)
    {
        if ( testItem.getQueryFile() == null )
        {
            //fail("Query test file is null") ;
            return null ;
        }

        Query query = QueryFactory.read(testItem.getQueryFile(), null, testItem.getFileSyntax()) ;
        return query ;
    }

    public UpdateRequest updateFromString(String str)
    {
        return UpdateFactory.create(str) ;
    }

    public UpdateRequest updateFromTestItem(TestItem testItem)
    {
        if ( testItem.getQueryFile() == null )
        {
            //fail("Query test file is null") ;
            return null ;
        }

        UpdateRequest request = UpdateFactory.read(testItem.getQueryFile(), Syntax.syntaxSPARQL_11) ;
        return request ;
    }

    @Before
    final public void runTest(TestInfo testInfo, TestReporter testReporter) throws Throwable
    {
        try {
            runTestForReal(testInfo, testReporter) ;
            if ( ! resultRecorded )
                success() ;
        } catch (AssertionFailedError ex)
        {
            if ( ! resultRecorded )
                failure() ;
            throw ex ;
        }
    }

    public abstract void runTestForReal(TestInfo testInfo, TestReporter testReporter) throws Throwable ;

    // Increase visibility.
    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    public void success()
    {
        note() ;
        if ( report == null ) return ;
        report.success(testURI) ;
    }

    public void failure()
    {
        note() ;
        if ( report == null ) return ;
        report.failure(testURI) ;
    }

    public void notApplicable()
    {
        note() ;
        if ( report == null ) return ;
        report.notApplicable(testURI) ;
    }

    public void notTested()
    {
        resultRecorded = true ;
        if ( report == null ) return ;
        report.notTested(testURI) ;
    }
    
    private void note()
    {
        if ( resultRecorded )
            throw new ARQException("Duplicated test results: ") ;
        resultRecorded = true ;
    }

}
