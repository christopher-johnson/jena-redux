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

package org.apache.jena.dboe.tdb2.junit;

import static org.junit.Assert.assertTrue;

import java.util.List ;

import org.apache.jena.arq.system.Txn;
import org.apache.jena.arq.query.* ;
import org.apache.jena.core.rdf.model.Model ;
import org.apache.jena.arq.sparql.SystemARQ ;
import org.apache.jena.arq.sparql.engine.QueryEngineFactory ;
import org.apache.jena.arq.sparql.engine.QueryExecutionBase ;
import org.apache.jena.arq.sparql.engine.ref.QueryEngineRef ;
import org.apache.jena.arq.sparql.junit.EarlReport ;
import org.apache.jena.arq.sparql.junit.EarlTestCase ;
import org.apache.jena.arq.sparql.junit.TestItem ;
import org.apache.jena.arq.sparql.resultset.ResultSetCompare ;
import org.apache.jena.arq.sparql.resultset.SPARQLResult ;
import org.apache.jena.dboe.tdb2.TDB2Factory;
import org.apache.jena.core.util.FileManager ;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import junit.framework.TestResult;

@DisplayName("QueryTestTDB")
public class QueryTestTDB extends EarlTestCase implements junit.framework.Test
{
    // Changed to using in-memory graphs/datasets because this is testing the query
    // processing.  Physical graph/datsets is in package "store". 
    
    private static Logger log = LoggerFactory.getLogger(QueryTestTDB.class) ;
    private Dataset dataset = null ;

    boolean skipThisTest = false ;

    final List<String> defaultGraphURIs ;
    final List<String> namedGraphURIs ;
    final String queryFile ; 
    final SPARQLResult results ;
    
    // Track what's currently loaded in the GraphLocation
    private static List<String> currentDefaultGraphs = null ;
    private static List<String> currentNamedGraphs = null ;


    public QueryTestTDB(String testName, EarlReport report, TestItem item)
    {
        this(testName, report, item.getURI(), 
             item.getDefaultGraphURIs(), item.getNamedGraphURIs(), 
             item.getResults(), item.getQueryFile()
             ) ;
    }
    
    public QueryTestTDB(String testName, EarlReport report, 
                        String uri,
                        List<String> dftGraphs,
                        List<String> namedGraphs,
                        SPARQLResult rs,
                        String queryFile
                        )
    {
        super(testName, uri, report) ;
        this.defaultGraphURIs = dftGraphs ;
        this.namedGraphURIs = namedGraphs ;
        this.queryFile = queryFile ;
        this.results = rs ;
    }
    
    boolean oldValueUsePlainGraph = SystemARQ.UsePlainGraph ;
    
    @Before
    public void setUpTest() {
        dataset = TDB2Factory.createDataset() ;
        Txn.executeWrite(dataset, ()->{
            setupData() ;
        }) ;
        // Make sure a plain, no sameValueAs graph is used.
        oldValueUsePlainGraph = SystemARQ.UsePlainGraph ;
        SystemARQ.UsePlainGraph = true ;
    }
    
    @After
    public void tearDownTest()
    { 
        if ( dataset != null )
        {
            dataset.close() ;
            dataset = null ;
        }
        SystemARQ.UsePlainGraph = oldValueUsePlainGraph ;
    }
    
    public void setupData()
    {
        if ( compareLists(defaultGraphURIs, currentDefaultGraphs) &&
             compareLists(namedGraphURIs, currentNamedGraphs) )
            return ;
        
        if ( defaultGraphURIs == null )
            throw new TDBTestException("No default graphs given") ;

        //graphLocation.clear() ;
        
        for ( String fn : defaultGraphURIs )
            load(dataset.getDefaultModel(), fn) ;
        
        for ( String fn : namedGraphURIs )
            load(dataset.getNamedModel(fn), fn) ;
    }

    @Override
    public void runTestForReal(TestInfo testInfo) throws Throwable
    {
        if ( skipThisTest )
        {
            log.info(" : Skipped") ;
            return ;
        }
        
        Query query = QueryFactory.read(queryFile) ;
        Dataset ds = DatasetFactory.create(defaultGraphURIs, namedGraphURIs) ;
        
        // ---- First, get the expected results by executing in-memory or from a results file.
        
        ResultSetRewindable rs1$ = null ;
        String expectedLabel$ = "" ;
        if ( results != null )
        {
            rs1$ = ResultSetFactory.makeRewindable(results.getResultSet()) ;
            expectedLabel$ = "Results file" ;
        }
        else
        {
            QueryEngineFactory f = QueryEngineRef.getFactory() ;
            try(QueryExecution qExec1 = new QueryExecutionBase(query, ds, null, f)) {
                rs1$ = ResultSetFactory.makeRewindable(qExec1.execSelect()) ;
            }
            expectedLabel$ = "Standard engine" ;
        }
        // Effectively final.
        ResultSetRewindable rs1 = rs1$ ;
        String expectedLabel = expectedLabel$ ;
        // ---- Second, execute in persistent graph

        Dataset ds2 = dataset ; //DatasetFactory.create(model) ;
        Txn.executeRead(ds2, ()->{
            try(QueryExecution qExec2 = QueryExecutionFactory.create(query, ds2)) {
                ResultSet rs = qExec2.execSelect() ;
                ResultSetRewindable rs2 = ResultSetFactory.makeRewindable(rs) ;

                // See if the same.
                boolean b = ResultSetCompare.equalsByValue(rs1, rs2) ;
                if ( !b )
                {
                    rs1.reset() ;
                    rs2.reset() ;
                    System.out.println("------------------- "+testInfo.getDisplayName());
                    System.out.printf("**** Expected (%s)", expectedLabel) ;
                    ResultSetFormatter.out(System.out, rs1) ; 
                    System.out.println("**** Got (TDB)") ;
                    ResultSetFormatter.out(System.out, rs2) ;
                }

                assertTrue("Results sets not the same", b) ;
            }
        }) ;
    }

    private static void load(Model model, String fn)
    {
        FileManager.get().readModel(model, fn) ;
    }
    
    private static boolean compareLists(List<String> list1, List<String> list2)
    {
        if ( list1 == null )
            return ( list2 == null ) ;
        return list1.equals(list2) ;
    }

    @Override
    public int countTestCases() {
        return 0;
    }

    @Override
    public void run(TestResult result) {

    }
}
