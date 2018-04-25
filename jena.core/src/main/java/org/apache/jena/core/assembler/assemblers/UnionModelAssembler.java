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

package org.apache.jena.core.assembler.assemblers;

import org.apache.jena.core.assembler.Assembler ;
import org.apache.jena.core.assembler.JA ;
import org.apache.jena.core.assembler.Mode ;
import org.apache.jena.core.graph.Factory ;
import org.apache.jena.core.graph.Graph ;
import org.apache.jena.core.graph.compose.MultiUnion ;
import org.apache.jena.core.rdf.model.Model ;
import org.apache.jena.core.rdf.model.ModelFactory ;
import org.apache.jena.core.rdf.model.Resource ;
import org.apache.jena.core.rdf.model.StmtIterator ;

public class UnionModelAssembler extends ModelAssembler implements Assembler
    {
    
    @Override
    protected Model openEmptyModel( Assembler a, Resource root, Mode mode )
        {
        checkType( root, JA.UnionModel );
        MultiUnion union = new MultiUnion();
        union.addGraph( getRootModel( a, root, mode ) );
        addSubModels( a, root, union, mode );
        return ModelFactory.createModelForGraph( union );
        }

    private Graph getRootModel( Assembler a, Resource root, Mode mode )
        {
        Resource r = getUniqueResource( root, JA.rootModel );
        return r == null ? Factory.empty() : a.openModel( r, mode ).getGraph();
        }

    private void addSubModels( Assembler a, Resource root, MultiUnion union, Mode mode )
        {
        for (StmtIterator it = root.listProperties( JA.subModel ); it.hasNext();)
            {
            Resource resource = getResource( it.nextStatement() );
            union.addGraph( a.openModel( resource, mode ).getGraph() );        
            }
        }

    }
