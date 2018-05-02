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

package org.apache.jena.arq.query;

import java.util.List;
import java.util.Objects ;

import org.apache.jena.core.assembler.Assembler;
import org.apache.jena.core.rdf.model.Model;
import org.apache.jena.core.rdf.model.Resource;
import org.apache.jena.arq.sparql.ARQException;
import org.apache.jena.arq.sparql.core.DatasetGraph;
import org.apache.jena.arq.sparql.core.DatasetGraphFactory;
import org.apache.jena.arq.sparql.core.DatasetImpl;
import org.apache.jena.arq.sparql.core.DatasetOne;
import org.apache.jena.arq.sparql.core.assembler.DatasetAssembler;
import org.apache.jena.arq.sparql.util.DatasetUtils;
import org.apache.jena.arq.sparql.util.graph.GraphUtils;
import org.apache.jena.core.util.FileManager;

/**
 * Makes {@link Dataset}s in various ways.
 *
 */
public class DatasetFactory {

    /** Create an in-memory {@link Dataset}.
     * <p>
     * See also {@link #createTxnMem()} for a transactional dataset.
     * <p>
     * This implementation copies models when {@link Dataset#addNamedModel(String, Model)} is called.
     * <p>
     * This implementation does not support serialized transactions (it only provides MRSW locking). 
     * 
     * @see #createTxnMem
     */
    public static Dataset create() {
        return wrap(DatasetGraphFactory.create()) ;
    }
    
	/**
     * Create an in-memory. transactional {@link Dataset}.
     * <p> 
     * This fully supports transactions, including abort to roll-back changes.
     * It provides "autocommit" if operations are performed
     * outside a transaction but with a performance impact
     * (the implementation adds a begin/commit around each add or delete
     * so overheads can accumulate).
     * 
     * @return a transactional, in-memory, modifiable Dataset
     * 
     */
	public static Dataset createTxnMem() {
		return wrap(DatasetGraphFactory.createTxnMem());
	}

	/**
	 * Create a general-purpose  {@link Dataset}.<br/>
	 * Any graphs needed are in-memory unless explciitly added with {@link Dataset#addNamedModel}.
	 * </p>
	 * This dataset type can contain graphs from any source when added via {@link Dataset#addNamedModel}.
	 * These are held as links to the supplied graph and not copied.
	 * <p> 
	 * <em>This dataset does not support the graph indexing feature of jena-text.</em>
     * <p>
	 * This dataset does not support serialized transactions (it only provides MRSW locking). 
	 * <p>
	 * 
	 * @see #createTxnMem
	 * @return a general-purpose Dataset
	 */
	public static Dataset createGeneral() {
		return wrap(DatasetGraphFactory.createGeneral()); 
	}

    /** Create an in-memory {@link Dataset}.
     * <p>
     * See also {@link #createTxnMem()} for a transactional dataset.
     * <p>
     * Use {@link #createGeneral()} when needing to add graphs with mixed characteristics, 
     * e.g. inference graphs, or specific graphs from TDB.
     * <p>    
     * <em>It does not support the graph indexing feature of jena-text.</em>
     * <p>
     * <em>This factory operation is marked "deprecated" because the general purpose "add named graph of any implementation"
     * feature will be removed; this feature is now provided by {@link #createGeneral()}.
     * </em>
     * @deprecated Prefer {@link #createTxnMem()} or {@link #create()} or, for special cases, {@link #createGeneral()}.
     * @see #createTxnMem
     */
    @Deprecated
    public static Dataset createMem() {
        return createGeneral() ;
    }

    /**
     * Create a dataset, starting with the model argument as the default graph of the
     * dataset. Named graphs can be added. 
     * <p> 
     * Use {@link #wrap(Model)} to put dataset functionality around a single
     * model when named graphs will not be added.
     * 
     * @param model The model for the default graph
     * @return a dataset with the given model as the default graph
     */
	public static Dataset create(Model model) {
	    Objects.requireNonNull(model, "Default model must be provided") ;
		return new DatasetImpl(model);
	}

	/**
	 * @param dataset Dataset to clone structure from.
	 * @return a dataset: clone the dataset structure of named graphs, and share the graphs themselves.
	 * @deprecated This operation may be removed.
	 */
	@Deprecated
	public static Dataset create(Dataset dataset) {
	    Objects.requireNonNull(dataset, "Clone dataset is null") ;
		return new DatasetImpl(dataset);
	}

    /**
	 * Wrap a {@link DatasetGraph} to make a dataset
	 *
	 * @param dataset DatasetGraph
	 * @return Dataset
	 */
	public static Dataset wrap(DatasetGraph dataset) {
	    Objects.requireNonNull(dataset, "Can't wrap a null DatasetGraph reference") ;
		return DatasetImpl.wrap(dataset);
	}

    /**
     * Wrap a {@link Model} to make a dataset; the model is the default graph of the RDF Dataset. 
     * 
     * This dataset can not have additional models
     * added to it, including indirectly through SPARQL Update
     * adding new graphs.
     *
     * @param model
     * @return Dataset
     */
    public static Dataset wrap(Model model) {
        Objects.requireNonNull(model, "Can't wrap a null Model reference") ;
        return DatasetOne.create(model);
    }

    /**
	 * Wrap a {@link DatasetGraph} to make a dataset
	 *
	 * @param dataset DatasetGraph
	 * @return Dataset
	 * @deprecated Use {@link #wrap} 
	 */
	@Deprecated
	public static Dataset create(DatasetGraph dataset) {
	    return wrap(dataset);
	}

    /**
	 * @param uriList URIs merged to form the default dataset
	 * @return a dataset based on a list of URIs : these are merged into the default graph of the dataset.
	 */
	public static Dataset create(List<String> uriList) {
		return create(uriList, null, null);
	}

	/**
	 * @param uri URIs merged to form the default dataset
	 * @return a dataset with a default graph and no named graphs
	 */

	public static Dataset create(String uri) {
		return create(uri, null, null);
	}

	/**
	 * @param namedSourceList
	 * @return a named graph container of graphs based on a list of URIs.
	 */

	public static Dataset createNamed(List<String> namedSourceList) {
		return create((List<String>) null, namedSourceList, null);
	}

	/**
	 * Create a dataset based on two list of URIs. The first lists is used to create the background (unnamed graph) by
	 * merging, the second is used to create the collection of named graphs.
	 *
	 * (Jena calls graphs "Models" and triples "Statements")
	 *
	 * @param uriList graphs to be loaded into the unnamed, default graph
	 * @param namedSourceList graphs to be attached as named graphs
	 * @return Dataset
	 */

	public static Dataset create(List<String> uriList, List<String> namedSourceList) {
		return create(uriList, namedSourceList, null);
	}

	/**
	 * Create a dataset container based on two list of URIs. The first is used to create the background (unnamed graph),
	 * the second is used to create the collection of named graphs.
	 *
	 * (Jena calls graphs "Models" and triples "Statements")
	 *
	 * @param uri graph to be loaded into the unnamed, default graph
	 * @param namedSourceList graphs to be attached as named graphs
	 * @return Dataset
	 */

	public static Dataset create(String uri, List<String> namedSourceList) {
		return create(uri, namedSourceList, null);
	}

	/**
	 * Create a named graph container based on two list of URIs. The first is used to create the background (unnamed
	 * graph), the second is used to create the collection of named graphs.
	 *
	 * (Jena calls graphs "Models" and triples "Statements")
	 *
	 * @param uri graph to be loaded into the unnamed, default graph
	 * @param namedSourceList graphs to be attached as named graphs
	 * @param baseURI baseURI for relative URI expansion
	 * @return Dataset
	 */

	public static Dataset create(String uri, List<String> namedSourceList, String baseURI) {
		return DatasetUtils.createDataset(uri, namedSourceList, baseURI);
	}

	/**
	 * Create a named graph container based on two list of URIs. The first is used to create the background (unnamed
	 * graph), the second is used to create the collection of named graphs.
	 *
	 * (Jena calls graphs "Models" and triples "Statements")
	 *
	 * @param uriList graphs to be loaded into the unnamed, default graph
	 * @param namedSourceList graphs to be attached as named graphs
	 * @param baseURI baseURI for relative URI expansion
	 * @return Dataset
	 */

	public static Dataset create(List<String> uriList, List<String> namedSourceList, String baseURI) {
		return DatasetUtils.createDataset(uriList, namedSourceList, baseURI);
	}
//
//	public static Dataset make(Dataset ds, Model defaultModel) {
//		Dataset ds2 = new DatasetImpl(ds);
//		ds2.setDefaultModel(defaultModel);
//		return ds2;
//	}

	// Assembler-based Dataset creation.

	/**
	 * Assemble a dataset from the model in a file
	 *
	 * @param filename The filename
	 * @return Dataset
	 */
	public static Dataset assemble(String filename) {
	    Objects.requireNonNull(filename, "file name can not be null") ;
		Model model = FileManager.get().loadModel(filename);
		return assemble(model);
	}

	/**
	 * Assemble a dataset from the model in a file
	 *
	 * @param filename The filename
	 * @param resourceURI URI for the dataset to assembler
	 * @return Dataset
	 */
	public static Dataset assemble(String filename, String resourceURI) {
        Objects.requireNonNull(filename, "file name can not be null") ;
        Objects.requireNonNull(resourceURI, "resourceURI can not be null") ;
		Model model = FileManager.get().loadModel(filename);
		Resource r = model.createResource(resourceURI);
		return assemble(r);
	}

	/**
	 * Assemble a dataset from the model
	 *
	 * @param model
	 * @return Dataset
	 */
	public static Dataset assemble(Model model) {
        Objects.requireNonNull(model, "model can not be null") ;
		Resource r = GraphUtils.findRootByType(model, DatasetAssembler.getType());
		if (r == null) throw new ARQException("No root found for type <" + DatasetAssembler.getType() + ">");

		return assemble(r);
	}

	/**
	 * Assemble a dataset from a resource
	 *
	 * @param resource The resource for the dataset
	 * @return Dataset
	 */

	public static Dataset assemble(Resource resource) {
        Objects.requireNonNull(resource, "resource can not be null") ;
		Dataset ds = (Dataset) Assembler.general.open(resource);
		return ds;
	}
}