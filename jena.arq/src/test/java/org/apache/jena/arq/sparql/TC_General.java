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


import org.apache.jena.arq.query.TS_ParamString ;
import org.apache.jena.arq.sparql.algebra.TS_Algebra ;
import org.apache.jena.arq.sparql.algebra.optimize.TS_Optimization ;
import org.apache.jena.arq.sparql.api.TS_API ;
import org.apache.jena.arq.sparql.core.TS_Core ;
import org.apache.jena.arq.sparql.core.assembler.TS_Assembler ;
import org.apache.jena.arq.sparql.core.mem.TS_DatasetTxnMem ;
import org.apache.jena.arq.sparql.engine.TS_Engine ;
import org.apache.jena.arq.sparql.engine.join.TS_Join ;
import org.apache.jena.arq.sparql.expr.E_Function ;
import org.apache.jena.arq.sparql.expr.NodeValue ;
import org.apache.jena.arq.sparql.expr.TS_Expr ;
import org.apache.jena.arq.sparql.function.js.TS_FunctionJS;
import org.apache.jena.arq.sparql.function.library.TS_LibraryFunctions ;
import org.apache.jena.arq.sparql.function.user.TS_UserFunctions ;
import org.apache.jena.arq.sparql.graph.TS_Graph ;
import org.apache.jena.arq.sparql.lang.TS_Lang ;
import org.apache.jena.arq.sparql.modify.TS_Update ;
import org.apache.jena.arq.sparql.negation.TS_Negation ;
import org.apache.jena.arq.sparql.path.TS_Path ;
import org.apache.jena.arq.sparql.pfunction.library.TS_PFunction ;
import org.apache.jena.arq.sparql.resultset.TS_ResultSet ;
import org.apache.jena.arq.sparql.solver.TS_Solver ;
import org.apache.jena.arq.sparql.syntax.TS_SSE ;
import org.apache.jena.arq.sparql.syntax.TS_Syntax ;
import org.apache.jena.arq.sparql.transaction.TS_Transaction ;
import org.apache.jena.arq.sparql.util.TS_Util ;
import org.junit.AfterClass ;
import org.junit.BeforeClass ;
import org.junit.runner.RunWith ;
import org.junit.runners.Suite ;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
    TS_SSE.class
    , TS_Lang.class
    , TS_Graph.class
    , TS_Util.class
    
    , TS_Expr.class
    , TS_LibraryFunctions.class
    , TS_UserFunctions.class
    , TS_FunctionJS.class
    , TS_PFunction.class
    
    , TS_ResultSet.class
    , TS_Engine.class
    , TS_Negation.class
    , TS_Solver.class
    , TS_Algebra.class
    , TS_Join.class
    , TS_Optimization.class
    , TS_ResultSet.class
    , TS_Syntax.class
    , TS_API.class
    , TS_Core.class
    , TS_Assembler.class
    , TS_DatasetTxnMem.class
    , TS_Path.class
    , TS_ParamString.class
    , TS_Update.class
    , TS_Transaction.class
})

public class TC_General
{
    @BeforeClass
    public static void beforeClass() {
        NodeValue.VerboseWarnings = false;
        E_Function.WarnOnUnknownFunction = false;
    }

    @AfterClass
    public static void afterClass() { }
}
