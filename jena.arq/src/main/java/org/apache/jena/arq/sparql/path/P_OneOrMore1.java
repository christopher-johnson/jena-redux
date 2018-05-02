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

package org.apache.jena.arq.sparql.path ;

import org.apache.jena.arq.sparql.util.NodeIsomorphismMap ;

/** One or more - unique results */
public class P_OneOrMore1 extends P_Path1 {
    public P_OneOrMore1(Path path) {
        super(path) ;
    }

    @Override
    public boolean equalTo(Path path2, NodeIsomorphismMap isoMap) {
        if ( !(path2 instanceof P_OneOrMore1) )
            return false ;
        P_OneOrMore1 other = (P_OneOrMore1)path2 ;
        return getSubPath().equalTo(other.getSubPath(), isoMap) ;
    }

    @Override
    public int hashCode() {
        return hashOneOrMore1 ^ getSubPath().hashCode() ;
    }

    @Override
    public void visit(PathVisitor visitor) {
        visitor.visit(this) ;
    }
}