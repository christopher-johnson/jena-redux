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

package org.apache.jena.dboe.tdb2.solver;

import org.apache.jena.base.atlas.lib.Lib ;
import org.apache.jena.arq.query.Query ;
import org.apache.jena.arq.sparql.algebra.Algebra ;
import org.apache.jena.arq.sparql.algebra.Op ;
import org.apache.jena.arq.sparql.core.DatasetDescription ;
import org.apache.jena.arq.sparql.core.DatasetGraph ;
import org.apache.jena.arq.sparql.core.DynamicDatasets ;
import org.apache.jena.arq.sparql.core.Substitute ;
import org.apache.jena.arq.sparql.engine.Plan ;
import org.apache.jena.arq.sparql.engine.QueryEngineFactory ;
import org.apache.jena.arq.sparql.engine.QueryEngineRegistry ;
import org.apache.jena.arq.sparql.engine.QueryIterator ;
import org.apache.jena.arq.sparql.engine.binding.Binding ;
import org.apache.jena.arq.sparql.engine.binding.BindingFactory ;
import org.apache.jena.arq.sparql.engine.iterator.QueryIteratorWrapper ;
import org.apache.jena.arq.sparql.engine.main.QueryEngineMain ;
import org.apache.jena.arq.sparql.mgt.Explain ;
import org.apache.jena.arq.sparql.util.Context ;
import org.apache.jena.dboe.tdb2.TDB2;
import org.apache.jena.dboe.tdb2.TDBException;
import org.apache.jena.dboe.tdb2.migrate.A2;
import org.apache.jena.dboe.tdb2.store.DatasetGraphTDB;
import org.apache.jena.dboe.tdb2.sys.TDBInternal;

// This exists to intercept the query execution setup.
//  e.g choose the transformation optimizations
// then to make the quad form.
// TDB also uses a custom OpExecutor to intercept certain parts 
// of the Op evaluations

public class QueryEngineTDB extends QueryEngineMain
{
    // ---- Wiring
    static public QueryEngineFactory getFactory() { return factory ; } 
    static public void register()       { QueryEngineRegistry.addFactory(factory) ; }
    static public void unregister()     { QueryEngineRegistry.removeFactory(factory) ; }
    
    private Binding initialInput ;

    // ---- Object
    protected QueryEngineTDB(Op op, DatasetGraphTDB dataset, Binding input, Context context)
    {
        super(op, dataset, input, context) ;
        this.initialInput = input ;
    }
    
    private boolean doingDynamicDatasetBySpecialDataset = false ;
    
    protected QueryEngineTDB(Query query, DatasetGraphTDB dataset, Binding input, Context cxt)
    { 
        super(query, dataset, input, cxt) ; 
        DatasetDescription dsDesc = DatasetDescription.create(query, context) ;
        
        if ( dsDesc != null )
        {
            doingDynamicDatasetBySpecialDataset = true ;
            super.dataset = DynamicDatasets.dynamicDataset(dsDesc, dataset, isUnionDefaultGraph(cxt) ) ;
        }
        this.initialInput = input ; 
    }
    
    private static boolean isUnionDefaultGraph(Context cxt) {
        return cxt.isTrue(TDB2.symUnionDefaultGraph1) || cxt.isTrue(TDB2.symUnionDefaultGraph2);
    }
    
    // Choose the algebra-level optimizations to invoke. 
    @Override
    protected Op modifyOp(Op op)
    {
        op = Substitute.substitute(op, initialInput) ;
        // Optimize (high-level)
        op = super.modifyOp(op) ;

        // Quadification
        // Only apply if not a rewritten DynamicDataset
        if ( ! doingDynamicDatasetBySpecialDataset )
            op = Algebra.toQuadForm(op) ;
        
        // Record it.
        setOp(op) ;
        return op ;
    }

    @Override
    public QueryIterator eval(Op op, DatasetGraph dsg, Binding input, Context context)
    {
        // Top of execution of a query.
        // Op is quad'ed by now but there still may be some (graph ....) forms e.g. paths
        
        // Fix DatasetGraph for global union.
        if ( isUnionDefaultGraph(context) && ! doingDynamicDatasetBySpecialDataset ) 
        {
            op = A2.unionDefaultGraphQuads(op) ;
            Explain.explain("REWRITE(Union default graph)", op, context) ;
        }
        QueryIterator results = super.eval(op, dsg, input, context) ;
        results = new QueryIteratorMaterializeBinding(results) ;
        return results ; 
    }
    
    /** Copy from any TDB internal BindingTDB to a Binding that
     *  does not have any connection to the database.   
     */
    static class QueryIteratorMaterializeBinding extends QueryIteratorWrapper
    {
        public QueryIteratorMaterializeBinding(QueryIterator qIter)
        {
            super(qIter) ;
        }
        
        @Override
        protected Binding moveToNextBinding()
        { 
            Binding b = super.moveToNextBinding() ;
            b = BindingFactory.materialize(b) ;
            return b ;
        }
    }
    
    // Execution time (needs wiring to ARQ).
    public long getMillis() { return -1 ; }
    
    // ---- Factory
    protected static QueryEngineFactory factory = new QueryEngineFactoryTDB() ;
        
    protected static class QueryEngineFactoryTDB implements QueryEngineFactory
    {
        private static boolean isHandledByTDB(DatasetGraph dataset) {
            return TDBInternal.isBackedByTDB(dataset);
        }
        
        protected DatasetGraphTDB dsgToQuery(DatasetGraph dataset) {
            try { 
                return TDBInternal.requireStorage(dataset);
            } catch (TDBException ex) {
                // Check to a more specific message. 
                throw new TDBException("Internal inconsistency: trying to execute query on unrecognized kind of DatasetGraph: "+Lib.className(dataset)) ;
            }
        }
        
        @Override
        public boolean accept(Query query, DatasetGraph dataset, Context context) 
        { return isHandledByTDB(dataset) ; }

        @Override
        public Plan create(Query query, DatasetGraph dataset, Binding input, Context context)
        {
            QueryEngineTDB engine = new QueryEngineTDB(query, dsgToQuery(dataset), input, context) ;
            return engine.getPlan() ;
        }
        
        @Override
        public boolean accept(Op op, DatasetGraph dataset, Context context) 
        { return isHandledByTDB(dataset) ; }

        @Override
        public Plan create(Op op, DatasetGraph dataset, Binding binding, Context context)
        {
            QueryEngineTDB engine = new QueryEngineTDB(op, dsgToQuery(dataset), binding, context) ;
            return engine.getPlan() ;
        }
    }
    
//    // By rewrite, not using a general purpose dataset with the right graphs in.
//    private static Op dynamicDatasetOp(Op op,  Context context)
//    {
//        Transform transform = null ;
//    
//        try {
//            @SuppressWarnings("unchecked")
//            Set<Node> defaultGraphs = (Set<Node>)(context.get(SystemTDB.symDatasetDefaultGraphs)) ;
//            @SuppressWarnings("unchecked")
//            Set<Node> namedGraphs = (Set<Node>)(context.get(SystemTDB.symDatasetNamedGraphs)) ;
//            if ( defaultGraphs != null || namedGraphs != null )
//                transform = new TransformDynamicDataset(defaultGraphs, 
//                                                        namedGraphs, 
//                                                        context.isTrue(TDB.symUnionDefaultGraph)) ;
//        } catch (ClassCastException ex)
//        {
//            Log.warn(QueryEngineTDB.class, "Bad dynamic dataset description (ClassCastException)", ex) ;
//            transform = null ;
//            return op ;
//        }
//
//        // Apply dynamic dataset modifications.
//        if ( transform != null )
//            op = Transformer.transform(transform, op) ;
//        return op ;
//    }        
//    
}
