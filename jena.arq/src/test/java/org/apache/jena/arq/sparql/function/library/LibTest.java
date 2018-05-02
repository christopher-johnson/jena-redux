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

package org.apache.jena.arq.sparql.function.library;

import static org.junit.Assert.assertEquals ;
import static org.junit.Assert.assertTrue ;

import org.apache.jena.core.graph.Node ;
import org.apache.jena.core.shared.PrefixMapping ;
import org.apache.jena.arq.sparql.ARQConstants ;
import org.apache.jena.arq.sparql.expr.Expr ;
import org.apache.jena.arq.sparql.expr.NodeValue ;
import org.apache.jena.arq.sparql.function.FunctionEnvBase ;
import org.apache.jena.arq.sparql.util.ExprUtils ;
import org.apache.jena.arq.sparql.util.NodeFactoryExtra ;

public class LibTest {
    private static PrefixMapping pmap = ARQConstants.getGlobalPrefixMap() ;

    static void test(String string) {
        test(string, "true");
    }

    static void test(String string, String result) {
        Expr expr = ExprUtils.parse(string, pmap) ;
        NodeValue nv = expr.eval(null, new FunctionEnvBase()) ;
        Node r = NodeFactoryExtra.parseNode(result) ;
        NodeValue nvr = NodeValue.makeNode(r) ;
        assertTrue("Not same value: Expected: " + nvr + " : Actual = " + nv, NodeValue.sameAs(nvr, nv)) ;
        // test result must be lexical form exact.
        assertEquals(r, nv.asNode()) ;
    }
}