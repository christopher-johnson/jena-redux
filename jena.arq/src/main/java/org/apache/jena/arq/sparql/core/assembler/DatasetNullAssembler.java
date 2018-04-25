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

package org.apache.jena.arq.sparql.core.assembler;

import org.apache.jena.core.assembler.Assembler;
import org.apache.jena.core.assembler.Mode;
import org.apache.jena.core.assembler.assemblers.AssemblerBase;
import org.apache.jena.base.atlas.lib.InternalErrorException;
import org.apache.jena.arq.query.Dataset;
import org.apache.jena.arq.query.DatasetFactory;
import org.apache.jena.core.rdf.model.Resource;
import org.apache.jena.arq.sparql.core.DatasetGraph;
import org.apache.jena.arq.sparql.core.DatasetGraphSink;
import org.apache.jena.arq.sparql.core.DatasetGraphZero;

/**
 * An assembler that creates datasets that do nothing, either a sink or a always empty one.

 * @see DatasetGraphSink
 * @see DatasetGraphZero
 */

public class DatasetNullAssembler extends AssemblerBase {
    private final Resource tDataset;

    public DatasetNullAssembler(Resource tDataset) {
        this.tDataset = tDataset;
    }

    @Override
    public Object open(Assembler a, Resource root, Mode mode) {
        DatasetGraph dsg;
        if ( DatasetAssemblerVocab.tDatasetSink.equals(tDataset) )
            dsg = new DatasetGraphSink();
        else if ( DatasetAssemblerVocab.tDatasetZero.equals(tDataset) )
            dsg = new DatasetGraphZero();
        else
            throw new InternalErrorException();
        Dataset ds = DatasetFactory.wrap(dsg);
        AssemblerUtils.setContext(root, ds.getContext());
        return ds;
    }
}
