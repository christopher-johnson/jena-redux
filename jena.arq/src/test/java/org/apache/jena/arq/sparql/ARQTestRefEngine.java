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

package org.apache.jena.arq.sparql;

import junit.framework.TestCase ;
import junit.framework.TestSuite ;
import org.apache.jena.arq.sparql.engine.ref.QueryEngineRef ;
import org.apache.jena.arq.sparql.expr.E_Function ;
import org.apache.jena.arq.sparql.expr.NodeValue ;
import org.apache.jena.arq.sparql.junit.ScriptTestSuiteFactory ;


public class ARQTestRefEngine extends TestCase
{
    public static TestSuite suite()
    {
        NodeValue.VerboseWarnings = false ;
        E_Function.WarnOnUnknownFunction = false ;
        return suiteRef() ;
    }
    
    private static TestSuite suiteRef()
    {
        QueryEngineRef.register() ;
        TestSuite ts = suiteMaker() ;
        //QueryEngineRef.unregister() ;
        return ts ;
    }
    
    private static TestSuite suiteMaker()
    {
        return ScriptTestSuiteFactory.make("testing/ARQ/manifest-ref-arq.ttl") ;
    }
    
}