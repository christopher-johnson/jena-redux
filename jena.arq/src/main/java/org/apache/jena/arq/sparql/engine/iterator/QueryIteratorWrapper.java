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

package org.apache.jena.arq.sparql.engine.iterator;

import org.apache.jena.base.atlas.io.IndentedWriter ;
import org.apache.jena.base.atlas.lib.Lib ;
import org.apache.jena.arq.sparql.engine.QueryIterator ;
import org.apache.jena.arq.sparql.engine.binding.Binding ;
import org.apache.jena.arq.sparql.serializer.SerializationContext ;

/** Wrap a QueryIterator so it can have some/all of it's methods intercepted. */

public class QueryIteratorWrapper extends QueryIteratorBase
{
    protected QueryIterator iterator ;
    
    public QueryIteratorWrapper(QueryIterator qIter)
    { 
        iterator = qIter ;
    }

    @Override
    protected boolean hasNextBinding() { return iterator.hasNext() ; } 
    
    @Override
    protected Binding moveToNextBinding() { return iterator.nextBinding() ; }

    @Override
    protected void closeIterator()
    {
        if ( iterator != null )
        {
            iterator.close() ;
            iterator = null ;
        }
    }
    
    @Override
    protected void requestCancel()
    {
        if ( iterator != null )
        {
           iterator.cancel();
        }
    }
    
    @Override
    public void output(IndentedWriter out, SerializationContext sCxt)
    {   
        out.println(Lib.className(this)+"/"+Lib.className(iterator)) ;
        out.incIndent() ;
        iterator.output(out, sCxt) ;
        out.decIndent() ;
        //out.println(Utils.className(this)+"/"+Utils.className(iterator)) ;
    }

}