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

import junit.framework.AssertionFailedError ;
import junit.framework.TestCase ;
import org.apache.jena.arq.query.Query ;
import org.apache.jena.arq.query.QueryFactory ;
import org.apache.jena.arq.query.Syntax ;
import org.apache.jena.arq.sparql.ARQException ;
import org.apache.jena.arq.update.UpdateFactory ;
import org.apache.jena.arq.update.UpdateRequest ;


public abstract class EarlTestCase extends TestCase
{
    public EarlReport report = null ;
    public String testURI = null ;
    private boolean resultRecorded = false ;

    public EarlTestCase(String name, String testURI, EarlReport earl)
    {
        super(name) ;
        this.report = earl ;
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
            fail("Query test file is null") ;
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
            fail("Query test file is null") ;
            return null ;
        }

        UpdateRequest request = UpdateFactory.read(testItem.getQueryFile(), Syntax.syntaxSPARQL_11) ;
        return request ;
    }

    @Override
    final public void runTest() throws Throwable
    {
        try {
            runTestForReal() ;
            if ( ! resultRecorded )
                success() ;
        } catch (AssertionFailedError ex)
        {
            if ( ! resultRecorded )
                failure() ;
            throw ex ;
        }
    }

    public abstract void runTestForReal() throws Throwable ;

    // Increase visibility.
    @Override
    public void setUp() {
        setUpTest() ;
    }

    @Override
    public void tearDown() {
        tearDownTest() ;
    }

    // Decouple from JUnit3.
    public void setUpTest() {}
    public void tearDownTest() {}

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
            throw new ARQException("Duplictaed test results: "+getName()) ;
        resultRecorded = true ;
    }

}
