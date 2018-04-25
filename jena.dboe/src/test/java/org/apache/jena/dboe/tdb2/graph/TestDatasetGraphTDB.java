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

package org.apache.jena.dboe.tdb2.graph;

import org.apache.jena.arq.query.ReadWrite ;
import org.apache.jena.arq.sparql.core.AbstractDatasetGraphTests ;
import org.apache.jena.arq.sparql.core.DatasetGraph ;
import org.apache.jena.dboe.tdb2.junit.TL;
import org.junit.After ;
import org.junit.Before ;
import org.junit.Test ;

// Quad tests
public class TestDatasetGraphTDB extends AbstractDatasetGraphTests
{
    DatasetGraph dsg = TL.createTestDatasetGraphMem() ;
    @Before public void before() {
        dsg.begin(ReadWrite.WRITE);
    }
    
    @After public void after() {
        dsg.abort();
        dsg.end();
        TL.expel(dsg);
    }
    
    @Override
    protected DatasetGraph emptyDataset() {
        return dsg ;
    }

    @Override
    // Empty graph tests: N/A
    @Test public void graph_01()
    {}

}
