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

package org.apache.jena.dboe.tdb2.assembler;

import static org.apache.jena.arq.sparql.util.graph.GraphUtils.exactlyOneProperty ;
import static org.apache.jena.arq.sparql.util.graph.GraphUtils.getStringValue ;
import static org.apache.jena.dboe.tdb2.assembler.VocabTDB2.pLocation;
import static org.apache.jena.dboe.tdb2.assembler.VocabTDB2.pUnionDefaultGraph;

import org.apache.jena.core.assembler.Assembler ;
import org.apache.jena.core.assembler.Mode ;
import org.apache.jena.core.assembler.exceptions.AssemblerException ;
import org.apache.jena.base.atlas.logging.Log ;
import org.apache.jena.dboe.base.file.Location;
import org.apache.jena.core.graph.Node ;
import org.apache.jena.arq.query.Dataset ;
import org.apache.jena.arq.query.DatasetFactory ;
import org.apache.jena.core.rdf.model.Resource ;
import org.apache.jena.arq.sparql.core.DatasetGraph ;
import org.apache.jena.arq.sparql.core.assembler.AssemblerUtils ;
import org.apache.jena.arq.sparql.core.assembler.DatasetAssembler ;
import org.apache.jena.arq.sparql.expr.NodeValue ;
import org.apache.jena.core.sys.JenaSystem ;
import org.apache.jena.dboe.tdb2.DatabaseMgr;
import org.apache.jena.dboe.tdb2.TDB2;

public class DatasetAssemblerTDB extends DatasetAssembler
{
    static { JenaSystem.init(); }
    
    @Override
    public Dataset createDataset(Assembler a, Resource root, Mode mode) {
        TDB2.init() ;
        return make(root) ;
    }

    static Dataset make(Resource root) {
        if ( !exactlyOneProperty(root, pLocation) )
            throw new AssemblerException(root, "No location given") ;

        String dir = getStringValue(root, pLocation) ;
        Location loc = Location.create(dir) ;
        DatasetGraph dsg = DatabaseMgr.connectDatasetGraph(loc) ;

        if ( root.hasProperty(pUnionDefaultGraph) ) {
            Node b = root.getProperty(pUnionDefaultGraph).getObject().asNode() ;
            NodeValue nv = NodeValue.makeNode(b) ;
            if ( nv.isBoolean() )
                dsg.getContext().set(TDB2.symUnionDefaultGraph, nv.getBoolean()) ;
            else
                Log.warn(DatasetAssemblerTDB.class, "Failed to recognize value for union graph setting (ignored): " + b) ;
        }

        /*
        <r> rdf:type tdb:DatasetTDB2 ;
            tdb:location "dir" ;
            //ja:context [ ja:cxtName "arq:queryTimeout" ;  ja:cxtValue "10000" ] ;
            tdb:unionGraph true ; # or "true"
        */
        AssemblerUtils.setContext(root, dsg.getContext());
        return DatasetFactory.wrap(dsg) ; 
    }
    
}
