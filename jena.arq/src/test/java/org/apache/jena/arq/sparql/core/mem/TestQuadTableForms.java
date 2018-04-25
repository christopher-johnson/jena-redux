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

package org.apache.jena.arq.sparql.core.mem;

import static org.apache.jena.core.graph.Node.ANY;
import static org.apache.jena.arq.sparql.core.mem.QuadTableForm.*;
import static org.apache.jena.arq.sparql.core.mem.TupleSlot.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.jena.arq.sparql.core.Quad;
import org.junit.Test;

public class TestQuadTableForms extends AbstractTestTupleTableForms<QuadTableForm> {

	@Override
	protected Stream<QuadTableForm> tableForms() {
		return QuadTableForm.tableForms();
	}

	@Override
	protected Stream<Set<TupleSlot>> queryPatterns() {
		return AbstractTestQuadTable.quadQueryPatterns();
	}

	private static Map<Set<TupleSlot>, Set<QuadTableForm>> answerKey = new HashMap<Set<TupleSlot>, Set<QuadTableForm>>() {
		{
			put(Set.of(GRAPH), Set.of(GSPO, GOPS));
			put(Set.of(GRAPH, SUBJECT), Set.of(GSPO));
			put(Set.of(GRAPH, SUBJECT, PREDICATE), Set.of(GSPO));
			put(Set.of(GRAPH, SUBJECT, OBJECT), Set.of(OSGP));
			put(Set.of(SUBJECT), Set.of(SPOG));
			put(Set.of(PREDICATE), Set.of(PGSO));
			put(Set.of(GRAPH, PREDICATE), Set.of(PGSO));
			put(Set.of(SUBJECT, PREDICATE), Set.of(SPOG));
			put(Set.of(OBJECT), Set.of(OPSG, OSGP));
			put(Set.of(GRAPH, OBJECT), Set.of(GOPS));
			put(Set.of(SUBJECT, OBJECT), Set.of(OSGP));
			put(Set.of(PREDICATE, OBJECT), Set.of(OPSG));
			put(Set.of(GRAPH, PREDICATE, OBJECT), Set.of(GOPS));
			put(Set.of(SUBJECT, PREDICATE, OBJECT), Set.of(SPOG));
			put(Set.of(SUBJECT, PREDICATE, OBJECT, GRAPH), Set.of(GSPO, GOPS, SPOG, OPSG, OSGP, PGSO));
			put(Set.of(), Set.of(GSPO));
		}
	};

	@Test
	public void addAndRemoveSomeQuads() {
		tableForms().map(QuadTableForm::get).map(table -> new AbstractTestQuadTable() {

			@Override
			protected QuadTable table() {
				return table;
			}

			@Override
			protected Stream<Quad> tuples() {
				return table.find(ANY, ANY, ANY, ANY);
			}
		}).forEach(AbstractTestTupleTable::addAndRemoveSomeTuples);
	}

	@Override
	protected QuadTableForm chooseFrom(final Set<TupleSlot> sample) {
		return QuadTableForm.chooseFrom(sample);
	}

	@Override
	protected Map<Set<TupleSlot>, Set<QuadTableForm>> answerKey() {
		return answerKey;
	}
}
